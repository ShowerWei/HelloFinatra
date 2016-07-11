package com.ryan.fitman.domain.http.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.twitter.finatra.response.JsonCamelCase

@JsonInclude(Include.NON_NULL)
case class GrantOptionResponse(
                                var allowScope: Option[List[String]] = None,
                                var allowUnverifiedUser: Option[Boolean] = None,
                                var clientApps: Option[List[String]] = None,
                                var defaultScope: Option[List[String]] = None,
                                var enableRefreshToken: Option[Boolean] = None,
                                var grantType: Option[String] = None,
                                var redirectionURL: Option[List[String]] = None,
                                var suspended: Option[Boolean] = None,
                                var tokenExpiresIn: Option[Int] = None
                              )