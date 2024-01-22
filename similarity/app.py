import numpy as np
import tensorflow_hub as hub
from flask import Flask, request, jsonify

app = Flask(__name__)
model = hub.load("https://tfhub.dev/google/universal-sentence-encoder/4")


def get_embedding(sentences):
    return model(sentences).numpy()


def cosine_similarity(embedding1, embedding2):
    dot_product = np.dot(embedding1, embedding2.T)
    norm_a = np.linalg.norm(embedding1)
    norm_b = np.linalg.norm(embedding2)
    return dot_product / (norm_a * norm_b)


@app.route('/compare', methods=['POST'])
def compare_sentences():
    content = request.json
    sentence1 = content['sentence1']
    sentence2 = content['sentence2']

    embedding1 = get_embedding([sentence1])[0]
    embedding2 = get_embedding([sentence2])[0]

    similarity = cosine_similarity(embedding1, embedding2)

    return jsonify({'similarity': float(similarity)})


@app.route('/health', methods=['GET'])
def healthcheck():
    return jsonify({'status': 'healthy'}), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=61237)
