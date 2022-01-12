package org.bitlap.zim.application

import zio.Has

/**
 * @author 梦境迷离
 * @version 1.0,2022/1/11
 */
package object ws {

  type WsService = Has[WsService.Service]

}
