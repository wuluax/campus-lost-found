/*
 Navicat Premium Dump SQL

 Source Server         : 本地docker8.0
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : 127.0.0.1:3307
 Source Schema         : school_lost_found

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 15/04/2026 15:21:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for slf_ai_message
-- ----------------------------
DROP TABLE IF EXISTS `slf_ai_message`;
CREATE TABLE `slf_ai_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '0为用户,1为AI',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '内容',
  `item_id` bigint NULL DEFAULT NULL COMMENT '若ai发送物品链接，存物品ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '如果是图片识图，存图片地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 462 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'AI对话记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_ai_message
-- ----------------------------
INSERT INTO `slf_ai_message` VALUES (1, 1, '0', '你好，我今天在西饭二楼吃饭，走的时候把苹果手表忘在桌子上了，表带是黑色的，帮我找找', NULL, '', '2026-02-10 11:02:21');
INSERT INTO `slf_ai_message` VALUES (2, 1, '1', '你好，根据你提供的信息在招领中为你找到了3个可能相关的物品，请查看下方卡片。', NULL, NULL, '2026-02-10 11:02:27');
INSERT INTO `slf_ai_message` VALUES (3, 1, '1', '{\"score\":95}', 2, NULL, '2026-02-10 11:02:27');
INSERT INTO `slf_ai_message` VALUES (4, 1, '1', '{\"score\":60}', 8, NULL, '2026-02-10 11:02:27');
INSERT INTO `slf_ai_message` VALUES (5, 1, '1', '{\"score\":55}', 6, NULL, '2026-02-10 11:02:27');
INSERT INTO `slf_ai_message` VALUES (6, 1, '0', '请帮我匹配这个物品', 1, NULL, '2026-02-10 11:03:38');
INSERT INTO `slf_ai_message` VALUES (7, 1, '1', '你好，根据你选择的物品为你找到了1个可能相关的物品，请查看下方卡片。', NULL, NULL, '2026-02-10 11:03:40');
INSERT INTO `slf_ai_message` VALUES (8, 1, '1', '{\"score\":95}', 2, NULL, '2026-02-10 11:03:40');
INSERT INTO `slf_ai_message` VALUES (15, 5, '0', '请帮我匹配这个物品', 5, NULL, '2026-02-10 14:21:10');
INSERT INTO `slf_ai_message` VALUES (16, 5, '1', '你好，根据你选择的物品为你找到了3个可能相关的物品，请查看下方卡片。', NULL, NULL, '2026-02-10 14:21:12');
INSERT INTO `slf_ai_message` VALUES (17, 5, '1', '{\"score\":85}', 8, NULL, '2026-02-10 14:21:12');
INSERT INTO `slf_ai_message` VALUES (18, 5, '1', '{\"score\":45}', 6, NULL, '2026-02-10 14:21:12');
INSERT INTO `slf_ai_message` VALUES (19, 5, '1', '{\"score\":30}', 7, NULL, '2026-02-10 14:21:12');
INSERT INTO `slf_ai_message` VALUES (43, 2, '0', '在西区食堂二层捡到一个iwatch，表带是黑色的，没电了', NULL, '', '2026-02-10 14:34:17');
INSERT INTO `slf_ai_message` VALUES (44, 2, '1', '你好，根据你提供的信息在失物中为你找到了1个可能相关的物品，请查看下方卡片。', NULL, NULL, '2026-02-10 14:34:22');
INSERT INTO `slf_ai_message` VALUES (45, 2, '1', '{\"score\":95}', 1, NULL, '2026-02-10 14:34:22');
INSERT INTO `slf_ai_message` VALUES (63, 3, '0', '我丢了', NULL, 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/ai/8e2ca2867cf9499e8a57ea8a7a961ba6.png', '2026-02-10 15:06:31');
INSERT INTO `slf_ai_message` VALUES (64, 3, '1', '你好，根据你提供的信息在招领中为你找到了1个可能相关的物品，请查看下方卡片。', NULL, NULL, '2026-02-10 15:06:39');
INSERT INTO `slf_ai_message` VALUES (65, 3, '1', '{\"score\":95}', 12, NULL, '2026-02-10 15:06:39');

-- ----------------------------
-- Table structure for slf_category
-- ----------------------------
DROP TABLE IF EXISTS `slf_category`;
CREATE TABLE `slf_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_category
-- ----------------------------
INSERT INTO `slf_category` VALUES (1, '证件卡片', 1, '2025-11-20 17:12:11', '2026-01-21 10:29:18');
INSERT INTO `slf_category` VALUES (2, '电子数码', 1, '2025-11-20 17:12:11', '2026-01-21 10:29:42');
INSERT INTO `slf_category` VALUES (3, '日用品', 1, '2025-11-20 17:12:11', '2026-01-21 10:29:47');
INSERT INTO `slf_category` VALUES (4, '书籍文具', 1, '2025-11-20 17:12:11', '2026-01-21 10:30:01');
INSERT INTO `slf_category` VALUES (5, '书包提包', 1, '2025-11-20 17:12:11', '2026-01-21 10:30:11');
INSERT INTO `slf_category` VALUES (6, '衣物饰品', 1, '2025-11-20 17:12:11', '2026-01-21 10:30:24');
INSERT INTO `slf_category` VALUES (7, '运动器材', 1, '2025-11-20 17:12:11', '2026-01-21 10:30:25');
INSERT INTO `slf_category` VALUES (8, '化妆护肤品', 1, '2025-11-20 17:12:11', '2026-01-21 10:30:26');
INSERT INTO `slf_category` VALUES (9, '钥匙', 1, '2025-11-20 17:12:11', '2026-01-21 10:30:38');
INSERT INTO `slf_category` VALUES (10, '其他', 1, '2026-01-20 10:13:57', '2026-01-21 10:30:44');
INSERT INTO `slf_category` VALUES (1010, '测试', 0, '2026-01-22 16:39:00', '2026-01-22 16:39:31');

-- ----------------------------
-- Table structure for slf_clue
-- ----------------------------
DROP TABLE IF EXISTS `slf_clue`;
CREATE TABLE `slf_clue`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_id` bigint NOT NULL COMMENT '物品id',
  `user_id` bigint NOT NULL COMMENT '线索提供人id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '线索内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '线索时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_clue
-- ----------------------------
INSERT INTO `slf_clue` VALUES (1, 1, 1, '补充一下：我的表盘背面有一道很浅的划痕', '2026-02-09 13:20:00');
INSERT INTO `slf_clue` VALUES (2, 1, 9, '同学，我看刚才有个同学发了招领，也是黑色苹果手表，地点也对得上，快去看看！', '2026-02-09 13:40:00');
INSERT INTO `slf_clue` VALUES (3, 9, 2, '林同学，你的证件我捡到了，我已经交到中饭二楼的经理办公室了。', '2026-01-25 15:40:00');
INSERT INTO `slf_clue` VALUES (4, 13, 5, '这个杯子底部贴了一个火影忍者的贴纸，如果有看到的同学麻烦联系我！', '2026-01-28 14:00:00');
INSERT INTO `slf_clue` VALUES (5, 15, 7, 'U盘里根目录下有一个文件夹叫“2026毕业设计”，这是我非常重要的资料。', '2026-02-07 18:30:00');
INSERT INTO `slf_clue` VALUES (6, 15, 8, '我是招领人，刚才试了一下U盘，里面的确有毕设文件夹，请联系我确认领取。', '2026-02-07 19:00:00');
INSERT INTO `slf_clue` VALUES (7, 21, 11, '请问这台手机的锁屏壁纸是不是一只金渐层猫咪？如果是的话就是我的！', '2026-01-04 15:20:00');
INSERT INTO `slf_clue` VALUES (8, 22, 6, '我把鼠标放到了3实301的讲台抽屉里了，没锁，你自己去拿就行。', '2026-02-04 16:00:00');
INSERT INTO `slf_clue` VALUES (9, 30, 6, '这支笔是长辈送的礼物，笔尖是EF尖，如果是找回来的同学，我愿意请喝一周奶茶！', '2026-02-02 10:00:00');
INSERT INTO `slf_clue` VALUES (10, 31, 12, '笔袋里是不是还有一把红色的美工刀？我有把类似的在3教丢了。', '2026-01-20 20:00:00');
INSERT INTO `slf_clue` VALUES (12, 2, 1, '11', '2026-03-23 11:22:26');
INSERT INTO `slf_clue` VALUES (13, 1, 2, '1', '2026-04-13 17:57:20');

-- ----------------------------
-- Table structure for slf_conversation
-- ----------------------------
DROP TABLE IF EXISTS `slf_conversation`;
CREATE TABLE `slf_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_a_id` bigint NOT NULL COMMENT '用户A（较小ID）',
  `user_b_id` bigint NOT NULL COMMENT '用户B（较大ID）',
  `last_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '最后一条消息内容',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后一条消息时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_pair`(`user_a_id` ASC, `user_b_id` ASC) USING BTREE,
  INDEX `idx_user_a`(`user_a_id` ASC) USING BTREE,
  INDEX `idx_user_b`(`user_b_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '私信会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slf_conversation
-- ----------------------------
INSERT INTO `slf_conversation` VALUES (1, 1, 2, '你好', '2026-04-13 17:41:03', '2026-04-13 17:41:03', '2026-04-13 17:41:03');
INSERT INTO `slf_conversation` VALUES (2, 1, 8, '在吗', '2026-04-13 18:13:38', '2026-04-13 18:13:38', '2026-04-13 18:13:38');
INSERT INTO `slf_conversation` VALUES (3, 1, 4, '应该是我的', '2026-04-13 18:13:50', '2026-04-13 18:13:50', '2026-04-13 18:13:50');

-- ----------------------------
-- Table structure for slf_favorite
-- ----------------------------
DROP TABLE IF EXISTS `slf_favorite`;
CREATE TABLE `slf_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_user_item`(`user_id` ASC, `item_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_item`(`item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_favorite
-- ----------------------------
INSERT INTO `slf_favorite` VALUES (1, 1, 2, '2026-01-18 10:30:00');
INSERT INTO `slf_favorite` VALUES (2, 1, 10, '2026-01-25 15:45:00');
INSERT INTO `slf_favorite` VALUES (3, 3, 4, '2026-01-21 09:15:00');
INSERT INTO `slf_favorite` VALUES (4, 3, 12, '2026-02-06 11:00:00');
INSERT INTO `slf_favorite` VALUES (5, 3, 20, '2026-02-01 18:20:00');
INSERT INTO `slf_favorite` VALUES (6, 5, 8, '2026-02-04 18:05:00');
INSERT INTO `slf_favorite` VALUES (7, 5, 14, '2026-01-28 13:50:00');
INSERT INTO `slf_favorite` VALUES (8, 7, 16, '2026-02-07 19:10:00');
INSERT INTO `slf_favorite` VALUES (9, 2, 1, '2026-01-17 16:00:00');
INSERT INTO `slf_favorite` VALUES (10, 4, 3, '2026-01-21 07:30:00');
INSERT INTO `slf_favorite` VALUES (11, 6, 30, '2026-02-02 09:00:00');
INSERT INTO `slf_favorite` VALUES (12, 8, 24, '2026-02-09 08:00:00');
INSERT INTO `slf_favorite` VALUES (13, 1, 25, '2026-01-31 10:00:00');

-- ----------------------------
-- Table structure for slf_item
-- ----------------------------
DROP TABLE IF EXISTS `slf_item`;
CREATE TABLE `slf_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '发布人id',
  `type` tinyint NOT NULL COMMENT '类型 0失物 1招领',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类id',
  `location_id` bigint NULL DEFAULT NULL COMMENT '地点id',
  `item_time` datetime NULL DEFAULT NULL COMMENT '物品发生时间',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物品照片',
  `contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常 2完成',
  `ai_tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'AI特征',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_type`(`type` ASC) USING BTREE,
  INDEX `idx_item_category`(`category_id` ASC) USING BTREE,
  INDEX `idx_item_location`(`location_id` ASC) USING BTREE,
  INDEX `idx_item_item_time`(`item_time` ASC) USING BTREE,
  INDEX `idx_item_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_item_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '物品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_item
-- ----------------------------
INSERT INTO `slf_item` VALUES (1, 1, 0, '在西饭二楼吃饭，走的时候把苹果手表忘在桌子上了，表带是黑色的，很着急。', 2, 4, '2026-02-08 11:40:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/18a8c7c24f014c5980fc5c5a8e849070.jpg', '12345678901', 1, '黑色表带,苹果手表,西饭二楼,食堂,电子数码,遗落在桌子上,表盘未亮起', '2026-02-08 13:10:05', '2026-02-10 10:38:43');
INSERT INTO `slf_item` VALUES (2, 2, 1, '在西区食堂捡到一个iwatch，黑色表带，失主请联系我', 2, 4, '2026-02-08 12:00:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/a007cc6b82c44eb19df3b82b4081adba.jpg', 'qq87219934', 1, '电子设备,智能手表,苹果品牌,黑色表带,方形屏幕,金属边框,运动模式,健康监测,GPS定位,防水功能', '2026-02-08 12:05:19', '2026-02-10 10:21:44');
INSERT INTO `slf_item` VALUES (3, 3, 0, '在1教302上完课，高数课本丢了，绿色封面的，里面夹着笔记本。', 4, 3, '2026-01-18 23:29:54', '', '微信Tests666', 1, '绿色封面,高数课本,夹带笔记本,教材,学习资料,丢失物品,教室物品,教学用书,大学教材', '2026-01-20 23:29:54', '2026-02-09 17:03:43');
INSERT INTO `slf_item` VALUES (4, 4, 1, '1教302教室讲台捡到一本高等数学教材，里面还有本笔记本。', 4, 3, '2026-01-21 06:41:18', '', '12345678901', 1, '教材,笔记本,高等数学,讲台,教室,教学楼,书籍,文具,数学教材', '2026-01-21 08:41:18', '2026-02-09 17:04:21');
INSERT INTO `slf_item` VALUES (5, 5, 0, '在图书馆自习，走的时候雨伞找不到了，粉色折叠伞，赛维纳牌的。', 3, 6, '2026-01-13 20:43:44', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/4f300a76039a4ed7a8e2b73d4bcb816e.jpg', 'qq44558822', 1, '粉色,折叠,雨伞,赛维纳牌,图书馆,丢失,自习', '2026-01-14 16:43:44', '2026-02-09 17:47:14');
INSERT INTO `slf_item` VALUES (6, 6, 1, '图书馆大厅捡到一把黑色长柄雨伞。', 3, 6, '2026-02-01 05:53:48', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/c9a5ab3af5584ec091b0d3288043daaa.jpg', '微信test_666', 1, '黑色雨伞,长柄,图书馆大厅,日用品,丢失物,灰色大理石地面,白色贴纸,黑色带子', '2026-02-03 05:53:48', '2026-02-09 17:47:39');
INSERT INTO `slf_item` VALUES (7, 7, 1, '图书馆阅览室捡到一个粉色保温杯，膳魔师的。', 3, 6, '2026-02-02 18:08:33', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/ce5e224209974d4091267cf5675149bb.jpg', '12345678901', 1, '保温杯,THERMOS,粉色,图书馆,阅览室,日用品', '2026-02-04 00:08:33', '2026-02-09 17:48:11');
INSERT INTO `slf_item` VALUES (8, 8, 1, '图书馆二楼书架旁发现一把粉色小雨伞。', 3, 6, '2026-02-04 11:08:47', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/51fe6182bf73453da50fe35cec639b36.jpg', 'qq100200300', 1, '粉色雨伞,图书馆,二楼,书架,小雨伞,日用品,学习,阅读,学术,书籍', '2026-02-04 17:08:47', '2026-02-09 17:48:50');
INSERT INTO `slf_item` VALUES (9, 1, 0, '在中饭食堂吃完饭，身份证丢了，后四位是3554，姓名林铠彦。', 1, 4, '2026-01-25 12:30:00', '', '12345678901', 1, '身份证,卡片,林铠彦,3554,食堂,证件,丢失,后四位,姓名,个人身份', '2026-01-25 14:00:00', '2026-02-09 16:55:05');
INSERT INTO `slf_item` VALUES (10, 2, 1, '中饭外面洗手台捡到一张身份证，名字叫林铠彦。', 1, 4, '2026-01-25 13:00:00', '', 'qq87219934', 1, '身份证,卡片,个人信息,姓名,林铠彦,塑料材质,标准尺寸,文字信息,照片,防伪特征', '2026-01-25 15:30:00', '2026-02-09 16:55:44');
INSERT INTO `slf_item` VALUES (11, 3, 0, '在东体篮球场把篮球落下了，是斯伯丁的，照片如图。', 7, 5, '2026-02-05 18:00:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/269a76f16b9c427f880bebc903799836.png', '微信Tests666', 1, '篮球,斯伯丁,棕色,有黑色条纹,室外,皮革材质,运动,球类,圆形,品牌logo', '2026-02-05 20:00:00', '2026-02-09 16:52:02');
INSERT INTO `slf_item` VALUES (12, 4, 1, '东区篮架下捡到一个篮球，我先带回宿舍了。', 7, 5, '2026-02-06 08:00:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/fdbf67e093744c4ca041e1d07f3d235e.png', '12345678901', 1, '篮球,棕色,斯伯丁品牌,有黑色条纹,放在水泥地上,蓝白色油漆斑驳,有白色线条,在室外球场。', '2026-02-06 09:00:00', '2026-02-09 16:51:45');
INSERT INTO `slf_item` VALUES (13, 5, 0, '在3教405落下了象印的黑色保温杯，有谁看到了嘛。', 3, 3, '2026-01-28 10:00:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/3ce96eca7d8f4b448181906b2bef3742.png', 'qq44558822', 1, '保温杯,黑色,金属质感,按钮式开盖,品牌标志,贴纸,密封圈,象印logo,3教405', '2026-01-28 12:00:00', '2026-02-09 16:46:21');
INSERT INTO `slf_item` VALUES (14, 6, 1, '在3教405最后一排捡到一个黑色杯子。', 3, 3, '2026-01-28 11:30:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/e320475fd27f464c8a1f11e0f6dc1a8a.png', '微信test_666', 1, '保温杯,ZOJIRUSHI,黑色,金属质感,按钮,便携式,水杯,3教405,教室,失物招领', '2026-01-28 13:00:00', '2026-02-09 16:44:57');
INSERT INTO `slf_item` VALUES (15, 7, 0, '我的金士顿U盘丢在图书馆电子阅览室了，银色的和图片一样，挂着红色绳子。', 2, 6, '2026-02-07 15:00:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/d888c1786fa44553a88a6aeab807f145.png', '12345678901', 1, '金属质感,U盘,Kingston品牌,银色,挂绳设计,图书馆,电子设备,数据存储,便携式,阅读室', '2026-02-07 16:00:00', '2026-02-09 16:41:57');
INSERT INTO `slf_item` VALUES (16, 8, 1, '图书馆电脑上拔下来一个银色U盘，有个红绳子。', 2, 6, '2026-02-07 17:00:00', '', 'qq100200300', 1, '银色,红绳,U盘,电子数码,便携存储,图书馆,USB设备,数据存储,电脑配件', '2026-02-07 18:00:00', '2026-02-09 16:41:05');
INSERT INTO `slf_item` VALUES (17, 1, 0, '东体丢了把羽毛球拍，李宁的，长图片这个样子', 7, 5, '2026-01-27 09:45:48', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/08f7cc07510e4a30bf4522c736a3964f.png', '18998959333', 1, '李宁羽毛球拍,红色白色相间,黑色手柄,白色线,红色包,黑色带子。', '2026-01-29 01:45:48', '2026-02-09 15:42:59');
INSERT INTO `slf_item` VALUES (18, 2, 1, '在西操看台那捡到一张建设银行卡，黄色的。', 1, 5, '2026-01-29 23:54:17', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/344c23fc4ccb4c36a1f4f2eda0fc180c.png', 'qq87219934', 1, '建设银行卡,黄色,龙卡通储蓄卡,芯片,62170004,77772,银联,UnionPay,DEBIT,CCB NM', '2026-01-31 11:54:17', '2026-02-09 15:46:26');
INSERT INTO `slf_item` VALUES (19, 3, 0, '在健身房丢了AirPods Pro白色充电仓，哪个好心人看到了T_T', 2, 5, '2026-01-26 04:12:22', '', '微信Tests666', 1, '白色,充电仓,AirPods Pro,电子数码,耳机配件,运动场馆,健身房,丢失物品,苹果产品,便携式', '2026-01-26 12:12:22', '2026-02-09 16:29:25');
INSERT INTO `slf_item` VALUES (20, 4, 1, '新综门口捡到一张水卡，卡号后四位是8802', 1, 11, '2026-01-31 13:40:34', '', '12345678901', 1, '水卡,电子卡,卡号8802,校园卡,消费卡,塑料材质,卡片形状,校园一卡通,学生证,充值卡', '2026-02-01 16:40:34', '2026-03-23 11:16:51');
INSERT INTO `slf_item` VALUES (21, 5, 0, '谁华为Mate60黑色手机落在东区操场了，带黑色皮壳。', 2, 5, '2026-01-02 11:47:40', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/cf54117f72004eedb7afed434043cee9.png', 'qq44558822', 1, '华为Mate60,黑色手机,带黑色皮壳,圆形摄像头,后置三摄,NFC功能,蓝牙5.2,OLED屏幕,曲面屏,安卓13系统', '2026-01-04 11:47:40', '2026-02-09 16:16:41');
INSERT INTO `slf_item` VALUES (22, 6, 1, '在3实301捡到一个罗技无线鼠标，型号应该是g502hero，带贴纸。', 2, 3, '2026-02-03 20:35:48', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/8e33d474f4284fb9a68e7cb218ece341.png', '微信test_666', 1, '电子设备,游戏外设,人体工学设计,可编程按钮,RGB灯光,无线连接,型号G502hero,罗技Logo,贴纸装饰', '2026-02-04 14:35:48', '2026-02-09 16:09:09');
INSERT INTO `slf_item` VALUES (23, 7, 0, '黑色耐克运动外套落在运动场了，回去找不见了，拉链坏了的，有谁看到联系我谢谢。', 6, 5, '2026-01-21 12:13:13', '', '12345678901', 1, '黑色,耐克,运动外套,拉链损坏,运动场,丢失,衣物,外套,运动品牌', '2026-01-22 11:13:13', '2026-02-09 15:59:16');
INSERT INTO `slf_item` VALUES (24, 8, 1, '在东饭捡到一个黑色双肩包，里面有书和雨伞，我放到失物处了', 5, 4, '2026-02-07 23:43:51', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/1d5d95d8e87e424390f8bcf10cc0ec7e.jpg', '东饭失物处', 1, '黑色,双肩包,书包提包,食堂,失物处,书,雨伞', '2026-02-09 06:37:31', '2026-02-09 15:50:28');
INSERT INTO `slf_item` VALUES (25, 1, 1, '西区公园捡到一条灰色围巾，羊毛的。', 6, 1, '2026-01-29 13:14:31', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/bd6a2b22733e4be6b6fe3a7694abb503.jpg', '12345678901', 2, '羊毛混纺,灰色,长方形,编织纹理,细密针脚,柔软舒适,可穿戴,配饰,冬季,保暖', '2026-01-30 14:14:31', '2026-02-10 10:38:39');
INSERT INTO `slf_item` VALUES (26, 2, 1, '在西8和西9中间捡到一串钥匙，挂着玩偶，我放到西8宿管那了', 9, 8, '2026-01-17 04:38:37', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/8b0279d84ce340c982a20ae396a3f8af.png', '放西8宿管保管了', 1, '钥匙,玩偶,宿舍楼,西8,西9,宿管处', '2026-01-18 02:38:37', '2026-02-09 15:49:58');
INSERT INTO `slf_item` VALUES (27, 3, 1, '在茶百道外面桌子捡了一把车钥匙，雅迪的。', 9, 1, '2026-02-03 20:06:01', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/0a110af9e1ce4ac4af9269a0c405b05e.png', '微信Tests666', 1, '黑色,,茶白道,椭圆形,雅迪,钥匙扣,车钥匙', '2026-02-05 18:06:01', '2026-02-09 16:28:18');
INSERT INTO `slf_item` VALUES (28, 4, 1, '行政楼面前草坪捡到一个透明文件袋，里面有一些马原复习资料。', 4, 10, '2026-01-13 04:49:25', '', '12345678901', 1, '透明文件袋,马原复习资料,书籍文具,行政楼草坪,学习资料,透明包装,纸质资料,高等教育,思想政治理论,校园遗失物品', '2026-01-14 02:49:25', '2026-02-09 16:19:43');
INSERT INTO `slf_item` VALUES (29, 5, 1, '在2教捡到一个灰色笔记本，上面有高数、线代等很多笔记', 4, 3, '2026-01-21 06:47:16', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/4650a72d981144deb1d7e3d4d6a4e556.png', 'qq44558822', 1, '灰色,笔记本,高数,线代,笔记,书籍,文具,2教,教学楼', '2026-01-21 17:47:16', '2026-02-09 16:14:28');
INSERT INTO `slf_item` VALUES (30, 6, 0, '在图书馆丢了一只凌美钢笔，金色的，这支笔对我来说很重要，大家帮忙找找！有偿', 4, 6, '2026-01-30 22:23:33', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/a4e6816548994818a7fc32add2c4995a.png', '微信test_666', 1, '金色,钢笔,图书馆,丢失,重要,有偿,凌美,笔尖,笔帽,书写工具', '2026-02-01 20:23:33', '2026-02-09 16:00:34');
INSERT INTO `slf_item` VALUES (31, 7, 1, '捡到一个笔袋，蓝色帆布的。', 4, 3, '2026-01-18 22:14:48', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/a978eb9e309245198dafd48d64ddc03c.png', '12345678901', 1, '蓝色帆布笔袋,教学楼里捡到,有黑色网状部分,拉链设计,MOVEMENT品牌标志,LET US BE TOGETHER标语', '2026-01-20 17:14:48', '2026-02-09 15:56:47');
INSERT INTO `slf_item` VALUES (32, 8, 0, '在7教301丢了一盒晨光水笔，有谁看到了嘛！', 4, 3, '2026-01-08 10:35:03', '', 'qq100200300', 1, '水笔,晨光品牌,蓝色,黑色,红色,绿色,塑料笔杆,金属笔尖,笔盒,学生用品', '2026-01-09 15:35:03', '2026-02-09 15:37:19');
INSERT INTO `slf_item` VALUES (35, 1, 0, '在综合楼2楼丢了这个！', 10, 1, '2026-03-23 12:20:00', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/item/0db22228f9324191a0037607eb6fe4a2.jpg', '', 1, '雨伞，粉色，萨维娜，品牌标志', '2026-03-23 12:20:36', '2026-04-13 09:10:15');

-- ----------------------------
-- Table structure for slf_location
-- ----------------------------
DROP TABLE IF EXISTS `slf_location`;
CREATE TABLE `slf_location`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地点名称',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40706 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '地点表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_location
-- ----------------------------
INSERT INTO `slf_location` VALUES (1, '全校范围', 1, '2026-01-21 11:35:00', '2026-01-21 13:48:36');
INSERT INTO `slf_location` VALUES (2, '神岗实训基地', 1, '2026-01-21 11:34:56', '2026-01-21 13:48:37');
INSERT INTO `slf_location` VALUES (3, '教学楼', 1, '2026-01-21 11:34:09', '2026-01-21 13:48:39');
INSERT INTO `slf_location` VALUES (4, '食堂', 1, '2026-01-21 11:34:16', '2026-01-21 13:48:40');
INSERT INTO `slf_location` VALUES (5, '运动场馆', 1, '2026-01-21 11:34:23', '2026-01-21 13:48:43');
INSERT INTO `slf_location` VALUES (6, '图书馆', 1, '2025-11-20 01:57:12', '2026-01-21 13:48:49');
INSERT INTO `slf_location` VALUES (7, '新综合楼', 1, '2025-11-20 01:56:43', '2026-01-21 13:48:53');
INSERT INTO `slf_location` VALUES (8, '宿舍楼', 1, '2026-01-21 11:34:36', '2026-01-21 13:48:54');
INSERT INTO `slf_location` VALUES (9, '聚贤楼', 1, '2026-01-21 11:34:42', '2026-01-21 13:48:56');
INSERT INTO `slf_location` VALUES (10, '行政楼', 1, '2025-11-20 01:57:02', '2026-01-21 13:48:57');
INSERT INTO `slf_location` VALUES (11, '商业中心', 1, '2025-11-20 01:57:26', '2026-01-21 13:49:00');
INSERT INTO `slf_location` VALUES (12, '学生活动中心', 1, '2025-11-20 01:57:38', '2026-01-21 13:49:00');
INSERT INTO `slf_location` VALUES (13, '车站', 1, '2025-11-20 01:57:47', '2026-01-21 13:49:01');
INSERT INTO `slf_location` VALUES (14, '音乐楼', 1, '2025-11-20 01:58:05', '2026-01-21 13:49:02');
INSERT INTO `slf_location` VALUES (15, '校医室', 1, '2025-11-20 01:58:12', '2026-01-21 13:49:04');
INSERT INTO `slf_location` VALUES (16, '礼贤楼', 1, '2025-11-20 01:58:18', '2026-01-21 13:49:04');
INSERT INTO `slf_location` VALUES (17, '专家楼', 1, '2025-11-20 01:58:24', '2026-01-21 13:49:07');
INSERT INTO `slf_location` VALUES (40705, '测试', 0, '2026-01-22 16:39:05', '2026-01-22 16:39:05');

-- ----------------------------
-- Table structure for slf_notification
-- ----------------------------
DROP TABLE IF EXISTS `slf_notification`;
CREATE TABLE `slf_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型：clue=新线索, favorite=被收藏, message=新私信, system=系统通知',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知内容摘要',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联对象ID（物品ID/消息ID等）',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '触发者用户ID',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_user_time`(`user_id` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slf_notification
-- ----------------------------
INSERT INTO `slf_notification` VALUES (1, 1, 'clue', '你的物品收到了新线索', '1', 1, 2, 0, '2026-04-13 17:57:20');
INSERT INTO `slf_notification` VALUES (2, 8, 'message', 'wulua 给你发了一条私信', '在吗', 2, 1, 0, '2026-04-13 18:13:38');
INSERT INTO `slf_notification` VALUES (3, 4, 'message', 'wulua 给你发了一条私信', '应该是我的', 3, 1, 0, '2026-04-13 18:13:50');

-- ----------------------------
-- Table structure for slf_private_message
-- ----------------------------
DROP TABLE IF EXISTS `slf_private_message`;
CREATE TABLE `slf_private_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL COMMENT '所属会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者用户ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0=未读，1=已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '私信消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slf_private_message
-- ----------------------------
INSERT INTO `slf_private_message` VALUES (1, 1, 1, 2, '你好', 0, '2026-04-13 17:41:03');
INSERT INTO `slf_private_message` VALUES (2, 2, 1, 8, '在吗', 0, '2026-04-13 18:13:38');
INSERT INTO `slf_private_message` VALUES (3, 3, 1, 4, '应该是我的', 0, '2026-04-13 18:13:50');

-- ----------------------------
-- Table structure for slf_user
-- ----------------------------
DROP TABLE IF EXISTS `slf_user`;
CREATE TABLE `slf_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `role` tinyint(1) NOT NULL DEFAULT 0 COMMENT '角色 0:普通用户 1:管理员',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of slf_user
-- ----------------------------
INSERT INTO `slf_user` VALUES (1, '12345678910', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', 'wulua', '12345678', '大帅哥', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/19e33021ced54cd89ebe98410ba8a8c4.jpg', 1, 1, '2025-11-19 05:37:22', '2026-04-13 19:05:42');
INSERT INTO `slf_user` VALUES (2, '02000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', 'cccccz', '', '', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/317ea9dbd8144fef90a53d13ea4553dc.jpg', 1, 1, '2025-11-20 10:24:16', '2026-04-13 17:57:11');
INSERT INTO `slf_user` VALUES (3, '03000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', 'Julian', '', '', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/26c220e529bf43979681952c28356f53.jpg', 0, 1, '2025-12-03 14:21:36', '2026-03-23 15:16:01');
INSERT INTO `slf_user` VALUES (4, '04000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '森', '', '', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/b74fc3ec0f0f4f03825b11ed79f0e6e6.jpg', 0, 1, '2025-12-03 14:19:27', '2026-02-09 17:03:59');
INSERT INTO `slf_user` VALUES (5, '05000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', 'dllala', NULL, NULL, 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/6d23d8ee6f18485584bc5fd88fab2655.jpg', 0, 1, '2025-12-02 17:33:09', '2026-04-13 09:46:01');
INSERT INTO `slf_user` VALUES (6, '06000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', 'owen', NULL, NULL, 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/dfb3dd2cb80a4d68868e9b461db8b42d.jpg', 0, 1, '2025-12-01 15:22:58', '2026-02-09 17:47:21');
INSERT INTO `slf_user` VALUES (7, '07000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', 'Zooixx', NULL, NULL, 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/d84dcdcba8f44b45947724e81f7a2098.jpg', 0, 1, '2025-11-26 15:09:24', '2026-02-09 17:47:50');
INSERT INTO `slf_user` VALUES (8, '08000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '薯条', NULL, NULL, 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/8240c642950b4e4abeed7cd19c3f17de.jpg', 1, 1, '2025-12-03 14:31:48', '2026-02-10 14:51:43');
INSERT INTO `slf_user` VALUES (9, '09000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '小八', NULL, '', 'https://school-lost-found-img.oss-cn-guangzhou.aliyuncs.com/avatar/ee2bb8008d2a44aa85bcf6af2dc11245.jpg', 0, 1, '2025-12-02 17:33:52', '2026-02-10 14:58:09');
INSERT INTO `slf_user` VALUES (10, '10000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '零零零', NULL, NULL, NULL, 0, 0, '2025-12-04 14:26:08', '2026-02-10 14:51:46');
INSERT INTO `slf_user` VALUES (11, '11000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '陈一', '', NULL, NULL, 0, 1, '2026-01-08 18:26:08', '2026-02-09 15:23:50');
INSERT INTO `slf_user` VALUES (12, '12000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '刘二', NULL, NULL, NULL, 0, 1, '2026-01-08 18:26:58', '2026-02-09 15:23:53');
INSERT INTO `slf_user` VALUES (13, '13000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '张三', NULL, NULL, NULL, 0, 1, '2026-01-08 18:30:49', '2026-02-09 15:23:55');
INSERT INTO `slf_user` VALUES (14, '14000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '李四', NULL, NULL, NULL, 0, 1, '2026-01-09 14:08:13', '2026-02-09 15:23:56');
INSERT INTO `slf_user` VALUES (15, '15000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '王五', NULL, NULL, NULL, 0, 1, '2026-01-09 14:08:20', '2026-02-09 15:23:58');
INSERT INTO `slf_user` VALUES (16, '16000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '赵六', NULL, NULL, NULL, 0, 1, '2026-01-09 14:08:23', '2026-02-09 15:23:59');
INSERT INTO `slf_user` VALUES (17, '17000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '钱七', NULL, NULL, NULL, 0, 1, '2026-01-09 14:08:30', '2026-02-09 15:24:01');
INSERT INTO `slf_user` VALUES (18, '18000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '孙八', NULL, NULL, NULL, 0, 1, '2026-01-09 14:08:35', '2026-02-09 15:24:03');
INSERT INTO `slf_user` VALUES (19, '19000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '周九', NULL, NULL, NULL, 0, 1, '2026-01-09 14:08:39', '2026-02-09 15:24:05');
INSERT INTO `slf_user` VALUES (20, '20000000000', '$2a$10$zzjqUShazCwqyuN33wUubeJfuZz5kItzt7GAJri2CU2NVilvm8yUW', '吴十', '', '', NULL, 0, 1, '2026-01-09 14:08:48', '2026-02-09 15:24:54');

SET FOREIGN_KEY_CHECKS = 1;
