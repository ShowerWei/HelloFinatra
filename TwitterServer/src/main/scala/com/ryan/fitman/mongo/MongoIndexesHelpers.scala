package com.ryan.fitman.mongo

import org.mongodb.scala.{MongoCollection, _}
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.bson.conversions._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ryan on 7/6/16.
  */
object MongoIndexesHelpers {

  implicit class CreateIndexes(val collection: MongoCollection[Document]) extends ImplicitMongoCollection[Document] {

  }

  trait ImplicitMongoCollection[C] {
    val collection: MongoCollection[C]

    def index(key: Bson, description: String): Unit = {
      collection.createIndex(key, IndexOptions().background(true)).toFuture().onComplete({
        case Success(res) =>
          println("Success with " + description + ": " + key)
        case Failure(throwable) =>
          println("Failure with " + description + ": " + key)
      })
    }
  }

}
