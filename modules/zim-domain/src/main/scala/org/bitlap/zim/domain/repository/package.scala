package org.bitlap.zim.domain

import eu.timepit.refined.api.{ Refined, Validate }
import scalikejdbc.SQLSyntax

package object repository {

  final case class Condition(key: String, value: Any)

  /**
   * define params value type
   */
  final case class ConditionValidator()

  object ConditionValidator {
    implicit val queryParamValueValidate: Validate.Plain[Condition, ConditionValidator] = Validate.fromPredicate(
      p =>
        p == null ||
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
