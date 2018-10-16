# Demo application for Spring 5, Reactor, Reactive Streams, WebFlux #


## Prequisities ##
* JDK 8+ (tested with 10)
* Maven
* Docker

## setting up a docker container with MongoDB ##

The app uses a change stream from MongoDB. This is supported only for Replica Sets, so one has to be provided.



### create a docker network ###

`docker network create my-mongo-cluster`

### create 3 instances of MongoDB within the network, expose their ports outside ###
```
docker run --name mongo-node1 -p30001:27017 -d --net my-mongo-cluster mongo:3.6 --replSet "rs0"
docker run --name mongo-node2 -p30002:27017 -d --net my-mongo-cluster mongo:3.6 --replSet "rs0"`
docker run --name mongo-node3 -p30003:27017 -d --net my-mongo-cluster mongo:3.6 --replSet "rs0"
```


### go to the MongoDB shell of the 1st instance: ###
`docker exec -it mongo-node1 mongo`


### define a configuration object ###

```
config = {
      "_id" : "rs0",
      "members" : [
          {
              "_id" : 0,
              "host" : "mongo-node1:27017"
          },
          {
              "_id" : 1,
              "host" : "mongo-node2:27017"
          },
          {
              "_id" : 2,
              "host" : "mongo-node3:27017"
          }
      ]
  }
```

### initialize replica set ###
`rs.initiate(config)`

### check the status (all 3 should be mentioned, the current one should be selected as PRIMARY) ###
`rs.status()`


