package com.ryan.fitman.mongo

import java.util

import com.mongodb.ServerAddress
import com.mongodb.connection.ClusterSettings
import com.twitter.inject.TwitterModule
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCollection, MongoCredential, MongoDatabase, _}


object MongoConfig extends TwitterModule {

  var mongoClient = MongoClient()

  private val host = "127.0.0.1:27017"
  private val port = 27017

  private val user = "user"
  private val password = "1234"

  val dbName = "finatra"
  val weightCollection = "weight"


  def connect(dbName: String, collection: String): MongoCollection[Document] = {
    val server: util.ArrayList[ServerAddress] = new util.ArrayList()

    server.add(new ServerAddress(host, port))

    val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(server).build()

    val credentials: util.ArrayList[MongoCredential] = new util.ArrayList()

    credentials.add(MongoCredential.createCredential(user, dbName, password.toCharArray))

    val clientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).credentialList(credentials).build()

    val mongoClient = MongoClient(clientSettings)

    val database: MongoDatabase = mongoClient.getDatabase(dbName)

    database.getCollection(collection)
  }

  def disConnect(): Unit = {
    mongoClient.close()
  }
}
