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

package org.bitlap.zim.auth

import java.util._

import scala.concurrent._
import scala.util.Try

import org.bitlap.zim.domain.ZimError._
import org.bitlap.zim.domain.input.UserToken.UserSecurityInfo
import org.bitlap.zim.domain.input._
import zio._

/** Cookie based authentication
 *
 *  @author
 *    梦境迷离
 *  @version 1.0,2022/1/25
 */
trait CookieAuthority {

  type AuthorityFunction = (String, String) => IO[Throwable, (Boolean, Option[UserSecurityInfo])]

  /** 鉴权，使用Redis缓存用户信息，没有再查库
   *  @param token
   *  @return
   */
  def authenticate(
    token: UserToken
  )(
    authorityFunction: AuthorityFunction
  )(implicit ec: ExecutionContext): Future[Either[Unauthorized, UserSecurityInfo]] =
    Future {
      val tk             = if (token.cookie.trim.contains(" ")) token.cookie.trim.split(" ")(1) else token.cookie
      val secret: String = Try(new String(Base64.getDecoder.decode(tk))).getOrElse(null)
      if (secret == null || secret.isEmpty) {
        Left(Unauthorized())
      } else if (secret.contains(":")) {
        val usernamePassword = secret.split(":")
        val email            = usernamePassword(0)
        val passwd           = usernamePassword(1)
        val ret              = authorityFunction(email, passwd)
        val (check, user) = Unsafe.unsafe { implicit unsafe =>
          zio.Runtime.default.unsafe.run(ret).getOrThrowFiberFailure()
        }
        if (!check) {
          Left(Unauthorized())
        } else {
          if (user.orNull == null) Left(Unauthorized()) else Right(user.orNull)
        }
      } else {
        Left(Unauthorized())
      }
    }

}
