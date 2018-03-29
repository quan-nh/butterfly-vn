# -*- coding: utf-8 -*-
import sys
from flask import Flask, request, jsonify
from chatterbot import ChatBot
from chatterbot.trainers import ChatterBotCorpusTrainer
from tensorflow.python.framework.errors_impl import InvalidArgumentError
from label_image import label_image
import json
from urllib.parse import unquote
from urllib.error import HTTPError

app = Flask(__name__)

chatbot = ChatBot(
  "Butterfly",
  preprocessors=[
    "chatterbot.preprocessors.clean_whitespace"
  ],
  filters=[
    "chatterbot.filters.RepetitiveResponseFilter"
  ],
  storage_adapter="chatterbot.storage.SQLStorageAdapter",
  database="./chatbot")

chatbot.set_trainer(ChatterBotCorpusTrainer)
chatbot.train(
    "./data"
)


@app.route("/chatbot/<string:query>")
def chatbot_response(query):
  return str(chatbot.get_response(query))


@app.route("/label_image")
def label_image_resp():
  image_url = unquote(request.args.get('image_url', ''))
  no_predict = request.args.get('no_predict', '2')
  
  try:
    image_label = label_image(image_url, int(no_predict))
    json_string = json.dumps(image_label)
    print(image_url)
    print(json_string)

    return jsonify(image_label)

  except (HTTPError, InvalidArgumentError):
    return '', 400

  except:
    print("Unexpected error:", sys.exc_info()[0])
    return '', 500


if __name__ == "__main__":
  app.run(host="0.0.0.0", port=5000)
