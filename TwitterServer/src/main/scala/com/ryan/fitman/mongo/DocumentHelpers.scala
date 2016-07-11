package com.ryan.fitman.mongo

import java.math.BigInteger
import java.security.SecureRandom

import com.ryan.fitman.controller.Weight
import org.joda.time.Instant
import org.mongodb.scala.Document

import scala.util.Random

object DocumentHelpers {

  val KEY_USER = "user"
  val KEY_WEIGHT = "weight"
  val KEY_AGE = "age"
  val KEY_STATUS = "status"
  val KEY_POSTED_AT = "posted_at"
  val randomUser = new BigInteger(130, new SecureRandom())
    .toString(32).substring(0, 24)

  implicit class Converter(weight: Weight) {
    def convertToDoc(): Document = {
      Document(
        KEY_USER -> weight.user,
        KEY_WEIGHT -> weight.weight,
        KEY_AGE -> weight.age,
        KEY_STATUS -> weight.status,
        KEY_POSTED_AT -> weight.postedAt.toDate)
    }
  }

  implicit class Jsonize(document: Seq[Document]) {
    def jsonizeDocs(): String = {
      val sb = new StringBuilder
      for (doc <- document) {
        if (sb.nonEmpty) {
          sb.append(",")
        }
        sb.append(doc.toJson)
      }
      sb.toString
    }
  }

  def randomDoc(): Document = {
    val now = Instant.now().toDate
    Document(
      KEY_USER -> new BigInteger(130, new SecureRandom()).toString(25).substring(0, 24),
      KEY_WEIGHT -> randomWeight(),
      KEY_AGE -> randomAge(),
      KEY_STATUS -> "Good",
      KEY_POSTED_AT -> now)
  }

  def randomWeight(): Int = Random.nextInt(100) + 25

  def randomAge(): Int = Random.nextInt(100) + 25
}
