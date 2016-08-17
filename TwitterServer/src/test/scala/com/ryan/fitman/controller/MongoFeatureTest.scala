package com.ryan.fitman.controller

import com.ryan.fitman.FitmanServer
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

class MongoFeatureTest extends FeatureTest {
  override val server = new EmbeddedHttpServer(
    twitterServer = new FitmanServer
  )

  "MongoDB" should {

    "List weight for a user when GET request is made" in {
      val response = server.httpPost(
        path = "/mongo/weights",
        postBody =
          s"""
            |{
            |"user":"test_user_1",
            |"weight":80,
            |"status":"Feeling great!!!",
            |"posted_at" : "2016-01-03T14:34:06.871Z"
            |}
          """.stripMargin,
        andExpect = Status.Created
      )

      server.httpGetJson[Weight](
        path = response.location.get, //Location -> /weights/test_user_1
        andExpect = Status.Ok,
        withJsonBody =
          s"""
            |{
            |"user" : "test_user_1",
            |"weight" : 80,
            |"status":"Feeling great!!!"
            |}
          """.stripMargin
      )
    }

    "Insert user weight when POST request is made " in {
      server.httpPost(
        path = "/mongo/weights",
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

    "Bad request when user is not present in request" in {
      server.httpPost(
        path = "/mongo/weights",
        postBody =
          """
            |{
            |"weight":85
            |}
          """.stripMargin,
        andExpect = Status.BadRequest
      )
    }

    "Bad request when data not in range" in {
      server.httpPost(
        path = "/mongo/weights",
        postBody =
          """
            |{
            |"user":"testing12345678910908980898978798797979789",
            |"weight":250
            |}
          """.stripMargin,
        andExpect = Status.BadRequest,
        withErrors = Seq(
          "user: size [42] is not between 1 and 25",
          "weight: [250] is not between 25 and 200"
        )
      )
    }

    "Update user weight when PUT request is made" in {
      val response = server.httpPut(
        path = "/mongo/weights/update",
        putBody =
          """
            |{
            |"user":"shekhar",
            |"weight":80,
            |"status":"Feeling great!!!",
            |"posted_at" : "2016-01-03T14:34:06.871Z"
            |}
          """.stripMargin,
        andExpect = Status.Ok,
        withLocation = "/weights/shekhar"
      )

      server.httpGetJson[Weight](
        path = response.location.get,
        andExpect = Status.Ok,
        withJsonBody =
          """
            |{
            |"user":"shekhar",
            |"weight":80,
            |"status":"Feeling great!!!"
            | }
          """.stripMargin
      )
    }

    "Replace user weight when PUT request is made" in {
      val response = server.httpPut(
        path = "/mongo/weights/replace",
        putBody =
          """
            |{
            |"user":"shekhar",
            |"weight":99,
            |"status":"Hungry",
            |"posted_at" : "2016-06-20T14:34:06.871Z"
            |}
          """.stripMargin,
        andExpect = Status.Ok,
        withLocation = "/weights/shekhar"
      )

      server.httpGetJson[Weight](
        path = response.location.get,
        andExpect = Status.Ok,
        withJsonBody =
          """
            |{
            |"user":"shekhar",
            |"weight":99,
            |"status":"Hungry"
            |}
          """.stripMargin
      )
    }

    "Delete user weight when DELETE request is made" in {

      val response = server.httpDelete(
        path = "/mongo/weights/delete",
        headers = Map("Content-Type" -> "application/json"),
        deleteBody =
          """
            |{
            |"user":"shekhar",
            |"weight":85
            |}
          """.stripMargin,
        andExpect = Status.Ok
      )
    }
  }
}
