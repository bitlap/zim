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
values ('边月', 'dfcdbc1cadb5acda5a22b0a6862fadc23bb1d3c70306ad7c3daad7c300ae60f6dbbb3539a9447086', '梦境亦是美，醒来亦是空',
        '15605832957@sina.com', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2002-10-20');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('侯梅希', '9d80b52eb57e5b8361eadf08ba98efa28fb2fcf35d3c72098d187a80425c05b29bed109d7451b347', '梦境亦是美，醒来亦是空',
        '13901622213@ask.com', 'http://localhost/static/image/avatar/avatar(0).jpg', 1, '123', 'hide', '2011-07-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('申山', 'd5b74714cb67fd86643845fd37cb259e5a0c0994fdb03bf11e06367713140765e29ec0efa5d8a642', '梦境亦是美，醒来亦是空',
        '13700505911@163.net', 'http://localhost/static/image/avatar/avatar(0).jpg', 0, '123', 'hide', '2003-09-07');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('殴云丽', '987b56b8c781c336418ba2352923b69f08060f4da0f58a7c9a93a8986deacc00a585a84a096a7b43', '梦境亦是美，醒来亦是空',
        '13400872279@0355.net', 'http://localhost/static/image/avatar/avatar(3).jpg', 1, '123', 'hide', '1991-03-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('赖希', 'd6069529a32f361694e230c92422fbb5c597a15e118f7abf728a4cd6d4a8942ed710c37ba221b6e6', '梦境亦是美，醒来亦是空',
        '15603347311@3721.net', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1997-04-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('韩莎', '6000cbe0ed6a895de8c75bae93aa3795d9a83d064b0544ac2a23500abf92c3551ff2d63f8cc2850b', '梦境亦是美，醒来亦是空',
        '15301204071@ask.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2004-10-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('胥婉苑', '88e3854764e84db42b9d3df50bbac7220b504d564bbd1c41c97f212fadc9c2cf6e29e3da0bd16935', '梦境亦是美，醒来亦是空',
        '15007697659@sina.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '2013-05-14');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('夏翔厚', 'fbacb489698ac22d97e239fdda829409f660dfeef425afd498982632423e630476bc7faaf3c0232d', '梦境亦是美，醒来亦是空',
        '15606848938@yahoo.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 0, '123', 'hide', '2009-06-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('竺兰婷', 'e8af4c7c4a05f0a711ee0481e32d962c542c905982a863c50dc4f89b15667d93f0999aedd3e55205', '梦境亦是美，醒来亦是空',
        '15802021990@live.com', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2013-05-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('戚友', 'e5107419293255dec4e83f41bed80e24cde7a39c0345bbce1141ebfd2a238679bb7362db245f8752', '梦境亦是美，醒来亦是空',
        '13903830257@live.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '1993-04-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('屠盛', 'b31465a961d12a08b2edb47a5057909e54bf4c84d8d1eb8ee7010e5f2c4154b829d499af7e5e9f1c', '梦境亦是美，醒来亦是空',
        '15503511548@126.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '1991-01-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('霍枫', '0028ba4e9d8f3202ba737cad3fd73dc670ef21f193b04be71a839feadae38ae95f8da3aa51a79182', '梦境亦是美，醒来亦是空',
        '15900273312@0355.net', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2001-05-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('容蕊芬', '007d2b2730f7f5994d86f58400e8f5cc088e562e4e26ce0f4c917368c5a03fbbb1db15c81bebfb3c', '梦境亦是美，醒来亦是空',
        '13703494814@gmail.com', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2010-08-30');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('卜庆承', '5904efc2959747bb3b05efcf2fd5eb342cd743fbaf31d1c5a36c33b369c70e540de4028eec177ead', '梦境亦是美，醒来亦是空',
        '13101768528@gmail.com', 'http://localhost/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '2007-11-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('满咏琴', '2ac2052743c040f98ca55d7a7bd1ef0c372f21b7f991a3f6c2c6e8c5515df3d4064e0bd8c6706d34', '梦境亦是美，醒来亦是空',
        '13201874403@qq.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2000-05-08');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('左清', '93a550eb35b9a048ce9061770975d262a7bd9b9db662dd4bc4b2bea1787bea290d7dcd0034539c52', '梦境亦是美，醒来亦是空',
        '13700345359@live.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1993-01-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('习胜', '6835ad882d54e51fe43521d80689306b929651bd8e53901056abc95c8867b508c1f4316e753e7532', '梦境亦是美，醒来亦是空',
        '15200532435@gmail.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1993-12-18');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宰琰', 'bcd85d7c244d8fbc76041ed1354a40e57aeb46429b2b7a56f5300c1a4049936a08786bba9f680d82', '梦境亦是美，醒来亦是空',
        '13207073510@msn.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2007-06-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('鲁飞鹏', '5597eb779b7d77cec67c74003acfffc16561c8ebece77c0a720b346b956a2bace4747c4d2e8d257a', '梦境亦是美，醒来亦是空',
        '15107594698@live.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 0, '123', 'hide', '2002-02-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('左波健', 'fb3ebd63edb9739fec5ff1badb7fcf70990c5ada27be49c2f39fade5843c8aa56e89c3de0b7523bf', '梦境亦是美，醒来亦是空',
        '15602927444@yeah.net', 'http://localhost/static/image/avatar/avatar(4).jpg', 0, '123', 'hide', '1999-04-20');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('官浩谦', 'b0511c1ef195446ddd3969143f13f88e8e1989afd348164fc7572a809d9d3df6780c71e702e39dbb', '梦境亦是美，醒来亦是空',
        '13801942028@sina.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2003-10-25');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('褚振磊', 'ee95d03fc84a038120ffb2370063430b77455df2572c3c5a8ed0007fd2ed6033f1ce80dfcdcf97ac', '梦境亦是美，醒来亦是空',
        '13503746557@126.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1999-12-19');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('姚斌', '9968bc8715cd67e4de2b370f661f2dcb1a0662bb0c8a55e6258338570bcbd93805bdff65b0147a4d', '梦境亦是美，醒来亦是空',
        '13603652150@yeah.net', 'http://localhost/static/image/avatar/avatar(4).jpg', 0, '123', 'hide', '2001-11-27');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('黄黛', 'a29abfe2323e221a56abc3568c4da1800240d60608271925003a333cb336da0fc5d34e985d03c6c5', '梦境亦是美，醒来亦是空',
        '15005846482@yeah.net', 'http://localhost/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '1994-11-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('柯飘', '2e917acebc570ffbfa55257c6348155b117cdcfe4e16ee7d60db813a7cef3efdd40fecfb85ed4fb1', '梦境亦是美，醒来亦是空',
        '13706055022@googlemail.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide',
        '1997-05-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('柴胜诚', '1c22899101da5d97930db845b6c401cf62d8a43e861384d5ae2a4aad614968c95049f254dd09176a', '梦境亦是美，醒来亦是空',
        '13202123926@msn.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1995-02-19');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('康柔桂', '4fdab4ad18e7f894c7986a80a2229299485a6c986699118d33e9916f66c03ae652ede72c026f4d69', '梦境亦是美，醒来亦是空',
        '13903317367@yeah.net', 'http://localhost/static/image/avatar/avatar(1).jpg', 1, '123', 'hide', '2010-04-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('微瑶婷', '9275c9a325c2f72a55a5360b90cec4933b5a504dcba545127f0133bbce32b43acb11c4379162f6ec', '梦境亦是美，醒来亦是空',
        '15706487221@263.net', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1994-03-19');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('柏仪', 'b30a14774dbb670a72b2a9d793440d1f966b61705416e52198b839d0e55f3990e97857d02469de51', '梦境亦是美，醒来亦是空',
        '15900147912@sohu.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '2003-08-03');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('田炎', 'e36db0ae710126d1d798140d6fffc9534f670f5179cd7f3e76c61e68a0f6b150f07daae7616555f0', '梦境亦是美，醒来亦是空',
        '13808781268@3721.net', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2009-01-12');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('东梁亮', 'abb706b23a3bf89b24440fa8109726d06ecddb1105774ec7bef72e664579eea9f793a16cc86f984f', '梦境亦是美，醒来亦是空',
        '15803836020@163.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2007-08-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('尉林', '25f133983ce6c5207aea1975d24f9dd978f5e9cdd1e7b79421008f92dfb772b54c2c48d32641cda8', '梦境亦是美，醒来亦是空',
        '13104611094@yahoo.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '2004-06-27');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('平燕岚', '0ee0586fcc02cdea5621ba3a06097cf4ef4bf7cf9a85aef38062db21fdcb17f248160627bfd10ce4', '梦境亦是美，醒来亦是空',
        '13207994837@0355.net', 'http://localhost/static/image/avatar/avatar(0).jpg', 0, '123', 'hide', '2004-01-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('颛才', '0df0394e60468b71dba2b47d2bbf273bb60419a05af75529ec48c18479fc451ebb121fa9e594948a', '梦境亦是美，醒来亦是空',
        '13804443526@gmail.com', 'http://localhost/static/image/avatar/avatar(4).jpg', 1, '123', 'hide', '1990-01-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('暨寒莺', '9dd7e47582ffd00251a6f1f3009e34b344605265d81cdf5598b15f67ccd88356fcb016a2d337c8ee', '梦境亦是美，醒来亦是空',
        '15302154003@126.com', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1994-09-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('莘栋泽', 'bed42dfaa331f2fa0d03dabe28aef8cde3b99b6e69886b0f72860fe24387d92c1593cbc4f162a501', '梦境亦是美，醒来亦是空',
        '13005174289@live.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '2014-06-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('融香琳', 'bc8db8cbdbbd6a73e758199896eba7d5397a4f42a255ca32a4096327423ca2951636c20416b78952', '梦境亦是美，醒来亦是空',
        '13307477257@qq.com', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2014-06-23');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('於致壮', 'a1c4e2e60dc3a173abf1bcf1cb16042bfda622d516a36ea02cbc09753bcde6fd5ff21e304590e468', '梦境亦是美，醒来亦是空',
        '13301417965@ask.com', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2002-08-14');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('焦凤', '019ca22fbb708169edecb5cbbdb32a6f417099e183426d0e1b09e4d184a0bd17371314c366ceb1fc', '梦境亦是美，醒来亦是空',
        '13306256329@qq.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '1999-05-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('葛河刚', '5f5ac1787e2a082d72ac5b3c1f79aae80bb39148418fd76758d6b647617334663551ebc02afd7db0', '梦境亦是美，醒来亦是空',
        '13308263395@googlemail.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide',
        '2010-05-07');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('寇芝希', 'a7c442e331680f8c61235a755d686f48c159ff7d4aefac7861f16b89f251c8825fb4e0f6ee12cec8', '梦境亦是美，醒来亦是空',
        '15205202731@hotmail.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide',
        '1992-07-05');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宗薇馨', 'a99ece898fc7b7f00c1b2924d6efafb491e96f5ebefcf445d0968fa824abc740ebb695c392696911', '梦境亦是美，醒来亦是空',
        '13107077759@sohu.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '2005-12-29');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('涂婉', '21f6b2708f4a92f4bbeccdf6664142c5437129a59035b2acebe3acaa63cf4817db0b6627ab6989c7', '梦境亦是美，醒来亦是空',
        '13006913015@yahoo.com', 'http://localhost/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '1999-01-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('逄裕', '5f4efc0a3695018f669d49cddcbe4a92abddd67b7edc5f89fad91ada77acb490de8fd2aedd602e81', '梦境亦是美，醒来亦是空',
        '15505815686@qq.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1996-04-27');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('双承利', '8194a2eb55b9b67aa2cc68a397e161cc4a80406bec67513c5209945d3635e112ffd1c85c71e4647a', '梦境亦是美，醒来亦是空',
        '15301965725@0355.net', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2004-01-23');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('皇咏琦', '4a2b9a7b0124a8f48a231a8208d066e1191fe84339942c672d16e63c955e5bfa0f8aac4867a0972b', '梦境亦是美，醒来亦是空',
        '13602400122@qq.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 0, '123', 'hide', '2003-04-08');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('逯楠', 'ec0889e9e15f6853c5e6b342b820eae51f5192149ca2d3a393e9758f38ed528d9315f0856a5849f5', '梦境亦是美，醒来亦是空',
        '13100255491@163.net', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1998-01-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('楚妹冰', '70272118f0b485916a7e87d09853a6e15aeb0b3f98c817308192930c9ccdbf01e05b49bc0824df40', '梦境亦是美，醒来亦是空',
        '13905441770@live.com', 'http://localhost/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '2000-10-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('父珠', 'ca734a88407031d0833951604357c2b32fe2a6b2c0c89bfed45505d053c14bd4b32381bbf5964f16', '梦境亦是美，醒来亦是空',
        '13901066514@sina.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2008-11-09');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宰义', '410b71c275ea8c0efea1cdad5335264e77694f13b373ee59b7381c5e690c5f167b8452741b01eeab', '梦境亦是美，醒来亦是空',
        '13707301937@sina.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2012-08-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('唐维', '736b786dbbb38a63d894bb3c51fd9f9e02940a14d5a14a93775732cbb29d143c3e3de8743d16dbc8', '梦境亦是美，醒来亦是空',
        '13408056175@gmail.com', 'http://localhost/static/image/avatar/avatar(0).jpg', 1, '123', 'hide', '1996-08-12');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宰咏', '482d5de82f5d0834626d83be4f46201d2c19ff83c9edeb295c2835f0c6399ca233c7423e35d6f3d7', '梦境亦是美，醒来亦是空',
        '13404142271@163.net', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2012-09-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('门贞贞', '707afcdc13eb89449248b5408c72c6b63efa8dd66bf6036f4552f511693488aab9bc87b921154325', '梦境亦是美，醒来亦是空',
        '13408748220@163.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1996-06-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('那婵', '0abf57704f27f75aa45ff76f56fc0fc39412265550e1745ba41e84e6041ea352016b2026e3831e04', '梦境亦是美，醒来亦是空',
        '13306056717@163.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '1998-06-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('杭波绍', '0fc4af50c80fbbf7c622ebdb2b636e0e7c9034cda5daeef51a479810871dfde70e5d2a69d2a78816', '梦境亦是美，醒来亦是空',
        '15100674200@yeah.net', 'http://localhost/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '1992-09-18');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('申翠', '355864407ab5c7a3017df12bcbadc9a41ec30e01c0dbe2da38f2652540be5fb0ca25d2f0556ceacc', '梦境亦是美，醒来亦是空',
        '13205334430@ask.com', 'http://localhost/static/image/avatar/avatar(6).jpg', 1, '123', 'hide', '1990-04-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('凌博', 'b508d028bdc53fb9dbac6daf5137ec4cf31a31bf6a969d8a3f22f6675268b74583f5f6b15f0746b5', '梦境亦是美，醒来亦是空',
        '15306637225@googlemail.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 0, '123', 'hide',
        '2010-02-20');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('徐琰', 'fb08d7d3b627e8e723a98dc9c587ab117ab6582b3f9084c8f70c42eb477c09ee3bff0e5335034408', '梦境亦是美，醒来亦是空',
        '13403493904@sohu.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1991-05-04');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宗航', 'b4c827e1ac7f48ccf226e29860fc2e7fd6eaf6f82db980bc94d2aa1918947c39ab0dd2727702a603', '梦境亦是美，醒来亦是空',
        '15703675257@aol.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2001-05-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('有蓓妍', '12312155ce0733e22f638845f0e48fb5032ee00f3b14f7c2846557f1bb2c8d679178000ec267974f', '梦境亦是美，醒来亦是空',
        '13304025380@hotmail.com', 'http://localhost/static/image/avatar/avatar(1).jpg', 0, '123', 'hide',
        '1995-03-21');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('龙浩', '484e5a85a74e41921228fb704fae9795a2b9065ee43b3aff68d778e0e2415f1b71cc3efccda5d531', '梦境亦是美，醒来亦是空',
        '15001835427@sina.com', 'http://localhost/static/image/avatar/avatar(0).jpg', 0, '123', 'hide', '1994-11-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('臧军先', '60c32cefc959ede582a7baddb186e815c0d22e6a8732a5ec27a466967d9a758411f9da8f52b873d9', '梦境亦是美，醒来亦是空',
        '15107493124@aol.com', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2004-01-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('年钧友', 'd9d40e4ecb8c552939554c1be5cdda99e726d96328a3363957ee034d4bd0e3f188d3af8fd805c9c9', '梦境亦是美，醒来亦是空',
        '13207528433@qq.com', 'http://localhost/static/image/avatar/avatar(1).jpg', 1, '123', 'hide', '1994-12-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('褚霄滢', '2b6c29c6b1d23ba61cc8a84e0e9517637c0d8ad94851d0e97daf1ece785294fe855ccdde071aa443', '梦境亦是美，醒来亦是空',
        '13100894652@msn.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2007-05-12');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('平健', '344428edb092619b160760109e98e7cb6db9239ba42f72d7f336c17f671417ce118e28f3f7a03f83', '梦境亦是美，醒来亦是空',
        '13700928788@163.com', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '1995-12-31');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('羊蕊玲', '5e4d20efaaf8d00a0c8593881756f3efae41daf76afee681ae54943d0879cb6d6a66ad675d3e1dc2', '梦境亦是美，醒来亦是空',
        '13200152690@163.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2004-02-25');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('戚坚茂', 'c972f8d0d538d6bd6271d1c4d7a1d5721e6db71c98a8a5fce6ef09a71fbdee4261eabfb098c6441c', '梦境亦是美，醒来亦是空',
        '13207525160@aol.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 1, '123', 'hide', '2006-11-06');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('阳寒', 'f1b535e9b1d7cf927ab618fb1c8949df7f96e53f2149ae0194246d882f862a8c0cc1eaad4d7e6018', '梦境亦是美，醒来亦是空',
        '13105951913@yahoo.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1991-12-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宓之', '840aad7af118e4983debef68cd97ad19101bd8b9769e5071cbe0f6208989428f24112ab8355b2dba', '梦境亦是美，醒来亦是空',
        '15201911216@live.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 0, '123', 'hide', '2006-10-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谈羽', '010758337a5313f568551b18e98abf2c3630e6fae8855aeeb0a259e4194d9abde76205715a7f0093', '梦境亦是美，醒来亦是空',
        '15002600226@hotmail.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide',
        '2011-03-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('梁榕昌', '7040cb03147921762c0178f06762b8174fc504a6a5b30d173e19083e2120f239cfb1df0d9934ad40', '梦境亦是美，醒来亦是空',
        '15708052377@qq.com', 'http://localhost/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '2006-04-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('龚丹飘', 'fc7c5004947f97d7885537f4aa127f40826524c8beccb2118596b3dff8aeed294071f70383e2c5be', '梦境亦是美，醒来亦是空',
        '13407394931@googlemail.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide',
        '2000-09-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('方超山', 'a72953234c09699f65a0add84ce9f64b5c64c385187daf71640ea2e7ea869f2040153a98ddae0f5c', '梦境亦是美，醒来亦是空',
        '15803194907@yeah.net', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '2000-05-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('拔广冠', '6143bcf418cb55a749c0fd0580b7ce1f1d6c2a13269a91e95f2ee8462fede316d11486f530a762bd', '梦境亦是美，醒来亦是空',
        '13501161119@263.net', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '1994-07-15');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('里凝', '6ff49b8fe03e279f36074b2ae9ddde3a57ba71a75c28bd620ef3605a30ad6e31cb2baed2a7e7c295', '梦境亦是美，醒来亦是空',
        '13308445850@163.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '2009-07-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('宋怡', 'b9384f0adbefbee0bce69ad48af275b19ead5ee43e9899fc39a9401fe998130ad236038063499ef2', '梦境亦是美，醒来亦是空',
        '15104496675@3721.net', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2013-07-16');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谭娜伊', 'a0cfc4107e5e0dfb6cf079e08c5b0fc77e1c199c6d6f4232509072190973cec0b374f00fff97df38', '梦境亦是美，醒来亦是空',
        '13603931551@gmail.com', 'http://localhost/static/image/avatar/avatar(2).jpg', 0, '123', 'hide', '1994-01-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('司强勇', '6468216286044673a863e3066508876456feac656dfa332a5cea8c9128b27b9d50222d57778f7c8c', '梦境亦是美，醒来亦是空',
        '15101077205@yeah.net', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '1997-03-10');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('房琴', '024b18d031132002294ac54acc058c3374326c420b3305c517b64149da4753a1a474483b7eaeb508', '梦境亦是美，醒来亦是空',
        '15507700151@hotmail.com', 'http://localhost/static/image/avatar/avatar(1).jpg', 1, '123', 'hide',
        '1993-12-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谢星贵', 'c273a719995a530f64555fc44932574a2197a701765c12e3163363a8772674d42c3a62d9b32bc00e', '梦境亦是美，醒来亦是空',
        '13001633106@yeah.net', 'http://localhost/static/image/avatar/avatar(8).jpg', 1, '123', 'hide', '2013-04-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('狐静茗', '14486aefed5a8908aab4257f95394e5848407e52583151503d7a8f7fb4baaf94968537dcbf8cc7ef', '梦境亦是美，醒来亦是空',
        '13305247316@sina.com', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '2010-07-22');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('薛勤静', 'f20c15d6da5ea8b577f475757fd2306987ee4afcae116e94615c6a0d71f418a567534114a43a3ea7', '梦境亦是美，醒来亦是空',
        '15904345915@3721.net', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '1991-10-17');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谯岩', '228ac93010878506c1ee5cc93e20218aa18c1df7cfcbf0b73663c3845de42b9671ebe444a0d2d2be', '梦境亦是美，醒来亦是空',
        '13808763279@aol.com', 'http://localhost/static/image/avatar/avatar(4).jpg', 1, '123', 'hide', '2015-02-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('言腾弘', 'b9c171837b5a7c043bb73a27df18242c53b94040f291121c8c7177fe559190d77518eeef96a41f9a', '梦境亦是美，醒来亦是空',
        '15107042392@263.net', 'http://localhost/static/image/avatar/avatar(1).jpg', 0, '123', 'hide', '1994-05-30');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('厍之德', '29f27c7e8c4698b1a9624ce60a8bb17be2913091875779650409532217b6a79049ba77acdec3b2b2', '梦境亦是美，醒来亦是空',
        '13103816897@sohu.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 0, '123', 'hide', '1992-02-28');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('汪素', 'e70eff52a4040a370692083ddf17b0ec98779afd3f220d2e55dc6a8e7a6fd7acfeb35d8f8f88025e', '梦境亦是美，醒来亦是空',
        '15206122291@live.com', 'http://localhost/static/image/avatar/avatar(0).jpg', 1, '123', 'hide', '2013-08-05');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('邱芸滢', 'fbe30085cd519567ebf948ea2488f13cfcea16435d76517eb69c0b1c25c58b01aa193e12042afde9', '梦境亦是美，醒来亦是空',
        '13306888302@msn.com', 'http://localhost/static/image/avatar/avatar(9).jpg', 1, '123', 'hide', '2011-02-01');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('钭荷', '0707010c1a16bee60f937d1f88c01933b4d7dc78f3adf548bc5241d48e12c10987f4da71b864af4a', '梦境亦是美，醒来亦是空',
        '15206746459@263.net', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '1998-07-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('倪秋', '60f529cc881596f445b0dc1aa3086406c5470d145b296a49f1ca0b6547fddfa801a9658144e2d358', '梦境亦是美，醒来亦是空',
        '15508373082@163.net', 'http://localhost/static/image/avatar/avatar(0).jpg', 0, '123', 'hide', '2013-08-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('申裕厚', '4a610ccf8475c78201b8814137e85fccbc8a107cf4462568f2936eb1b8149a9e86b1c64a04187de1', '梦境亦是美，醒来亦是空',
        '15700771177@aol.com', 'http://localhost/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '2007-03-23');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('终聪芸', '7fac9e2599f7496e467ca06fc8440acf8b035ae156375df05927e0286e5850da13f6c15484203861', '梦境亦是美，醒来亦是空',
        '15803822828@163.net', 'http://localhost/static/image/avatar/avatar(0).jpg', 1, '123', 'hide', '2011-02-02');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('狄婉艺', '55a9906b88de7382d3d1e49bcb21033f912205e341e67e4606c34b9e66c0d8a5d4b6f11a4ca55e71', '梦境亦是美，醒来亦是空',
        '13504970131@sina.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1995-08-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('公伯', 'd4d8f6733a8d791d88fde4028e2dc8363a91d4e1536c0babf560722fd16e6a977f1337c3985ad8d3', '梦境亦是美，醒来亦是空',
        '15607053770@qq.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1993-01-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('莫聪', '465dccb94fad8c7ad5772b48b655e50b87bf6cf2a1fe56428785cf7161e269c28909d37201f8be25', '梦境亦是美，醒来亦是空',
        '13504271752@yeah.net', 'http://localhost/static/image/avatar/avatar(3).jpg', 0, '123', 'hide', '2000-05-11');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('家茗嘉', '162950ef7b0c3d97b402d924551f4021ca6e879f9456010e0fb4591a5a345599e2de0bc27de42787', '梦境亦是美，醒来亦是空',
        '13705762571@163.net', 'http://localhost/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '1992-09-16');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('商明毅', '41166f3a41052c08cdb6c0dc232fa27611791176608ded94495c75ff8e86b773aec5833099be91b7', '梦境亦是美，醒来亦是空',
        '15008175682@aol.com', 'http://localhost/static/image/avatar/avatar(4).jpg', 1, '123', 'hide', '1997-10-14');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('卞羽', '9a768c720e3214ce0131eb34f26fb5b27bef50d3989fc8d51caf772ec41385e4a913e8379bc42c92', '梦境亦是美，醒来亦是空',
        '15904301500@qq.com', 'http://localhost/static/image/avatar/avatar(5).jpg', 1, '123', 'hide', '2006-02-13');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('谢卿锦', '230b8462ec4bddfee4c1f99852f526cabb4f4961a07f4cb2dc8b4c79883488d2f69b5889d797b980', '梦境亦是美，醒来亦是空',
        '15802335560@ask.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 0, '123', 'hide', '1992-11-26');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('马风震', 'a9618d39d049b7d8facc7d6299860a3516ee649b43cb5e1ff5092351fa58878d2176f7b6fa098775', '梦境亦是美，醒来亦是空',
        '15908510852@263.net', 'http://localhost/static/image/avatar/avatar(2).jpg', 1, '123', 'hide', '1995-03-24');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('赵毓', 'c2b5bef53070a76d98d582e419ad44f498ebfa2374505e00ae35c527843b48a8409549531347ca90', '梦境亦是美，醒来亦是空',
        '15600620712@msn.com', 'http://localhost/static/image/avatar/avatar(7).jpg', 1, '123', 'hide', '2012-06-16');
insert into t_user(username, password, sign, email, avatar, sex, active, status, create_date)
values ('卞豪', 'e3f2c5a2d73492475078630580d782f5526c5eeddb102151a2e8506b8de95c7fb0e40d8a7ce91b23', '梦境亦是美，醒来亦是空',
        '15602253303@yeah.net', 'http://localhost/static/image/avatar/avatar(8).jpg', 0, '123', 'hide', '2015-12-08');

-- 给每个人初始化默认的好友列表
insert into `t_friend_group` (uid, group_name)
select id, '我的好友'
from t_user
