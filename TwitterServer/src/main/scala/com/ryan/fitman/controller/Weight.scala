package com.ryan.fitman.controller

import com.twitter.finatra.validation._
import org.joda.time.Instant
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.twitter.finatra.request.QueryParam

@JsonInclude(Include.NON_NULL)
case class Weight(
                   @Size(min = 1, max = 25) user: String,
                   @Range(min = 25, max = 100) weight: Int,
                   @Range(min = 25, max = 100) age: Int,
                   @Max(100) @QueryParam max: Option[Int],
                   status: Option[String],
                   postedAt: Instant = Instant.now()
                 )
