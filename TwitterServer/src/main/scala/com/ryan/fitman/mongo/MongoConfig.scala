package com.ryan.fitman.mongo

import java.util

import com.typesafe.config._
import com.mongodb.ServerAddress
import com.mongodb.connection.ClusterSettings
import com.twitter.inject.{Injector, TwitterModule}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCollection, MongoCredential, MongoDatabase, _}
import com.ryan.fitman.mongo.DocumentHelpers._
import org.mongodb.scala.model.IndexOptions

import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}


object MongoConfig extends TwitterModule {

  private var mongoClient = MongoClient()

  val config = ConfigFactory.load("mongo")
  //TODO: Default
  val host = config.getString("mongo.host")
  val port = config.getInt("mongo.port") | 27017

  val user = config.getString("mongo.weight.user")
  val password = config.getString("mongo.weight.password")

  val dbName = config.getString("mongo.dbname")
  val weightCollection = config.getString("mongo.collection.weight")


  def connect(dbName: String, collectionName: String): MongoCollection[Document] = {
    println("=================db connect==================")
    val server: util.ArrayList[ServerAddress] = new util.ArrayList()

    server.add(new ServerAddress(host, port))

    val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(server).build()

    val credentials: util.ArrayList[MongoCredential] = new util.ArrayList()

    credentials.add(MongoCredential.createCredential(user, dbName, password.toCharArray))

    val clientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).credentialList(credentials).build()

    mongoClient = MongoClient(clientSettings)

    val database: MongoDatabase = mongoClient.getDatabase(dbName)

    val collection = database.getCollection(collectionName)

    collection.createIndex(Document(KEY_AGE -> 1), IndexOptions().background(true)).toFuture().onComplete({
      case Success(res) =>
        println("createIndex success")
      case Failure(throwable) =>
        println("createIndex fail")
    }    )

    collection
  }

  override def singletonShutdown(injector: Injector) {
    mongoClient.close()
    println("=================db disconnect==================")
  }
}
