#!/usr/bin/env python
import sys
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
from urllib.error import HTTPError
from tensorflow.python.framework.errors_impl import InvalidArgumentError
import json
from label_image import label_image

# HTTPRequestHandler class
class simpleHTTPServer_RequestHandler(BaseHTTPRequestHandler):

  # GET
  def do_GET(self):
    parsed_path = urlparse(self.path)

    if parsed_path.path == '/label_image' and 'image_url' in parsed_path.query:
      image_url = parse_qs(parsed_path.query)['image_url'][0]

      try:
        image_label = label_image(image_url)
        json_string = json.dumps(image_label)
        print(image_url)
        print(json_string)

        # Send response status code
        self.send_response(200)

        # Send headers
        self.send_header('Content-type','application/json')
        self.end_headers()

        # Send message back to client
        self.wfile.write(bytes(json_string, "utf8"))

      except (HTTPError, InvalidArgumentError):
        # Send response status code
        self.send_error(400)

      except:
        print("Unexpected error:", sys.exc_info()[0])
        self.send_error(500)

    else:
      # Send response status code
      self.send_error(404)

    return


def run():
  print('starting server...')

  server_address = ('', 8000)
  httpd = HTTPServer(server_address, simpleHTTPServer_RequestHandler)
  print('running server...')
  httpd.serve_forever()


run()