/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.server

import org.bitlap.zim.infrastructure.InfrastructureConfiguration
import org.bitlap.zim.infrastructure.properties.ZimConfigurationProperties
import org.bitlap.zim.server.module._
import org.bitlap.zim.server.service.ApiServiceImpl
import zio._

/** main方法
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2021/12/24
 */
object ZimServer extends zio.ZIOAppDefault {
  override def run: ZIO[Any, Throwable, Unit] = (for {
    _ <- Console.printLine("""
                             |                                 ____
                             |                ,--,           ,'  , `.
                             |        ,----,,--.'|        ,-+-,.' _ |
                             |      .'   .`||  |,      ,-+-. ;   , ||
                             |   .'   .'  .'`--'_     ,--.'|'   |  ||
                             | ,---, '   ./ ,' ,'|   |   |  ,', |  |,
                             | ;   | .'  /  '  | |   |   | /  | |--'
                             | `---' /  ;--,|  | :   |   : |  | ,
                             |   /  /  / .`|'  : |__ |   : |  |/
                             | ./__;     .' |  | '.'||   | |`-'
                             | ;   |  .'    ;  :    ;|   ;/
                             | `---'        |  ,   / '---'""".stripMargin)
    _ <- ZIO.environmentWithZIO[AkkaHttpModule](_.get.httpServer())
    _ <- ZIO.never
  } yield ()).provide(
    AkkaModule.live,
    AkkaHttpModule.live,
    InfrastructureConfiguration.live,
    ZimConfigurationProperties.live,
    ApiServiceImpl.live,
    Scope.default
  )
}
