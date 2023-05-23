# api Project
## Tech Stack
- Backend: Java Framework: This project uses Quarkus, the Supersonic Subatomic Java Framework. If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .
- Frontend: JavaScript, HTML
- Database: PostgreSQL via Docker
- Infrastructure: API Gateway (Kong)

## Purpose
This projects illustrates the use of the API Gateway 'Kong' (Services, Routes, API Rate-Limiting, API Key) via a simple Java Web App that adds or subtracts two numbers. The Add-Feature further determines wheter the result is a prime number or not. 



## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Quarkus Extension for Spring Web API ([guide](https://quarkus.io/guides/spring-web)): Use Spring Web annotations to create your REST services

## Provided Code

### Spring Web

Spring, the Quarkus way! Start your RESTful Web Services with a Spring Controller.

[Related guide section...](https://quarkus.io/guides/spring-web#greetingcontroller)

# Java application
## maven
Apache Maven 3.8.5
## jdk
Java 11.0.15 (jdk from amazon coretto)
## os
Cloud VM: Red Hat Enterprise 7.9
## start application
### kong
#### Running on Linux Cloud VM:  
Kong only runs on linux. Hence, directly installed on Google Cloud VM (Red Hat)
Kong Manager GUI can be accessed via port 8002: http://localhost:8002 (only for enterpreise edition, use Admin API instead for free use. See [API Documentation](https://docs.konghq.com/gateway/latest/admin-api/#main))
Kong is configured on port 8001
Kong listens on port 8000

See installation guide here
https://konghq.com/blog/kong-gateway-tutorial#download
https://docs.konghq.com/gateway/latest/install-and-run/rhel/

Set up Database (here postgresql)
Adjust 'Datastore' section in kong.conf configuration file 
Kong uses root user, so do `kong start -c {'path to kong.conf'}` as root user (`sudo su -`) 

Google Cloud VM already comes with maven and java. Check whether version fits.
If java app runs on google vm, opening the app via `firefox http://localhost:8080 --no-proxy` may load very slow. Instead, you may consider to let the java app run locally and use ssh tunnel to connect kong from remote cloud linux vm  with locally running java app.
If Java applications is run locally (i.e. not on cloud vm), use ssh tunnel with putty
Java application runs local on port 8080.
Kong runs remote on Google VM on port 8000 (listening) and 8002 (kong manager gui)

Destination = Local: 
- L8000 - localhost:8000
- L8002 - localhost:8002

Destination = Remote: R8080 - localhost:8080


#### Running in docker container (https://hub.docker.com/_/kong): 
- pull image: `docker pull kong`
- start kong database (postgreSQL). Since the App uses port 5432 already for the postgreSQL App database to store the input numbers, we check whether port 5431 is unused by Linux command `lsof -i :5431` or `ss -lntp`. We then use port 5431 and map it to postgreSQL port 5432
```
docker run -d --name kong-database \
                -p 5431:5432 \
                -e "POSTGRES_USER=kong" \
                -e "POSTGRES_DB=kong" \
                -e "POSTGRES_PASSWORD=kong" \
                postgres
```
- prepare kong database: set user and password. Bootstrap database
```
docker run --rm \
    --link kong-database:kong-database \
    -e "KONG_DATABASE=postgres" \
    -e "KONG_PG_HOST=kong-database" \
    -e "KONG_PG_USER=kong" \
    -e "KONG_PG_PASSWORD=kong" \
    -e "KONG_CASSANDRA_CONTACT_POINTS=kong-database" \
    kong kong migrations bootstrap
```

- start a Kong container and link it to the database container
```
docker run -d --name kong \
    --link kong-database:kong-database \
    -e "KONG_DATABASE=postgres" \
    -e "KONG_PG_HOST=kong-database" \
    -e "KONG_PG_PASSWORD=kong" \
    -e "KONG_CASSANDRA_CONTACT_POINTS=kong-database" \
    -e "KONG_PROXY_ACCESS_LOG=/dev/stdout" \
    -e "KONG_ADMIN_ACCESS_LOG=/dev/stdout" \
    -e "KONG_PROXY_ERROR_LOG=/dev/stderr" \
    -e "KONG_ADMIN_ERROR_LOG=/dev/stderr" \
    -e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl" \
    -p 8000:8000 \
    -p 8443:8443 \
    -p 8001:8001 \
    -p 8444:8444 \
    kong
```


### java app
### localhost
java app uses postgresql db for endpoint /add to get list of calculations. Set in application properties: `quarkus.datasource.jdbc.url=jdbc:postgresql://localhost/postgres`
run postgresql via docker container: https://hevodata.com/learn/docker-postgresql/. Command line for docker container postgresql according to application.properties file: `docker run --name postgres -e POSTGRES_PASSWORD=0  -p 5432:5432 -d postgres`. Access Database
- You may use a postgresql client like 'PostgreSQL Management Tool' (ckolkman) to run queries on the database (like `Select * FROM Input`).
- You may use the bash: log in to interactive mode: `docker exec -it postgres bash`, log in to database: `psql -U postgres`, `select * from input;`
start application in right folder (where pom-file is located): `mvn quarkus:dev`

open in browser 
- without kong api gateway: http://localhost:8080
- with kong api gateway: http://localhost:8000/home

### docker container
Since the app consists out of java app and a postgres-database, `docker network` is used to containerize application. 
Here is a nice tutorial: https://www.youtube.com/watch?v=S2s28PCg4M4
1.	create network: `docker network create mynetwork`
2.	Start postgres database connected with network: `docker run --name postgres-net --network mynetwork -e POSTGRES_PASSWORD=0 -p 5432:5432 -d postgres`
3.	change url in application.properties and adjust for postgres database container: `quarkus.datasource.jdbc.url=jdbc:postgresql://postgres-net/postgres`
4.	build jar-file: `mvn clean install` or `mvn clean package`
5.	Build docker image based on docker file: `docker build -t quarkuspostgres .`
6.	Run container for java app: `docker run --network mynetwork --name quarkuspostgres-container -p 8080:8080 -d quarkuspostgres`


## Set up API Gateway with kong

**Using gitpod**, 

- you may set ports 8080 (port to run app) and port 8001 (kong confugration port) as public instead private for token-handling.

- Instead of localhost, use gitpod workspace url.
Example: `curl -i -X POST https://{8001-gitpod-workspace-url}/services --data name=calc_service --data url='https://{8080-gitpod-workspace-url}'`

### Set up Service (via Admin API)
```
curl -i -X POST http://localhost:8001/services \
  --data name=calc_service \
  --data url='http://localhost:8080'
```


`curl -X GET "http://localhost:8001/services"`

### Setup Route (via Admin API)
```
curl -i -X POST http://localhost:8001/services/calc_service/routes \
  --data 'paths[]=/home' \
  --data name=Home
```


`curl -X GET "http://localhost:8001/routes"`

`curl -i -X GET "http://localhost:8000/home"`

### Protect Service: API Rate Limiting
#### Globally
```
curl -i -X POST http://localhost:8001/plugins \
  --data name=rate-limiting \
  --data config.minute=3 \
  --data config.policy=local
```


#### locally
```
curl -X POST http://localhost:8001/services/calc_service/plugins \
    --data "name=rate-limiting"  \
    --data "config.minute=3" \
    --data "config.policy=local"
```


  
`curl -X GET "http://localhost:8001/plugins"`

### Secure Service: API Key Authentication on Route

#### 1. Set up key authentication plugin:
```
curl -i -X POST http://localhost:8001/routes/Home/plugins \
  --data name=key-auth
```

  
`curl -X GET "http://localhost:8001/plugins"`


#### 2. Set up Consumer:
```
curl -i -X POST http://localhost:8001/consumers/ \
  --data username=dominik \
  --data custom_id=001
```

  
`curl -X GET "http://localhost:8001/consumers"`

#### 3.  Provision a key for the consumer created above:
```
curl -i -X POST http://localhost:8001/consumers/dominik/key-auth \
  --data key=meinapikey
```

  
`curl -X GET "http://localhost:8001/consumers/dominik/key-auth"`
  
#### 4. Open URL:
Browser: `http://localhost:8000/home?apikey=meinapikey`
cURL: 
```
curl -i http://localhost:8000/home \
  -H 'apikey:meinapikey'
```

 ### Reset
- Delete API Key on Consumer: curl -X DELETE "http://localhost:8001/consumers/dominik/key-auth/{id}"
- Delete Consumer: curl -X DELETE "http://localhost:8001/consumers/{id}"
- Delete API Key Plugin: curl -X DELETE "http://localhost:8001/plugins/{id}"
- Delete API Rate Limit Plugin: curl -X DELETE "http://localhost:8001/plugins/{id}"
- Delete Route: curl -X DELETE "http://localhost:8001/routes/{id}"
- Delete Service: curl -X DELETE "http://localhost:8001/services/{id}"

## Example to set up Addition feature via Kong services and routes
### Set up homepage: Decide whether you want to add or subtract
`curl -i -X POST http://localhost:8001/services --data name=calc_service --data url="http://localhost:8080"`
`curl -i -X POST http://localhost:8001/services/calc_service/routes --data 'paths[]=/home' --data name=Home`

### Set up add.html
`curl -i -X POST http://localhost:8001/services --data name=add --data url="http://localhost:8080/add.html"`
`curl -i -X POST http://localhost:8001/services/add/routes --data 'paths[]=/add.html' --data name=Add`

### Set up Addition
`curl -i -X POST http://localhost:8001/services --data name=add2 --data url="http://localhost:8080/add"`
`curl -i -X POST http://localhost:8001/services/add2/routes --data 'paths[]=/add' --data name=Add2`

### Set up Prime number result
`curl -i -X POST http://localhost:8001/services --data name=prime --data url="http://localhost:8080/prime"`
`curl -i -X POST http://localhost:8001/services/prime/routes --data 'paths[]=/prime' --data name=prime`

# Api Gateway Security
OWASP API Security Project
https://owasp.org/www-project-api-security/
https://juice-shop.herokuapp.com/#/

Most of the top security risks can be covered by API Gateways, although not all.
