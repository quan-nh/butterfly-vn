FROM openjdk:11

COPY ./butterfly.jar /apps/
WORKDIR /apps

ENTRYPOINT ["java", "-cp", "butterfly.jar", "clojure.main", "-m", "www"]
