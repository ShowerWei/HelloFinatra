package com.ryan.fitman.api

import com.ryan.fitman.mongo.DocumentHelpers._
import com.ryan.fitman.mongo.MongoConfig
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.utils.FuturePools
import com.twitter.inject.Logging
import MongoConfig.dbName
import MongoConfig.weightCollection
import com.mongodb.client.model.UpdateOptions
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.Document

import com.twitter.bijection.Conversion._
import com.twitter.bijection.twitter_util.UtilBijections.twitter2ScalaFuture
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.{Future => TwitterFuture}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Create = PUT with a new URI
//          POST to a base URI returning a newly created URI
// Read   = GET
// Update = PUT with an existing URI
// Delete = DELETE

class WeightMongoDB extends Controller with Logging {

  val collection = MongoConfig.connect(dbName, weightCollection)

  val futurePool = FuturePools.unboundedPool("CallbackConverter")

  get("/mongo/weights") { request: Request =>
    val r = time("finding all weights for all users...is %d ms") {
      val result = (for {
        seqDocs <- collection.find()
          .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_AGE, KEY_STATUS), excludeId())).toFuture()
      } yield {
        response.ok.json(seqDocs.jsonizeDocs())
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  get("/mongo/weights/user/:user") { request: Request =>
    val r = time(s"finding weight for user ${request.params(KEY_USER)} is %d ms") {
      val result = (for {
        seqDocs <- collection.find(equal(KEY_USER, request.params(KEY_USER)))
          .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_STATUS), excludeId())).toFuture()
      } yield {
        response.ok.json(seqDocs.jsonizeDocs())
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  get("/mongo/weights/weight/:weight") { request: Request =>
    val r = time(s"Total time take to search weight ${request.params(KEY_WEIGHT)} is %d ms") {
      val result = (for {
        seqDocs <- collection.find(equal(KEY_WEIGHT, request.params(KEY_WEIGHT).toInt))
          .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_AGE, KEY_STATUS), excludeId())).toFuture()
      } yield {
        response.ok.json(seqDocs.jsonizeDocs())
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  get("/mongo/weights/age/:age") { request: Request =>
    val r = time(s"Total time take to search age ${request.params(KEY_AGE)} is %d ms") {
      val result = (for {
        seqDocs <- collection.find(equal(KEY_AGE, request.params(KEY_AGE).toInt))
          .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_AGE, KEY_STATUS), excludeId())).toFuture()
      } yield {
        response.ok.json(seqDocs.jsonizeDocs())
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  post("/mongo/weights") { weight: Weight =>
    val r = time(s"Total time take to POST weight for user '${weight.user}' is %d ms") {
      val result = (for {
        seqDocs <- collection.insertOne(weight.convertToDoc()).toFuture()
      } yield {
        response.created.location(s"/mongo/weights/${weight.user}")
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  post("/mongo/weights/random") { weight: Weight =>
    val r = time(s"Total time take to post random users is %d ms") {

      val result = (for {
        listDocs <- Future {
          val listBuf = ListBuffer[Document]()
          for (a <- 1 to 50000) {
            listBuf += weight.randomDoc()
          }
          listBuf.toList
        }

        seqDocs <- collection.insertMany(listDocs).toFuture()
      } yield {
        response.created.location(s"/mongo/weights/${weight.user}")
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  put("/mongo/weights/update") { weight: Weight =>
    val r = time(s"Total time take to Update weight for user ${weight.user} is %d ms") {
      collection.updateOne(equal(KEY_USER, weight.user),
        set(KEY_WEIGHT, weight.weight),
        new UpdateOptions().upsert(true)).toFuture()
      response.ok.location(s"/mongo/weights/${weight.user}")
    }
    r
  }

  put("/mongo/weights/replace") { weight: Weight =>
    val r = time(s"Total time take to Replace weight for user ${weight.user} is %d ms") {
      val result = (for {
        seqDocs <- collection.findOneAndReplace(equal(KEY_USER, weight.user), weight.convertToDoc()).toFuture()
      } yield {
        response.ok.location(s"/mongo/weights/${weight.user}")
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }

  delete("/mongo/weights/delete") { weight: Weight =>
    val r = time(s"Total time take to DELETE weight for user '${weight.user}' is %d ms") {
      val result = (for {
        seqDocs <- collection.findOneAndDelete(equal(KEY_USER, weight.user)).toFuture()
      } yield {
        response.ok.location(s"/mongo/weights/${weight.user}")
      }).recover {
        case e: Throwable => println("error" + e)
          response.internalServerError
      }
      result.as[TwitterFuture[ResponseBuilder#EnrichedResponse]]
    }
    r
  }
}



