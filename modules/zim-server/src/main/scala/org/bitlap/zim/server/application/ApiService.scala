package org.bitlap.zim.server.application

import org.bitlap.zim.domain.model.User
import zio.{ stream, Has }

/**
 * @author 梦境迷离
 * @since 2022/1/8
 * @version 1.0
 */
private final class ApiService(userApplication: UserApplication) extends ApiApplication {

  override def findById(id: Long): stream.Stream[Throwable, User] = userApplication.findById(id)
}

object ApiService {

  type ZApiApplication = Has[ApiApplication]

  def apply(userApplication: UserApplication): ApiApplication = new ApiService(userApplication)

}
