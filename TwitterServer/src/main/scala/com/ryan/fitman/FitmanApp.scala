package com.ryan.fitman

import com.github.xiaodongw.swagger.finatra.SwaggerController
import com.ryan.fitman.controller.WeightMongoDB
import com.ryan.fitman.mongo.MongoConfig
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.json.utils.CamelCasePropertyNamingStrategy
import com.typesafe.config.ConfigFactory
import io.swagger.models.{Info, Swagger}
import io.swagger.util.Json

object FitmanApp extends FitmanServer

class FitmanServer extends HttpServer {

  override val modules = Seq(MongoConfig)

  override protected def defaultFinatraHttpPort: String = ConfigFactory.load("application").getString("fitman.HttpPort")

  val info = new Info()
    .description("The User / Weight management API, this is a sample for swagger document generation")
    .version("1.0.1")
    .title("Fitman Management API")
  SampleSwagger.info(info)

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add(new SwaggerController(swagger = SampleSwagger))
      .add[WeightMongoDB]
  }
}

object SampleSwagger extends Swagger {
  Json.mapper().setPropertyNamingStrategy(CamelCasePropertyNamingStrategy)
}



