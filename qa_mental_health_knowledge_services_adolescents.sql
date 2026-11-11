/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : qa_mental_health_knowledge_services_adolescents

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 08/10/2026 12:02:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '科普文章主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `article_type_id` int NULL DEFAULT NULL COMMENT '文章分类',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文章标题',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片',
  `article_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章内容',
  `publisher_id` int NULL DEFAULT NULL COMMENT '发布人',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `view_count` int NULL DEFAULT NULL COMMENT '浏览量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `article_type_id`(`article_type_id` ASC) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `article_ibfk_2` FOREIGN KEY (`article_type_id`) REFERENCES `article_type` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科普文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, '2026-09-30 07:32:31', 1, '青少年如何识别和管理自己的情绪', 'http://localhost:7406/282347044/article-detail-writing.png', '<p>情绪是我们内心世界的信号灯。青少年时期，由于身体和心理的快速变化，情绪波动是非常正常的现象。学会识别自己的情绪，是情绪管理的第一步。</p><p>当你感到不舒服时，试着问自己：我现在感受到的是什么情绪？是愤怒、悲伤、焦虑还是失望？给情绪命名，能帮助你更好地理解自己。</p><p>深呼吸、写日记、运动都是有效的情绪调节方法。记住，所有的情绪都是正常的，重要的是我们如何应对它们。</p>', 1, '2026-09-30 08:02:31', 130);
INSERT INTO `article` VALUES (2, '2026-09-23 07:36:02', 2, '应对考试焦虑的五个实用技巧', 'http://localhost:7406/85179697/chat-calm-corner.png', '<p>考试焦虑是很多同学都会遇到的问题。适度的紧张可以帮助我们集中注意力，但过度焦虑则会影响发挥。</p><p>技巧一：制定合理的复习计划，避免临时抱佛脚。技巧二：考前进行深呼吸放松练习。技巧三：保持规律的作息和适当运动。技巧四：用积极的自我对话替代消极想法。技巧五：接受不完美，关注过程而非结果。</p>', 1, '2026-09-23 08:06:02', 97);
INSERT INTO `article` VALUES (3, '2026-09-16 07:39:33', 3, '如何建立健康的同伴关系', 'http://localhost:7406/405004801/home-friends-walking.png', '<p>同伴关系是青少年社会化发展的重要组成部分。健康的友谊建立在相互尊重、信任和支持的基础上。</p><p>学会倾听是建立良好关系的关键。当朋友向你倾诉时，给予真诚的关注和回应。同时，也要学会表达自己的感受和需求，真诚的沟通是友谊的桥梁。</p><p>遇到矛盾时，尝试站在对方的角度思考问题，用平和的方式表达自己的想法，寻找双方都能接受的解决方案。</p>', 1, '2026-09-16 08:09:33', 77);
INSERT INTO `article` VALUES (4, '2026-09-09 08:14:04', 4, '青春期的自我认知与成长', 'http://localhost:7406/566304215/article-self-growth.png', '<p>青春期是自我认知发展的关键时期。你可能会思考\"我是谁\"\"我想成为什么样的人\"这样的问题，这是非常正常且重要的。</p><p>每个人都是独特的，不需要和别人比较。发现自己的兴趣和优势，接纳自己的不完美，这些都是自我成长的重要部分。</p><p>尝试新事物、阅读、与不同的人交流，都能帮助你更好地认识自己。成长是一个过程，给自己时间和耐心。</p>', 1, '2026-09-09 08:44:04', 64);
INSERT INTO `article` VALUES (5, '2026-09-11 07:57:08', 5, '亲子沟通的正确打开方式', 'http://localhost:7406/340462527/article-family-conversation.png', '<p>很多青少年觉得父母不理解自己，而父母也常常感到和孩子沟通困难。其实，良好的亲子沟通需要双方共同努力。</p><p>选择合适的时机和场合进行沟通，避免在情绪激动时讨论问题。用\"我感觉...\"的方式表达自己的感受，而不是指责对方。</p><p>试着理解父母的担心和期望，同时也让他们了解你的想法和需求。沟通不是为了争输赢，而是为了相互理解。</p>', 1, '2026-09-11 08:27:08', 97);

-- ----------------------------
-- Table structure for article_comment
-- ----------------------------
DROP TABLE IF EXISTS `article_comment`;
CREATE TABLE `article_comment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章评论主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `article_id` int NULL DEFAULT NULL COMMENT '关联文章',
  `user_id` int NULL DEFAULT NULL COMMENT '评论者',
  `comment_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评论内容',
  `parent_id` int NULL DEFAULT NULL COMMENT '父评论ID（回复哪条评论）',
  `reply_to_user_id` int NULL DEFAULT NULL COMMENT '回复目标用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_reply_to_user_id`(`reply_to_user_id` ASC) USING BTREE,
  CONSTRAINT `article_comment_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `article_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9700022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article_comment
-- ----------------------------
INSERT INTO `article_comment` VALUES (1, '2026-09-30 08:03:31', 1, 2, '写得很好，学到了很多情绪管理的方法，以后会试着给自己的情绪命名', NULL, NULL);
INSERT INTO `article_comment` VALUES (2, '2026-09-30 08:03:31', 1, 3, '深呼吸确实很有用，上次考试前试了一下，感觉好多了', NULL, NULL);
INSERT INTO `article_comment` VALUES (3, '2026-09-23 08:07:02', 2, 4, '考试焦虑一直困扰我，这五个技巧很实用，谢谢分享', NULL, NULL);
INSERT INTO `article_comment` VALUES (4, '2026-09-16 08:10:33', 3, 2, '和朋友闹矛盾后看了这篇文章，决定主动去沟通一下', NULL, NULL);
INSERT INTO `article_comment` VALUES (5, '2026-10-02 08:17:35', 5, 5, '和爸妈的关系最近好了很多，感谢这篇文章的建议', NULL, NULL);
INSERT INTO `article_comment` VALUES (6, '2026-10-02 08:18:35', 5, 4, '我也准备试着主动沟通，感谢分享这些方法。', 5, 5);
INSERT INTO `article_comment` VALUES (9700001, '2026-10-07 10:30:00', 2, 9100007, '把每天的小目标记录下来，让我更容易看到自己的进步。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700002, '2026-10-07 12:30:00', 3, 9100001, '学习安排之外也需要休息，准备和家人一起调整计划。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700003, '2026-10-07 14:30:00', 4, 9100003, '先倾听再表达的沟通方法很有启发，谢谢分享。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700004, '2026-10-06 10:30:00', 5, 9100006, '这篇文章帮助我整理了自己的感受，准备和老师进一步交流。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700005, '2026-10-06 12:30:00', 1, 9100001, '把每天的小目标记录下来，让我更容易看到自己的进步。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700006, '2026-10-06 14:30:00', 2, 9100004, '学习安排之外也需要休息，准备和家人一起调整计划。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700007, '2026-10-05 10:30:00', 3, 9100005, '先倾听再表达的沟通方法很有启发，谢谢分享。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700008, '2026-10-05 12:30:00', 4, 9100001, '这篇文章帮助我整理了自己的感受，准备和老师进一步交流。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700009, '2026-10-05 14:30:00', 5, 9100005, '把每天的小目标记录下来，让我更容易看到自己的进步。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700010, '2026-10-04 10:30:00', 1, 9100004, '学习安排之外也需要休息，准备和家人一起调整计划。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700011, '2026-10-04 12:30:00', 2, 9100001, '先倾听再表达的沟通方法很有启发，谢谢分享。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700012, '2026-10-04 14:30:00', 3, 9100002, '这篇文章帮助我整理了自己的感受，准备和老师进一步交流。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700013, '2026-10-03 10:30:00', 4, 9100003, '把每天的小目标记录下来，让我更容易看到自己的进步。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700014, '2026-10-03 12:30:00', 5, 9100001, '学习安排之外也需要休息，准备和家人一起调整计划。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700015, '2026-10-03 14:30:00', 1, 9100001, '先倾听再表达的沟通方法很有启发，谢谢分享。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700016, '2026-10-02 10:30:00', 2, 9100002, '这篇文章帮助我整理了自己的感受，准备和老师进一步交流。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700017, '2026-10-02 12:30:00', 3, 9100001, '把每天的小目标记录下来，让我更容易看到自己的进步。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700018, '2026-10-02 14:30:00', 4, 9100002, '学习安排之外也需要休息，准备和家人一起调整计划。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700019, '2026-10-01 10:30:00', 5, 9100001, '先倾听再表达的沟通方法很有启发，谢谢分享。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700020, '2026-10-01 12:30:00', 1, 9100001, '这篇文章帮助我整理了自己的感受，准备和老师进一步交流。', NULL, NULL);
INSERT INTO `article_comment` VALUES (9700021, '2026-10-01 14:30:00', 2, 9100001, '把每天的小目标记录下来，让我更容易看到自己的进步。', NULL, NULL);

-- ----------------------------
-- Table structure for article_type
-- ----------------------------
DROP TABLE IF EXISTS `article_type`;
CREATE TABLE `article_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文章分类主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名称',
  `type_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article_type
-- ----------------------------
INSERT INTO `article_type` VALUES (1, '2026-09-30 07:31:31', '情绪管理', '帮助青少年认识和管理自己的情绪');
INSERT INTO `article_type` VALUES (2, '2026-09-23 07:35:02', '学业压力', '应对学业压力与考试焦虑的方法');
INSERT INTO `article_type` VALUES (3, '2026-09-16 07:38:33', '人际交往', '青少年社交技巧与人际关系处理');
INSERT INTO `article_type` VALUES (4, '2026-09-09 08:13:04', '自我成长', '青少年自我认知与个人成长');
INSERT INTO `article_type` VALUES (5, '2026-09-11 07:56:08', '家庭关系', '亲子沟通与家庭关系改善');

-- ----------------------------
-- Table structure for assessment_record
-- ----------------------------
DROP TABLE IF EXISTS `assessment_record`;
CREATE TABLE `assessment_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '测评记录主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` int NULL DEFAULT NULL COMMENT '用户',
  `scale_info_id` int NULL DEFAULT NULL COMMENT '测评量表',
  `total_score` int NULL DEFAULT NULL COMMENT '总分',
  `result_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '结果标签',
  `result_interpretation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '结果解读',
  `answer_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '作答详情JSON',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `scale_info_id`(`scale_info_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `assessment_record_ibfk_1` FOREIGN KEY (`scale_info_id`) REFERENCES `scale_info` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `assessment_record_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9500022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测评记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of assessment_record
-- ----------------------------
INSERT INTO `assessment_record` VALUES (1, '2026-09-30 08:03:31', 2, 1, 7, '轻度焦虑', '你存在轻度焦虑倾向，可能在某些情境下会感到紧张不安。', '[{\"questionIndex\":1,\"selectedScore\":2},{\"questionIndex\":2,\"selectedScore\":2},{\"questionIndex\":3,\"selectedScore\":1},{\"questionIndex\":4,\"selectedScore\":1},{\"questionIndex\":5,\"selectedScore\":1}]', '2026-09-30 08:13:31');
INSERT INTO `assessment_record` VALUES (2, '2026-09-23 08:07:02', 3, 2, 10, '压力过大', '你的学业压力已经过大，可能影响身心健康。', '[{\"questionIndex\":1,\"selectedScore\":2},{\"questionIndex\":2,\"selectedScore\":3},{\"questionIndex\":3,\"selectedScore\":2},{\"questionIndex\":4,\"selectedScore\":2},{\"questionIndex\":5,\"selectedScore\":1}]', '2026-09-23 08:17:02');
INSERT INTO `assessment_record` VALUES (3, '2026-09-16 08:10:33', 4, 1, 3, '正常', '你的焦虑水平处于正常范围，情绪状态良好。', '[{\"questionIndex\":1,\"selectedScore\":1},{\"questionIndex\":2,\"selectedScore\":1},{\"questionIndex\":3,\"selectedScore\":0},{\"questionIndex\":4,\"selectedScore\":1},{\"questionIndex\":5,\"selectedScore\":0}]', '2026-09-16 08:20:33');
INSERT INTO `assessment_record` VALUES (4, '2026-09-09 08:14:04', 5, 3, 8, '适应一般', '你在人际交往中存在一些困难，可能需要提升社交技巧。', '[{\"questionIndex\":1,\"selectedScore\":2},{\"questionIndex\":2,\"selectedScore\":2},{\"questionIndex\":3,\"selectedScore\":1},{\"questionIndex\":4,\"selectedScore\":2},{\"questionIndex\":5,\"selectedScore\":1}]', '2026-09-09 08:24:04');
INSERT INTO `assessment_record` VALUES (5, '2026-10-02 08:17:35', 4, 1, 5, '轻度焦虑', '你存在轻度焦虑倾向，可能在某些情境下会感到紧张不安。', '[{\"questionIndex\":1,\"selectedScore\":0},{\"questionIndex\":2,\"selectedScore\":2},{\"questionIndex\":3,\"selectedScore\":1},{\"questionIndex\":4,\"selectedScore\":1},{\"questionIndex\":5,\"selectedScore\":1}]', '2026-10-02 08:27:35');
INSERT INTO `assessment_record` VALUES (9500001, '2026-10-07 10:10:00', 9100007, 1, 5, '轻度焦虑', '你存在轻度焦虑倾向，可能在某些情境下会感到紧张不安。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 2}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-07 10:20:00');
INSERT INTO `assessment_record` VALUES (9500002, '2026-10-07 12:10:00', 9100001, 2, 8, '压力偏高', '你感受到的学业压力偏高，需要注意调节。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 2}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-07 12:20:00');
INSERT INTO `assessment_record` VALUES (9500003, '2026-10-07 14:10:00', 9100003, 3, 11, '适应困难', '你在人际关系方面存在较大困难，可能感到孤立或不被理解。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 2}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-07 14:20:00');
INSERT INTO `assessment_record` VALUES (9500004, '2026-10-06 10:10:00', 9100006, 1, 14, '中重度焦虑', '你的焦虑水平较高，可能已经影响到日常生活和学习。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 3}, {\"questionIndex\": 5, \"selectedScore\": 2}]', '2026-10-06 10:20:00');
INSERT INTO `assessment_record` VALUES (9500005, '2026-10-06 12:10:00', 9100001, 2, 2, '压力正常', '你的学业压力处于正常水平，能够较好地应对学习任务。', '[{\"questionIndex\": 1, \"selectedScore\": 2}, {\"questionIndex\": 2, \"selectedScore\": 0}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-06 12:20:00');
INSERT INTO `assessment_record` VALUES (9500006, '2026-10-06 14:10:00', 9100004, 3, 5, '适应一般', '你在人际交往中存在一些困难，可能需要提升社交技巧。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 2}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-06 14:20:00');
INSERT INTO `assessment_record` VALUES (9500007, '2026-10-05 10:10:00', 9100005, 1, 8, '轻度焦虑', '你存在轻度焦虑倾向，可能在某些情境下会感到紧张不安。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 2}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-05 10:20:00');
INSERT INTO `assessment_record` VALUES (9500008, '2026-10-05 12:10:00', 9100001, 2, 11, '压力过大', '你的学业压力已经过大，可能影响身心健康。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 2}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-05 12:20:00');
INSERT INTO `assessment_record` VALUES (9500009, '2026-10-05 14:10:00', 9100005, 3, 14, '适应困难', '你在人际关系方面存在较大困难，可能感到孤立或不被理解。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 3}, {\"questionIndex\": 5, \"selectedScore\": 2}]', '2026-10-05 14:20:00');
INSERT INTO `assessment_record` VALUES (9500010, '2026-10-04 10:10:00', 9100004, 1, 2, '正常', '你的焦虑水平处于正常范围，情绪状态良好。', '[{\"questionIndex\": 1, \"selectedScore\": 2}, {\"questionIndex\": 2, \"selectedScore\": 0}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-04 10:20:00');
INSERT INTO `assessment_record` VALUES (9500011, '2026-10-04 12:10:00', 9100001, 2, 5, '压力偏高', '你感受到的学业压力偏高，需要注意调节。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 2}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-04 12:20:00');
INSERT INTO `assessment_record` VALUES (9500012, '2026-10-04 14:10:00', 9100002, 3, 8, '适应一般', '你在人际交往中存在一些困难，可能需要提升社交技巧。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 2}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-04 14:20:00');
INSERT INTO `assessment_record` VALUES (9500013, '2026-10-03 10:10:00', 9100003, 1, 11, '中重度焦虑', '你的焦虑水平较高，可能已经影响到日常生活和学习。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 2}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-03 10:20:00');
INSERT INTO `assessment_record` VALUES (9500014, '2026-10-03 12:10:00', 9100001, 2, 14, '压力过大', '你的学业压力已经过大，可能影响身心健康。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 3}, {\"questionIndex\": 5, \"selectedScore\": 2}]', '2026-10-03 12:20:00');
INSERT INTO `assessment_record` VALUES (9500015, '2026-10-03 14:10:00', 9100001, 3, 2, '适应良好', '你的人际关系适应能力良好，能够较好地与同伴相处。', '[{\"questionIndex\": 1, \"selectedScore\": 2}, {\"questionIndex\": 2, \"selectedScore\": 0}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-03 14:20:00');
INSERT INTO `assessment_record` VALUES (9500016, '2026-10-02 10:10:00', 9100002, 1, 5, '轻度焦虑', '你存在轻度焦虑倾向，可能在某些情境下会感到紧张不安。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 2}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-02 10:20:00');
INSERT INTO `assessment_record` VALUES (9500017, '2026-10-02 12:10:00', 9100001, 2, 8, '压力偏高', '你感受到的学业压力偏高，需要注意调节。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 2}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-02 12:20:00');
INSERT INTO `assessment_record` VALUES (9500018, '2026-10-02 14:10:00', 9100002, 3, 11, '适应困难', '你在人际关系方面存在较大困难，可能感到孤立或不被理解。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 2}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-02 14:20:00');
INSERT INTO `assessment_record` VALUES (9500019, '2026-10-01 10:10:00', 9100001, 1, 14, '中重度焦虑', '你的焦虑水平较高，可能已经影响到日常生活和学习。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 3}, {\"questionIndex\": 3, \"selectedScore\": 3}, {\"questionIndex\": 4, \"selectedScore\": 3}, {\"questionIndex\": 5, \"selectedScore\": 2}]', '2026-10-01 10:20:00');
INSERT INTO `assessment_record` VALUES (9500020, '2026-10-01 12:10:00', 9100001, 2, 2, '压力正常', '你的学业压力处于正常水平，能够较好地应对学习任务。', '[{\"questionIndex\": 1, \"selectedScore\": 2}, {\"questionIndex\": 2, \"selectedScore\": 0}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-01 12:20:00');
INSERT INTO `assessment_record` VALUES (9500021, '2026-10-01 14:10:00', 9100001, 3, 5, '适应一般', '你在人际交往中存在一些困难，可能需要提升社交技巧。', '[{\"questionIndex\": 1, \"selectedScore\": 3}, {\"questionIndex\": 2, \"selectedScore\": 2}, {\"questionIndex\": 3, \"selectedScore\": 0}, {\"questionIndex\": 4, \"selectedScore\": 0}, {\"questionIndex\": 5, \"selectedScore\": 0}]', '2026-10-01 14:20:00');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '轮播图主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '轮播图片',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '轮播标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '2026-09-30 08:03:31', 'http://localhost:7406/92026212/home-garden-hero.png', '关注青少年心理健康');
INSERT INTO `banner` VALUES (2, '2026-09-23 08:07:02', 'http://localhost:7406/800843511/chat-garden-hero.png', '智能问答 温暖陪伴');
INSERT INTO `banner` VALUES (3, '2026-09-16 08:10:33', 'http://localhost:7406/30713205/article-reading-hero.png', '心理测评 了解自我');

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '对话消息主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `chat_session_id` int NULL DEFAULT NULL COMMENT '所属会话',
  `sender_role` int NULL DEFAULT NULL COMMENT '发送角色枚举(1:用户,2:系统)',
  `message_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '消息内容',
  `retrieved_chunk_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检索分块',
  `token_usage` int NULL DEFAULT NULL COMMENT 'Token消耗量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `chat_session_id`(`chat_session_id` ASC) USING BTREE,
  CONSTRAINT `chat_message_ibfk_1` FOREIGN KEY (`chat_session_id`) REFERENCES `chat_session` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9300043 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对话消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (1, '2026-09-30 08:04:31', 1, 1, '马上要期中考试了，我特别紧张，晚上都睡不着觉，怎么办？', NULL, NULL);
INSERT INTO `chat_message` VALUES (2, '2026-09-30 08:05:31', 1, 2, '我理解你的感受，考试前感到紧张是很正常的。适度的紧张其实可以帮助你集中注意力。以下是一些缓解考试焦虑的建议：1. 制定合理的复习计划，每天完成一部分，避免临考前突击；2. 考前做几次深呼吸，帮助身体放松；3. 保持规律作息，考前不要熬夜；4. 用积极的自我对话鼓励自己。如果焦虑持续严重影响生活，建议和老师或家长沟通。', '5', 256);
INSERT INTO `chat_message` VALUES (3, '2026-09-30 08:06:31', 1, 1, '可是我总觉得自己复习不够，怎么都不放心', NULL, NULL);
INSERT INTO `chat_message` VALUES (4, '2026-09-30 08:07:31', 1, 2, '这种\"总觉得不够\"的感觉其实是焦虑的一种表现。你可以试试这个方法：把已经复习过的内容列一个清单，看到自己已经完成的部分，会让你更有信心。同时，接受\"不可能准备到100%完美\"这个事实，把注意力放在你能控制的事情上。记住，考试只是检验学习的一种方式，不是衡量你价值的标准。', '4,5', 198);
INSERT INTO `chat_message` VALUES (5, '2026-09-23 08:12:02', 2, 1, '我和班上一个同学闹矛盾了，现在见面都不说话，我该怎么办？', NULL, NULL);
INSERT INTO `chat_message` VALUES (6, '2026-09-23 08:13:02', 2, 2, '同学之间发生矛盾是很常见的，重要的是如何处理。首先，给彼此一些冷静的时间和空间。等情绪平复后，可以找一个合适的时机主动沟通，用\"我感觉...\"的方式表达你的感受，而不是指责对方。试着站在对方的角度想想，理解彼此的立场。如果直接沟通有困难，也可以请一个你们都信任的朋友帮忙调解。', '3', 186);
INSERT INTO `chat_message` VALUES (7, '2026-09-16 08:17:33', 3, 1, '每天作业特别多，还要上补习班，感觉喘不过气来', NULL, NULL);
INSERT INTO `chat_message` VALUES (8, '2026-09-16 08:18:33', 3, 2, '听起来你承受了很大的学业压力，这种感受是完全可以理解的。建议你：1. 学会时间管理，按照重要性和紧急程度安排任务；2. 适当给自己留一些休息和放松的时间，比如运动、听音乐；3. 和父母坦诚沟通你的感受，一起讨论是否需要调整补习安排；4. 如果感到持续疲惫，要学会说\"不\"，保护自己的身心健康。', '4', 220);
INSERT INTO `chat_message` VALUES (9, '2026-09-16 08:19:33', 3, 1, '可是爸妈觉得别人都在补课，不补就会落后', NULL, NULL);
INSERT INTO `chat_message` VALUES (10, '2026-09-16 08:20:33', 3, 2, '我理解你的处境。父母的出发点是关心你的未来，但每个人的承受能力和学习方式不同。你可以试着和父母进行一次认真的谈话：1. 选择大家都心情平和的时候；2. 先表达你理解他们的用心；3. 用具体的例子说明你目前的压力状况；4. 提出你自己的学习计划作为替代方案。让父母看到你是有规划的，他们会更容易接受。', '2,4', 245);
INSERT INTO `chat_message` VALUES (11, '2026-09-09 08:25:04', 4, 1, '最近总是感觉很低落，对什么事情都提不起兴趣', NULL, NULL);
INSERT INTO `chat_message` VALUES (12, '2026-09-09 08:26:04', 4, 2, '感谢你愿意分享你的感受。持续的情绪低落和兴趣减退需要认真对待。首先，这不是你的错，情绪低落可能有很多原因。建议你：1. 尝试保持基本的日常作息规律；2. 每天做一些简单的运动，哪怕只是散步；3. 和你信任的人聊聊你的感受；4. 记录每天让你感到哪怕一点点开心的事情。如果这种状态持续超过两周，建议寻求学校心理老师或专业心理咨询师的帮助。你不是一个人，有很多人愿意帮助你。', '6', 312);
INSERT INTO `chat_message` VALUES (13, '2026-09-16 08:23:33', 3, 1, '和同学发生矛盾了该怎么处理？', NULL, NULL);
INSERT INTO `chat_message` VALUES (14, '2026-09-16 08:24:33', 3, 2, '抱歉，我暂时无法回答你的问题。请稍后再试。', '44', NULL);
INSERT INTO `chat_message` VALUES (15, '2026-09-16 08:25:33', 3, 1, '感觉学习压力很大，经常失眠怎么办？', NULL, NULL);
INSERT INTO `chat_message` VALUES (16, '2026-09-16 08:26:33', 3, 2, '看到你正在经历学习压力和失眠的困扰，这一定很辛苦。当压力影响到睡眠时，确实需要认真对待。根据你之前提到的补习压力，这可能是一个重要的压力来源。\n\n我们可以从几个方面来尝试改善：\n\n**1. 调整睡前习惯，为睡眠做准备**\n*   **建立“放松仪式”**：睡前一小时，尝试做一些能让大脑放松下来的事情，比如听舒缓的音乐、洗个热水澡、阅读轻松的书籍（非课本），或者进行5-10分钟的深呼吸、正念冥想。\n*   **减少屏幕时间**：睡前一小时尽量避免使用手机、电脑等电子设备。屏幕蓝光会抑制褪黑素分泌，干扰睡眠。\n*   **创造舒适的睡眠环境**：确保卧室安静、黑暗、温度适宜。\n\n**2. 管理白天的压力与焦虑**\n*   **为压力“写日记”**：如果躺在床上思绪纷乱，可以准备一个本子，睡前把心里的担忧和待办事项写下来。告诉自己：“我已经记下了，明天再处理。” 这能帮助大脑“清空缓存”。\n*   **安排“担忧时间”**：白天可以专门设定15分钟作为“担忧时间”，集中思考那些让你焦虑的事情。其他时间如果焦虑念头冒出来，就告诉自己：“留到‘担忧时间’再处理。”\n*   **保证日间活动与放松**：白天进行适度的体育锻炼（但避免睡前剧烈运动），并确保有短暂的休息和社交时间，让大脑有张有弛。\n\n**3. 与父母进行有效沟通（结合之前的对话）**\n这可能是解决压力源的关键一步。你可以找一个合适的时机，用更具体的方式和父母谈谈：\n*   **分享你的感受**：可以这样说：“爸妈，我知道你们让我补课是为我好，希望我不落后。但我最近压力大到晚上睡不着，白天精神也不好，反而更影响学习效率了。”\n*   **提出具体建议**：比如，“我们能不能一起看看，哪些补习是当前最必要的？或者我们先尝试调整一下频率，看看我的睡眠和学习状态会不会改善？”\n*   **展现你的主动性**：让他们看到你对自己的学习有思考和规划，而不仅仅是抗拒。\n\n**4. 寻求外部支持**\n*   **和信任的人聊聊**：除了父母，也可以和学校的心理老师、信任的班主任或好朋友谈谈你的压力，倾诉本身就能缓解很多情绪。\n*   **如果情况持续**：如果失眠和压力感持续两周以上，并且严重影响了白天的精神状态，**请务必告诉父母或老师，考虑寻求专业心理咨询师或医生的帮助**。他们能提供更专业的评估和指导。\n\n**请记住**：你的身心健康永远是第一位的。良好的睡眠是高效学习的基础，而不是代价。先照顾好自己，才能更好地面对挑战。你愿意尝试从以上哪个小步骤开始呢？', '45', NULL);
INSERT INTO `chat_message` VALUES (17, '2026-09-16 08:27:33', 3, 1, '父母不理解我，沟通总是吵架怎么办？', NULL, NULL);
INSERT INTO `chat_message` VALUES (18, '2026-09-16 08:28:33', 3, 2, '看到你为和父母的沟通感到困扰和难过，这真的很不容易。当最亲近的人无法理解我们时，那种孤独和挫败感会特别强烈。你愿意主动寻找解决方法，这已经是非常勇敢和成熟的一步了。\n\n根据我们之前讨论过的内容，以及处理冲突的有效方法，我为你梳理了一个更详细的“沟通行动指南”。这可能需要一些练习和耐心，但很多青少年都发现它很有帮助。\n\n### **沟通行动指南：从冲突到理解**\n\n**第一步：按下“暂停键”，主动冷却**\n*   **识别信号**：当感觉到对话开始升温，音量变大、情绪激动、或者开始重复争吵时，就是最好的暂停时机。\n*   **如何说**：用平静但坚定的语气说：“爸爸/妈妈，我感觉我们现在都有点激动，这样谈下去可能会伤害彼此。我们能不能先暂停10分钟，大家都冷静一下再聊？” 这显示了你的成熟，而不是逃避。\n*   **做什么**：离开现场，去自己的房间做几次深呼吸，听听音乐，或者喝杯水。目的是让情绪回落。\n\n**第二步：自我反思，理清思路（最关键的一步）**\n冷静下来后，独自思考这几个问题：\n1.  **核心问题**：我们到底在吵什么？是具体的事情（比如玩手机时间、交友），还是更深层的需求（比如我需要更多信任/自主权）？\n2.  **我的感受与需求**：在这件事上，我真正的**感受**是什么（是觉得不被尊重、被控制，还是感到失望）？我真正的**需求**又是什么（是希望自己做决定，还是需要一些支持）？\n3.  **父母的角度**：他们为什么会反对或担心？他们的担忧可能是什么？（例如：怕你受伤、影响学习、未来吃亏）。尝试站在他们的角度理解，即使你不完全同意。\n\n**第三步：选择时机，重启对话**\n*   **时机**：选择父母情绪较好、比较空闲的时候，比如饭后休息时。可以主动说：“爸妈，关于昨天我们讨论的XX事，我现在冷静下来了，我们能再心平气和地聊聊吗？”\n*   **姿态**：确保你的姿态是开放的，目的是“互相理解”，而不是“说服对方”或“赢得争吵”。\n\n**第四步：使用“非暴力沟通”技巧表达**\n这是避免吵架的核心技巧，公式是：**观察 + 感受 + 需要 + 请求**\n*   **举例**（关于玩手机）：\n    *   **观察（说事实）**：“妈妈，我注意到每次我拿起手机超过半小时，你就会很生气地让我放下。”（而不是：“你总是管着我！”）\n    *   **感受（说情绪）**：“这让我感到有点委屈和烦躁。”\n    *   **需要（说原因）**：“因为我需要用手机和同学讨论作业，而且我也需要一些放松的时间。我理解你是担心我沉迷。”\n    *   **请求（说具体方案）**：“我们能不能一起商量一个合理的每天使用时间？或者我可以在完成作业后，集中使用一段时间，并主动向你汇报？这样你能放心，我也能安排好自己的时间。”\n\n**第五步：协商与尝试**\n*   沟通的目标是达成一个双方都能接受的“试行方案”。你可能需要做出一些妥协，父母也是。\n*   **例如**：“那我们能不能先按这个计划试一个星期？周末我们再看看效果，需不需要调整。”\n\n**第六步：用行动建立信任**\n*   一旦达成约定，就尽力去遵守。**行动比语言更有力量**。当你通过行动证明了自己的责任感（比如按时回家、主动完成学习任务），父母自然会慢慢给予你更多信任和空间。\n\n### **一些重要的提醒**\n*   **降低期待**：不要指望一次谈话就能彻底改变。关系的改善是无数次“有效沟通”累积的结果。\n*   **倾听他们**：在你表达完后，也真诚地邀请父母说说他们的想法和感受。认真听，不打断。有时，他们只是需要被听到。\n*   **寻求支持**：如果家庭沟通非常困难，让你感到压抑或痛苦，**请一定向你信任的成年人求助**，比如学校的心理老师、班主任或其他亲属。他们可以作为中立的第三方提供帮助。\n\n改变沟通模式就像学习一门新语言，一开始会有点别扭，但只要你愿意尝试，情况一定会慢慢改善。你正在为改善家庭关系付出努力，这非常了不起。先从“暂停”和“反思”这两个小步骤开始试试看，好吗？', '44', NULL);
INSERT INTO `chat_message` VALUES (19, '2026-10-02 08:36:35', 5, 1, '考试前总是很紧张焦虑怎么办？', NULL, NULL);
INSERT INTO `chat_message` VALUES (20, '2026-10-02 08:37:35', 5, 2, '考试焦虑是很常见的现象，很多同学都会遇到。根据心理学知识，这里有几个方法可以帮助你缓解紧张情绪：\n\n**1. 调整心态**\n- 告诉自己“适度的紧张有助于发挥”，完全放松反而可能影响状态\n- 把考试看作检验学习成果的机会，而不是对自己价值的评判\n\n**2. 做好充分准备**\n- 制定合理的复习计划，避免临时抱佛脚\n- 提前熟悉考试流程和题型，减少未知带来的焦虑\n\n**3. 放松技巧**\n- 深呼吸：吸气4秒，屏气2秒，呼气6秒，重复几次\n- 肌肉放松：从脚到头依次收紧再放松肌肉群\n- 考前听些舒缓的音乐\n\n**4. 保持健康作息**\n- 考前保证充足睡眠，避免熬夜\n- 适当运动可以帮助释放压力\n- 注意均衡饮食，避免过多咖啡因\n\n**如果焦虑严重影响到你的睡眠、食欲或日常学习，建议告诉家长或老师，考虑寻求学校心理老师或专业心理咨询师的帮助。**\n\n记住，你已经为考试付出了努力，要相信自己的准备。考试只是人生中的一站，无论结果如何，都不能定义你的全部价值。加油！', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300001, '2026-10-07 10:00:00', 9200001, 1, '最近考试前总是紧张，怎样安排复习和休息？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300002, '2026-10-07 10:01:00', 9200001, 2, '可以把复习任务分成小步骤，同时给休息留出时间。若紧张影响日常学习，可以向家长或学校心理老师说明感受，一起讨论支持方式。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300003, '2026-10-07 12:00:00', 9200002, 1, '最近考试前总是紧张，怎样安排复习和休息？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300004, '2026-10-07 12:01:00', 9200002, 2, '可以把复习任务分成小步骤，同时给休息留出时间。若紧张影响日常学习，可以向家长或学校心理老师说明感受，一起讨论支持方式。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300005, '2026-10-07 14:00:00', 9200003, 1, '最近考试前总是紧张，怎样安排复习和休息？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300006, '2026-10-07 14:01:00', 9200003, 2, '可以把复习任务分成小步骤，同时给休息留出时间。若紧张影响日常学习，可以向家长或学校心理老师说明感受，一起讨论支持方式。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300007, '2026-10-06 10:00:00', 9200004, 1, '最近和同学沟通不太顺利，怎样表达自己的想法？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300008, '2026-10-06 10:01:00', 9200004, 2, '可以先整理想表达的具体事情，选择双方愿意倾听的时间，用自己的感受说明需求，也给同学回应的机会。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300009, '2026-10-06 12:00:00', 9200005, 1, '最近和同学沟通不太顺利，怎样表达自己的想法？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300010, '2026-10-06 12:01:00', 9200005, 2, '可以先整理想表达的具体事情，选择双方愿意倾听的时间，用自己的感受说明需求，也给同学回应的机会。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300011, '2026-10-06 14:00:00', 9200006, 1, '最近和同学沟通不太顺利，怎样表达自己的想法？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300012, '2026-10-06 14:01:00', 9200006, 2, '可以先整理想表达的具体事情，选择双方愿意倾听的时间，用自己的感受说明需求，也给同学回应的机会。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300013, '2026-10-05 10:00:00', 9200007, 1, '最近作业和课外活动很多，怎样做好时间管理？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300014, '2026-10-05 10:01:00', 9200007, 2, '先列出近期任务，区分必须完成和可以调整的部分，再和老师、家长讨论可行的安排，给兴趣活动和休息保留时间。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300015, '2026-10-05 12:00:00', 9200008, 1, '最近作业和课外活动很多，怎样做好时间管理？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300016, '2026-10-05 12:01:00', 9200008, 2, '先列出近期任务，区分必须完成和可以调整的部分，再和老师、家长讨论可行的安排，给兴趣活动和休息保留时间。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300017, '2026-10-05 14:00:00', 9200009, 1, '最近作业和课外活动很多，怎样做好时间管理？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300018, '2026-10-05 14:01:00', 9200009, 2, '先列出近期任务，区分必须完成和可以调整的部分，再和老师、家长讨论可行的安排，给兴趣活动和休息保留时间。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300019, '2026-10-04 10:00:00', 9200010, 1, '最近睡前总想着学习的事情，怎样记录和整理心情？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300020, '2026-10-04 10:01:00', 9200010, 2, '可以简短记录今天发生的事情和自己的感受，把明天需要处理的任务另列一张清单。持续困扰时可以告诉信任的成年人。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300021, '2026-10-04 12:00:00', 9200011, 1, '最近睡前总想着学习的事情，怎样记录和整理心情？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300022, '2026-10-04 12:01:00', 9200011, 2, '可以简短记录今天发生的事情和自己的感受，把明天需要处理的任务另列一张清单。持续困扰时可以告诉信任的成年人。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300023, '2026-10-04 14:00:00', 9200012, 1, '最近睡前总想着学习的事情，怎样记录和整理心情？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300024, '2026-10-04 14:01:00', 9200012, 2, '可以简短记录今天发生的事情和自己的感受，把明天需要处理的任务另列一张清单。持续困扰时可以告诉信任的成年人。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300025, '2026-10-03 10:00:00', 9200013, 1, '最近想和父母商量学习计划，怎样开始这次沟通？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300026, '2026-10-03 10:01:00', 9200013, 2, '可以选一个平静的时间，说明近期的任务和感受，邀请父母一起讨论目标、休息时间以及希望得到的帮助。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300027, '2026-10-03 12:00:00', 9200014, 1, '最近想和父母商量学习计划，怎样开始这次沟通？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300028, '2026-10-03 12:01:00', 9200014, 2, '可以选一个平静的时间，说明近期的任务和感受，邀请父母一起讨论目标、休息时间以及希望得到的帮助。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300029, '2026-10-03 14:00:00', 9200015, 1, '最近想和父母商量学习计划，怎样开始这次沟通？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300030, '2026-10-03 14:01:00', 9200015, 2, '可以选一个平静的时间，说明近期的任务和感受，邀请父母一起讨论目标、休息时间以及希望得到的帮助。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300031, '2026-10-02 10:00:00', 9200016, 1, '最近参加集体活动有些害羞，怎样逐步认识新朋友？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300032, '2026-10-02 10:01:00', 9200016, 2, '可以先参与自己熟悉的活动，从和一位同学交流开始，逐步尝试小组合作，不必要求自己立即适应所有场合。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300033, '2026-10-02 12:00:00', 9200017, 1, '最近参加集体活动有些害羞，怎样逐步认识新朋友？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300034, '2026-10-02 12:01:00', 9200017, 2, '可以先参与自己熟悉的活动，从和一位同学交流开始，逐步尝试小组合作，不必要求自己立即适应所有场合。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300035, '2026-10-02 14:00:00', 9200018, 1, '最近参加集体活动有些害羞，怎样逐步认识新朋友？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300036, '2026-10-02 14:01:00', 9200018, 2, '可以先参与自己熟悉的活动，从和一位同学交流开始，逐步尝试小组合作，不必要求自己立即适应所有场合。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300037, '2026-10-01 10:00:00', 9200019, 1, '最近对自己的长处不太确定，怎样了解和接纳自己？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300038, '2026-10-01 10:01:00', 9200019, 2, '可以记录自己愿意投入时间的事情、完成过的小目标，以及朋友对自己的积极反馈，在日常体验中逐步认识自己的特点。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300039, '2026-10-01 12:00:00', 9200020, 1, '最近对自己的长处不太确定，怎样了解和接纳自己？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300040, '2026-10-01 12:01:00', 9200020, 2, '可以记录自己愿意投入时间的事情、完成过的小目标，以及朋友对自己的积极反馈，在日常体验中逐步认识自己的特点。', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300041, '2026-10-01 14:00:00', 9200021, 1, '最近对自己的长处不太确定，怎样了解和接纳自己？', NULL, NULL);
INSERT INTO `chat_message` VALUES (9300042, '2026-10-01 14:01:00', 9200021, 2, '可以记录自己愿意投入时间的事情、完成过的小目标，以及朋友对自己的积极反馈，在日常体验中逐步认识自己的特点。', NULL, NULL);

-- ----------------------------
-- Table structure for chat_session
-- ----------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '对话会话主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` int NULL DEFAULT NULL COMMENT '用户',
  `session_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会话标题',
  `message_count` int NULL DEFAULT NULL COMMENT '消息数量',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `chat_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9200022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对话会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_session
-- ----------------------------
INSERT INTO `chat_session` VALUES (1, '2026-09-30 08:03:31', 2, '考试焦虑怎么办', 4, '2026-09-30 08:07:31');
INSERT INTO `chat_session` VALUES (2, '2026-09-23 08:07:02', 3, '和同学关系不好', 2, '2026-09-23 08:13:02');
INSERT INTO `chat_session` VALUES (3, '2026-09-16 08:10:33', 4, '学习压力太大了', 10, '2026-09-16 08:28:33');
INSERT INTO `chat_session` VALUES (4, '2026-09-09 08:14:04', 5, '最近总是心情不好', 2, '2026-09-09 08:26:04');
INSERT INTO `chat_session` VALUES (5, '2026-10-02 08:17:35', 4, '考试前总是很紧张焦虑怎么办？', 2, '2026-10-02 08:37:35');
INSERT INTO `chat_session` VALUES (9200001, '2026-10-07 10:00:00', 9100007, '近期心情记录·考试准备·1', 2, '2026-10-07 10:01:00');
INSERT INTO `chat_session` VALUES (9200002, '2026-10-07 12:00:00', 9100001, '近期心情记录·考试准备·2', 2, '2026-10-07 12:01:00');
INSERT INTO `chat_session` VALUES (9200003, '2026-10-07 14:00:00', 9100003, '近期心情记录·考试准备·3', 2, '2026-10-07 14:01:00');
INSERT INTO `chat_session` VALUES (9200004, '2026-10-06 10:00:00', 9100006, '近期心情记录·同伴沟通·1', 2, '2026-10-06 10:01:00');
INSERT INTO `chat_session` VALUES (9200005, '2026-10-06 12:00:00', 9100001, '近期心情记录·同伴沟通·2', 2, '2026-10-06 12:01:00');
INSERT INTO `chat_session` VALUES (9200006, '2026-10-06 14:00:00', 9100004, '近期心情记录·同伴沟通·3', 2, '2026-10-06 14:01:00');
INSERT INTO `chat_session` VALUES (9200007, '2026-10-05 10:00:00', 9100005, '近期心情记录·时间安排·1', 2, '2026-10-05 10:01:00');
INSERT INTO `chat_session` VALUES (9200008, '2026-10-05 12:00:00', 9100001, '近期心情记录·时间安排·2', 2, '2026-10-05 12:01:00');
INSERT INTO `chat_session` VALUES (9200009, '2026-10-05 14:00:00', 9100005, '近期心情记录·时间安排·3', 2, '2026-10-05 14:01:00');
INSERT INTO `chat_session` VALUES (9200010, '2026-10-04 10:00:00', 9100004, '近期心情记录·心情整理·1', 2, '2026-10-04 10:01:00');
INSERT INTO `chat_session` VALUES (9200011, '2026-10-04 12:00:00', 9100001, '近期心情记录·心情整理·2', 2, '2026-10-04 12:01:00');
INSERT INTO `chat_session` VALUES (9200012, '2026-10-04 14:00:00', 9100002, '近期心情记录·心情整理·3', 2, '2026-10-04 14:01:00');
INSERT INTO `chat_session` VALUES (9200013, '2026-10-03 10:00:00', 9100003, '近期心情记录·亲子交流·1', 2, '2026-10-03 10:01:00');
INSERT INTO `chat_session` VALUES (9200014, '2026-10-03 12:00:00', 9100001, '近期心情记录·亲子交流·2', 2, '2026-10-03 12:01:00');
INSERT INTO `chat_session` VALUES (9200015, '2026-10-03 14:00:00', 9100001, '近期心情记录·亲子交流·3', 2, '2026-10-03 14:01:00');
INSERT INTO `chat_session` VALUES (9200016, '2026-10-02 10:00:00', 9100002, '近期心情记录·集体活动·1', 2, '2026-10-02 10:01:00');
INSERT INTO `chat_session` VALUES (9200017, '2026-10-02 12:00:00', 9100001, '近期心情记录·集体活动·2', 2, '2026-10-02 12:01:00');
INSERT INTO `chat_session` VALUES (9200018, '2026-10-02 14:00:00', 9100002, '近期心情记录·集体活动·3', 2, '2026-10-02 14:01:00');
INSERT INTO `chat_session` VALUES (9200019, '2026-10-01 10:00:00', 9100001, '近期心情记录·认识自己·1', 2, '2026-10-01 10:01:00');
INSERT INTO `chat_session` VALUES (9200020, '2026-10-01 12:00:00', 9100001, '近期心情记录·认识自己·2', 2, '2026-10-01 12:01:00');
INSERT INTO `chat_session` VALUES (9200021, '2026-10-01 14:00:00', 9100001, '近期心情记录·认识自己·3', 2, '2026-10-01 14:01:00');

-- ----------------------------
-- Table structure for document_chunk
-- ----------------------------
DROP TABLE IF EXISTS `document_chunk`;
CREATE TABLE `document_chunk`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文档分块主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `knowledge_document_id` int NULL DEFAULT NULL COMMENT '所属文档',
  `chunk_index` int NULL DEFAULT NULL COMMENT '分块序号',
  `chunk_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '分块内容',
  `token_count` int NULL DEFAULT NULL COMMENT 'Token数量',
  `vector_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '向量数据库',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `knowledge_document_id`(`knowledge_document_id` ASC) USING BTREE,
  CONSTRAINT `document_chunk_ibfk_1` FOREIGN KEY (`knowledge_document_id`) REFERENCES `knowledge_document` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文档分块表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of document_chunk
-- ----------------------------
INSERT INTO `document_chunk` VALUES (1, '2026-09-30 08:13:31', 1, 1, '青少年心理发展是指12至18岁个体在认知、情感、社会性等方面的心理变化过程。这一时期的青少年开始形成独立的自我意识，对自我身份的探索成为核心发展任务。', 86, 'vec_001');
INSERT INTO `document_chunk` VALUES (2, '2026-09-30 08:13:31', 1, 2, '青春期的情绪特点包括情绪体验的强烈性、情绪变化的不稳定性以及情绪表达的内隐性。青少年可能会经历情绪的剧烈波动，这是正常的发展现象。', 78, 'vec_002');
INSERT INTO `document_chunk` VALUES (3, '2026-09-23 08:17:02', 2, 1, '情绪调节是指个体对自身情绪的觉察、理解和管理过程。常用的情绪调节策略包括认知重评、表达抑制、注意力转移和问题解决等。', 72, 'vec_003');
INSERT INTO `document_chunk` VALUES (4, '2026-09-23 08:17:02', 2, 2, '压力管理的核心在于识别压力源、评估压力水平和采取有效的应对策略。青少年常见的压力源包括学业压力、人际关系压力和自我期望压力。', 75, 'vec_004');
INSERT INTO `document_chunk` VALUES (5, '2026-09-16 08:20:33', 3, 1, '焦虑是一种以担忧、紧张和不安为主要特征的情绪状态。青少年焦虑的常见表现包括过度担心、注意力难以集中、睡眠困难、身体紧张等。', 80, 'vec_005');
INSERT INTO `document_chunk` VALUES (6, '2026-09-16 08:20:33', 3, 2, '抑郁情绪的主要表现包括持续的情绪低落、兴趣减退、精力下降、自我评价降低等。如果这些症状持续两周以上，建议寻求专业帮助。', 76, 'vec_006');
INSERT INTO `document_chunk` VALUES (7, '2026-10-02 08:27:35', 5, 1, '自我认同是指个体对\"我是谁\"这一问题的回答，包括对自身价值观、信念、目标和社会角色的认识与整合。心理学家埃里克森认为，青少年期是自我认同发展的关键阶段。', 92, 'vec_007');
INSERT INTO `document_chunk` VALUES (8, '2026-10-02 08:27:35', 5, 2, '根据马西亚的理论，青少年的自我认同可以分为四种状态：认同达成、认同延缓、认同早闭和认同混乱。认同达成是最成熟的状态，经过充分探索后做出了明确的承诺。', 88, 'vec_008');
INSERT INTO `document_chunk` VALUES (9, '2026-10-02 08:27:35', 5, 3, '影响自我认同发展的因素包括家庭环境、同伴关系、学校教育和社会文化。温暖、支持且鼓励独立的家庭环境有利于青少年的认同发展。', 74, 'vec_009');
INSERT INTO `document_chunk` VALUES (10, '2026-10-02 08:27:35', 5, 4, '促进健康自我认同发展的建议：鼓励探索、尊重个体差异、提供支持而非控制、培养批判性思维、建立积极的自我对话。', 62, 'vec_010');
INSERT INTO `document_chunk` VALUES (11, '2026-09-25 08:31:06', 6, 1, '青少年时期的人际关系发生了显著变化，社交圈逐渐从以家庭为中心转向以同伴为中心。友谊变得更加深入和复杂，青少年开始追求真正的情感连接和相互理解。', 85, 'vec_011');
INSERT INTO `document_chunk` VALUES (12, '2026-09-25 08:31:06', 6, 2, '常见的人际困扰包括社交焦虑、同伴压力、校园欺凌和友谊冲突。社交焦虑表现为害怕在社交场合中被评价或拒绝，回避社交活动。', 78, 'vec_012');
INSERT INTO `document_chunk` VALUES (13, '2026-09-25 08:31:06', 6, 3, '核心社交技能包括积极倾听、清晰表达、同理心、冲突解决和边界意识。用\"我\"开头的句式表达感受，比如\"我感到不舒服，因为……\"。', 80, 'vec_013');
INSERT INTO `document_chunk` VALUES (14, '2026-09-25 08:31:06', 6, 4, '改善人际关系的实用方法：主动社交、发展共同兴趣、练习感恩、管理社交媒体使用、在困扰严重时寻求专业帮助。', 60, 'vec_014');
INSERT INTO `document_chunk` VALUES (15, '2026-09-18 08:34:37', 7, 1, '考试焦虑是指在考试前或考试过程中产生的过度紧张、担忧和恐惧的情绪反应。适度的焦虑可以提高注意力和学习效率，但过度的焦虑则会严重影响考试表现。', 88, 'vec_015');
INSERT INTO `document_chunk` VALUES (16, '2026-09-18 08:34:37', 7, 2, '考试焦虑的成因包括过高的自我期望、外部压力、准备不充分、消极的自我评价和过去的失败经历。越焦虑越无法集中精力学习，容易形成恶性循环。', 82, 'vec_016');
INSERT INTO `document_chunk` VALUES (17, '2026-09-18 08:34:37', 7, 3, '应对考试焦虑的认知调整方法：用合理的想法替代灾难化思维，关注过程而非结果，接纳适度焦虑。放松技巧包括腹式呼吸法、渐进性肌肉放松和正念冥想。', 90, 'vec_017');
INSERT INTO `document_chunk` VALUES (18, '2026-09-18 08:34:37', 7, 4, '学业压力的长期管理策略：使用番茄工作法进行时间管理、设定SMART目标、建立学习支持系统、培养兴趣爱好保持生活平衡。', 72, 'vec_018');
INSERT INTO `document_chunk` VALUES (19, '2026-09-18 08:34:37', 7, 5, '考试当天的应对策略：提前到达考场、考前做深呼吸、先浏览整张试卷从有把握的题目开始、遇到不会的题先跳过、考完一科不对答案。', 76, 'vec_019');
INSERT INTO `document_chunk` VALUES (20, '2026-09-11 08:38:08', 8, 1, '睡眠对青少年的身心发展至关重要。青少年每天需要8-10小时的睡眠。睡眠不足会导致学习能力下降、情绪问题增加、免疫力下降和行为问题。', 82, 'vec_020');
INSERT INTO `document_chunk` VALUES (21, '2026-09-11 08:38:08', 8, 2, '青少年常见的睡眠问题包括入睡困难、睡眠节律紊乱、睡眠质量差和手机依赖性失眠。睡前长时间使用手机，蓝光会抑制褪黑素分泌，导致难以入睡。', 86, 'vec_021');
INSERT INTO `document_chunk` VALUES (22, '2026-09-11 08:38:08', 8, 3, '改善睡眠的方法：建立规律作息时间、创造良好睡眠环境、睡前30分钟停止使用电子设备、限制下午3点后的咖啡因摄入、白天适度运动。', 78, 'vec_022');
INSERT INTO `document_chunk` VALUES (23, '2026-09-11 08:38:08', 8, 4, '情绪调节的基本方法包括情绪觉察、认知重评、表达性写作、身体活动和社会支持。学会识别和命名自己的情绪是情绪调节的第一步。', 74, 'vec_023');
INSERT INTO `document_chunk` VALUES (24, '2026-09-11 08:38:08', 8, 5, '情绪急救方法：5-4-3-2-1接地技术、冰水刺激、蝴蝶拥抱、安全空间想象。这些方法可以在情绪特别强烈时帮助快速平复。', 68, 'vec_024');
INSERT INTO `document_chunk` VALUES (25, '2026-10-04 08:41:39', 9, 1, '心理危机是指个体面临突发或重大生活事件时，原有的应对方式无法有效处理当前困境，导致心理平衡被打破的状态。常见触发事件包括考试失利、关系破裂、家庭变故等。', 94, 'vec_025');
INSERT INTO `document_chunk` VALUES (26, '2026-10-04 08:41:39', 9, 2, '心理危机的预警信号包括：持续情绪低落、社交退缩、学习成绩突然下降、说出\"活着没意思\"等话语、不明原因的身体疼痛、自我伤害行为。', 82, 'vec_026');
INSERT INTO `document_chunk` VALUES (27, '2026-10-04 08:41:39', 9, 3, '自我保护方法：建立至少3人的安全网络、学会求助、制定安全计划、在情绪极度低落时远离危险物品、保持日常生活规律。', 66, 'vec_027');
INSERT INTO `document_chunk` VALUES (28, '2026-10-04 08:41:39', 9, 4, '帮助处于危机中的同伴：认真倾听、直接但温和地询问、不保守涉及安全的秘密、陪伴但不替代专业帮助、鼓励寻求专业支持。', 70, 'vec_028');
INSERT INTO `document_chunk` VALUES (29, '2026-10-04 08:41:39', 9, 5, '重要求助资源：全国心理援助热线400-161-9995、生命热线400-821-1215。每一个生命都是珍贵的，困难是暂时的，帮助就在身边。', 72, 'vec_029');
INSERT INTO `document_chunk` VALUES (41, '2026-09-27 08:45:10', 10, 1, '亲子沟通与家庭关系改善\n\n一、青少年期亲子关系的变化\n\n进入青春期后，亲子关系会经历一系列自然的变化。青少年开始追求独立和自主，对父母的权威产生质疑，这是正常的发展过程。然而，这种变化常常导致亲子之间的冲突和误解。\n\n常见的亲子冲突领域：\r\n- 学业期望：父母对成绩的要求与青少年的实际能力或兴趣不匹配\r\n- 生活习惯：作息时间、手机使用、房间整理等日常事务\r\n- 社交自由：交友选择、外出时间、隐私空间\r\n- 价值观差异：对未来规划、兴趣爱好、穿着打扮的不同看法\n\n二、理解父母的角度', 243, '073cbcb2-1926-4842-af3e-75c6fcd90b71');
INSERT INTO `document_chunk` VALUES (42, '2026-09-27 08:45:10', 10, 2, '很多时候，亲子冲突源于双方的误解。父母的管教行为背后通常是对孩子的关心和担忧：\r\n- 催促学习可能是因为担心孩子的未来\r\n- 限制手机使用可能是担心影响视力和学习\r\n- 询问交友情况可能是出于安全考虑\r\n- 唠叨可能是不知道如何更好地表达关心\n\n理解父母的出发点，并不意味着要完全接受他们的方式，而是为有效沟通创造基础。\n\n三、有效的亲子沟通技巧\n\n1. 选择合适的时机：避免在双方情绪激动时讨论敏感话题。可以选择饭后散步、一起做家务等轻松的场景。\n\n2. 使用\"我\"的表达方式：\r\n- 不要说：\"你从来不理解我！\"\r\n- 可以说：\"当我的想法没有被听到时，我感到很沮丧。\"', 287, 'ba185ffa-e9c4-4309-a00f-1eb0ddd563a4');
INSERT INTO `document_chunk` VALUES (43, '2026-09-27 08:45:10', 10, 3, '3. 具体而非笼统：\r\n- 不要说：\"你管得太多了！\"\r\n- 可以说：\"我希望周末能有2个小时自由安排的时间。\"\n\n4. 先倾听再表达：在表达自己的观点之前，先认真听完父母的想法，并用自己的话复述确认理解正确。\n\n5. 寻找共同点：在分歧中寻找双方都能接受的方案，比如\"我可以先完成作业，然后用1小时玩手机\"。\n\n四、处理亲子冲突的步骤', 168, '99bec663-df81-4868-b123-ad9a240dd551');
INSERT INTO `document_chunk` VALUES (44, '2026-09-27 08:45:10', 10, 4, '1. 暂停：当冲突升级时，先暂停对话。可以说\"我需要冷静一下，我们过一会儿再谈\"。\r\n2. 反思：冷静后想想冲突的核心问题是什么，自己的需求是什么，父母的担忧是什么。\r\n3. 沟通：选择合适的时机重新开始对话，用平和的语气表达自己的想法和感受。\r\n4. 协商：提出具体的解决方案，愿意做出适当的妥协。\r\n5. 执行：按照约定执行，用行动证明自己的责任感。\n\n五、改善家庭关系的日常实践', 192, 'dc01d4dd-ba7a-401d-8774-1abf3d4c1f3b');
INSERT INTO `document_chunk` VALUES (45, '2026-09-27 08:45:10', 10, 5, '1. 表达感谢：对父母的付出表示感谢，哪怕只是一句简单的\"谢谢妈妈做的饭\"。\r\n2. 分享日常：主动和父母分享学校里有趣的事情，让他们了解你的生活。\r\n3. 参与家庭活动：一起做饭、看电影、运动等，增加积极的互动时间。\r\n4. 承担家务：主动承担一些力所能及的家务，展现自己的成长和责任感。\r\n5. 尊重边界：在追求独立的同时，也尊重家庭的规则和父母的感受。\n\n六、当家庭问题超出自己能力时\n\n如果家庭中存在以下情况，请及时向信任的成年人或专业机构求助：\r\n- 家庭暴力（身体或精神上的虐待）\r\n- 父母严重的心理健康问题\r\n- 长期被忽视基本需求\r\n- 家庭冲突严重影响了你的学习和生活', 294, '92ba1a2c-f0aa-49db-9ad6-c4d1855e6262');
INSERT INTO `document_chunk` VALUES (46, '2026-09-27 08:45:10', 10, 6, '记住，寻求帮助不是背叛家庭，而是为了让整个家庭变得更好。', 28, 'f254d2d0-5fcc-4e50-a3fb-60fb116903fc');
INSERT INTO `document_chunk` VALUES (52, '2026-09-13 08:52:12', 12, 1, '考试焦虑与学业压力应对指南\n\n一、认识考试焦虑\n\n考试焦虑是指在考试前或考试过程中产生的过度紧张、担忧和恐惧的情绪反应。适度的焦虑可以提高注意力和学习效率，但过度的焦虑则会严重影响考试表现和身心健康。\n\n考试焦虑的常见表现：\r\n- 认知方面：注意力难以集中、记忆力下降、思维混乱、脑子一片空白\r\n- 情绪方面：紧张不安、烦躁易怒、恐惧害怕、情绪低落\r\n- 身体方面：心跳加速、手心出汗、胃部不适、头痛头晕、失眠\r\n- 行为方面：坐立不安、逃避学习、考试时频繁看时间\n\n二、考试焦虑的成因', 243, '27f3aac8-a55f-4908-9aaf-c3a4916c515b');
INSERT INTO `document_chunk` VALUES (53, '2026-09-13 08:52:12', 12, 2, '1. 过高的自我期望：给自己设定不切实际的目标，害怕达不到预期。\r\n2. 外部压力：来自父母、老师的期望和比较，同学之间的竞争氛围。\r\n3. 准备不充分：复习不到位导致的不自信，越焦虑越无法集中精力学习，形成恶性循环。\r\n4. 消极的自我评价：总是想着\"我一定考不好\"\"我不够聪明\"等消极想法。\r\n5. 过去的失败经历：之前考试失利的阴影，导致对考试产生条件性恐惧。\n\n三、应对考试焦虑的方法\n\n1. 认知调整\r\n- 用合理的想法替代灾难化思维：\"即使这次考不好，也不代表世界末日\"\r\n- 关注过程而非结果：\"我只需要尽力发挥自己的水平\"\r\n- 接纳适度焦虑：\"紧张是正常的，说明我在乎这件事\"', 297, '132bb6fd-9c62-47f2-ac0d-332a8b536e67');
INSERT INTO `document_chunk` VALUES (54, '2026-09-13 08:52:12', 12, 3, '2. 放松技巧\r\n- 腹式呼吸法：缓慢深呼吸，吸气时腹部鼓起，呼气时腹部收缩，每次练习5-10分钟\r\n- 渐进性肌肉放松：从脚趾开始，依次紧绷和放松全身各部位的肌肉\r\n- 正念冥想：将注意力集中在当下的呼吸上，不评判地观察自己的想法和感受\n\n3. 学习策略优化\r\n- 制定合理的复习计划，将大目标分解为小步骤\r\n- 使用间隔重复法，定期回顾已学内容\r\n- 通过模拟考试熟悉考试环境和节奏\r\n- 保证充足的睡眠和适当的运动\n\n四、学业压力的长期管理', 223, '31ceef8e-9109-479f-aecd-a710df20f63b');
INSERT INTO `document_chunk` VALUES (55, '2026-09-13 08:52:12', 12, 4, '1. 时间管理：使用番茄工作法等工具，合理分配学习和休息时间。\r\n2. 目标设定：设定SMART目标（具体、可衡量、可达成、相关、有时限）。\r\n3. 建立支持系统：与同学组成学习小组，互相鼓励和帮助。\r\n4. 培养兴趣爱好：在学习之余发展自己的兴趣，保持生活的平衡。\r\n5. 学会求助：当压力超出自己的承受范围时，及时向老师、家长或心理咨询师寻求帮助。\n\n五、考试当天的应对策略', 189, 'ceea892e-f361-421f-86d7-1240f90a6fc5');
INSERT INTO `document_chunk` VALUES (56, '2026-09-13 08:52:12', 12, 5, '1. 提前到达考场，给自己留出适应环境的时间\r\n2. 考前做几次深呼吸，让身体放松下来\r\n3. 先浏览整张试卷，从自己最有把握的题目开始做\r\n4. 遇到不会的题目先跳过，不要在一道题上花太多时间\r\n5. 考完一科不要对答案，专注准备下一科', 119, 'c9daa1c2-b3dd-4f6c-9ab6-b5468a44db5f');

-- Document 4: sourced project summary; generate vector IDs locally before use.
INSERT INTO `document_chunk` VALUES (57, '2026-11-11 11:44:32', 4, 1, '# 心理健康自我保健知识\n\n资料性质：项目根据公开科普资料整理的学习稿，经 AI 辅助整理；不是机构原文，未经临床专家审定，不用于诊断、治疗或个体用药决策。\n整理与链接核对日期：2026-11-11。\n阅读方法：各节末尾的 S1、S2 对应文末参考来源；具体内容和适用范围以链接中的原文为准。\n\n## 一、认识心理保健与支持环境\n\n心理健康不只是个人意志的问题。家庭、学校和社区的条件，以及人与人之间的支持关系，都与心理状态有关。青少年需要适合成长的环境，遇到困扰时可以把家庭、学校和社区中的支持一并考虑，而非独自承担。依据：S1。\n\n## 二、把活动、睡眠和压力应对一起关注', 288, NULL);
INSERT INTO `document_chunk` VALUES (58, '2026-11-11 11:44:32', 4, 2, '适合自身情况的身体活动可以成为情绪保健的一部分。睡眠持续不佳值得重视；减压时可检查想法是否符合实际、寻求人际支持，并选择有益身心的生活习惯。这些科普方法不能代替对持续困扰的专业评估。依据：S2。\n\n## 三、怎样理解主动求助\n\n寻求专业支持不等于软弱，也不能据此断定自己患病。可以向正规医疗机构或专业心理服务了解评估与支持方式；已有精神类处方药的使用者，应把变化反馈给医生，用药调整由医生指导。依据：S2。\n\n## 参考来源', 212, NULL);
INSERT INTO `document_chunk` VALUES (59, '2026-11-11 11:44:32', 4, 3, '- [S1｜世界卫生组织：精神卫生（页面日期 2026-09-11）](https://www.who.int/zh/news-room/fact-sheets/detail/mental-health-strengthening-our-response)\n- [S2｜中国科学院心理研究所：心理健康素养十条（2018 年版；网页发布 2019-01-12）](https://mhl.psych.ac.cn/index/index.html)', 223, NULL);
INSERT INTO `document_chunk` VALUES (60, '2026-11-11 11:44:32', 4, 4, '引用说明：S2 页面介绍了国家卫健委疾控局组织编制的十条科普内容。本稿只作简要归纳，不代表上述机构对本项目的审核、背书或授权；项目整理与表述由项目负责。', 76, NULL);

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` int NULL DEFAULT NULL COMMENT '用户',
  `favorite_type` int NULL DEFAULT NULL COMMENT '收藏类型枚举(1:科普文章,2:公告)',
  `target_id` int NULL DEFAULT NULL COMMENT '收藏目标',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9600022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (1, '2026-09-30 08:03:31', 2, 1, 1);
INSERT INTO `favorite` VALUES (2, '2026-09-23 08:07:02', 2, 1, 2);
INSERT INTO `favorite` VALUES (3, '2026-09-16 08:10:33', 3, 1, 3);
INSERT INTO `favorite` VALUES (5, '2026-10-02 08:17:35', 5, 2, 1);
INSERT INTO `favorite` VALUES (8, '2026-09-11 08:28:08', 4, 1, 5);
INSERT INTO `favorite` VALUES (9600001, '2026-10-07 10:25:00', 9100007, 1, 2);
INSERT INTO `favorite` VALUES (9600002, '2026-10-07 12:25:00', 9100001, 1, 3);
INSERT INTO `favorite` VALUES (9600003, '2026-10-07 14:25:00', 9100003, 1, 4);
INSERT INTO `favorite` VALUES (9600004, '2026-10-06 10:25:00', 9100006, 1, 5);
INSERT INTO `favorite` VALUES (9600005, '2026-10-06 12:25:00', 9100001, 1, 1);
INSERT INTO `favorite` VALUES (9600006, '2026-10-06 14:25:00', 9100004, 1, 2);
INSERT INTO `favorite` VALUES (9600007, '2026-10-05 10:25:00', 9100005, 1, 3);
INSERT INTO `favorite` VALUES (9600008, '2026-10-05 12:25:00', 9100001, 1, 4);
INSERT INTO `favorite` VALUES (9600009, '2026-10-05 14:25:00', 9100005, 1, 5);
INSERT INTO `favorite` VALUES (9600010, '2026-10-04 10:25:00', 9100004, 1, 1);
INSERT INTO `favorite` VALUES (9600011, '2026-10-04 12:25:00', 9100001, 1, 2);
INSERT INTO `favorite` VALUES (9600012, '2026-10-04 14:25:00', 9100002, 1, 3);
INSERT INTO `favorite` VALUES (9600014, '2026-10-03 12:25:00', 9100001, 1, 5);
INSERT INTO `favorite` VALUES (9600016, '2026-10-02 10:25:00', 9100002, 1, 2);
INSERT INTO `favorite` VALUES (9600018, '2026-10-02 14:25:00', 9100002, 1, 4);

-- ----------------------------
-- Table structure for hot_question
-- ----------------------------
DROP TABLE IF EXISTS `hot_question`;
CREATE TABLE `hot_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '热门问题主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `question_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '问题文本',
  `ask_count` int NULL DEFAULT NULL COMMENT '提问次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9800008 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '热门问题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hot_question
-- ----------------------------
INSERT INTO `hot_question` VALUES (1, '2026-09-30 08:03:31', '考试前总是很紧张焦虑怎么办？', 57);
INSERT INTO `hot_question` VALUES (2, '2026-09-23 08:07:02', '和同学发生矛盾了该怎么处理？', 44);
INSERT INTO `hot_question` VALUES (3, '2026-09-16 08:10:33', '感觉学习压力很大，经常失眠怎么办？', 39);
INSERT INTO `hot_question` VALUES (4, '2026-09-09 08:14:04', '总是感到不开心，对什么都提不起兴趣', 35);
INSERT INTO `hot_question` VALUES (5, '2026-10-02 08:17:35', '父母不理解我，沟通总是吵架怎么办？', 32);
INSERT INTO `hot_question` VALUES (9800001, '2026-10-07 09:00:00', '最近考试前总是紧张，怎样安排复习和休息？', 3);
INSERT INTO `hot_question` VALUES (9800002, '2026-10-06 09:00:00', '最近和同学沟通不太顺利，怎样表达自己的想法？', 3);
INSERT INTO `hot_question` VALUES (9800003, '2026-10-05 09:00:00', '最近作业和课外活动很多，怎样做好时间管理？', 3);
INSERT INTO `hot_question` VALUES (9800004, '2026-10-04 09:00:00', '最近睡前总想着学习的事情，怎样记录和整理心情？', 3);
INSERT INTO `hot_question` VALUES (9800005, '2026-10-03 09:00:00', '最近想和父母商量学习计划，怎样开始这次沟通？', 3);
INSERT INTO `hot_question` VALUES (9800006, '2026-10-02 09:00:00', '最近参加集体活动有些害羞，怎样逐步认识新朋友？', 3);
INSERT INTO `hot_question` VALUES (9800007, '2026-10-01 09:00:00', '最近对自己的长处不太确定，怎样了解和接纳自己？', 3);

-- ----------------------------
-- Table structure for knowledge_document
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_document`;
CREATE TABLE `knowledge_document`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '知识文档主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `knowledge_type_id` int NULL DEFAULT NULL COMMENT '知识分类',
  `doc_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文档标题',
  `doc_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文档描述',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件路径',
  `file_format` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件格式',
  `file_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件大小',
  `chunk_count` int NULL DEFAULT NULL COMMENT '分块数量',
  `vector_status_type` int NULL DEFAULT NULL COMMENT '向量化状态枚举(1:待处理,2:处理中,3:已完成,4:失败)',
  `vector_time` datetime NULL DEFAULT NULL COMMENT '向量化完成时间',
  `uploader_id` int NULL DEFAULT NULL COMMENT '上传人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uploader_id`(`uploader_id` ASC) USING BTREE,
  INDEX `knowledge_type_id`(`knowledge_type_id` ASC) USING BTREE,
  CONSTRAINT `knowledge_document_ibfk_1` FOREIGN KEY (`uploader_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `knowledge_document_ibfk_2` FOREIGN KEY (`knowledge_type_id`) REFERENCES `knowledge_type` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识文档表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of knowledge_document
-- ----------------------------
INSERT INTO `knowledge_document` VALUES (1, '2026-09-30 08:03:31', 1, '青少年心理发展特征概述', '系统介绍12-18岁青少年心理发展的主要特征与规律', NULL, 'PDF', '2.3MB', 2, 3, '2026-09-30 08:33:31', 1);
INSERT INTO `knowledge_document` VALUES (2, '2026-09-23 08:07:02', 2, '情绪调节与压力管理指南', '提供科学的情绪调节方法和压力管理策略', NULL, 'PDF', '1.8MB', 2, 3, '2026-09-23 08:37:02', 1);
INSERT INTO `knowledge_document` VALUES (3, '2026-09-16 08:10:33', 3, '青少年常见心理问题识别手册', '帮助识别焦虑、抑郁、社交恐惧等常见心理问题的表现与应对', NULL, 'TXT', '1.2MB', 2, 3, '2026-09-16 08:40:33', 1);
INSERT INTO `knowledge_document` VALUES (4, '2026-09-09 08:14:04', 5, '心理健康自我保健知识', '项目根据世界卫生组织及中科院心理研究所公开科普资料整理的学习稿，2026-11-11补充；非机构原文，未经临床专家审定。各节标注来源，完整链接见文末。', '/api/1000000040/心理健康自我保健知识.md', 'MD', '1.9KB', 4, 1, NULL, 1);
INSERT INTO `knowledge_document` VALUES (5, '2026-10-02 08:17:35', 1, '青少年自我认同与身份发展', '介绍青少年自我认同的四种状态及影响因素，提供促进健康认同发展的建议', 'http://localhost:7406/100000001/青少年自我认同与身份发展.txt', 'TXT', '3.2KB', 4, 3, '2026-10-02 08:47:35', 1);
INSERT INTO `knowledge_document` VALUES (6, '2026-09-25 08:21:06', 1, '青少年人际交往与社交技能', '涵盖青少年人际关系特点、常见困扰、核心社交技能及改善方法', 'http://localhost:7406/100000002/青少年人际交往与社交技能.txt', 'TXT', '2.8KB', 4, 3, '2026-09-25 08:51:06', 1);
INSERT INTO `knowledge_document` VALUES (7, '2026-09-18 08:24:37', 2, '考试焦虑与学业压力应对指南', '系统讲解考试焦虑的成因、应对方法和学业压力的长期管理策略', 'http://localhost:7406/100000003/考试焦虑与学业压力应对指南.txt', 'TXT', '3.5KB', 5, 3, '2026-09-18 08:54:37', 1);
INSERT INTO `knowledge_document` VALUES (8, '2026-09-11 08:28:08', 2, '青少年睡眠健康与情绪调节', '介绍青少年睡眠问题及改善方法，以及情绪调节的基本技巧和急救方法', 'http://localhost:7406/100000004/青少年睡眠健康与情绪调节.txt', 'TXT', '3.0KB', 5, 3, '2026-09-11 08:58:08', 1);
INSERT INTO `knowledge_document` VALUES (9, '2026-10-04 08:31:39', 4, '心理危机识别与自我保护', '帮助青少年识别心理危机预警信号，掌握自我保护方法和求助资源', 'http://localhost:7406/100000005/心理危机识别与自我保护.txt', 'TXT', '3.1KB', 5, 3, '2026-10-04 09:01:39', 1);
INSERT INTO `knowledge_document` VALUES (10, '2026-09-27 08:35:10', 5, '亲子沟通与家庭关系改善', '提供有效的亲子沟通技巧和家庭关系改善方法，帮助青少年处理家庭冲突', 'http://localhost:7406/100000006/亲子沟通与家庭关系改善.txt', 'TXT', '3.4KB', 6, 3, '2026-09-27 09:05:10', 1);
INSERT INTO `knowledge_document` VALUES (12, '2026-09-13 08:42:12', 2, '考试焦虑与学业压力应对指南', '本文档详细阐述了考试焦虑的定义、表现及成因，并提供了认知调整、放松技巧、学习策略优化等多方面的具体应对方法。同时，也涵盖了学业压力的长期管理策略和考试当天的实用技巧，旨在帮助个体有效管理因考试和学业产生的焦虑与压力，提升应对能力。', 'http://localhost:7406/210299771/考试焦虑与学业压力应对指南.txt', 'TXT', '2.8KB', 5, 3, '2026-09-13 09:12:12', 1);

-- ----------------------------
-- Table structure for knowledge_type
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_type`;
CREATE TABLE `knowledge_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '知识分类主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名称',
  `type_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of knowledge_type
-- ----------------------------
INSERT INTO `knowledge_type` VALUES (1, '2026-09-25 08:20:06', '青少年心理发展', '青少年心理发展阶段与特征相关知识');
INSERT INTO `knowledge_type` VALUES (2, '2026-09-11 08:27:08', '情绪与压力管理', '情绪调节、压力应对与放松技巧');
INSERT INTO `knowledge_type` VALUES (3, '2026-09-16 08:09:33', '常见心理问题', '焦虑、抑郁、社交恐惧等常见心理问题');
INSERT INTO `knowledge_type` VALUES (4, '2026-09-09 08:14:04', '心理危机干预', '心理危机识别与干预方法');
INSERT INTO `knowledge_type` VALUES (5, '2026-09-09 08:13:04', '心理健康教育', '心理健康基础知识与自我保健');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告信息主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `notice_type_id` int NULL DEFAULT NULL COMMENT '公告分类',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公告标题',
  `notice_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '公告内容',
  `publisher_id` int NULL DEFAULT NULL COMMENT '发布人',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `publisher_id`(`publisher_id` ASC) USING BTREE,
  INDEX `notice_type_id`(`notice_type_id` ASC) USING BTREE,
  CONSTRAINT `notice_ibfk_1` FOREIGN KEY (`publisher_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `notice_ibfk_2` FOREIGN KEY (`notice_type_id`) REFERENCES `notice_type` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '2026-09-30 08:03:31', 1, '平台正式上线公告', '<p>欢迎使用青少年心理健康知识服务型问答系统！本平台基于RAG技术，为青少年提供专业、即时、私密的心理健康智能问答服务。如有任何问题，请随时联系我们。</p>', 1, '2026-09-30 08:33:31');
INSERT INTO `notice` VALUES (2, '2026-09-23 08:07:02', 2, '心理健康周活动通知', '<p>为提升青少年心理健康意识，本平台将于本周开展心理健康主题活动，包括在线心理测评、专家讲座等，欢迎同学们积极参与。</p>', 1, '2026-09-23 08:37:02');
INSERT INTO `notice` VALUES (3, '2026-09-16 08:10:33', 3, '知识库更新通知', '<p>本平台知识库已新增\"青少年情绪管理\"\"学业压力应对\"等多个专题的专业知识文档，问答质量将进一步提升，欢迎体验。</p>', 1, '2026-09-16 08:40:33');

-- ----------------------------
-- Table structure for notice_type
-- ----------------------------
DROP TABLE IF EXISTS `notice_type`;
CREATE TABLE `notice_type`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告分类主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类名称',
  `type_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice_type
-- ----------------------------
INSERT INTO `notice_type` VALUES (1, '2026-09-30 08:02:31', '系统公告', '平台系统相关通知');
INSERT INTO `notice_type` VALUES (2, '2026-09-23 08:06:02', '活动通知', '心理健康活动相关通知');
INSERT INTO `notice_type` VALUES (3, '2026-09-16 08:09:33', '知识更新', '知识库内容更新通知');

-- ----------------------------
-- Table structure for qa_feedback
-- ----------------------------
DROP TABLE IF EXISTS `qa_feedback`;
CREATE TABLE `qa_feedback`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '问答反馈主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_id` int NULL DEFAULT NULL COMMENT '用户',
  `chat_message_id` int NULL DEFAULT NULL COMMENT '关联消息',
  `rating` int NULL DEFAULT NULL COMMENT '评分',
  `feedback_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '反馈内容',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `chat_message_id`(`chat_message_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `qa_feedback_ibfk_1` FOREIGN KEY (`chat_message_id`) REFERENCES `chat_message` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `qa_feedback_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9400022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '问答反馈表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qa_feedback
-- ----------------------------
INSERT INTO `qa_feedback` VALUES (1, '2026-09-30 08:07:31', 2, 2, 5, '回答很专业，给了我很多实用的建议，感觉好多了');
INSERT INTO `qa_feedback` VALUES (2, '2026-09-30 08:09:31', 2, 4, 4, '建议很有帮助，列清单的方法我会试试');
INSERT INTO `qa_feedback` VALUES (3, '2026-09-23 08:15:02', 3, 6, 5, '说得很对，我决定主动找同学沟通一下');
INSERT INTO `qa_feedback` VALUES (4, '2026-09-09 08:28:04', 5, 12, 5, '谢谢温暖的回答，我会试着和心理老师聊聊');
INSERT INTO `qa_feedback` VALUES (5, '2026-09-16 08:22:33', 4, 10, 5, '满意');
INSERT INTO `qa_feedback` VALUES (6, '2026-10-02 08:39:35', 4, 20, 5, '满意');
INSERT INTO `qa_feedback` VALUES (9400001, '2026-10-07 10:03:00', 9100007, 9300002, 2, '建议比较笼统，希望增加具体步骤');
INSERT INTO `qa_feedback` VALUES (9400002, '2026-10-07 12:03:00', 9100001, 9300004, 3, '提供了参考，我还需要进一步沟通');
INSERT INTO `qa_feedback` VALUES (9400003, '2026-10-07 14:03:00', 9100003, 9300006, 4, '思路清晰，准备和家人一起讨论');
INSERT INTO `qa_feedback` VALUES (9400004, '2026-10-06 10:03:00', 9100006, 9300008, 5, '回答容易理解，帮助我整理了想法');
INSERT INTO `qa_feedback` VALUES (9400005, '2026-10-06 12:03:00', 9100001, 9300010, 1, '希望回答更贴合我的具体情况');
INSERT INTO `qa_feedback` VALUES (9400006, '2026-10-06 14:03:00', 9100004, 9300012, 2, '建议比较笼统，希望增加具体步骤');
INSERT INTO `qa_feedback` VALUES (9400007, '2026-10-05 10:03:00', 9100005, 9300014, 3, '提供了参考，我还需要进一步沟通');
INSERT INTO `qa_feedback` VALUES (9400008, '2026-10-05 12:03:00', 9100001, 9300016, 4, '思路清晰，准备和家人一起讨论');
INSERT INTO `qa_feedback` VALUES (9400009, '2026-10-05 14:03:00', 9100005, 9300018, 5, '回答容易理解，帮助我整理了想法');
INSERT INTO `qa_feedback` VALUES (9400010, '2026-10-04 10:03:00', 9100004, 9300020, 1, '希望回答更贴合我的具体情况');
INSERT INTO `qa_feedback` VALUES (9400011, '2026-10-04 12:03:00', 9100001, 9300022, 2, '建议比较笼统，希望增加具体步骤');
INSERT INTO `qa_feedback` VALUES (9400012, '2026-10-04 14:03:00', 9100002, 9300024, 3, '提供了参考，我还需要进一步沟通');
INSERT INTO `qa_feedback` VALUES (9400013, '2026-10-03 10:03:00', 9100003, 9300026, 4, '思路清晰，准备和家人一起讨论');
INSERT INTO `qa_feedback` VALUES (9400014, '2026-10-03 12:03:00', 9100001, 9300028, 5, '回答容易理解，帮助我整理了想法');
INSERT INTO `qa_feedback` VALUES (9400015, '2026-10-03 14:03:00', 9100001, 9300030, 1, '希望回答更贴合我的具体情况');
INSERT INTO `qa_feedback` VALUES (9400016, '2026-10-02 10:03:00', 9100002, 9300032, 2, '建议比较笼统，希望增加具体步骤');
INSERT INTO `qa_feedback` VALUES (9400017, '2026-10-02 12:03:00', 9100001, 9300034, 3, '提供了参考，我还需要进一步沟通');
INSERT INTO `qa_feedback` VALUES (9400018, '2026-10-02 14:03:00', 9100002, 9300036, 4, '思路清晰，准备和家人一起讨论');
INSERT INTO `qa_feedback` VALUES (9400019, '2026-10-01 10:03:00', 9100001, 9300038, 5, '回答容易理解，帮助我整理了想法');
INSERT INTO `qa_feedback` VALUES (9400020, '2026-10-01 12:03:00', 9100001, 9300040, 1, '希望回答更贴合我的具体情况');
INSERT INTO `qa_feedback` VALUES (9400021, '2026-10-01 14:03:00', 9100001, 9300042, 2, '建议比较笼统，希望增加具体步骤');

-- ----------------------------
-- Table structure for scale_info
-- ----------------------------
DROP TABLE IF EXISTS `scale_info`;
CREATE TABLE `scale_info`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '测评量主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `scale_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '量表名称',
  `scale_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '量表描述',
  `scale_type` int NULL DEFAULT NULL COMMENT '量表类型枚举(1:情绪评估,2:压力评估,3:人际关系,4:自我认知,5:综合评估)',
  `target_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适用人群',
  `question_count` int NULL DEFAULT NULL COMMENT '题目数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测评量表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of scale_info
-- ----------------------------
INSERT INTO `scale_info` VALUES (1, '2026-09-16 08:09:33', '青少年焦虑自评量表', '用于评估青少年焦虑情绪的严重程度，帮助了解自身焦虑状况', 1, '12-18岁青少年', 5);
INSERT INTO `scale_info` VALUES (2, '2026-09-23 08:06:02', '学业压力评估量表', '评估青少年在学业方面感受到的压力水平', 2, '初中生、高中生', 5);
INSERT INTO `scale_info` VALUES (3, '2026-09-09 08:13:04', '人际关系适应量表', '评估青少年在同伴交往中的适应能力与社交技巧', 3, '12-18岁青少年', 5);

-- ----------------------------
-- Table structure for scale_question
-- ----------------------------
DROP TABLE IF EXISTS `scale_question`;
CREATE TABLE `scale_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '测评题目主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `scale_info_id` int NULL DEFAULT NULL COMMENT '所属量表',
  `question_index` int NULL DEFAULT NULL COMMENT '题目序号',
  `question_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '题目标题',
  `question_type` int NULL DEFAULT NULL COMMENT '题目类型枚举(1:单选,2:多选)',
  `option_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '选项与分值JSON',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `scale_info_id`(`scale_info_id` ASC) USING BTREE,
  CONSTRAINT `scale_question_ibfk_1` FOREIGN KEY (`scale_info_id`) REFERENCES `scale_info` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测评题目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of scale_question
-- ----------------------------
INSERT INTO `scale_question` VALUES (1, '2026-09-16 08:09:33', 1, 1, '在过去两周内，你是否经常感到紧张或不安？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (2, '2026-09-16 08:09:33', 1, 2, '你是否容易对未来的事情感到过度担忧？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (3, '2026-09-16 08:09:33', 1, 3, '你是否因为焦虑而难以集中注意力？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (4, '2026-09-16 08:09:33', 1, 4, '你是否因为紧张而出现身体不适（如头痛、胃痛）？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (5, '2026-09-16 08:09:33', 1, 5, '你是否因为焦虑而影响了睡眠质量？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (6, '2026-09-23 08:06:02', 2, 1, '你觉得目前的学业任务量对你来说怎么样？', 1, '[{\"label\":\"很轻松\",\"score\":0},{\"label\":\"适中\",\"score\":1},{\"label\":\"有些重\",\"score\":2},{\"label\":\"非常重\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (7, '2026-09-23 08:06:02', 2, 2, '考试前你通常会感到多大程度的压力？', 1, '[{\"label\":\"没有压力\",\"score\":0},{\"label\":\"轻微压力\",\"score\":1},{\"label\":\"较大压力\",\"score\":2},{\"label\":\"极大压力\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (8, '2026-09-23 08:06:02', 2, 3, '你是否因为学业压力而减少了休闲活动？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (9, '2026-09-23 08:06:02', 2, 4, '你是否担心自己的成绩不够好？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (10, '2026-09-23 08:06:02', 2, 5, '学业压力是否影响了你和家人朋友的关系？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (11, '2026-09-09 08:13:04', 3, 1, '你在班级中是否容易交到朋友？', 1, '[{\"label\":\"非常容易\",\"score\":0},{\"label\":\"比较容易\",\"score\":1},{\"label\":\"有些困难\",\"score\":2},{\"label\":\"非常困难\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (12, '2026-09-09 08:13:04', 3, 2, '当与同学发生分歧时，你通常如何处理？', 1, '[{\"label\":\"积极沟通\",\"score\":0},{\"label\":\"尝试妥协\",\"score\":1},{\"label\":\"回避冲突\",\"score\":2},{\"label\":\"感到无助\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (13, '2026-09-09 08:13:04', 3, 3, '你是否经常感到被同学排斥或孤立？', 1, '[{\"label\":\"从不\",\"score\":0},{\"label\":\"偶尔\",\"score\":1},{\"label\":\"经常\",\"score\":2},{\"label\":\"总是\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (14, '2026-09-09 08:13:04', 3, 4, '你是否愿意在集体活动中主动参与？', 1, '[{\"label\":\"非常愿意\",\"score\":0},{\"label\":\"比较愿意\",\"score\":1},{\"label\":\"不太愿意\",\"score\":2},{\"label\":\"非常不愿意\",\"score\":3}]');
INSERT INTO `scale_question` VALUES (15, '2026-09-09 08:13:04', 3, 5, '你是否能够在需要帮助时向他人求助？', 1, '[{\"label\":\"总是可以\",\"score\":0},{\"label\":\"大多数时候\",\"score\":1},{\"label\":\"很少\",\"score\":2},{\"label\":\"从不\",\"score\":3}]');

-- ----------------------------
-- Table structure for score_rule
-- ----------------------------
DROP TABLE IF EXISTS `score_rule`;
CREATE TABLE `score_rule`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评分规则主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `scale_info_id` int NULL DEFAULT NULL COMMENT '所属量表',
  `min_score` int NULL DEFAULT NULL COMMENT '最低分',
  `max_score` int NULL DEFAULT NULL COMMENT '最高分',
  `result_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '结果标签',
  `result_interpretation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '结果解读',
  `suggestion` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '建议',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `scale_info_id`(`scale_info_id` ASC) USING BTREE,
  CONSTRAINT `score_rule_ibfk_1` FOREIGN KEY (`scale_info_id`) REFERENCES `scale_info` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评分规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of score_rule
-- ----------------------------
INSERT INTO `score_rule` VALUES (1, '2026-09-16 08:09:33', 1, 0, 4, '正常', '你的焦虑水平处于正常范围，情绪状态良好。', '继续保持积极的生活态度，适当运动和社交有助于维持良好的心理状态。');
INSERT INTO `score_rule` VALUES (2, '2026-09-16 08:09:33', 1, 5, 9, '轻度焦虑', '你存在轻度焦虑倾向，可能在某些情境下会感到紧张不安。', '建议学习一些放松技巧，如深呼吸、冥想等，保持规律作息，适当运动。');
INSERT INTO `score_rule` VALUES (3, '2026-09-16 08:09:33', 1, 10, 15, '中重度焦虑', '你的焦虑水平较高，可能已经影响到日常生活和学习。', '建议尽快与学校心理老师或专业心理咨询师沟通，寻求专业帮助和指导。');
INSERT INTO `score_rule` VALUES (4, '2026-09-23 08:06:02', 2, 0, 4, '压力正常', '你的学业压力处于正常水平，能够较好地应对学习任务。', '继续保持良好的学习习惯和时间管理能力。');
INSERT INTO `score_rule` VALUES (5, '2026-09-23 08:06:02', 2, 5, 9, '压力偏高', '你感受到的学业压力偏高，需要注意调节。', '建议合理安排学习时间，适当减少不必要的课外负担，增加休闲放松时间。');
INSERT INTO `score_rule` VALUES (6, '2026-09-23 08:06:02', 2, 10, 15, '压力过大', '你的学业压力已经过大，可能影响身心健康。', '建议与家长和老师沟通调整学习安排，必要时寻求心理咨询帮助。');
INSERT INTO `score_rule` VALUES (7, '2026-09-09 08:13:04', 3, 0, 4, '适应良好', '你的人际关系适应能力良好，能够较好地与同伴相处。', '继续保持开放友善的态度，积极参与集体活动。');
INSERT INTO `score_rule` VALUES (8, '2026-09-09 08:13:04', 3, 5, 9, '适应一般', '你在人际交往中存在一些困难，可能需要提升社交技巧。', '建议多参加集体活动，学习倾听和表达技巧，主动与他人建立联系。');
INSERT INTO `score_rule` VALUES (9, '2026-09-09 08:13:04', 3, 10, 15, '适应困难', '你在人际关系方面存在较大困难，可能感到孤立或不被理解。', '建议与学校心理老师沟通，参加社交技能训练，逐步建立信任关系。');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `creation_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `pass_word` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `image_urls` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role_type` int NULL DEFAULT NULL COMMENT '角色枚举(1:用户,2:系统)',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `nick_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9100008 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
