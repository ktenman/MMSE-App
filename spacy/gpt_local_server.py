from flask import Flask, request, jsonify
from transformers import GPT2LMHeadModel, GPT2Tokenizer

app = Flask(__name__)

# Load pre-trained model and tokenizer
model = GPT2LMHeadModel.from_pretrained("distilgpt2")
tokenizer = GPT2Tokenizer.from_pretrained("distilgpt2", use_fast=True)

@app.route('/check_phrase', methods=['POST'])
def check_phrase():
    # Get parameters from the request
    text = request.json.get('text', '')
    max_length = request.json.get('max_length', 50)  # Default to 50 if not provided
    target_phrase = request.json.get('target_phrase', "not a pencil")  # Default to "not a pencil"

    # Validate max_length
    if not isinstance(max_length, int) or max_length <= 0:
        return jsonify({"error": "max_length must be a positive integer"}), 400

    input_ids = tokenizer.encode(text, return_tensors='pt')
    output = model.generate(input_ids, max_length=max_length, num_return_sequences=1)

    decoded_output = tokenizer.decode(output[0], skip_special_tokens=True)

    # Check if the target phrase is in the model's output
    if target_phrase.lower() in decoded_output.lower():
        return jsonify({"status": "incorrect"})
    else:
        return jsonify({"status": "correct"})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)


