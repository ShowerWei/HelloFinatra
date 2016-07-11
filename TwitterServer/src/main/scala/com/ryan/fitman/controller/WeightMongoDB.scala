package com.ryan.fitman.controller


import java.util.UUID

import com.ryan.fitman.mongo.DocumentHelpers._
import com.ryan.fitman.mongo.MongoConfig
import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.Controller
import com.twitter.finatra.utils.FuturePools
import com.twitter.inject.Logging
import MongoConfig.dbName
import MongoConfig.weightCollection
import com.github.xiaodongw.swagger.finatra.SwaggerSupport
import com.mongodb.client.model.UpdateOptions
import com.ryan.fitman.SampleSwagger
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

class WeightMongoDB extends Controller with Logging with SwaggerSupport {

  implicit protected val swagger = SampleSwagger

  val collection = MongoConfig.connect(dbName, weightCollection)

  val futurePool = FuturePools.unboundedPool("CallbackConverter")

  get("/mongo/weights", swagger { o =>
    o.summary("Retrieve all weights for all users.")
      .tag("Clients")
      .produces("application/json")
      .routeParam[UUID]("id", "The client id")
      .headerParam[String](
      "accessToken",
      "The access token, please use this server's client id and client_credentials to generate access token.")
      .responseWith(Status.BadRequest.code, "Bad client id !")
      .responseWith(Status.Unauthorized.code, "Bad access token !")
      .responseWith(Status.NotFound.code, "Can't find a specific client !")
      .responseWith(Status.InternalServerError.code, "Unexpected error !")
      .responseWith[Weight](Status.Ok.code, "Retrieve a specific client.")
  }
  ) { request: Request =>
    val r = time("finding all weights for all users...is %d ms") {
      val result = (for {
        seqDocs <- collection.find()
          .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_AGE, KEY_STATUS), excludeId()))
          .toFuture()
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
        seqDocs <- collection
          .find(equal(KEY_USER, request.params(KEY_USER)))
          .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_STATUS), excludeId()))
          .toFuture()
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
        seqDocs <- collection
          .find(equal(KEY_WEIGHT, request.params(KEY_WEIGHT).toInt))
          .projection(fields(include(KEY_USER, KEY_AGE, KEY_WEIGHT), excludeId()))
          .sort(Document(KEY_WEIGHT -> 1, KEY_AGE -> 1))
          .toFuture()
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
        seqDocs <- collection
          .find(equal(KEY_AGE, request.params(KEY_AGE).toInt))
          .projection(fields(include(KEY_USER, KEY_AGE, KEY_WEIGHT), excludeId()))
          .sort(Document(KEY_USER -> 1, KEY_AGE -> 1, KEY_WEIGHT -> -1))
          .toFuture()
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

  post("/mongo/weights/random/:num") { request: Request =>
    val r = time(s"Total time take to post random users is %d ms") {
      val result = (for {
        listDocs <- Future {
          val listBuf = ListBuffer[Document]()
          for (i <- 1 to request.getParam("num").toInt) {
            listBuf += randomDoc()
          }
          listBuf.toList
        }

        seqDocs <- collection.insertMany(listDocs).toFuture()
      } yield {
        response.created
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
      val result = (for {
        seqDocs <- collection.updateOne(equal(KEY_USER, weight.user),
          set(KEY_WEIGHT, weight.weight),
          new UpdateOptions().upsert(true)).toFuture()
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



