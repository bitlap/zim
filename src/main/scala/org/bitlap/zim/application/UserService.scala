package org.bitlap.zim.application

import org.bitlap.zim.domain.model.{ User, UserDBO }
import org.bitlap.zim.repository.UserRepository
import zio.{ Has, stream }

/**
 * 用户服务
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class UserService(userRepository: UserRepository[UserDBO]) extends UserApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] = {
    userRepository.findById(id).map(user => User(user.id, user.username))
  }

  override def findAll(): stream.Stream[Throwable, User] = {
    userRepository.findAll().map(user => User(user.id, user.username))
  }
}

object UserService {

  type ZUserApplication = Has[UserApplication]

  def apply(userRepository: UserRepository[UserDBO]): UserApplication = new UserService(userRepository)
}