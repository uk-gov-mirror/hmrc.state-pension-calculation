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

package endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.errors.ApiServiceError
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, DesStub}
import support.IntegrationSpec
import support.data.CalculationTestData.{Response => testData}

class CalculationISpec extends IntegrationSpec {

  private trait Test {

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"/calculation")
    }
  }

  "Calling the /calculation endpoint" when {

    "the request is valid initial calc" should {

      trait InitialCalcTest extends Test {
        lazy val desResponse: JsValue = Json.parse(testData.initialCalcJson)
        lazy val requestBody: JsValue = Json.obj(
          "nino" -> "AA123456A",
          "checkBrick" -> "SMIJ",
          "gender" -> "M",
          "finalise" -> false
        )

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          DesStub.initialCalc(Status.CREATED, desResponse)
        }

        lazy val response: WSResponse = await(request().post(requestBody))
      }

      "return a 201 status code" in new InitialCalcTest {
        response.status shouldBe Status.CREATED
      }

      "return the correct JSON" in new InitialCalcTest {
        response.body shouldBe testData.generatedJson
      }

      "have the correct Content-Type header and value" in new InitialCalcTest {
        response.header(HeaderNames.CONTENT_TYPE) shouldBe Some("application/json")
      }

    }

    "the request is valid final calc" should {

      trait FinalCalcTest extends Test {
        lazy val desResponse: JsValue = Json.parse(testData.finalCalcJson)
        lazy val requestBody: JsValue = Json.obj(
          "nino" -> "AA123456A",
          "checkBrick" -> "SMIJ",
          "gender" -> "M",
          "finalise" -> false
        )

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          DesStub.finalCalc(Status.CREATED, desResponse)
        }

        lazy val response: WSResponse = await(request().post(requestBody))
      }

      "return a 200 status code" in new FinalCalcTest {
        response.status shouldBe Status.CREATED
      }

      "return the correct JSON" in new FinalCalcTest {
        response.body shouldBe testData.generatedJson
      }

      "have the correct Content-Type header and value" in new FinalCalcTest {
        response.header(HeaderNames.CONTENT_TYPE) shouldBe Some("application/json")
      }

    }

    def testDesErrorHandling(desErrorCode: String,
                             desStatusCode: Int,
                             desResponseBody: JsValue,
                             apiStatusCode: Int,
                             apiResponseBody: JsValue): Unit = {

      s"DES responds with $desErrorCode" should {

        trait CalcTest extends Test {
          lazy val desResponse: JsValue = desResponseBody
          lazy val requestBody: JsValue = Json.obj(
            "nino" -> "AA123456A",
            "checkBrick" -> "SMIJ",
            "gender" -> "M",
            "finalise" -> true
          )

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            DesStub.finalCalc(desStatusCode, desResponse)
          }

          lazy val response: WSResponse = await(request().post(requestBody))
        }

        s"return a $apiStatusCode status code" in new CalcTest {
          response.status shouldBe apiStatusCode
        }

        "return the correct JSON" in new CalcTest {
          response.body[JsValue] shouldBe apiResponseBody
        }

        "have the correct Content-Type header and value" in new CalcTest {
          response.header(HeaderNames.CONTENT_TYPE) shouldBe Some("application/json")
        }

      }
    }

    {
      val invalidPayloadErrorCode = "INVALID_PAYLOAD"
      val invalidPayloadBody = Json.obj(
        "code" -> invalidPayloadErrorCode,
        "reason" -> "Submission has not passed validation. Invalid Payload."
      )

      testDesErrorHandling(invalidPayloadErrorCode,
        Status.BAD_REQUEST,
        invalidPayloadBody,
        Status.INTERNAL_SERVER_ERROR,
        Json.toJson(ApiServiceError))
    }

    {
      val invalidNinoErrorCode = "INVALID_NINO"
      val invalidNinoBody = Json.obj(
        "code" -> invalidNinoErrorCode,
        "reason" -> "Submission has not passed validation. Invalid parameter nino."
      )

      testDesErrorHandling(invalidNinoErrorCode,
        Status.BAD_REQUEST,
        invalidNinoBody,
        Status.INTERNAL_SERVER_ERROR,
        Json.toJson(ApiServiceError))
    }

    {
      val invalidCorrelationIdErrorCode = "INVALID_CORRELATIONID"
      val invalidCorrelationIdBody = Json.obj(
        "code" -> invalidCorrelationIdErrorCode,
        "reason" -> "Submission has not passed validation. Invalid header CorrelationId."
      )

      testDesErrorHandling(invalidCorrelationIdErrorCode,
        Status.BAD_REQUEST,
        invalidCorrelationIdBody,
        Status.INTERNAL_SERVER_ERROR,
        Json.toJson(ApiServiceError))
    }

    {
      val multipleErrorCodes = "INVALID_PAYLOAD and INVALID_NINO"
      val invalidBody = Json.obj(
        "failures" -> Json.arr(
          Json.obj(
            "code" -> "INVALID_PAYLOAD",
            "reason" -> "Submission has not passed validation. Invalid Payload."
          ),
          Json.obj(
            "code" -> "INVALID_NINO",
            "reason" -> "Submission has not passed validation. Invalid parameter nino."
          )
        )
      )
      testDesErrorHandling(multipleErrorCodes,
        Status.BAD_REQUEST,
        invalidBody,
        Status.INTERNAL_SERVER_ERROR,
        Json.toJson(ApiServiceError))
    }
  }

}
