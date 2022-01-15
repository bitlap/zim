package org.bitlap.zim.application

import org.bitlap.zim.domain.model.User

/**
 *  直接提供给endpoint使用
 *  对userService做一定的包装
 *
 * @author 梦境迷离
 * @since 2022/1/8
 * @version 1.0
 */
trait ApiApplication extends BaseApplication[User]
