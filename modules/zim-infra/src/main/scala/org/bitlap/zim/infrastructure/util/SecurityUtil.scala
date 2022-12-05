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

package org.bitlap.zim.infrastructure.util

import zio._
import zio.crypto.hash.{ Hash, HashAlgorithm, MessageDigest }

import java.nio.charset.StandardCharsets.US_ASCII

/** SpringSecurity加密工具 PasswordEncoder
 *
 *  @since 2021年12月31日
 *  @author
 *    梦境迷离
 */
object SecurityUtil {

  /** 采用SHA-256算法
   *
   *  @param rawPassword
   *  @return
   *    80位加密后的密码
   */
  def encrypt(rawPassword: String): Task[MessageDigest[String]] =
    Hash.hash[HashAlgorithm.SHA256](rawPassword, US_ASCII).provideLayer(Hash.live)

  /** 验证密码和加密后密码是否一致
   *
   *  @param rawPassword
   *    明文密码
   *  @param password
   *    加密后的密码
   *  @return
   *    Boolean
   */
  def matched(rawPassword: String, password: String): Task[Boolean] =
    (if (rawPassword == null && password == null) ZIO.succeed(true)
     else if (rawPassword == null || password == null) ZIO.succeed(false)
     else if (rawPassword == "" || password == "") ZIO.succeed(false)
     else Hash.verify[HashAlgorithm.SHA256](rawPassword, MessageDigest[String](password), charset = US_ASCII))
      .provideLayer(Hash.live)

}
