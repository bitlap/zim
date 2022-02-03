-- ----------------------------
-- Records of t_add_message
-- ----------------------------
INSERT INTO `t_add_message`
VALUES ('1', '106', '19', '1', '', '1', '0', '2017-04-16 12:00:14');
INSERT INTO `t_add_message`
VALUES ('2', '106', '21', '3', '', '0', '0', '2017-04-16 12:00:48');
INSERT INTO `t_add_message`
VALUES ('3', '106', '18', '3', '我是王培坤', '0', '0', '2017-04-16 12:01:01');
INSERT INTO `t_add_message`
VALUES ('4', '106', '1', '1', '', '0', '0', '2017-04-17 09:04:16');
INSERT INTO `t_add_message`
VALUES ('13', '106', '8', '1', '我是是是是', '0', '0', '2017-04-16 15:27:06');
INSERT INTO `t_add_message`
VALUES ('14', '1', '102', '6', '我是嘻嘻嘻 ', '0', '0', '2017-04-16 15:38:18');
INSERT INTO `t_add_message`
VALUES ('15', '102', '100', '7', '我是silence', '0', '0', '2017-04-16 16:22:56');
INSERT INTO `t_add_message`
VALUES ('16', '45', '106', '1', 'hello you are', '2', '0', '2017-04-20 16:58:31');
INSERT INTO `t_add_message`
VALUES ('17', '46', '106', '2', 'can you ', '1', '0', '2017-04-07 16:58:52');
INSERT INTO `t_add_message`
VALUES ('18', '56', '106', '2', '123', '0', '0', '2017-04-14 16:59:10');
INSERT INTO `t_add_message`
VALUES ('19', '57', '106', '2', 'whyy', '1', '0', '2017-04-20 16:59:26');
INSERT INTO `t_add_message`
VALUES ('20', '69', '106', '3', '我是谁', '2', '0', '2017-04-12 16:59:47');
INSERT INTO `t_add_message`
VALUES ('21', '95', '106', '8', '我是id95 思月', '1', '0', '2017-04-16 19:49:28');
INSERT INTO `t_add_message`
VALUES ('22', '35', '19', '10', '我是id 35', '1', '0', '2017-04-16 21:51:44');
INSERT INTO `t_add_message`
VALUES ('23', '35', '1', '10', '我是谁', '0', '0', '2017-04-16 21:54:02');
INSERT INTO `t_add_message`
VALUES ('24', '106', '53', '3', '我是王培坤', '1', '0', '2017-04-16 22:18:06');

-- ----------------------------
-- Records of t_friend_group
-- ----------------------------
INSERT INTO `t_friend_group`
VALUES ('1', '106', '前端小组');
INSERT INTO `t_friend_group`
VALUES ('2', '106', '大数据小组');
INSERT INTO `t_friend_group`
VALUES ('3', '106', '策划小组');
INSERT INTO `t_friend_group`
VALUES ('4', '106', '产品小组');
INSERT INTO `t_friend_group`
VALUES ('5', '100', '调研小组');
INSERT INTO `t_friend_group`
VALUES ('6', '1', 'UI小组');
INSERT INTO `t_friend_group`
VALUES ('7', '102', '我的好友');
INSERT INTO `t_friend_group`
VALUES ('8', '95', '我的好友');
INSERT INTO `t_friend_group`
VALUES ('9', '19', '我的好友');
INSERT INTO `t_friend_group`
VALUES ('10', '35', '我的好友');
INSERT INTO `t_friend_group`
VALUES ('11', '53', '我的好友');
INSERT INTO `t_friend_group`
VALUES ('12', '57', '我的好友');
INSERT INTO `t_friend_group`
VALUES ('13', '100', '我的好友');


-- ----------------------------
-- Records of t_friend_group_friends
-- ----------------------------
INSERT INTO `t_friend_group_friends`
VALUES ('1', '1', '1');
INSERT INTO `t_friend_group_friends`
VALUES ('2', '1', '2');
INSERT INTO `t_friend_group_friends`
VALUES ('3', '1', '3');
INSERT INTO `t_friend_group_friends`
VALUES ('66', '1', '19');
INSERT INTO `t_friend_group_friends`
VALUES ('63', '1', '95');
INSERT INTO `t_friend_group_friends`
VALUES ('18', '1', '100');
INSERT INTO `t_friend_group_friends`
VALUES ('4', '2', '4');
INSERT INTO `t_friend_group_friends`
VALUES ('5', '2', '5');
INSERT INTO `t_friend_group_friends`
VALUES ('6', '2', '6');
INSERT INTO `t_friend_group_friends`
VALUES ('10', '2', '7');
INSERT INTO `t_friend_group_friends`
VALUES ('7', '2', '8');
INSERT INTO `t_friend_group_friends`
VALUES ('8', '2', '9');
INSERT INTO `t_friend_group_friends`
VALUES ('9', '2', '10');
INSERT INTO `t_friend_group_friends`
VALUES ('11', '2', '11');
INSERT INTO `t_friend_group_friends`
VALUES ('12', '2', '12');
INSERT INTO `t_friend_group_friends`
VALUES ('57', '2', '95');
INSERT INTO `t_friend_group_friends`
VALUES ('72', '2', '106');
INSERT INTO `t_friend_group_friends`
VALUES ('13', '3', '13');
INSERT INTO `t_friend_group_friends`
VALUES ('70', '3', '53');
INSERT INTO `t_friend_group_friends`
VALUES ('14', '4', '14');
INSERT INTO `t_friend_group_friends`
VALUES ('15', '4', '15');
INSERT INTO `t_friend_group_friends`
VALUES ('16', '4', '17');
INSERT INTO `t_friend_group_friends`
VALUES ('71', '4', '57');
INSERT INTO `t_friend_group_friends`
VALUES ('19', '5', '106');
INSERT INTO `t_friend_group_friends`
VALUES ('20', '6', '106');
INSERT INTO `t_friend_group_friends`
VALUES ('64', '8', '106');
INSERT INTO `t_friend_group_friends`
VALUES ('67', '9', '35');
INSERT INTO `t_friend_group_friends`
VALUES ('65', '9', '106');
INSERT INTO `t_friend_group_friends`
VALUES ('68', '10', '19');
INSERT INTO `t_friend_group_friends`
VALUES ('69', '11', '106');

-- ----------------------------
-- Records of t_group
-- ----------------------------
INSERT INTO `t_group`
VALUES ('1', 'Java群', '/static/image/group/group_1.gif', '106', '2017-04-10 20:39:11');
INSERT INTO `t_group`
VALUES ('2', 'Scala群', '/static/image/group/group_2.gif', '106', '2017-04-10 20:39:22');
INSERT INTO `t_group`
VALUES ('3', 'SpringBoot群', '/static/image/group/group_3.jpg', '106', '2017-04-10 20:40:44');
INSERT INTO `t_group`
VALUES ('4', 'Redis群', '/static/image/group/group_4.jpg', '1', '2017-04-10 20:40:47');

-- ----------------------------
-- Records of t_group_members
-- ----------------------------
INSERT INTO `t_group_members`
VALUES ('1', '1', '1');
INSERT INTO `t_group_members`
VALUES ('2', '1', '2');
INSERT INTO `t_group_members`
VALUES ('3', '1', '3');
INSERT INTO `t_group_members`
VALUES ('4', '1', '100');
INSERT INTO `t_group_members`
VALUES ('5', '1', '105');
INSERT INTO `t_group_members`
VALUES ('6', '2', '1');
INSERT INTO `t_group_members`
VALUES ('7', '2', '8');
INSERT INTO `t_group_members`
VALUES ('8', '3', '14');
INSERT INTO `t_group_members`
VALUES ('9', '3', '18');
INSERT INTO `t_group_members`
VALUES ('10', '3', '23');
INSERT INTO `t_group_members`
VALUES ('11', '3', '55');
INSERT INTO `t_group_members`
VALUES ('12', '4', '106');
INSERT INTO `t_group_members`
VALUES ('13', '4', '1');
INSERT INTO `t_group_members`
VALUES ('14', '4', '2');
INSERT INTO `t_group_members`
VALUES ('15', '4', '76');
INSERT INTO `t_group_members`
VALUES ('16', '1', '106');
INSERT INTO `t_group_members`
VALUES ('17', '2', '106');
INSERT INTO `t_group_members`
VALUES ('18', '3', '106');

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message`
VALUES ('278', '100', '106', '106', 'can you see me', 'friend', '1492044311037', '1');
INSERT INTO `t_message`
VALUES ('279', '106', '100', '100', 'yes i can', 'friend', '1492044336744', '1');
INSERT INTO `t_message`
VALUES ('284', '100', '106', '106', 'face[鼓掌] ', 'friend', '1492044394069', '1');
INSERT INTO `t_message`
VALUES ('285', '100', '106', '106', 'img[/upload/image/2017-04-13/078f382fbb4d440d8fbf48af6c06f6a9.gif]', 'friend',
        '1492044396894', '1');
INSERT INTO `t_message`
VALUES ('286', '100', '106', '106',
        'file(/upload/file/2017-04-13/82b68e3fb3e84bb0b8907c4b3577debd/vaish.pdf)[vaish.pdf]', 'friend',
        '1492044402645', '1');
INSERT INTO `t_message`
VALUES ('287', '100', '106', '106', 'fdf', 'friend', '1492045672858', '1');
INSERT INTO `t_message`
VALUES ('288', '100', '106', '106', 'fdfdfd', 'friend', '1492045675434', '1');
INSERT INTO `t_message`
VALUES ('289', '100', '106', '106', 'img[/upload/image/2017-04-13/938f4da10d394ac88092a162b74764b7.gif]', 'friend',
        '1492045686012', '1');
INSERT INTO `t_message`
VALUES ('290', '100', '106', '106', 'img[/upload/image/2017-04-13/44585b3a65284c519dd378e8b156f77a.gif]', 'friend',
        '1492045693439', '1');
INSERT INTO `t_message`
VALUES ('291', '100', '106', '106', 'face[晕] face[晕] ', 'friend', '1492045697463', '1');
INSERT INTO `t_message`
VALUES ('292', '100', '106', '106', '有', 'friend', '1492059765813', '1');
INSERT INTO `t_message`
VALUES ('293', '100', '106', '106', 'you are here', 'friend', '1492059770843', '1');
INSERT INTO `t_message`
VALUES ('294', '106', '100', '100', 'youy  fdfdfd', 'friend', '1492059891380', '1');
INSERT INTO `t_message`
VALUES ('299', '100', '106', '106', 'silfdnld', 'friend', '1492060061492', '1');
INSERT INTO `t_message`
VALUES ('304', '106', '100', '100', 'you are', 'friend', '1492063193913', '1');
INSERT INTO `t_message`
VALUES ('305', '100', '106', '106', 'img[/upload/image/2017-04-13/aa6172e85c0948b9af449d8ffca49f8a.gif]', 'friend',
        '1492063200807', '1');
INSERT INTO `t_message`
VALUES ('306', '106', '100', '100', 'img[/upload/image/2017-04-13/5c8ed451cd934ebb82e7808792f40841.png]', 'friend',
        '1492063207684', '1');
INSERT INTO `t_message`
VALUES ('307', '100', '106', '106', 'haha ', 'friend', '1492063218341', '1');
INSERT INTO `t_message`
VALUES ('308', '100', '106', '106', 'can you see', 'friend', '1492063277745', '1');
INSERT INTO `t_message`
VALUES ('309', '1', '106', '106', 'ni shi sha bi a ', 'friend', '1492063320353', '0');
INSERT INTO `t_message`
VALUES ('314', '106', '100', '100', 'hell you are', 'friend', '1492065740132', '0');
INSERT INTO `t_message`
VALUES ('315', '100', '106', '106', 'hahahahaahaha', 'friend', '1492065768066', '1');
INSERT INTO `t_message`
VALUES ('316', '100', '106', '106', 'you care', 'friend', '1492066916971', '1');
INSERT INTO `t_message`
VALUES ('317', '100', '106', '106', 'can you see', 'friend', '1492066920771', '1');
INSERT INTO `t_message`
VALUES ('318', '106', '100', '100', 'fgf', 'friend', '1492066970584', '1');
INSERT INTO `t_message`
VALUES ('319', '106', '100', '100', 'you fd', 'friend', '1492068412715', '1');
INSERT INTO `t_message`
VALUES ('320', '100', '106', '106', 'you can', 'friend', '1492086999497', '1');
INSERT INTO `t_message`
VALUES ('321', '106', '100', '100', 'so you can', 'friend', '1492087007954', '1');
INSERT INTO `t_message`
VALUES ('334', '106', '100', '100', 'can you', 'friend', '1492091732776', '1');
INSERT INTO `t_message`
VALUES ('335', '100', '106', '106', 'yes you can', 'friend', '1492091740621', '1');
INSERT INTO `t_message`
VALUES ('340', '106', '100', '100', '12313', 'friend', '1492130436919', '0');
INSERT INTO `t_message`
VALUES ('397', '1', '1', '100', 'you are heere', 'group', '1492134078521', '0');
INSERT INTO `t_message`
VALUES ('398', '100', '106', '106', 'fdfd', 'friend', '1492134112114', '1');
INSERT INTO `t_message`
VALUES ('399', '1', '1', '106', 'you are here too', 'group', '1492134126326', '1');
INSERT INTO `t_message`
VALUES ('400', '1', '1', '106', 'ni shi shabi a ', 'group', '1492138398473', '1');
INSERT INTO `t_message`
VALUES ('401', '1', '1', '100', 'ni caishi shabi', 'group', '1492138410246', '1');
INSERT INTO `t_message`
VALUES ('402', '1', '1', '106', 'img[/upload/image/2017-04-14/3489a8d6f13d4f1386f40c7e4f1b1331.gif]', 'group',
        '1492138415268', '1');
INSERT INTO `t_message`
VALUES ('403', '1', '1', '106', 'face[拜拜] ', 'group', '1492138419344', '1');
INSERT INTO `t_message`
VALUES ('404', '1', '1', '106', ' can you see me', 'group', '1492138432879', '1');
INSERT INTO `t_message`
VALUES ('405', '1', '1', '1', 'you', 'group', '1492138737038', '1');
INSERT INTO `t_message`
VALUES ('406', '1', '1', '1', ' hahah a', 'group', '1492138740731', '1');
INSERT INTO `t_message`
VALUES ('407', '1', '1', '1', 'img[/upload/image/2017-04-14/2bbfdee13a454d8cb452eb8be10cec02.jpg]', 'group',
        '1492138746490', '1');
INSERT INTO `t_message`
VALUES ('408', '106', '100', '100', 'face[晕] ', 'friend', '1492138938078', '0');
INSERT INTO `t_message`
VALUES ('409', '106', '100', '100', 'you are here', 'friend', '1492151190441', '1');
INSERT INTO `t_message`
VALUES ('410', '100', '106', '106', 'so you can see me?', 'friend', '1492151198992', '1');
INSERT INTO `t_message`
VALUES ('411', '106', '100', '100', 'of course', 'friend', '1492151209763', '1');
INSERT INTO `t_message`
VALUES ('412', '100', '106', '106', 'img[/upload/image/2017-04-14/b273ba2372c54ed5b8294948daea9aad.gif]', 'friend',
        '1492151213114', '1');
INSERT INTO `t_message`
VALUES ('413', '100', '106', '106',
        'file(/upload/file/2017-04-14/a505714207a24208ba93609115d0d86b/railstutorial4th-sample.pdf)[railstutorial4th-sample.pdf]',
        'friend', '1492151222502', '1');
INSERT INTO `t_message`
VALUES ('414', '106', '100', '100', 'fdfd', 'friend', '1492151279802', '1');
INSERT INTO `t_message`
VALUES ('415', '100', '106', '106', 'can', 'friend', '1492156031241', '1');
INSERT INTO `t_message`
VALUES ('416', '106', '100', '100', 'yes i can', 'friend', '1492156049674', '1');
INSERT INTO `t_message`
VALUES ('417', '100', '106', '106', 'sorry you can?', 'friend', '1492167152813', '0');
INSERT INTO `t_message`
VALUES ('418', '100', '106', '106', 'you can see  me?', 'friend', '1492221564710', '0');
INSERT INTO `t_message`
VALUES ('419', '1', '1', '106', 'can', 'group', '1492233567749', '0');
INSERT INTO `t_message`
VALUES ('420', '106', '100', '100', '123', 'friend', '1492233588308', '1');
INSERT INTO `t_message`
VALUES ('421', '100', '106', '106', 'can you', 'friend', '1492244367611', '1');
INSERT INTO `t_message`
VALUES ('422', '100', '106', '106', 'ca', 'friend', '1492244458305', '1');
INSERT INTO `t_message`
VALUES ('423', '1', '1', '106', 'you see me?', 'group', '1492244469314', '1');
INSERT INTO `t_message`
VALUES ('424', '100', '106', '106', 'fdf', 'friend', '1492244570981', '1');
INSERT INTO `t_message`
VALUES ('425', '1', '1', '106', '3123', 'group', '1492244582373', '1');
INSERT INTO `t_message`
VALUES ('426', '1', '1', '100', 'you aree here', 'group', '1492244589829', '1');
INSERT INTO `t_message`
VALUES ('427', '1', '1', '106', 'so you', 'group', '1492244593489', '1');
INSERT INTO `t_message`
VALUES ('428', '100', '106', '106', 'fd', 'friend', '1492244596254', '1');
INSERT INTO `t_message`
VALUES ('429', '100', '106', '106', 'img[/upload/image/2017-04-15/83b8d86b1105487295b50f70210357b5.gif]', 'friend',
        '1492244600136', '1');
INSERT INTO `t_message`
VALUES ('430', '106', '100', '100', 'fdfd', 'friend', '1492322377739', '1');
INSERT INTO `t_message`
VALUES ('431', '106', '100', '100', 'fdf', 'friend', '1492322384545', '1');
INSERT INTO `t_message`
VALUES ('432', '106', '100', '100', 'img[/upload/image/2017-04-16/9b0615d4aa834988bf06398495930cbe.gif]', 'friend',
        '1492322389875', '1');
INSERT INTO `t_message`
VALUES ('433', '106', '100', '100', 'img[/upload/image/2017-04-16/ea9f884c214c45dd9025b1d872ff8baa.gif]', 'friend',
        '1492322402801', '1');
INSERT INTO `t_message`
VALUES ('434', '106', '100', '100', 'img[/upload/image/2017-04-16/8b8ba9e15d24492b8b3d344e47c3a90e.jpg]', 'friend',
        '1492322415470', '1');
INSERT INTO `t_message`
VALUES ('435', '1', '1', '100', 'fddf', 'group', '1492322486128', '1');
INSERT INTO `t_message`
VALUES ('436', '106', '100', '100', 'fdfd', 'friend', '1492390696658', '1');
INSERT INTO `t_message`
VALUES ('437', '100', '106', '106', 'fdfdfd', 'friend', '1492390732772', '1');
INSERT INTO `t_message`
VALUES ('438', '106', '100', '100', 'img[/upload/image/2017-04-17/ac6a6ccb25a445e29908c256830f19d1.png]', 'friend',
        '1492390738798', '1');
INSERT INTO `t_message`
VALUES ('439', '106', '100', '100',
        'file(/upload/file/2017-04-17/0a7e89e4c39045428cff192b13f3c7bc/vaish.pdf)[vaish.pdf]', 'friend',
        '1492390750063', '1');
INSERT INTO `t_message`
VALUES ('440', '1', '1', '100', 'fdfd', 'group', '1492390819118', '1');
INSERT INTO `t_message`
VALUES ('441', '1', '1', '106', 'img[/upload/image/2017-04-17/405636c995db4f6982f374bdf59d2418.gif]', 'group',
        '1492390865176', '1');
INSERT INTO `t_message`
VALUES ('442', '1', '1', '106', 'file(/upload/file/2017-04-17/88e9c655485c4db9a5a925a1a6f7cc2c/vaish.pdf)[vaish.pdf]',
        'group', '1492390869928', '1');
INSERT INTO `t_message`
VALUES ('443', '1', '1', '106', 'fdfd', 'group', '1492390925888', '1');

-- ----------------------------
-- Records of t_user
-- ----------------------------
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('边月', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15605832957@sina.com', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2002-10-20');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('侯梅希', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13901622213@ask.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2011-07-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('申山', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13700505911@163.net', '/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '2003-09-07');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('殴云丽', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13400872279@0355.net', '/static/image/avatar/avatar(3).jpg', 1, '123', 'hide', '1991-03-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('赖希', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15603347311@3721.net', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1997-04-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('韩莎', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15301204071@ask.com', '/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2004-10-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('胥婉苑', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15007697659@sina.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '2013-05-14');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('夏翔厚', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15606848938@yahoo.com', '/static/image/avatar/avatar(5).jpg', 0, '123', 'hide', '2009-06-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('竺兰婷', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15802021990@live.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2013-05-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('戚友', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13903830257@live.com', '/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '1993-04-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('屠盛', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15503511548@126.com', '/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '1991-01-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('霍枫', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15900273312@0355.net', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2001-05-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('容蕊芬', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13703494814@gmail.com', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2010-08-30');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('卜庆承', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13101768528@gmail.com', '/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '2007-11-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('满咏琴', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13201874403@qq.com', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2000-05-08');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('左清', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13700345359@live.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1993-01-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('习胜', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15200532435@gmail.com', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1993-12-18');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宰琰', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13207073510@msn.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2007-06-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('鲁飞鹏', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15107594698@live.com', '/static/image/avatar/avatar(6).jpg', 0, '123', 'hide', '2002-02-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('左波健', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15602927444@yeah.net', '/static/image/avatar/avatar(4).jpg', 0, '123', 'hide', '1999-04-20');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('官浩谦', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13801942028@sina.com', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2003-10-25');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('褚振磊', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13503746557@126.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1999-12-19');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('姚斌', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13603652150@yeah.net', '/static/image/avatar/avatar(4).jpg', 0, '123', 'hide', '2001-11-27');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('黄黛', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15005846482@yeah.net', '/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '1994-11-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('柯飘', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13706055022@googlemail.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide',
        '1997-05-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('柴胜诚', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13202123926@msn.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1995-02-19');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('康柔桂', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13903317367@yeah.net', '/static/image/avatar/avatar(1).jpg', 1, '123', 'hide', '2010-04-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('微瑶婷', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15706487221@263.net', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1994-03-19');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('柏仪', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15900147912@sohu.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '2003-08-03');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('田炎', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13808781268@3721.net', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2009-01-12');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('东梁亮', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15803836020@163.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2007-08-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('尉林', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13104611094@yahoo.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '2004-06-27');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('平燕岚', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13207994837@0355.net', '/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '2004-01-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('颛才', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13804443526@gmail.com', '/static/image/avatar/avatar(4).jpg', 1, '123', 'hide', '1990-01-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('暨寒莺', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15302154003@126.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1994-09-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('莘栋泽', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13005174289@live.com', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '2014-06-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('融香琳', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13307477257@qq.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2014-06-23');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('於致壮', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13301417965@ask.com', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2002-08-14');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('焦凤', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13306256329@qq.com', '/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '1999-05-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('葛河刚', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13308263395@googlemail.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide',
        '2010-05-07');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('寇芝希', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15205202731@hotmail.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide',
        '1992-07-05');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宗薇馨', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13107077759@sohu.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '2005-12-29');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('涂婉', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13006913015@yahoo.com', '/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '1999-01-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('逄裕', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15505815686@qq.com', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1996-04-27');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('双承利', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15301965725@0355.net', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2004-01-23');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('皇咏琦', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13602400122@qq.com', '/static/image/avatar/avatar(5).jpg', 0, '123', 'hide', '2003-04-08');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('逯楠', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13100255491@163.net', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1998-01-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('楚妹冰', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13905441770@live.com', '/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '2000-10-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('父珠', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13901066514@sina.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2008-11-09');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宰义', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13707301937@sina.com', '/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2012-08-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('唐维', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13408056175@gmail.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1996-08-12');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宰咏', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13404142271@163.net', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2012-09-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('门贞贞', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13408748220@163.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1996-06-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('那婵', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13306056717@163.com', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '1998-06-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('杭波绍', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15100674200@yeah.net', '/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '1992-09-18');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('申翠', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13205334430@ask.com', '/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1990-04-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('凌博', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15306637225@googlemail.com', '/static/image/avatar/avatar(5).jpg', 0, '123', 'hide',
        '2010-02-20');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('徐琰', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13403493904@sohu.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1991-05-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宗航', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15703675257@aol.com', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2001-05-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('有蓓妍', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13304025380@hotmail.com', '/static/image/avatar/avatar(1).jpg', 0, '123', 'hide',
        '1995-03-21');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('龙浩', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15001835427@sina.com', '/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '1994-11-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('臧军先', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15107493124@aol.com', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2004-01-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('年钧友', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13207528433@qq.com', '/static/image/avatar/avatar(1).jpg', 1, '123', 'hide', '1994-12-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('褚霄滢', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13100894652@msn.com', '/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2007-05-12');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('平健', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13700928788@163.com', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '1995-12-31');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('羊蕊玲', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13200152690@163.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2004-02-25');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('戚坚茂', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13207525160@aol.com', '/static/image/avatar/avatar(9).jpg', 1, '123', 'hide', '2006-11-06');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('阳寒', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13105951913@yahoo.com', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1991-12-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宓之', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15201911216@live.com', '/static/image/avatar/avatar(5).jpg', 0, '123', 'hide', '2006-10-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谈羽', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15002600226@hotmail.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide',
        '2011-03-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('梁榕昌', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15708052377@qq.com', '/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '2006-04-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('龚丹飘', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13407394931@googlemail.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide',
        '2000-09-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('方超山', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15803194907@yeah.net', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '2000-05-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('拔广冠', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13501161119@263.net', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '1994-07-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('里凝', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13308445850@163.com', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '2009-07-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宋怡', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15104496675@3721.net', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2013-07-16');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谭娜伊', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13603931551@gmail.com', '/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '1994-01-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('司强勇', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15101077205@yeah.net', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '1997-03-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('房琴', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15507700151@hotmail.com', '/static/image/avatar/avatar(1).jpg', 1, '123', 'hide',
        '1993-12-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谢星贵', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13001633106@yeah.net', '/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2013-04-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('狐静茗', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13305247316@sina.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2010-07-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('薛勤静', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15904345915@3721.net', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '1991-10-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谯岩', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13808763279@aol.com', '/static/image/avatar/avatar(4).jpg', 1, '123', 'hide', '2015-02-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('言腾弘', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15107042392@263.net', '/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '1994-05-30');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('厍之德', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13103816897@sohu.com', '/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1992-02-28');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('汪素', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15206122291@live.com', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2013-08-05');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('邱芸滢', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13306888302@msn.com', '/static/image/avatar/avatar(9).jpg', 1, '123', 'hide', '2011-02-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('钭荷', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15206746459@263.net', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1998-07-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('倪秋', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15508373082@163.net', '/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '2013-08-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('申裕厚', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15700771177@aol.com', '/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '2007-03-23');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('终聪芸', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15803822828@163.net', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2011-02-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('狄婉艺', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13504970131@sina.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1995-08-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('公伯', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15607053770@qq.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1993-01-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('莫聪', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13504271752@yeah.net', '/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2000-05-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('家茗嘉', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '13705762571@163.net', '/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '1992-09-16');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('商明毅', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15008175682@aol.com', '/static/image/avatar/avatar(4).jpg', 1, '123', 'hide', '1997-10-14');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('卞羽', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15904301500@qq.com', '/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2006-02-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谢卿锦', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15802335560@ask.com', '/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1992-11-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('马风震', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15908510852@263.net', '/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1995-03-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('赵毓', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15600620712@msn.com', '/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2012-06-16');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('卞豪', 'jZae727K08KaOmKSgOaGzww/XVqGr/PKEgIMkjrcbJI=', '梦境亦是美，醒来亦是空',
        '15602253303@yeah.net', '/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '2015-12-08');

-- 给每个人初始化默认的好友列表
insert into `t_friend_group` (uid, group_name)
select id, '我的好友'
from t_user
