package com.ryan.fitman.api

import com.ryan.fitman.FitmanServer
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest


class MongoFeatureTest extends FeatureTest {
  override val server = new EmbeddedHttpServer(
    twitterServer = new FitmanServer
  )

  "MongoDB" should {
    "Insert user weight when POST request is made " in {
      server.httpPost(
        path = "/weights",
        postBody =
          """
            |{
            |"user":"shekhar",
            |"weight":85,
            |"status":"Feeling great!!!"
            |}
          """.stripMargin,
        andExpect = Status.Created,
        withLocation = "/weights/shekhar"
      )
    }
  }
}
