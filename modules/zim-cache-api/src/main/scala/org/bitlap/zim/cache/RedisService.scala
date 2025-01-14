/*
 * Copyright 2023 bitlap
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

package org.bitlap.zim.cache

import io.circe._

/** Redis缓存服务
 *
 *  @author
 *    梦境迷离
 *  @version 3.0,2022/1/10
 */

trait RedisService[F[_]] {

  /** 获取Set集合数据
   *
   *  @param k
   *  @return
   *    List[String]
   */
  def getSets(k: String): F[List[String]]

  /** 移除Set集合中的value
   *
   *  @param k
   *  @param m
   *  @return
   *    Long
   */
  def removeSetValue(k: String, m: String): F[Long]

  /** 保存到Set集合中
   *
   *  @param k
   *  @param m
   *  @return
   *    Long
   */
  def setSet(k: String, m: String): F[Long]

  /** 存储key-value
   *
   *  @param k
   *  @param v
   *  @param expireTime
   *  @return
   *    Boolean
   */
  def set[T](k: String, v: T, expireTime: JavaDuration = java.time.Duration.ofMinutes(30))(implicit
      encoder: Encoder[T]
  ): F[Boolean]

  /** 根据key获取value
   *
   *  @param key
   *  @return
   *    Object
   */
  def get[T](key: String)(implicit decoder: Decoder[T]): F[Option[T]]

  /** 判断key是否存在
   *
   *  @param key
   *  @return
   *    Boolean
   */
  def exists(key: String): F[Boolean]

  /** 删除key
   *
   *  @param key
   *  @return
   */
  def del(key: String): F[Boolean]

}
