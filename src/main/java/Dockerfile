FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/spagnuolocarmine/distributedsystems.git
WORKDIR ./distributedsystems
RUN git checkout doomdragon

FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY --from=0 /app/distributedsystems/submissions/doomdragon/distribuitedsystems /app
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
ENV NAME=default
ENV ID=0
ENV HOSTNAME=127.0.0.0
COPY --from=1 /app/target/distribuitedsystems-1.0-jar-with-dependencies.jar /app

CMD /usr/bin/java -jar distribuitedsystems-1.0-jar-with-dependencies.jar -n $NAME -id $ID -h $HOSTNAME
