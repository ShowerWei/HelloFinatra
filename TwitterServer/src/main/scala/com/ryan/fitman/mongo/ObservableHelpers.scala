package com.ryan.fitman.mongo

/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.concurrent.TimeUnit

import com.twitter.util.{CountDownLatch, Duration}

import org.mongodb.scala.{Document, Observable}

import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global


object ObservableHelpers {

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val converter: (Document) => String = (doc) => doc.toJson
  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val converter: (C) => String = (doc) => doc.toString
  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val converter: (C) => String

    def future(): String = {
      val str = StringBuilder.newBuilder
      val latch = new CountDownLatch(1)

      observable.toFuture().onComplete({
        case Success(res) =>
          res.foreach(doc => str.append(converter(doc)))
          println("future success")
          latch.countDown()
        case Failure(throwable) =>
          println("future fail")
      })
      latch.await(Duration(10, TimeUnit.SECONDS))
      str.toString()
    }
  }

}
