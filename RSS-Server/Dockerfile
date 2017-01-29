FROM java:8

MAINTAINER "Pierre HENNEBOIS <pierre.hennebois@epitech.eu>"

RUN apt-get update
RUN apt-get install -y maven

ADD ./context.xml /usr/local/tomcat/webapps/manager/META-INF
ADD ./settings.xml /usr/local/tomcat/conf/
ADD ./tomcat-users.xml /usr/local/tomcat/conf/

ADD pom.xml /pom.xml
ADD ./src/main/webapp/WEB-INF/web.xml /src/main/webapp/WEB-INF/web.xml
RUN ["mvn", "dependency:resolve"]

ADD ./src /code/src
RUN ["mvn", "package"]
CMD ["usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-war", "target/rssAggregatorServer.war"]

FROM tomcat:8.5.11-jre8
COPY ./target/rssAggregatorServer.war /usr/local/tomcat/webapps/rssAggregatorServer.war

