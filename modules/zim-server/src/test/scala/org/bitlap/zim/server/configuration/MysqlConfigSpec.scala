/*
 * Copyright 2022 bitlap
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

package org.bitlap.zim.server.configuration

import org.bitlap.zim.server.ZIOBaseSuit
import scalikejdbc._
import zio.test.Assertion._
import zio.test._
import zio.Scope

object MysqlConfigSpec extends ZIOBaseSuit {

  def spec: Spec[Environment with TestEnvironment with Scope, Any] = suite("MysqlConfigSpec")(
    test("test the database connect working state") {
      assert(isConnected)(equalTo(true))
    }
  )

  val isConnected: Boolean = NamedDB(Symbol(h2ConfigurationProperties.databaseName)).conn.isValid(0)

}
