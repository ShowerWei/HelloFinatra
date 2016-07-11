package com.ryan.fitman

import com.ryan.fitman.controller.{WeightMongoDB, WeightResource}
import com.ryan.fitman.mongo.MongoConfig
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.filters.CommonFilters
import com.typesafe.config.ConfigFactory

object FitmanApp extends FitmanServer

class FitmanServer extends HttpServer {

  override val modules = Seq(MongoConfig)

  override protected def defaultFinatraHttpPort: String = ConfigFactory.load("application").getString("fitman.HttpPort")

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add[WeightResource]
      .add[WeightMongoDB]
  }
}



