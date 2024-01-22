import logging
from flask import Flask, request, jsonify
from fuzzywuzzy import fuzz
from sentence_transformers import SentenceTransformer, util

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)

model = SentenceTransformer('all-MiniLM-L12-v2')
app.logger.info("Model loaded successfully.")


@app.route('/synonym', methods=['POST'])
def check_synonym():
    data = request.get_json()
    text1 = data.get('text1', '')
    text2 = data.get('text2', '')
    app.logger.info(f"Received texts: '{text1}' and '{text2}' for synonym check.")

    # Check for typo similarity using FuzzyWuzzy
    typo_similarity = fuzz.ratio(text1, text2)
    app.logger.info(f"Typo similarity score: {typo_similarity}")
    if typo_similarity >= 80:  # If texts are very similar, return as synonyms
        app.logger.info("Texts are considered as synonyms based on typo similarity.")
        response = jsonify({'is_synonym': True, 'similarity_score': typo_similarity / 100.0})
        app.logger.info("Response sent: ", response)
        return response

    # Generate embeddings for more in-depth similarity check
    embeddings1 = model.encode(text1, convert_to_tensor=True)
    embeddings2 = model.encode(text2, convert_to_tensor=True)
    app.logger.info("Embeddings generated.")

    # Compute cosine similarity
    cosine_scores = util.pytorch_cos_sim(embeddings1, embeddings2)
    app.logger.info(f"Cosine similarity score: {cosine_scores.item()}")

    # Consider texts as synonyms if the similarity score is above a certain threshold
    threshold = 0.6  # This is an arbitrary threshold
    is_synonym = cosine_scores.item() >= threshold
    app.logger.info(f"Texts {'are' if is_synonym else 'are not'} synonyms based on cosine similarity.")

    return jsonify({'is_synonym': is_synonym, 'similarity_score': cosine_scores.item()})


@app.route('/health', methods=['GET'])
def healthcheck():
    app.logger.info("Health check requested.")
    return jsonify({'status': 'healthy'}), 200


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=61238)
