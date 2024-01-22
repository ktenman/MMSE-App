import librosa
import logging
import time
import torch
from flask import Flask, request, jsonify
from transformers import Wav2Vec2ForCTC, Wav2Vec2Processor

# Initialize logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
app.logger.setLevel(logging.INFO)

# Configuration
LANG_ID = "en"
MODEL_ID = "jonatasgrosman/wav2vec2-large-xlsr-53-english"
device = "cuda" if torch.cuda.is_available() else "cpu"
logger.info(f"Using device: {device}")

# Load model and processor
processor = Wav2Vec2Processor.from_pretrained(MODEL_ID)
model = Wav2Vec2ForCTC.from_pretrained(MODEL_ID).to(device)


# Audio file processing function
def speech_file_to_array_fn(file_path):
    speech_array, sampling_rate = librosa.load(file_path, sr=16_000)
    inputs = processor(speech_array, sampling_rate=16_000, return_tensors="pt", padding=True)
    with torch.no_grad():
        logits = model(inputs.input_values, attention_mask=inputs.attention_mask).logits
    predicted_ids = torch.argmax(logits, dim=-1)
    return processor.batch_decode(predicted_ids)


# Transcription endpoint
@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    start_time = time.time()
    if 'file' not in request.files:
        logger.error("No file part in the request.")
        return jsonify({'error': 'No file part'})

    file = request.files['file']
    if file.filename == '':
        logger.error("No file selected.")
        return jsonify({'error': 'No selected file'})

    temp_file_path = "temp_audio_file.wav"
    file.save(temp_file_path)
    logger.info(f"File saved at {temp_file_path}")

    try:
        predicted_sentences = speech_file_to_array_fn(temp_file_path)
        duration = time.time() - start_time
        response = {
            'file_name': file.filename,
            'transcription': predicted_sentences,
            'duration_seconds': duration,
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
    app.run(host='0.0.0.0', port=61236, debug=True)
