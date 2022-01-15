package org.bitlap.zim.api.endpoint

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.bitlap.zim.domain.model.FriendGroup
import org.bitlap.zim.domain.ChatHistory
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._
import org.bitlap.zim.domain
import org.bitlap.zim.domain.{ AddInfo, FriendAndGroupInfo, FriendList, UploadResult, UserVo, ZimError }
import org.bitlap.zim.domain.model.{ GroupList, User }

/**
 * 用户接口的端点
 *
 * @author 梦境迷离
 * @since 2021/12/25
 * @version 1.0
 */
trait UserEndpoint extends ApiErrorMapping {

  // API 最前缀path
  private[api] lazy val userResource: EndpointInput[Unit] = "user"
  // API  资源描述
  private[api] lazy val userDescriptionGetResource: String = "User Endpoint"

  //================================================用户API定义（这是用于测试的接口）===============================================================
  private[api] lazy val userGetOneEndpoint: Endpoint[Long, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "getOne" / query[Long]("id").example(10086L).description("query parameter"))
      .name("查询一个用户")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  //================================================用户API定义（正式接口）===============================================================
  private[api] lazy val leaveOutGroupEndpoint
    : Endpoint[(Int, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "leaveOutGroup" / query[Int]("groupId") / query[Int]("uid"))
      .name("退出群")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val removeFriendEndpoint: Endpoint[Int, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "removeFriend" / query[Int]("removeFriend"))
      .name("删除好友")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val changeGroupEndpoint
    : Endpoint[(Int, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "changeGroup" / query[Int]("groupId") / query[Int]("userId"))
      .name("移动好友分组")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val refuseFriendEndpoint
    : Endpoint[(Int, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "refuseFriend" / query[Int]("messageBoxId") / query[Int]("to"))
      .name("拒绝添加好友")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val agreeFriendEndpoint
    : Endpoint[(Int, Int, Int, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(
        userResource / "agreeFriend" / query[Int]("uid") / query[Int]("from_group") / query[Int]("group") / query[Int](
          "messageBoxId"
        )
      )
      .name("同意添加好友")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val findAddInfoEndpoint
    : Endpoint[(Int, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "findAddInfo" / query[Int]("uid") / query[Int]("page"))
      .name("查询消息盒子信息")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derivedSchema[AddInfo].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val findUsersEndpoint
    : Endpoint[(Int, Option[Boolean], Option[Int]), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(
        userResource / "findUsers" / query[Int]("page")
          .default(1) / query[Option[Boolean]]("name") / query[Option[Int]]("sex")
      )
      .name("分页查找好友")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val findGroupsEndpoint
    : Endpoint[(Int, Option[Boolean]), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "findGroups" / query[Int]("page").default(1) / query[Option[Boolean]]("name"))
      .name("分页查找群组")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[GroupList].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val findMyGroupsEndpoint
    : Endpoint[(Int, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "findMyGroups" / query[Int]("page").default(1) / query[Int]("createId"))
      .name("分页查询我的创建的群组")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[GroupList].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val chatLogEndpoint
    : Endpoint[(Int, String, Int), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "chatLog" / query[Int]("id") / query[String]("type") / query[Int]("page").default(1))
      .name("获取聊天记录")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 返回页面且携带数据
  private[api] lazy val chatLogIndexEndpoint
    : Endpoint[(Int, String), ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "chatLogIndex" / query[Int]("id") / query[String]("type"))
      .name("弹出聊天记录页面")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val getOffLineMessageEndpoint
    : Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "getOffLineMessage")
      .name("获取离线消息")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[domain.ChatHistory].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val updateSignEndpoint: Endpoint[String, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "updateSign" / query[String]("sign"))
      .name("更新签名")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 返回的是redirect
  private[api] lazy val activeUserEndpoint: Endpoint[String, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "active" / path[String]("activeCode"))
      .name("激活")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SString), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val registerEndpoint: Endpoint[User, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "register")
      .in(jsonBody[User])
      .name("注册")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val loginEndpoint: Endpoint[User, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "login")
      .in(jsonBody[User])
      .name("登录")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val initEndpoint: Endpoint[Int, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "init" / path[Int]("userId"))
      .name("初始化主界面数据")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derivedSchema[FriendAndGroupInfo].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val getMembersEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "getMembers")
      .name("获取群成员")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[FriendList].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 上传文件 file
  private[api] lazy val uploadImageEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "upload" / "image")
      .name("客户端上传图片")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 上传文件 file
  private[api] lazy val uploadFileEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "upload" / "file")
      .name("客户端上传文件")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 上传文件 file
  private[api] lazy val uploadGroupAvatarEndpoint
    : Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "upload" / "groupAvatar")
      .name("上传群组头像")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 上传文件 avatar
  private[api] lazy val updateAvatarEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "updateAvatar")
      .name("用户更新头像")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[UploadResult].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val createGroupEndpoint
    : Endpoint[GroupList, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "createGroup")
      .in(jsonBody[GroupList])
      .name("用户创建群组")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val createUserGroupEndpoint
    : Endpoint[domain.model.FriendGroup, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "createUserGroup")
      .in(jsonBody[domain.model.FriendGroup])
      .name("用户创建好友分组")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SInteger), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val updateInfoEndpoint: Endpoint[UserVo, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.post
      .in(userResource / "updateInfo")
      .in(jsonBody[UserVo])
      .name("更新信息个人信息")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // TODO 跳转主页
  private[api] lazy val indexEndpoint: Endpoint[Unit, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "index")
      .name("跳转主页")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SString), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val findUserEndpoint: Endpoint[Int, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "findUser" / query[Int]("id"))
      .name("根据id查找用户信息")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[User].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val existEmailEndpoint: Endpoint[String, ZimError, Source[ByteString, Any], Any with AkkaStreams] =
    endpoint.get
      .in(userResource / "existEmail" / query[String]("email"))
      .name("判断邮件是否存在")
      .description(userDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(SchemaType.SBoolean), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  // 由于schema是严格的，domain 的case class可能必须改成Option

}

object UserEndpoint extends UserEndpoint
