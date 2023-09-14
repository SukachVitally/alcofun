# alcosearch_backend
Alcosearch backend

## Database migration
pgcli postgres://user:password@localhost:5432/alcosearch

mvn clean flyway:migrate -Dflyway.user=user -Dflyway.password=password -Dflyway.url=jdbc:postgresql://localhost:5432/alcosearch -Duser.timezone=Europe/Kiev

## Deploy
### Compile jar file locally
```shell
mvn package -DskipTests
```

### Run app from jar
```shell
java -jar target/searchapp-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=prod

```

## Deploy with ansible
```shell
ansible-playbook -i ansible/hosts.ini ansible/deploy_dev.yaml
```
