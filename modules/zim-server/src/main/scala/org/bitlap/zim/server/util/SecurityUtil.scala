package org.bitlap.zim.server.util

import zio.crypto.hash.{ Hash, HashAlgorithm, MessageDigest }
import zio.{ RIO, Task }

import java.nio.charset.StandardCharsets.US_ASCII

/**
 * SpringSecurity加密工具 PasswordEncoder
 *
 * @since 2021年12月31日
 * @author 梦境迷离
 */
object SecurityUtil {

  /**
   * 采用SHA-256算法
   *
   * @param rawPassword
   * @return 80位加密后的密码
   */
  def encrypt(rawPassword: String): Task[MessageDigest[String]] =
    Hash.hash[HashAlgorithm.SHA256](rawPassword, US_ASCII).provideLayer(Hash.live)

  /**
   * 验证密码和加密后密码是否一致
   *
   * @param rawPassword 明文密码
   * @param password    加密后的密码
   * @return Boolean
   */
  def matched(rawPassword: String, password: String): Task[Boolean] =
    (if (rawPassword == null && password == null) RIO.succeed(true)
     else if (rawPassword == null || password == null) RIO.succeed(false)
     else Hash.verify[HashAlgorithm.SHA256](rawPassword, MessageDigest[String](password), charset = US_ASCII))
      .provideLayer(Hash.live)

}
