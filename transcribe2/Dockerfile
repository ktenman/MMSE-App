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

# Expose the port the app runs on
EXPOSE 61236

# Run app.py when the container launches
CMD ["python", "app.py"]
