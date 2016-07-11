package com.ryan.fitman.domain.http.response


import java.util.UUID

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

@JsonInclude(Include.NON_NULL)
case class ClientResponse(
                           id: UUID,
                           name: String,
                           credential: Option[String] = None,
                           grantOptions: List[GrantOptionResponse]
                         )