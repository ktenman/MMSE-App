from flask import Flask, request, jsonify
from transformers import AutoModelForCTC, AutoProcessor
import torch
import logging
from pydub import AudioSegment
import torchaudio
import time

# Initialize logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Check if MPS (Apple Silicon GPU) is available and set device
device = "mps" if torch.backends.mps.is_available() else "cpu"
logger.info(f"Using device: {device}")

# Define a list of model names
MODEL_NAMES = [
    "facebook/wav2vec2-large-960h",
    "facebook/wav2vec2-large-robust-ft-libri-960h"
]

# Load models and processors
models = {}
processors = {}

for model_name in MODEL_NAMES:
    processor = AutoProcessor.from_pretrained(model_name)
    model = AutoModelForCTC.from_pretrained(model_name).to(device)
    models[model_name] = model
    processors[model_name] = processor

def convert_mp3_to_wav(mp3_file_path, wav_file_path):
    try:
        audio = AudioSegment.from_mp3(mp3_file_path)
        audio.export(wav_file_path, format="wav")
        return wav_file_path
    except Exception as e:
        logger.error(f"Error converting MP3 to WAV: {e}")
        raise

def transcribe_process(audio_input, model_name):
    logger.info(f"Transcribing file: {audio_input} with model {model_name}")

    try:
        waveform, sample_rate = torchaudio.load(audio_input)
        waveform = waveform.to(device)

        if sample_rate != 16000:
            resampler = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=16000)
            waveform = resampler(waveform)

        processor = processors[model_name]
        model = models[model_name]

        input_values = processor(waveform.squeeze(0), sampling_rate=16000, return_tensors="pt").input_values
        input_values = input_values.to(device)
        with torch.no_grad():
            logits = model(input_values).logits
        predicted_ids = torch.argmax(logits, dim=-1)
        transcription = processor.decode(predicted_ids[0])
    except Exception as e:
        logger.error(f"Error in processing audio for transcription: {e}")
        raise

    logger.info(f"Transcription completed: {transcription}")
    return transcription

@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    model_name = request.form.get('model_name')
    if model_name not in MODEL_NAMES:
        return jsonify({'error': 'Invalid model name'}), 400

    if 'file' not in request.files:
        logger.error("No file part in the request.")
        return jsonify({'error': 'No file part'})

    file = request.files['file']
    if file.filename == '':
        logger.error("No file selected.")
        return jsonify({'error': 'No selected file'})

    convert_mp3 = request.form.get('convert_mp3', 'false').lower() == 'true'

    temp_file_path = "temp_audio_file"
    file_extension = ".mp3" if file.filename.lower().endswith('.mp3') and convert_mp3 else ".wav"
    temp_file_path_with_extension = temp_file_path + file_extension
    file.save(temp_file_path_with_extension)
    logger.info(f"File saved at {temp_file_path_with_extension}")

    try:
        start_time = time.time()
        if file_extension == ".mp3":
            wav_file_path = convert_mp3_to_wav(temp_file_path_with_extension, temp_file_path + ".wav")
            transcription = transcribe_process(wav_file_path, model_name)
        else:
            transcription = transcribe_process(temp_file_path_with_extension, model_name)
        duration = time.time() - start_time

        response = {
            'file_name': file.filename,
            'transcription': transcription,
            'duration_seconds': duration
        }
        return jsonify(response)
    except Exception as e:
        logger.error(f"Error during transcription: {e}")
        return jsonify({'error': 'Error during transcription'}), 500

@app.route('/health', methods=['GET'])
def healthcheck():
    logger.info("Health check requested.")
    return jsonify({'status': 'healthy'}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=61235)
