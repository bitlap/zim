package org.bitlap.zim.domain

/**
 * 系统常量
 *
 * @since 2021年12月25日
 * @author 梦境迷离
 */
object SystemConstant {

  final val SUCCESS = 0

  final val ERROR = 1

  final val LOGGING_SUCCESS = "登陆成功"
  final val NOT_LOGIN = "未登陆"

  final val UPDATE_INFO_SUCCESS = "个人信息修改成功"

  final val CREATE_GROUP_ERROR = "群组创建失败"

  final val CREATE_USER_GROUP_ERROR = "分组创建失败"

  final val CREATE_GROUP_SUCCCESS = "群组创建成功"

  final val CREATE_USER_GROUP_SUCCCESS = "分组创建成功"

  final val UPDATE_INFO_FAIL = "个人信息修改失败"

  final val UPDATE_INFO_PASSWORD_FAIL = "旧密码错误"

  final val NONACTIVED = "用户未激活"

  final val LOGIN_ERROR = "用户名或密码错误"

  final val REGISTER_SUCCESS = "注册成功"

  final val LOGIN_FAIL = "登陆失败"

  final val SUCCESS_MESSAGE = "操作成功"

  final val ERROR_MESSAGE = "操作失败"

  final val UPLOAD_SUCCESS = "上传成功"

  final val UPLOAD_FAIL = "上传失败"

  final val IMAGE_PATH = "/upload/image/"

  final val FILE_PATH = "/upload/file/"

  final val AVATAR_PATH = "/static/image/avatar/"

  final val GROUP_AVATAR_PATH = "/static/image/group/"

  final val DEFAULT_GROUP_NAME = "我的好友"

  final val ERROR_ADD_REPETITION = "已是好友，请勿重复添加"

  //电子邮件相关
  final val SUBJECT = "zim 即时通讯系统邮箱激活邮件"

  //Redis Key相关
  final val ONLINE_USER = "ONLINE_USER"

  final val SYSTEM_PAGE = 6

  final val USER_PAGE = 21

  final val ADD_MESSAGE_PAGE = 4

  final val GROUP_TYPE = "group"

  final val FRIEND_TYPE = "friend"

  object status {
    val ONLINE = "online"
    val HIDE = "hide"
    val ONLINE_DESC = "在线"
    val HIDE_DESC = "离线"
  }

}
