# HelloFinatra
Simple Finatra demo with MongoDB

Swagger API-Docs: http://localhost:8087/api-docs/ui
## Prerequisite 
Setup MongoDB
## Dockerizing MongoDB
### Step 1: Setup MongoDB with data volume
Create data volume
```
docker create --name mongo-data-volume -v /data/db mongo:3.2
```
Run mongo container
```
docker run -d --name ryan-mongo -p 27017:27017 --volumes-from mongo-data-volume mongo:3.2 --auth
```
### Step 2: Authorized admin
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
use finatra
db.createUser({ user: "user", pwd: "1234", roles: ["readWrite"] })
```
## appendix
### Using cURL to automate HTTP jobs

get all users' weight
```
curl -i http://localhost:8087/mongo/weights
```

get someone's weight
```
curl -i http://localhost:8087/mongo/weights/user_name
```

create an user
```
curl -H "Content-Type: application/json" -i http://localhost:8087/mongo/weights -X POST -d '{"user":"CY","weight":"33","status":"Good"}'
```

update an user
```
curl -H "Content-Type: application/json" -i http://localhost:8087/mongo/weights/update -X PUT -d '{"user":"CY","weight":"55"}'
```

replace an user
```
curl -H "Content-Type: application/json" -i http://localhost:8087/mongo/weights/replace -X PUT -d '{"user":"CY","weight":"50","status":"GG"}'
```

delete an user
```
curl -H "Content-Type: application/json" -i http://localhost:8087/mongo/weights/delete -X DELETE -d '{"user":"CY","weight":"50"}'
```
