# alcosearch_backend
Alcosearch backend

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/#build-image)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsinge/index.html#web.reactive)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsinge/index.html#data.sql.r2dbc)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)

### Additional Links
These additional references should also help you:

* [R2DBC Homepage](https://r2dbc.io)

## Database migration
pgcli postgres://user:password@localhost:5432/alcosearch

mvn clean flyway:migrate -Dflyway.user=user -Dflyway.password=password -Dflyway.url=jdbc:postgresql://localhost:5432/alcosearch -Duser.timezone=Europe/Kiev

## Deploy
### Compile jar file locally
```shell
mvn package -DskipTests
```

### Run app from jar (prod env)
```shell
java -jar target/searchapp-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=prod
```

### Run app from jar (local)
```shell
java -Duser.timezone=Europe/Zaporozhye -jar target/searchapp-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=prod
```

## Deploy with ansible
```shell
ansible-playbook -i ansible/hosts.ini ansible/deploy_dev.yaml
```

## Deploy docker image to private registry
```shell
docker build . -t virusx:32000/alcosearch
docker pull virusx:32000/alcosearch
```