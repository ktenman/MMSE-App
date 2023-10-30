FROM python:3.8-slim

# Update the system packages
RUN apt-get update && apt-get clean

WORKDIR /app

COPY ./gpt_local_server.py /app/gpt_local_server.py
COPY ./requirements.txt /app/requirements.txt

# Upgrade pip
RUN pip install --upgrade pip

# Install Python dependencies
RUN pip install -r requirements.txt

EXPOSE 5000


CMD ["python", "/app/gpt_local_server.py"]

