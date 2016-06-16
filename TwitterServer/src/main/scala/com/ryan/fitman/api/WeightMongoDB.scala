package com.ryan.fitman.api

import com.ryan.fitman.mongo.Helpers._
import com.ryan.fitman.mongo.MongoHelper._
import com.ryan.fitman.mongo.{MongoConfig, MongoHelper}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.utils.FuturePools
import com.twitter.inject.Logging
import org.mongodb.scala.{Completed, Document, Observer}
import MongoConfig.dbName
import MongoConfig.weightCollection
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._


// Create = PUT with a new URI
//          POST to a base URI returning a newly created URI
// Read   = GET
// Update = PUT with an existing URI
// Delete = DELETE

class WeightMongoDB extends Controller with Logging {

  val collection = MongoConfig.connect(dbName, weightCollection)

  val futurePool = FuturePools.unboundedPool("CallbackConverter")

  get("/mongo/weights") { request: Request =>
    info("finding all weights for all users...")

  }

  get("/mongo/weights/:user") { request: Request =>
    futurePool {
      info(s"finding weight for user ${request.params("user")}")

    }
  }

  post("/mongo/weights") { weight: Weight =>
    futurePool {
      val r = time(s"Total time take to POST weight into MongoDB for user '${weight.user}' is %d ms") {

        val doc = MongoHelper.convert(weight)

        collection.insertOne(doc).subscribe(new Observer[Completed] {

          override def onNext(result: Completed): Unit = println("Inserted")

          override def onError(e: Throwable): Unit = println("Failed")

          override def onComplete(): Unit = println("Completed")
        })
        response.created.location(s"/weights/${weight.user}")
      }
      r
    }
  }

  put("/mongo/weights/update") { weight: Weight =>
    info(s"Update weight ${weight.user}")

    response.created.location(s"/weights/${weight.user}")
  }



  delete("/mongo/weights/delete") { weight: Weight =>
    futurePool {
      val r = time(s"Total time take to POST weight into MongoDB for user '${weight.user}' is %d ms") {

        val doc = MongoHelper.convert(weight)

        collection.deleteOne(equal("user", weight.user)).results()
        response.created.location(s"/weights/${weight.user}")
      }
      r
    }
    response.created.location(s"/weights/${weight.user}")
  }

}



