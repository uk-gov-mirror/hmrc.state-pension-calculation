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

package support.data

import models.{CalculationNote, CalculationResponse, CalculationResult, QualifyingYear}

object CalculationTestData {

  object Result {
    val json: String =
      """
        |{
        |  "nino": "AA123456A",
        |  "protectedPaymentAmount": 123.45,
        |  "rebateDerivedAmount": 123.45,
        |  "newStatePensionEntitlementAmount": 123.45,
        |  "reducedRateElectionToConsider": false,
        |  "pensionShareOrderContractedOutEmploymentGroup": true,
        |  "pensionShareOrderStateEarningsRelatedPensionService": true,
        |  "isleOfManContributions": false,
        |  "contractedOutEmploymentGroupInvestigationPosition": true,
        |  "statePensionOldRulesAmount": 123.45,
        |  "basicAmount": 123.45,
        |  "additionalPensionPre1997GrossAmount": 123.45,
        |  "additionalPensionPre1997NetAmount": 123.45,
        |  "additionalPensionPost1997Amount": 123.45,
        |  "additionalPensionPost2002Amount": 123.45,
        |  "graduatedRetirementBenefitAmount": 123.45,
        |  "statePensionNewRulesAmount": 123.45,
        |  "statePensionAgeDate": "2018-11-12",
        |  "post2016YearsUsed": 2,
        |  "newStatePensionQualifyingYears": 1,
        |  "incompleteContributionRecordIndicator": true
        |}
        |""".stripMargin

    val expectedModel = CalculationResult(
      nino = "AA123456A",
      protectedPaymentAmount = 123.45,
      rebateDerivedAmount = 123.45,
      newStatePensionEntitlementAmount = 123.45,
      reducedRateElectionToConsider = false,
      pensionShareOrderContractedOutEmploymentGroup = true,
      pensionShareOrderStateEarningsRelatedPensionService = true,
      isleOfManContributions = false,
      contractedOutEmploymentGroupInvestigationPosition = true,
      statePensionOldRulesAmount = 123.45,
      basicAmount = 123.45,
      additionalPensionPre1997GrossAmount = 123.45,
      additionalPensionPre1997NetAmount = 123.45,
      additionalPensionPost1997Amount = 123.45,
      additionalPensionPost2002Amount = 123.45,
      graduatedRetirementBenefitAmount = 123.45,
      statePensionNewRulesAmount = 123.45,
      statePensionAgeDate = "2018-11-12",
      post2016YearsUsed = 2,
      newStatePensionQualifyingYears = 1,
      incompleteContributionRecordIndicator = true)
  }

  object Notes {
    val json: String =
      """
        |{
        |  "noteIdentifier": 1,
        |  "numberOfNoteFields": 4,
        |  "fieldsList": [
        |    {
        |      "noteField": "note #1"
        |    },
        |    {
        |      "noteField": "note #2"
        |    },
        |    {
        |      "noteField": "note #3"
        |    },
        |    {
        |      "noteField": "note #4"
        |    }
        |  ]
        |}
      """.stripMargin

    val expectedModel = CalculationNote(1,
      Seq(
        "note #1",
        "note #2",
        "note #3",
        "note #4"
      )
    )

    val emptyJson: String =
      """
        |{
        |  "noteIdentifier": 1,
        |  "numberOfNoteFields": 0,
        |  "fieldsList": []
        |}
      """.stripMargin

    val expectedEmptyModel = CalculationNote(1, Seq())

  }

  object QualifyingYears {
    val taxYear = 2017

    val json: String =
      s"""
         |{
         |  "taxYear": $taxYear,
         |  "qualifyingTaxYear": true,
         |  "earningsAmount": 12345.67
         |}
      """.stripMargin

    val expectedModel = QualifyingYear(taxYear, qualifyingTaxYear = true, BigDecimal("12345.67"))

  }

  object Response {
    val initialCalcJson: String =
      s"""
         |{
         |  "initialRequestResult": ${Result.json},
         |  "associatedNotes": [${Notes.json}],
         |  "listOfQualifyingYears": [${QualifyingYears.json}]
         |}
      """.stripMargin

    val finalCalcJson: String =
      s"""
         |{
         |  "finalRequestResult": ${Result.json},
         |  "associatedNotes": [${Notes.json}],
         |  "listOfQualifyingYears": [${QualifyingYears.json}]
         |}
      """.stripMargin

    val expectedModel = CalculationResponse(Result.expectedModel,
      Seq(Notes.expectedModel),
      Seq(QualifyingYears.expectedModel)
    )

    val generatedJson: String =
      """
        |{
        |  "nino": "AA123456A",
        |  "statePensionEligibilityDate": "2018-11-12",
        |  "amounts": {
        |    "protectedPayment": 123.45,
        |    "rebateDerived": 123.45,
        |    "newStatePensionEntitlement": 123.45,
        |    "basicPension": 123.45,
        |    "oldRulesStatePension": 123.45,
        |    "newRulesStatePension": 123.45,
        |    "graduatedRetirementBenefit": 123.45,
        |    "additionalPension": {
        |      "grossPre1997": 123.45,
        |      "netPre1997": 123.45,
        |      "post1997": 123.45,
        |      "post2002": 123.45
        |    }
        |  },
        |  "reducedRateElection": false,
        |  "pensionShareOrder": {
        |    "contractedOutEmploymentGroup": true,
        |    "stateEarningsRelatedPensionService": true
        |  },
        |  "contributions": {
        |    "isleOfMan": false,
        |    "post2016YearsUsed": 2,
        |    "newStatePensionQualifyingYears": 1,
        |    "incomplete": true,
        |    "qualifyingYears": [
        |      {
        |        "taxYear": 2017,
        |        "qualifyingTaxYear": true,
        |        "earningsAmount": 12345.67
        |      }
        |    ]
        |  },
        |  "underInvestigation": true,
        |  "associatedNotes": [
        |    {
        |      "id": 1,
        |      "notes": [
        |        "note #1",
        |        "note #2",
        |        "note #3",
        |        "note #4"
        |      ]
        |    }
        |  ]
        |}
      """.stripMargin
  }

}