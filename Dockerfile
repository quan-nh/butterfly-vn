FROM clojure:openjdk-11-tools-deps as builder
WORKDIR /code
COPY . .
RUN clojure -A:depstar -m hf.depstar.uberjar butterfly.jar

FROM openjdk:11
WORKDIR /apps
COPY --from=builder /code/butterfly.jar .
ENTRYPOINT ["java", "-cp", "butterfly.jar", "clojure.main", "-m", "www"]
