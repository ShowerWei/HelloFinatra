package com.ryan.fitman.api

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.validation.{Range, Size}
import com.twitter.inject.Logging
import org.joda.time.Instant

import scala.collection.mutable


// Create = PUT with a new URI
//          POST to a base URI returning a newly created URI
// Read   = GET
// Update = PUT with an existing URI
// Delete = DELETE

class WeightResource extends Controller with Logging {

  val db = mutable.Map[String, List[Weight]]()

  get("/weights") { request: Request =>
    info("finding all weights for all users...")
    db
  }

  get("/weights/:user") { request: Request =>
    info(s"finding weight for user ${request.params("user")}")

    db.getOrElse(request.params("user"), List())
  }

  post("/weights") { weight: Weight =>
    val r = time(s"Total time take to POST weight for user '${weight.user}' is %d ms") {
      val weightsForUser = db.get(weight.user) match {
        case Some(weights) => weights :+ weight
        case None => List(weight)
      }
      db += (weight.user -> weightsForUser)
      response.created.location(s"/weights/${weight.user}")
    }
    r
  }

  put("/weights/update") { weight: Weight =>
    info(s"Update weight ${weight.user}")

    val r = time(s"Total time take to PUT weight for user '${weight.user}' is %d ms") {
      val weightsForUser = db.get(weight.user) match {
        case Some(_) => Some(List(weight))
        case _ => None
      }
      db += (weight.user -> weightsForUser.getOrElse(List()))
      db.foreach((tuple: (String, List[Weight])) => println(tuple._1 + "=" + tuple._2))

      response.created.location(s"/weights/${weight.user}")
    }
    r
  }

  delete("/weights/delete") { weight: Weight =>
    info(s"delete weight ${weight.user}")

    val r = time(s"Total time take to DELETE weight for user '${weight.user}' is %d ms") {
      db.get(weight.user) match {
        case Some(_) => db -= weight.user
        case _ => info(s"User ${weight.user} doesn't exist")
      }
      response.created.location(s"/weights/${weight.user}")
    }
    r
  }

}

case class Weight(
                   @Size(min = 1, max = 25) user: String,
                   @Range(min = 25, max = 200) weight: Int,
                   status: Option[String],
                   postedAt: Instant = Instant.now()
                 )
