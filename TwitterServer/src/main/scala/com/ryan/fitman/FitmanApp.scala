package com.ryan.fitman

import com.ryan.fitman.api.WeightResource
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.filters.CommonFilters

object FitmanApp extends FitmanServer

class FitmanServer extends HttpServer {
  override protected def defaultFinatraHttpPort: String = ":8087"

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add[HelloController]
      .add[WeightResource]
  }
}

class HelloController extends Controller {

  get("/hello") { request: Request =>
    "Fitman says hello"
  }

}

