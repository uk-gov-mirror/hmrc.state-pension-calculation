/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-27" % "3.4.0",
    "uk.gov.hmrc" %% "play-hmrc-api" % "6.2.0-play-27"
  )

  val test: Seq[ModuleID] = Seq(
    "com.typesafe.play" %% "play-test" % current % "test",
    "org.scalamock" %% "scalamock" % "4.4.0" % "test",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test, it",
    "org.pegdown" % "pegdown" % "1.6.0" % "test, it",
    "com.github.tomakehurst" % "wiremock-standalone" % "2.27.2" % "test, it"
  )

  // Fixes a transitive dependency clash between wiremock and scalatestplus-play
  val overrides: Seq[ModuleID] = {
    val jettyFromWiremockVersion = "9.2.24.v20180105"
    Seq(
      "org.eclipse.jetty" % "jetty-client" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-continuation" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-http" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-io" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-security" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-server" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-servlet" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-servlets" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-util" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-webapp" % jettyFromWiremockVersion,
      "org.eclipse.jetty" % "jetty-xml" % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-api" % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-client" % jettyFromWiremockVersion,
      "org.eclipse.jetty.websocket" % "websocket-common" % jettyFromWiremockVersion
    )
  }
}
