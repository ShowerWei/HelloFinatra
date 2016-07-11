package com.ryan.fitman.mongo

import java.util

import com.typesafe.config._
import com.mongodb.ServerAddress
import com.mongodb.connection.ClusterSettings
import com.twitter.inject.{Injector, TwitterModule}
import org.mongodb.scala._
import com.ryan.fitman.mongo.MongoIndexesHelpers._
import com.ryan.fitman.mongo.DocumentHelpers._


object MongoConfig extends TwitterModule {

  private var mongoClient = MongoClient()

  val appConfig = ConfigFactory.load("application")
  val config = ConfigFactory.load(appConfig.getString("fitman.mongoconfig"))

  //TODO: Default
  val host = config.getString("mongo.host")
  val port = config.getInt("mongo.port") | 27017

  val user = config.getString("mongo.weight.user")
  val password = config.getString("mongo.weight.password")

  val dbName = config.getString("mongo.dbname")
  val weightCollection = config.getString("mongo.collection.weight")

  override def singletonStartup(injector: Injector) {
    println("=================db startup==================")
    val server: util.ArrayList[ServerAddress] = new util.ArrayList()

    server.add(new ServerAddress(host, port))

    val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(server).build()

    val credentials: util.ArrayList[MongoCredential] = new util.ArrayList()

    credentials.add(MongoCredential.createCredential(user, dbName, password.toCharArray))

    val clientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).credentialList(credentials).build()

    mongoClient = MongoClient(clientSettings)
  }

  def connect(dbName: String, collectionName: String): MongoCollection[Document] = {

    val database: MongoDatabase = mongoClient.getDatabase(dbName)

    val collection = database.getCollection(collectionName)

    //db.serverStatus() could check memory usage
    //db.collection.stats() could check index/data size
    /*MongoDB only needs to keep the parts of the index
    that hold the most recent or 「right-most」 values in RAM.
    This allows for efficient index use for read and write operations
    and minimize the amount of RAM required to support the index.
     */
    collection.index(Document(KEY_AGE -> 1), "singleIndex")
    collection.index(Document(KEY_WEIGHT -> 1, KEY_AGE -> 1), "compoundIndex")
    collection.index(Document(KEY_USER -> 1, KEY_AGE -> 1, KEY_WEIGHT -> -1), "compoundIndex")

    collection
  }

  override def singletonShutdown(injector: Injector) {
    mongoClient.close()
    println("=================db disconnect==================")
  }
}
