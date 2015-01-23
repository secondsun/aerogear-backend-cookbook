FROM jboss/keycloak-adapter-wildfly:1.1.0.Beta2
MAINTAINER jpkroehling@redhat.com

WORKDIR /


RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/

COPY files/postgresql-9.3-1102.jdbc41.jar  /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/

COPY files/module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main/

COPY files/configure-postgres.cli /tmp/

COPY files/execute.sh /tmp/

RUN /tmp/execute.sh 

COPY files/launch.sh /opt/jboss/
COPY files/keycloak-ds.xml /opt/jboss/keycloak/deployments/
COPY files/sync-demo-ds.xml /opt/jboss/keycloak/deployments/

RUN cp -R /opt/jboss/keycloak/deployments/* /opt/jboss/wildfly/standalone/deployments/

CMD /opt/jboss/launch.sh

