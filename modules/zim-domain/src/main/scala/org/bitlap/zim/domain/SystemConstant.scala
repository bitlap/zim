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

package org.bitlap.zim.domain

/**
 * 系统常量
 *
 * @since 2021年12月25日
 * @author 梦境迷离
 */
object SystemConstant {

  final val SUCCESS: Int = 0
  final val ERROR: Int = 1

  final val NOT_LOGIN: String = "未登陆"

  final val NON_ACTIVE: String = "用户未激活"

  final val REGISTER_FAIL: String = "注册失败"

  final val LOGIN_ERROR: String = "用户名或密码错误"

  final val SUCCESS_MESSAGE: String = "操作成功"

  final val ERROR_MESSAGE: String = "操作失败"

  final val PARAM_ERROR: String = "参数错误"

  final val IMAGE_PATH: String = "/static/upload/image/"

  final val FILE_PATH: String = "/static/upload/file/"

  final val AVATAR_PATH: String = "/static/image/avatar/"

  final val GROUP_AVATAR_PATH: String = "/static/image/group/"

  final val DEFAULT_GROUP_NAME: String = "我的好友"

  //电子邮件相关
  final val SUBJECT: String = "zim 即时通讯系统邮箱激活邮件"

  //Redis Key相关
  final val ONLINE_USER: String = "ONLINE_USER"

  final val SYSTEM_PAGE: Int = 6

  final val USER_PAGE: Int = 21

  final val ADD_MESSAGE_PAGE: Int = 4

  final val GROUP_TYPE: String = "group"

  final val FRIEND_TYPE: String = "friend"

  object status {
    val ONLINE: String = "online"
    val HIDE: String = "hide"
    val ONLINE_DESC: String = "在线"
    val HIDE_DESC: String = "离线"
  }

}
