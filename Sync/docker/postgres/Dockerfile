FROM postgres
USER postgres


RUN pg_dropcluster $PG_VERSION main && pg_createcluster --locale en_US.UTF-8 $PG_VERSION main
RUN echo "host all all 0.0.0.0/0 trust" >> /etc/postgresql/$PG_VERSION/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/$PG_VERSION/main/postgresql.conf

RUN service postgresql start &&\
    psql --command "CREATE USER keycloak WITH PASSWORD 'keycloak';" &&\
    createdb -O keycloak keycloak && \
    su postgres sh -c "psql -c \"GRANT ALL PRIVILEGES ON DATABASE keycloak to keycloak;\""
RUN service postgresql start &&\
    psql --command "CREATE USER syncdemo WITH PASSWORD 'syncdemo';" &&\
    createdb -O keycloak keycloak && \
    su postgres sh -c "psql -c \"GRANT ALL PRIVILEGES ON DATABASE syncdemo to syncdemo;\""

RUN echo "host keycloak  keycloak    0.0.0.0/0  md5" >> /etc/postgresql/$PG_VERSION/main/pg_hba.conf
