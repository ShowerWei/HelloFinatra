package com.ryan.fitman.mongo

import com.ryan.fitman.api.Weight
import org.mongodb.scala.Document

object DocumentHelpers {

  val KEY_USER = "user"
  val KEY_WEIGHT = "weight"
  val KEY_STATUS = "status"
  val KEY_POSTED_AT = "posted_at"

  implicit class Converter(weight: Weight) {
    def convertToDoc(): Document = {
      Document(
        KEY_USER -> weight.user,
        KEY_WEIGHT -> weight.weight,
        KEY_STATUS -> weight.status,
        KEY_POSTED_AT -> weight.postedAt.toDate)
    }
  }
}
