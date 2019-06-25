FROM clojure:tools-deps-alpine

COPY ./bot /code
COPY ./db /code
COPY ./deps.edn /code/

WORKDIR /code

ENTRYPOINT ["clojure", "-m", "www"]
