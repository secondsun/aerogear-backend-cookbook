docker run -p 5433:5432 --name keycloak-postgres -e POSTGRES_PASSWORD=keycloak -e POSTGRES_USER=keycloak -v $PWD/data:/var/lib/postgresql/data -d postgres
docker run --link postgres:postgres postgres
docker build -t keycloak_wildfly ./
docker run -it --name=keycloak-wildfly -p 8080:8080 -p 9090:9090 -p 9990:9990 --link keycloak-postgres:postgres keycloak_wildfly
docker run -it --name=sync-server -p 7777:7777 sync-server
