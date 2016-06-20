package com.ryan.fitman.api


import com.ryan.fitman.mongo.Helpers._
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


// Create = PUT with a new URI
//          POST to a base URI returning a newly created URI
// Read   = GET
// Update = PUT with an existing URI
// Delete = DELETE

class WeightMongoDB extends Controller with Logging {

  val collection = MongoConfig.connect(dbName, weightCollection)

  val futurePool = FuturePools.unboundedPool("CallbackConverter")

  get("/mongo/weights") { request: Request =>
    futurePool {
      info("finding all weights for all users...")
      collection.find()
        .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_POSTED_AT), excludeId())).outputResult()
    }
  }

  get("/mongo/weights/:user") { request: Request =>
    futurePool {
      info(s"finding weight for user ${request.params(KEY_USER)}")
      collection.find(equal(KEY_USER, request.params(KEY_USER)))
        .projection(fields(include(KEY_USER, KEY_WEIGHT, KEY_POSTED_AT), excludeId())).outputResult()
    }
  }

  post("/mongo/weights") { weight: Weight =>
    futurePool {
      val r = time(s"Total time take to POST weight for user '${weight.user}' is %d ms") {
        //mongo.Helpers._ wraps an observable and provides a new method, results().
        //mongo.DocumentHelps._ wraps an Weight and provides a new method, convertToDoc().
        collection.insertOne(weight.convertToDoc()).outputResult()
        response.created.location(s"/mongo/weights/${weight.user}")
      }
      r
    }
  }

  put("/mongo/weights/update") { weight: Weight =>
    futurePool {
      info(s"Total time take to Update weight for user ${weight.user}")
      collection.updateOne(equal(KEY_USER, weight.user), set(KEY_WEIGHT, weight.weight), new UpdateOptions().upsert(true)).results()
      response.created.location(s"/mongo/weights/${weight.user}")
    }
  }

  put("/mongo/weights/replace") { weight: Weight =>
    futurePool {
      info(s"Total time take to Update weight for user ${weight.user}")
      collection.findOneAndReplace(equal(KEY_USER, weight.user), weight.convertToDoc()).results()
      response.created.location(s"/mongo/weights/${weight.user}")
    }
  }

  delete("/mongo/weights/delete") { weight: Weight =>
    futurePool {
      val r = time(s"Total time take to DELETE weight for user '${weight.user}' is %d ms") {
        collection.findOneAndDelete(equal(KEY_USER, weight.user)).results()
        response.created.location(s"/mongo/weights/${weight.user}")
      }
      r
    }
  }

}



