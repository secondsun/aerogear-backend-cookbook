FROM jboss/base-jdk:7

USER root
COPY files/sync.config /opt/jboss/
COPY files/sync-server /opt/jboss/sync-server
RUN cd /opt/jboss/sync-server; cp ../sync.config ./sync.config; jar -cMf ../sync-server.jar .;
RUN chown -R jboss:jboss /opt/jboss

EXPOSE 7777
EXPOSE 5235
CMD java -jar /opt/jboss/sync-server.jar
