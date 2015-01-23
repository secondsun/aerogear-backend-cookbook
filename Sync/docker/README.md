Edit sync/files/sync.config to add a GCM sender id and client id   

from the project root dir (the one with this file)  do the following   

```
cd postgres  
mkdir data  
docker run -p 5433:5432 --name keycloak-postgres -e POSTGRES_PASSWORD=keycloak -e POSTGRES_USER=keycloak -v $PWD/data:/var/lib/postgresql/data -d postgres  
docker start keycloak-postgres   

cd -  
cd wildfly  
docker build -t keycloak_wildfly ./  
docker run -it --name=keycloak-wildfly -p 8080:8080 -p 9090:9090 -p 9990:9990 --link keycloak-postgres:postgres keycloak_wildfly  

cd -  
cd sync  
docker run -it --name=sync-server -p 7777:7777 sync-server  
```

Log into KeyCloak http://localhost:8080/auth

Import the sync-realm.json file

cd into SyncDoc
run `mvn clean install wildfly:deploy`
username admin
password Admin


now you should have everythign up and running

