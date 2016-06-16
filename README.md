# HelloFinatra
Simple Finatra demo with MongoDB

##

##Dockerizing MongoDB
###Step 1: Setup MongoDB with data volume
Create data volume
```
docker create --name mongo-data-volume -v /data/db mongo:latest
```
Run mongo container
```
docker run -d --name ryan-mongo -p 27017:27017 --volumes-from mongo-data-volume mongo:latest --auth
```

###Step 2: Authorized admin
Connecting to: admin
```
docker exec -it ryan-mongo mongo admin
```
Add first admin user
```
db.createUser({ user: 'admin', pwd: 'password', roles: [ { role: "root", db: "admin" } ] });exit;
```

Shell back into mongodb with the above admin user
```
docker exec -it ryan-mongo mongo --port 27017 -u admin -p password --authenticationDatabase admin
```
Connect using the new user to any database you want and add further users
```
use another
db.createUser({ user: "user", pwd: "1234", roles: ["readWrite"] })
```
