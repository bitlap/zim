package org.bitlap.zim.server.util

import zio.crypto.hash.{ Hash, HashAlgorithm, MessageDigest }
import zio.{ Has, RIO }

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
  def encrypt(rawPassword: String): RIO[Has[Hash], MessageDigest[String]] =
    Hash.hash[HashAlgorithm.SHA256](rawPassword, US_ASCII)

  /**
   * 验证密码和加密后密码是否一致
   *
   * @param rawPassword 明文密码
   * @param password    加密后的密码
   * @return Boolean
   */
  def matched(rawPassword: String, password: MessageDigest[String]): RIO[Has[Hash], Boolean] =
    if (rawPassword == null && password == null) RIO.succeed(true)
    else Hash.verify[HashAlgorithm.SHA256](rawPassword, password, charset = US_ASCII)

  // 继承extends zio.App测试
//  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
//    (for {
//      digest <- encrypt("LGB123")
//      ret <- matched("LGB123", digest)
//    } yield println(ret)).exitCode.provideCustomLayer(Hash.live)

}
