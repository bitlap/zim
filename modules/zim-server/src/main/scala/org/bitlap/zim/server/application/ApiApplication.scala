package org.bitlap.zim.server.application
import org.bitlap.zim.domain.input.UserInput
import zio.stream
import org.bitlap.zim.domain.model.User
import org.bitlap.zim.domain.input.UserSecurity

/**
 *  直接提供给endpoint使用
 *  对userService做一定的包装
 *
 * @author 梦境迷离
 * @since 2022/1/8
 * @version 1.0
 */
trait ApiApplication extends BaseApplication[User] {

  def existEmail(email: String): stream.Stream[Throwable, Boolean]

  def findUserById(id: Int): stream.Stream[Throwable, User]

  def updateInfo(user: UserInput): stream.Stream[Throwable, Boolean]

  def login(user: UserSecurity.UserSecurityInfo): stream.Stream[Throwable, User]

}
