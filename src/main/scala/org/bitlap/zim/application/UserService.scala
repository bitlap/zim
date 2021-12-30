package org.bitlap.zim.application

import org.bitlap.zim.domain.model.{ GroupList, User }
import org.bitlap.zim.repository.{ GroupRepository, UserRepository }
import zio.{ stream, Has }

import org.bitlap.zim.domain.model.GroupList
import org.bitlap.zim.repository.GroupRepository

/**
 * 用户服务
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
private final class UserService(
  userRepository: UserRepository[User],
  groupRepository: GroupRepository[GroupList]
) extends UserApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] =
    userRepository.findById(id)

  override def findAll(): stream.Stream[Throwable, User] =
    userRepository.findAll()
}

object UserService {

  type ZUserApplication = Has[UserApplication]

  def apply(userRepository: UserRepository[User], groupRepository: GroupRepository[GroupList]): UserApplication =
    new UserService(userRepository, groupRepository)
}
