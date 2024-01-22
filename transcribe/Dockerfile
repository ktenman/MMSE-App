# Start from a base Python image that is compatible with ARM architecture
FROM python:3.8-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libsndfile1 \
    && rm -rf /var/lib/apt/lists/*

# Copy the current directory contents into the container at /usr/src/app
COPY . .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Install PyTorch compatible with M1
RUN pip install torch torchvision torchaudio --extra-index-url https://download.pytorch.org/whl/nightly/cpu

# Preload models
RUN mkdir -p /usr/src/app/pretrained_models/wav2vec2-large-960h
RUN python -c "from transformers import AutoModelForCTC, Wav2Vec2Processor; \
               model = AutoModelForCTC.from_pretrained('facebook/wav2vec2-large-960h'); \
               model.save_pretrained('/usr/src/app/pretrained_models/wav2vec2-large-960h'); \
               processor = Wav2Vec2Processor.from_pretrained('facebook/wav2vec2-large-960h'); \
               processor.save_pretrained('/usr/src/app/pretrained_models/wav2vec2-large-960h')"

# Preload models for wav2vec2-large-robust-ft-libri-960h
RUN mkdir -p /usr/src/app/pretrained_models/wav2vec2-large-robust-ft-libri-960h
RUN python -c "from transformers import AutoModelForCTC, Wav2Vec2Processor; \
               model = AutoModelForCTC.from_pretrained('facebook/wav2vec2-large-robust-ft-libri-960h'); \
               model.save_pretrained('/usr/src/app/pretrained_models/wav2vec2-large-robust-ft-libri-960h'); \
               processor = Wav2Vec2Processor.from_pretrained('facebook/wav2vec2-large-robust-ft-libri-960h'); \
               processor.save_pretrained('/usr/src/app/pretrained_models/wav2vec2-large-robust-ft-libri-960h')" \

# Expose the port the app runs on
EXPOSE 61235

# Run app.py when the container launches
CMD ["python", "app.py"]
