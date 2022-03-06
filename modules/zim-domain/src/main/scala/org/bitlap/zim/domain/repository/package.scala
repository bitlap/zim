/*
 * Copyright 2021 bitlap
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

package org.bitlap.zim.domain

import eu.timepit.refined.api.{ Refined, Validate }
import scalikejdbc.SQLSyntax

package object repository {

  final case class Condition(key: String, value: Any)

  object Condition {

    /**
     * define params value type
     */
    final case class ConditionValidator()

    object ConditionValidator {
      implicit val conditionValueValidate: Validate.Plain[Condition, ConditionValidator] = Validate.fromPredicate(
        p =>
          p != null ||
            p.value.isInstanceOf[Serializable] ||
            p.value.isInstanceOf[SQLSyntax] ||
            (p.value match {
              case Some(o) => o.isInstanceOf[Serializable] || o.isInstanceOf[SQLSyntax]
              case None    => true
              case _       => false
            }),
        p => s"($p should be a instance of the `org.bitlap.zim.domain.Condition`)",
        ConditionValidator()
      )
    }

    // 有点丑, 目前仅支持常量的编译器校验
    type ZCondition = Condition Refined ConditionValidator

  }
}
