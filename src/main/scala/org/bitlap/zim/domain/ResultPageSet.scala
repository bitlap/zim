package org.bitlap.zim.domain

case class ResultPageSet(override val data: Any, pages: Int) extends ResultSet(data)