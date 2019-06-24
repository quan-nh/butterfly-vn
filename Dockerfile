FROM clojure:tools-deps-alpine

ADD ./bot /code
ADD ./db /code
ADD ./deps.edn /code

WORKDIR /code

ENTRYPOINT ["clojure", "-m", "www"]
