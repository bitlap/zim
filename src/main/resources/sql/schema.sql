SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_add_message`
-- ----------------------------
DROP TABLE IF EXISTS `t_add_message`;
CREATE TABLE `t_add_message` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `from_uid` int(10) NOT NULL COMMENT '谁发起的请求',
  `to_uid` int(10) NOT NULL COMMENT '发送给谁的申请,可能是群，那么就是创建该群组的用户',
  `group_id` int(10) NOT NULL COMMENT '如果是添加好友则为from_id的分组id，如果为群组则为群组id',
  `remark` varchar(255) DEFAULT NULL COMMENT '附言',
  `agree` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0未处理，1同意，2拒绝',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '类型，可能是添加好友或群组',
  `time` datetime NOT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `add_friend_unique` (`from_uid`,`to_uid`,`group_id`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `t_friend_group`
-- ----------------------------
DROP TABLE IF EXISTS `t_friend_group`;
CREATE TABLE `t_friend_group` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `uid` int(10) NOT NULL COMMENT '该分组所属的用户ID',
  `group_name` varchar(64) NOT NULL COMMENT '分组名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `t_friend_group_friends`
-- ----------------------------
DROP TABLE IF EXISTS `t_friend_group_friends`;
CREATE TABLE `t_friend_group_friends` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `fgid` int(10) NOT NULL COMMENT '分组id',
  `uid` int(10) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `g_uid_unique` (`fgid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `t_group`
-- 格式问题，使用了其他默认时间
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) NOT NULL COMMENT '群组名称',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '群组图标',
  `create_id` int(20) NOT NULL COMMENT '创建者id',
  `create_time` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for `t_group_members`
-- ----------------------------
DROP TABLE IF EXISTS `t_group_members`;
CREATE TABLE `t_group_members` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `gid` int(20) NOT NULL COMMENT '群组ID',
  `uid` int(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `t_message`
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `toid` int(10) NOT NULL COMMENT '发送给哪个用户或者组id',
  `mid` int(10) NOT NULL COMMENT '消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）',
  `fromid` int(10) NOT NULL COMMENT '消息的发送者id（比如群组中的某个消息发送者）',
  `content` varchar(512) NOT NULL COMMENT '消息内容',
  `type` varchar(10) NOT NULL DEFAULT '' COMMENT '聊天窗口来源类型',
  `timestamp` bigint(25) NOT NULL COMMENT '服务器动态时间',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=444 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `sign` varchar(255) DEFAULT NULL COMMENT '签名',
  `email` varchar(64) NOT NULL COMMENT '邮箱地址',
  `avatar` varchar(255) DEFAULT '/static/image/avatar/avatar(3).jpg' COMMENT '头像地址',
  `sex` int(2) NOT NULL DEFAULT '1' COMMENT '性别',
  `active` varchar(64) NOT NULL COMMENT '激活码',
  `status` varchar(16) NOT NULL DEFAULT 'nonactivated' COMMENT '是否激活',
  `create_date` date NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4;