package org.bitlap.zim.domain.model

import scalikejdbc._

trait BaseModel[T] extends SQLSyntaxSupport[T] {

  def extract(rs: WrappedResultSet)(implicit sp: SyntaxProvider[T]): T

}
