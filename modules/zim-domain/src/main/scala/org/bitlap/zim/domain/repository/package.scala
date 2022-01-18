package org.bitlap.zim.domain

import eu.timepit.refined.api.{Refined, Validate}
import scalikejdbc.SQLSyntax

package object repository {

  /**
   * define params value type
   */
  final case class QueryParamValueValidator()

  object QueryParamValueValidator {
    implicit val queryParamValueValidate: Validate.Plain[Any, QueryParamValueValidator] = Validate.fromPredicate(
      p => p == null ||
        p.isInstanceOf[Serializable] ||
        p.isInstanceOf[SQLSyntax] ||
        (p match {
          case Some(o) => o.isInstanceOf[Serializable] || o.isInstanceOf[SQLSyntax]
          case None => true
          case _ => false
        }),
      p => s"($p is in QueryParamValueValidator)",
      QueryParamValueValidator()
    )
  }

  // 有点丑, 目前仅支持常量的编译器校验
  type QueryParamValue = Any Refined QueryParamValueValidator
}
