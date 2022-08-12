package org.bitlap.zim.server.configuration

import org.bitlap.zim.server.BaseSuit
import scalikejdbc._
import zio.test._
import zio.test.Assertion._

object MysqlConfigSpec extends BaseSuit {

  def spec = suite("MysqlConfigSpec")(
    test("test the database connect working state") {
      assert(isConnected)(equalTo(true))
    }
  )

  val isConnected: Boolean = NamedDB(Symbol(h2ConfigurationProperties.databaseName)).conn.isValid(0)

}
