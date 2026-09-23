/*
 Navicat Premium Data Transfer

 Source Server         : 随便写
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : luoman_highschool

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 29/03/2024 16:06:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_archives
-- ----------------------------
DROP TABLE IF EXISTS `sys_archives`;
CREATE TABLE `sys_archives`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `year` int(11) NULL DEFAULT NULL COMMENT '年度',
  `grade_id` bigint(20) NULL DEFAULT NULL COMMENT '年级ID',
  `grade_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年级名称',
  `clazz_id` bigint(20) NULL DEFAULT NULL COMMENT '班级ID',
  `clazz_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级名称',
  `student_id` bigint(20) NULL DEFAULT NULL COMMENT '学生ID',
  `teacher_id` bigint(20) NULL DEFAULT NULL COMMENT '教师ID',
  `admin_id` bigint(20) NULL DEFAULT NULL COMMENT '管理员ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '档案名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '档案编码',
  `learning_needs` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学习需求',
  `learning_motivation` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学习动机',
  `learning_interests` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学习兴趣',
  `learning_attitude` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学习态度',
  `learning_self_confidence` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学习自信心',
  `listening` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '倾听',
  `query` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '质疑',
  `independent_thinking` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '独立思考',
  `group_cooperation_awareness` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '小组合作意识',
  `expressing_willingness_to_communicate` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '表达交流意愿',
  `record_awareness` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '记录意识',
  `self_reflection_consciousness` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自我反思意识',
  `review_organize_awareness` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '复习整理意识',
  `preview_awareness` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '预习意识',
  `disciplinary_thinking` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学科思维',
  `subject_language_expression` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '学科语言表达',
  `out_of_class_activities` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '课外学习',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '档案状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学情档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_archives
-- ----------------------------
INSERT INTO `sys_archives` VALUES (7, 2024, 3, '高一', 3, '一班', 2, NULL, 2, '2024/高一/一班/李四同学的档案', 'No.10001', 'dfafd', 'gdasfgda', 'afdsfdas', 'adsfdas', 'gasdfgasgd', 'fdasfdas', 'agdasgfd', 'fdasfda', 'gadsg', 'fdasfdas', 'fdasfda', 'dgfasgdd', 'fdasfd', 'fdasfsd', 'afddsafgd', 'afdasfd', 'afdasfed', 1, 1, 0, '2024-03-29 09:55:17', '2024-03-29 15:20:30');
INSERT INTO `sys_archives` VALUES (8, 2024, 3, '高一', 6, '二班', 13, NULL, 2, '大哥发的', '放大发大水', '发过的范德萨发的', '多撒DSAd撒', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, 0, '2024-03-29 11:20:19', '2024-03-29 14:25:56');
INSERT INTO `sys_archives` VALUES (9, 2024, 3, '高一', 3, '一班', 2, 7, NULL, '李四的学习档案', 'No.10002', '大噶的风格', '法大师傅大师傅反对', '放大是个大塞', 'fdasfdfadsf', '发大水国防大厦', '地方萨芬大是大非', '爱的色放法国大使馆大', '发大水法大士大夫', '噶第三个法大师傅', '发大水发大水', '爱国哈书法大赛', '发大水发大水', '啊大厦的说法伽师', '啊发射点发发的', '啊范德萨范德萨', '法大师傅大师傅的', '发大水法大反对', 1, 1, 0, '2024-03-29 16:01:31', '2024-03-29 16:01:31');

-- ----------------------------
-- Table structure for sys_arrange
-- ----------------------------
DROP TABLE IF EXISTS `sys_arrange`;
CREATE TABLE `sys_arrange`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clazz_id` bigint(20) NULL DEFAULT NULL COMMENT '班级ID',
  `course_id` bigint(20) NULL DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint(20) NULL DEFAULT NULL COMMENT '教师ID',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态(1:正常;0:禁用)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 99 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '教学安排表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_arrange
-- ----------------------------
INSERT INTO `sys_arrange` VALUES (81, 3, 7, 2, 1, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (82, 3, 8, 3, 2, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (83, 3, 9, 4, 3, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (84, 3, 15, 7, 4, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (85, 3, 13, 5, 5, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (86, 3, 14, 4, 6, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (87, 6, 7, 2, 1, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (88, 6, 8, 3, 2, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (89, 6, 9, 4, 3, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (90, 6, 10, 5, 4, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (91, 6, 11, 7, 5, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (92, 6, 12, 7, 6, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (93, 7, 7, 2, 1, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (94, 7, 8, 3, 2, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (95, 7, 9, 4, 3, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (96, 7, 13, 5, 4, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (97, 7, 14, 7, 5, 1, '', NULL, NULL);
INSERT INTO `sys_arrange` VALUES (98, 7, 15, 2, 6, 1, '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_clazz
-- ----------------------------
DROP TABLE IF EXISTS `sys_clazz`;
CREATE TABLE `sys_clazz`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '班级编号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '班级名称',
  `sort` int(11) NOT NULL COMMENT '班级顺序',
  `status` tinyint(1) NOT NULL COMMENT '班级状态',
  `manager_id` bigint(20) NULL DEFAULT NULL COMMENT '班级主任',
  `grade_id` bigint(20) NULL DEFAULT NULL COMMENT '所属年级',
  `clazz_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级类型',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_clazz
-- ----------------------------
INSERT INTO `sys_clazz` VALUES (3, 'One', '一班', 1, 1, 2, 3, '2', 0, '2024-03-03 18:48:06', '2024-03-17 11:08:24');
INSERT INTO `sys_clazz` VALUES (4, 'One', '一班', 2, 1, 2, 4, '3', 0, '2024-03-03 18:48:13', '2024-03-16 16:51:21');
INSERT INTO `sys_clazz` VALUES (5, 'One', '一班', 3, 1, 3, 5, '1', 0, '2024-03-03 18:48:26', '2024-03-04 15:21:14');
INSERT INTO `sys_clazz` VALUES (6, 'Two', '二班', 1, 1, 2, 3, '3', 0, '2024-03-04 15:21:31', '2024-03-17 19:44:28');
INSERT INTO `sys_clazz` VALUES (7, 'Three', '三班', 1, 1, 2, 3, '2', 0, '2024-03-19 23:10:47', '2024-03-19 23:10:47');

-- ----------------------------
-- Table structure for sys_clazz_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_clazz_student`;
CREATE TABLE `sys_clazz_student`  (
  `clazz_id` bigint(20) NOT NULL COMMENT '班级ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `year` int(11) NULL DEFAULT NULL COMMENT '年度',
  PRIMARY KEY (`clazz_id`, `student_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '班级学生关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_clazz_student
-- ----------------------------
INSERT INTO `sys_clazz_student` VALUES (3, 2, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 4, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 5, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 6, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 7, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 8, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 9, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 10, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 11, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 12, 2024);
INSERT INTO `sys_clazz_student` VALUES (3, 22, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 13, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 14, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 15, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 16, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 17, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 18, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 19, 2024);
INSERT INTO `sys_clazz_student` VALUES (6, 20, 2024);

-- ----------------------------
-- Table structure for sys_course
-- ----------------------------
DROP TABLE IF EXISTS `sys_course`;
CREATE TABLE `sys_course`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '课程名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程编码',
  `subject_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '课程类型',
  `full_score` int(11) NULL DEFAULT NULL COMMENT '满分',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '课程状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '课程表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_course
-- ----------------------------
INSERT INTO `sys_course` VALUES (7, '语文', 'Chinese', '1', 150, 1, 1, 0, '2024-03-16 15:40:51', '2024-03-17 19:41:56');
INSERT INTO `sys_course` VALUES (8, '数学', 'Math', '1', 150, 2, 1, 0, '2024-03-16 15:41:55', '2024-03-17 19:42:03');
INSERT INTO `sys_course` VALUES (9, '英语', 'English', '1', 150, 3, 1, 0, '2024-03-16 15:42:09', '2024-03-17 19:42:10');
INSERT INTO `sys_course` VALUES (10, '物理', 'Physics', '2', 100, 4, 1, 0, '2024-03-16 15:42:43', '2024-03-17 19:42:17');
INSERT INTO `sys_course` VALUES (11, '化学', 'Chemistry', '2', 100, 5, 1, 0, '2024-03-16 15:43:26', '2024-03-17 19:42:24');
INSERT INTO `sys_course` VALUES (12, '生物', 'Organism', '2', 100, 6, 1, 0, '2024-03-16 15:43:53', '2024-03-17 19:42:30');
INSERT INTO `sys_course` VALUES (13, '地理', 'Geography', '3', 100, 7, 1, 0, '2024-03-16 15:44:17', '2024-03-17 19:42:36');
INSERT INTO `sys_course` VALUES (14, '历史', 'History', '3', 100, 8, 1, 0, '2024-03-16 15:44:40', '2024-03-17 19:42:44');
INSERT INTO `sys_course` VALUES (15, '政治', 'Politics', '3', 100, 9, 1, 0, '2024-03-16 15:45:19', '2024-03-17 19:42:50');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父节点id',
  `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '父节点id路径',
  `sort` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态(1:正常;0:禁用)',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除标识(1:已删除;0:未删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 172 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '幼儿园', 0, '0', 1, 1, 0, NULL, '2024-03-13 13:39:00', 1, 1);
INSERT INTO `sys_dept` VALUES (2, '教研组', 1, '0,1', 1, 1, 0, NULL, '2024-03-13 13:42:01', 2, 2);
INSERT INTO `sys_dept` VALUES (3, '后勤部', 1, '0,1', 1, 1, 0, NULL, '2024-03-13 13:42:12', 2, 2);
INSERT INTO `sys_dept` VALUES (171, '保健部', 1, '0,1', 1, 1, 0, '2024-03-13 13:42:29', '2024-03-13 13:42:29', NULL, NULL);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型编码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项名称',
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项值',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态(1:正常;0:禁用)',
  `defaulted` tinyint(4) NULL DEFAULT 0 COMMENT '是否默认(1:是;0:否)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 145 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, 'gender', '男', '1', 1, 1, 0, NULL, '2019-05-05 13:07:52', '2022-06-12 23:20:39');
INSERT INTO `sys_dict` VALUES (2, 'gender', '女', '2', 2, 1, 0, NULL, '2019-04-19 11:33:00', '2019-07-02 14:23:05');
INSERT INTO `sys_dict` VALUES (3, 'gender', '未知', '0', 1, 1, 0, NULL, '2020-10-17 08:09:31', '2020-10-17 08:09:31');
INSERT INTO `sys_dict` VALUES (69, 'family', '父亲', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (70, 'family', '母亲', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (71, 'family', '爷爷', '3', 3, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (72, 'family', '奶奶', '4', 4, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (73, 'family', '外公', '5', 5, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (74, 'family', '外婆', '6', 6, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (75, 'family', '哥哥', '7', 7, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (76, 'family', '姐姐', '8', 8, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (122, 'subjectType', '主科', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (123, 'subjectType', '文科', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (124, 'subjectType', '理科', '3', 3, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (125, 'clazzType', '普通班', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (126, 'clazzType', '文科班', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (127, 'clazzType', '理科班', '3', 3, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (128, 'examType', '周考', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (129, 'examType', '月考', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (130, 'examType', '季度', '3', 3, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (131, 'examType', '期中', '4', 4, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (132, 'examType', '期末', '5', 5, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (133, 'semester', '上学期', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (134, 'semester', '下学期', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (135, 'scoreDegree', 'A', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (136, 'scoreDegree', 'B', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (137, 'scoreDegree', 'C', '3', 3, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (138, 'scoreDegree', 'D', '4', 4, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (139, 'scoreDegree', 'E', '5', 5, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (140, 'scoreChineseDegree', '优秀', '1', 1, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (141, 'scoreChineseDegree', '良好', '2', 2, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (142, 'scoreChineseDegree', '中等', '3', 3, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (143, 'scoreChineseDegree', '合格', '4', 4, 1, 0, '', NULL, NULL);
INSERT INTO `sys_dict` VALUES (144, 'scoreChineseDegree', '不合格', '5', 5, 1, 0, '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型编码',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0:正常;1:禁用)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `type_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '性别', 'gender', 1, NULL, '2019-12-06 19:03:32', '2022-06-12 16:21:28');
INSERT INTO `sys_dict_type` VALUES (89, '亲属关系', 'family', 1, NULL, '2024-03-04 17:13:22', '2024-03-04 17:13:22');
INSERT INTO `sys_dict_type` VALUES (101, '课程类型', 'subjectType', 1, '主科，文科，理科', '2024-03-16 15:48:43', '2024-03-16 16:09:57');
INSERT INTO `sys_dict_type` VALUES (102, '班级类型', 'clazzType', 1, NULL, '2024-03-16 16:35:33', '2024-03-16 16:35:33');
INSERT INTO `sys_dict_type` VALUES (103, '考试类型', 'examType', 1, NULL, '2024-03-17 16:02:38', '2024-03-17 16:02:38');
INSERT INTO `sys_dict_type` VALUES (104, '学期', 'semester', 1, NULL, '2024-03-18 09:31:40', '2024-03-18 09:31:40');
INSERT INTO `sys_dict_type` VALUES (105, '成绩等级', 'scoreDegree', 1, NULL, '2024-03-18 21:03:58', '2024-03-18 21:03:58');
INSERT INTO `sys_dict_type` VALUES (106, '成绩中文等级', 'scoreChineseDegree', 1, NULL, '2024-03-19 21:11:31', '2024-03-19 21:11:31');

-- ----------------------------
-- Table structure for sys_exam
-- ----------------------------
DROP TABLE IF EXISTS `sys_exam`;
CREATE TABLE `sys_exam`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '考试名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考试编码',
  `semester` int(11) NULL DEFAULT NULL COMMENT '学期',
  `year` int(11) NULL DEFAULT NULL COMMENT '考试年度',
  `exam_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '考试类型（期中、期末）',
  `exam_date` datetime NULL DEFAULT NULL COMMENT '考试日期',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '考试状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考试表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_exam
-- ----------------------------
INSERT INTO `sys_exam` VALUES (13, '2024年第一次月考', 'E10001', 1, 2024, '2', '2024-03-20 12:00:00', 1, 1, 0, NULL, NULL);
INSERT INTO `sys_exam` VALUES (15, '2024年第二次月考', 'E10002', 1, 2024, '2', '2024-04-01 12:00:00', 1, 1, 0, NULL, NULL);
INSERT INTO `sys_exam` VALUES (16, '2024年第三次月考', 'E10003', 1, 2024, '2', '2024-05-01 12:00:00', 1, 1, 0, NULL, NULL);

-- ----------------------------
-- Table structure for sys_exam_body
-- ----------------------------
DROP TABLE IF EXISTS `sys_exam_body`;
CREATE TABLE `sys_exam_body`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `grade_clazz_id` bigint(20) NOT NULL COMMENT '年级或者班级ID',
  `g_or_c` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '年级或者班级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 289 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '考试主体表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_exam_body
-- ----------------------------
INSERT INTO `sys_exam_body` VALUES (266, 13, 3, 'G');
INSERT INTO `sys_exam_body` VALUES (267, 13, 3, 'C');
INSERT INTO `sys_exam_body` VALUES (268, 13, 6, 'C');
INSERT INTO `sys_exam_body` VALUES (269, 13, 4, 'G');
INSERT INTO `sys_exam_body` VALUES (270, 13, 4, 'C');
INSERT INTO `sys_exam_body` VALUES (271, 13, 5, 'G');
INSERT INTO `sys_exam_body` VALUES (272, 13, 5, 'C');
INSERT INTO `sys_exam_body` VALUES (273, 15, 3, 'G');
INSERT INTO `sys_exam_body` VALUES (274, 15, 3, 'C');
INSERT INTO `sys_exam_body` VALUES (275, 15, 6, 'C');
INSERT INTO `sys_exam_body` VALUES (276, 15, 7, 'C');
INSERT INTO `sys_exam_body` VALUES (277, 15, 4, 'G');
INSERT INTO `sys_exam_body` VALUES (278, 15, 4, 'C');
INSERT INTO `sys_exam_body` VALUES (279, 15, 5, 'G');
INSERT INTO `sys_exam_body` VALUES (280, 15, 5, 'C');
INSERT INTO `sys_exam_body` VALUES (281, 16, 3, 'G');
INSERT INTO `sys_exam_body` VALUES (282, 16, 3, 'C');
INSERT INTO `sys_exam_body` VALUES (283, 16, 6, 'C');
INSERT INTO `sys_exam_body` VALUES (284, 16, 7, 'C');
INSERT INTO `sys_exam_body` VALUES (285, 16, 4, 'G');
INSERT INTO `sys_exam_body` VALUES (286, 16, 4, 'C');
INSERT INTO `sys_exam_body` VALUES (287, 16, 5, 'G');
INSERT INTO `sys_exam_body` VALUES (288, 16, 5, 'C');

-- ----------------------------
-- Table structure for sys_grade
-- ----------------------------
DROP TABLE IF EXISTS `sys_grade`;
CREATE TABLE `sys_grade`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '年级编号',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '年级名称',
  `sort` int(11) NOT NULL COMMENT '年级顺序',
  `status` tinyint(1) NOT NULL COMMENT '年级状态',
  `manager_id` bigint(20) NULL DEFAULT NULL COMMENT '年级主任',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '年级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_grade
-- ----------------------------
INSERT INTO `sys_grade` VALUES (3, 'SeniorOne', '高一', 1, 1, 2, 0, '2024-03-03 18:48:06', '2024-03-16 12:52:29');
INSERT INTO `sys_grade` VALUES (4, 'SeniorTwo', '高二', 2, 1, 3, 0, '2024-03-03 18:48:13', '2024-03-16 12:52:47');
INSERT INTO `sys_grade` VALUES (5, 'SeniorThree', '高三', 3, 1, 2, 0, '2024-03-03 18:48:26', '2024-03-16 12:53:26');

-- ----------------------------
-- Table structure for sys_guarder
-- ----------------------------
DROP TABLE IF EXISTS `sys_guarder`;
CREATE TABLE `sys_guarder`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '与监护人关系',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '监护人姓名',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '监护人电话',
  `work` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工作',
  `student_id` bigint(20) NULL DEFAULT NULL COMMENT '学生ID',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态(1:正常;0:禁用)',
  `defaulted` tinyint(4) NULL DEFAULT 0 COMMENT '是否默认(1:是;0:否)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 81 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '监护人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_guarder
-- ----------------------------
INSERT INTO `sys_guarder` VALUES (78, '3', '天地精华', '17758586969', '无业', 2, 1, 1, 0, '我', NULL, NULL);
INSERT INTO `sys_guarder` VALUES (79, '2', '不知道', '15768962353', '国企', 2, 2, 1, 0, '33', NULL, NULL);
INSERT INTO `sys_guarder` VALUES (80, '1', '张三丰', '12345678900', '美工', 4, 1, 1, 0, '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_hobbies
-- ----------------------------
DROP TABLE IF EXISTS `sys_hobbies`;
CREATE TABLE `sys_hobbies`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '兴趣名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '兴趣编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '兴趣状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '兴趣爱好表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_hobbies
-- ----------------------------
INSERT INTO `sys_hobbies` VALUES (7, '足球', 'football', 1, 1, 0, '2024-03-28 15:43:39', '2024-03-28 15:44:38');
INSERT INTO `sys_hobbies` VALUES (8, '篮球', 'basketball', 1, 1, 0, '2024-03-28 15:44:00', '2024-03-28 15:44:00');
INSERT INTO `sys_hobbies` VALUES (9, '羽毛球', 'badminton', 1, 1, 0, '2024-03-28 15:44:28', '2024-03-28 15:44:28');

-- ----------------------------
-- Table structure for sys_investigation
-- ----------------------------
DROP TABLE IF EXISTS `sys_investigation`;
CREATE TABLE `sys_investigation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `year` int(11) NULL DEFAULT NULL COMMENT '年度',
  `course_id` bigint(20) NULL DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint(20) NULL DEFAULT NULL COMMENT '教师ID',
  `exam_id` bigint(20) NULL DEFAULT NULL COMMENT '考试ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '问卷名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问卷编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '问卷状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问卷表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_investigation
-- ----------------------------
INSERT INTO `sys_investigation` VALUES (7, 2024, 7, 7, 13, '沈老师的语文课问卷调查一', 'No.12', 1, 1, 0, '2024-03-23 19:45:21', '2024-03-27 20:54:49');
INSERT INTO `sys_investigation` VALUES (9, 2024, 8, 7, 13, 'tea', 'dfa', 1, 1, 0, '2024-03-24 13:59:40', '2024-03-27 20:54:56');
INSERT INTO `sys_investigation` VALUES (10, 2024, 7, NULL, 13, '放大发大水', '发大水放大', 1, 1, 0, '2024-03-27 20:56:36', '2024-03-27 20:56:36');

-- ----------------------------
-- Table structure for sys_investigation_body
-- ----------------------------
DROP TABLE IF EXISTS `sys_investigation_body`;
CREATE TABLE `sys_investigation_body`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `investigation_id` bigint(20) NOT NULL COMMENT '问卷ID',
  `grade_clazz_id` bigint(20) NOT NULL COMMENT '年级或者班级ID',
  `g_or_c` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '年级或者班级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 321 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问卷主体表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_investigation_body
-- ----------------------------
INSERT INTO `sys_investigation_body` VALUES (315, 7, 3, 'G');
INSERT INTO `sys_investigation_body` VALUES (316, 7, 3, 'C');
INSERT INTO `sys_investigation_body` VALUES (317, 7, 6, 'C');
INSERT INTO `sys_investigation_body` VALUES (318, 9, 3, 'G');
INSERT INTO `sys_investigation_body` VALUES (319, 9, 3, 'C');
INSERT INTO `sys_investigation_body` VALUES (320, 9, 6, 'C');

-- ----------------------------
-- Table structure for sys_investigation_question
-- ----------------------------
DROP TABLE IF EXISTS `sys_investigation_question`;
CREATE TABLE `sys_investigation_question`  (
  `investigation_id` bigint(20) NOT NULL COMMENT '问卷ID',
  `question_id` bigint(20) NOT NULL COMMENT '问题ID',
  PRIMARY KEY (`investigation_id`, `question_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问卷问题关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_investigation_question
-- ----------------------------
INSERT INTO `sys_investigation_question` VALUES (7, 7);
INSERT INTO `sys_investigation_question` VALUES (7, 8);
INSERT INTO `sys_investigation_question` VALUES (7, 9);
INSERT INTO `sys_investigation_question` VALUES (9, 7);
INSERT INTO `sys_investigation_question` VALUES (9, 9);
INSERT INTO `sys_investigation_question` VALUES (9, 11);
INSERT INTO `sys_investigation_question` VALUES (9, 13);
INSERT INTO `sys_investigation_question` VALUES (9, 15);
INSERT INTO `sys_investigation_question` VALUES (10, 7);
INSERT INTO `sys_investigation_question` VALUES (10, 8);
INSERT INTO `sys_investigation_question` VALUES (10, 9);
INSERT INTO `sys_investigation_question` VALUES (10, 10);
INSERT INTO `sys_investigation_question` VALUES (10, 11);
INSERT INTO `sys_investigation_question` VALUES (10, 12);

-- ----------------------------
-- Table structure for sys_investigation_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_investigation_student`;
CREATE TABLE `sys_investigation_student`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `investigation_id` bigint(20) NOT NULL COMMENT '问卷ID',
  `year` int(20) NOT NULL COMMENT '年度',
  `grade_id` bigint(20) NOT NULL COMMENT '年级ID',
  `grade_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年级名称',
  `clazz_id` bigint(20) NOT NULL COMMENT '班级ID',
  `clazz_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级名称',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问卷学生关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_investigation_student
-- ----------------------------
INSERT INTO `sys_investigation_student` VALUES (1, 7, 2024, 3, '高一', 3, '一班', 2);
INSERT INTO `sys_investigation_student` VALUES (2, 7, 2024, 3, '高一', 3, '一班', 4);
INSERT INTO `sys_investigation_student` VALUES (3, 7, 2024, 3, '高一', 3, '一班', 5);
INSERT INTO `sys_investigation_student` VALUES (4, 7, 2024, 3, '高一', 3, '一班', 6);
INSERT INTO `sys_investigation_student` VALUES (5, 7, 2024, 3, '高一', 3, '一班', 7);
INSERT INTO `sys_investigation_student` VALUES (6, 7, 2024, 3, '高一', 3, '一班', 8);
INSERT INTO `sys_investigation_student` VALUES (7, 7, 2024, 3, '高一', 3, '一班', 9);
INSERT INTO `sys_investigation_student` VALUES (8, 7, 2024, 3, '高一', 3, '一班', 10);
INSERT INTO `sys_investigation_student` VALUES (9, 7, 2024, 3, '高一', 3, '一班', 11);
INSERT INTO `sys_investigation_student` VALUES (10, 7, 2024, 3, '高一', 3, '一班', 12);
INSERT INTO `sys_investigation_student` VALUES (11, 7, 2024, 3, '高一', 3, '一班', 22);
INSERT INTO `sys_investigation_student` VALUES (12, 7, 2024, 3, '高一', 6, '二班', 13);
INSERT INTO `sys_investigation_student` VALUES (13, 7, 2024, 3, '高一', 6, '二班', 14);
INSERT INTO `sys_investigation_student` VALUES (14, 7, 2024, 3, '高一', 6, '二班', 15);
INSERT INTO `sys_investigation_student` VALUES (15, 7, 2024, 3, '高一', 6, '二班', 16);
INSERT INTO `sys_investigation_student` VALUES (16, 7, 2024, 3, '高一', 6, '二班', 17);
INSERT INTO `sys_investigation_student` VALUES (17, 7, 2024, 3, '高一', 6, '二班', 18);
INSERT INTO `sys_investigation_student` VALUES (18, 7, 2024, 3, '高一', 6, '二班', 19);
INSERT INTO `sys_investigation_student` VALUES (19, 7, 2024, 3, '高一', 6, '二班', 20);
INSERT INTO `sys_investigation_student` VALUES (20, 9, 2024, 3, '高一', 3, '一班', 2);
INSERT INTO `sys_investigation_student` VALUES (21, 9, 2024, 3, '高一', 3, '一班', 4);
INSERT INTO `sys_investigation_student` VALUES (22, 9, 2024, 3, '高一', 3, '一班', 5);
INSERT INTO `sys_investigation_student` VALUES (23, 9, 2024, 3, '高一', 3, '一班', 6);
INSERT INTO `sys_investigation_student` VALUES (24, 9, 2024, 3, '高一', 3, '一班', 7);
INSERT INTO `sys_investigation_student` VALUES (25, 9, 2024, 3, '高一', 3, '一班', 8);
INSERT INTO `sys_investigation_student` VALUES (26, 9, 2024, 3, '高一', 3, '一班', 9);
INSERT INTO `sys_investigation_student` VALUES (27, 9, 2024, 3, '高一', 3, '一班', 10);
INSERT INTO `sys_investigation_student` VALUES (28, 9, 2024, 3, '高一', 3, '一班', 11);
INSERT INTO `sys_investigation_student` VALUES (29, 9, 2024, 3, '高一', 3, '一班', 12);
INSERT INTO `sys_investigation_student` VALUES (30, 9, 2024, 3, '高一', 3, '一班', 22);
INSERT INTO `sys_investigation_student` VALUES (31, 9, 2024, 3, '高一', 6, '二班', 13);
INSERT INTO `sys_investigation_student` VALUES (32, 9, 2024, 3, '高一', 6, '二班', 14);
INSERT INTO `sys_investigation_student` VALUES (33, 9, 2024, 3, '高一', 6, '二班', 15);
INSERT INTO `sys_investigation_student` VALUES (34, 9, 2024, 3, '高一', 6, '二班', 16);
INSERT INTO `sys_investigation_student` VALUES (35, 9, 2024, 3, '高一', 6, '二班', 17);
INSERT INTO `sys_investigation_student` VALUES (36, 9, 2024, 3, '高一', 6, '二班', 18);
INSERT INTO `sys_investigation_student` VALUES (37, 9, 2024, 3, '高一', 6, '二班', 19);
INSERT INTO `sys_investigation_student` VALUES (38, 9, 2024, 3, '高一', 6, '二班', 20);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL COMMENT '父菜单ID',
  `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点ID路径',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
  `type` tinyint(4) NOT NULL COMMENT '菜单类型(1:菜单 2:目录 3:外链 4:按钮)',
  `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由路径(浏览器地址栏路径)',
  `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径(vue页面完整路径，省略.vue后缀)',
  `perm` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `visible` tinyint(1) NOT NULL DEFAULT 1 COMMENT '显示状态(1-显示;0-隐藏)',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单图标',
  `redirect` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `always_show` tinyint(4) NULL DEFAULT NULL COMMENT '【目录】只有一个子路由是否始终显示(1:是 0:否)',
  `keep_alive` tinyint(4) NULL DEFAULT NULL COMMENT '【菜单】是否开启页面缓存(1:是 0:否)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 206 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '0', '系统管理', 2, '/system', 'Layout', NULL, 1, 1, 'system', '/system/user', '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL, NULL);
INSERT INTO `sys_menu` VALUES (2, 1, '0,1', '用户管理', 1, 'user', 'system/user/index', NULL, 1, 1, 'user', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL, 1);
INSERT INTO `sys_menu` VALUES (3, 1, '0,1', '角色管理', 1, 'role', 'system/role/index', NULL, 1, 2, 'role', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL, 1);
INSERT INTO `sys_menu` VALUES (4, 1, '0,1', '菜单管理', 1, 'menu', 'system/menu/index', NULL, 1, 3, 'menu', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL, 1);
INSERT INTO `sys_menu` VALUES (5, 1, '0,1', '部门管理', 1, 'dept', 'system/dept/index', NULL, 1, 4, 'tree', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL, 1);
INSERT INTO `sys_menu` VALUES (6, 1, '0,1', '字典管理', 1, 'dict', 'system/dict/index', NULL, 1, 5, 'dict', NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21', NULL, 1);
INSERT INTO `sys_menu` VALUES (20, 0, '0', '多级菜单', 2, '/multi-level', 'Layout', NULL, 1, 9, 'cascader', '/multi-level/multi-level1', '2022-02-16 23:11:00', '2022-02-16 23:11:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (21, 20, '0,20', '菜单一级', 1, 'multi-level1', 'demo/multi-level/level1', NULL, 1, 1, '', '/multi-level/multi-level2', '2022-02-16 23:13:38', '2022-02-16 23:13:38', NULL, 1);
INSERT INTO `sys_menu` VALUES (22, 21, '0,20,21', '菜单二级', 1, 'multi-level2', 'demo/multi-level/children/level2', NULL, 1, 1, '', '/multi-level/multi-level2/multi-level3-1', '2022-02-16 23:14:23', '2022-02-16 23:14:23', NULL, 1);
INSERT INTO `sys_menu` VALUES (23, 22, '0,20,21,22', '菜单三级-1', 1, 'multi-level3-1', 'demo/multi-level/children/children/level3-1', NULL, 1, 1, '', '', '2022-02-16 23:14:51', '2022-02-16 23:14:51', NULL, 1);
INSERT INTO `sys_menu` VALUES (24, 22, '0,20,21,22', '菜单三级-2', 1, 'multi-level3-2', 'demo/multi-level/children/children/level3-2', NULL, 1, 2, '', '', '2022-02-16 23:15:08', '2022-02-16 23:15:08', NULL, 1);
INSERT INTO `sys_menu` VALUES (26, 0, '0', '平台文档', 2, '/doc', 'Layout', NULL, 1, 8, 'document', NULL, '2022-02-17 22:51:20', '2022-02-17 22:51:20', NULL, NULL);
INSERT INTO `sys_menu` VALUES (30, 26, '0,26', '平台文档(外链)', 3, 'https://juejin.cn/post/7228990409909108793', '', NULL, 1, 2, 'link', '', '2022-02-18 00:01:40', '2022-02-18 00:01:40', NULL, NULL);
INSERT INTO `sys_menu` VALUES (31, 2, '0,1,2', '用户新增', 4, '', NULL, 'sys:user:add', 1, 1, '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11', NULL, NULL);
INSERT INTO `sys_menu` VALUES (32, 2, '0,1,2', '用户编辑', 4, '', NULL, 'sys:user:edit', 1, 2, '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11', NULL, NULL);
INSERT INTO `sys_menu` VALUES (33, 2, '0,1,2', '用户删除', 4, '', NULL, 'sys:user:delete', 1, 3, '', '', '2022-10-23 11:04:08', '2022-10-23 11:04:11', NULL, NULL);
INSERT INTO `sys_menu` VALUES (36, 0, '0', '组件封装', 2, '/component', 'Layout', NULL, 1, 10, 'menu', '', '2022-10-31 09:18:44', '2022-10-31 09:18:47', NULL, NULL);
INSERT INTO `sys_menu` VALUES (37, 36, '0,36', '富文本编辑器', 1, 'wang-editor', 'demo/wang-editor', NULL, 1, 1, '', '', NULL, NULL, NULL, 1);
INSERT INTO `sys_menu` VALUES (38, 36, '0,36', '图片上传', 1, 'upload', 'demo/upload', NULL, 1, 2, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (39, 36, '0,36', '图标选择器', 1, 'icon-selector', 'demo/icon-selector', NULL, 1, 3, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (40, 0, '0', '接口', 2, '/api', 'Layout', NULL, 1, 7, 'api', '', '2022-02-17 22:51:20', '2022-02-17 22:51:20', 1, NULL);
INSERT INTO `sys_menu` VALUES (41, 40, '0,40', '接口文档', 1, 'api-doc', 'demo/api-doc', NULL, 1, 1, 'api', '', '2022-02-17 22:51:20', '2022-02-17 22:51:20', NULL, 1);
INSERT INTO `sys_menu` VALUES (70, 3, '0,1,3', '角色新增', 4, '', NULL, 'sys:role:add', 1, 1, '', NULL, '2023-05-20 23:39:09', '2023-05-20 23:39:09', NULL, NULL);
INSERT INTO `sys_menu` VALUES (71, 3, '0,1,3', '角色编辑', 4, '', NULL, 'sys:role:edit', 1, 2, '', NULL, '2023-05-20 23:40:31', '2023-05-20 23:40:31', NULL, NULL);
INSERT INTO `sys_menu` VALUES (72, 3, '0,1,3', '角色删除', 4, '', NULL, 'sys:role:delete', 1, 3, '', NULL, '2023-05-20 23:41:08', '2023-05-20 23:41:08', NULL, NULL);
INSERT INTO `sys_menu` VALUES (73, 4, '0,1,4', '菜单新增', 4, '', NULL, 'sys:menu:add', 1, 1, '', NULL, '2023-05-20 23:41:35', '2023-05-20 23:41:35', NULL, NULL);
INSERT INTO `sys_menu` VALUES (74, 4, '0,1,4', '菜单编辑', 4, '', NULL, 'sys:menu:edit', 1, 3, '', NULL, '2023-05-20 23:41:58', '2023-05-20 23:41:58', NULL, NULL);
INSERT INTO `sys_menu` VALUES (75, 4, '0,1,4', '菜单删除', 4, '', NULL, 'sys:menu:delete', 1, 3, '', NULL, '2023-05-20 23:44:18', '2023-05-20 23:44:18', NULL, NULL);
INSERT INTO `sys_menu` VALUES (76, 5, '0,1,5', '部门新增', 4, '', NULL, 'sys:dept:add', 1, 1, '', NULL, '2023-05-20 23:45:00', '2023-05-20 23:45:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (77, 5, '0,1,5', '部门编辑', 4, '', NULL, 'sys:dept:edit', 1, 2, '', NULL, '2023-05-20 23:46:16', '2023-05-20 23:46:16', NULL, NULL);
INSERT INTO `sys_menu` VALUES (78, 5, '0,1,5', '部门删除', 4, '', NULL, 'sys:dept:delete', 1, 3, '', NULL, '2023-05-20 23:46:36', '2023-05-20 23:46:36', NULL, NULL);
INSERT INTO `sys_menu` VALUES (79, 6, '0,1,6', '字典类型新增', 4, '', NULL, 'sys:dict_type:add', 1, 1, '', NULL, '2023-05-21 00:16:06', '2023-05-21 00:16:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (81, 6, '0,1,6', '字典类型编辑', 4, '', NULL, 'sys:dict_type:edit', 1, 2, '', NULL, '2023-05-21 00:27:37', '2023-05-21 00:27:37', NULL, NULL);
INSERT INTO `sys_menu` VALUES (84, 6, '0,1,6', '字典类型删除', 4, '', NULL, 'sys:dict_type:delete', 1, 3, '', NULL, '2023-05-21 00:29:39', '2023-05-21 00:29:39', NULL, NULL);
INSERT INTO `sys_menu` VALUES (85, 6, '0,1,6', '字典数据新增', 4, '', NULL, 'sys:dict:add', 1, 4, '', NULL, '2023-05-21 00:46:56', '2023-05-21 00:47:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (86, 6, '0,1,6', '字典数据编辑', 4, '', NULL, 'sys:dict:edit', 1, 5, '', NULL, '2023-05-21 00:47:36', '2023-05-21 00:47:36', NULL, NULL);
INSERT INTO `sys_menu` VALUES (87, 6, '0,1,6', '字典数据删除', 4, '', NULL, 'sys:dict:delete', 1, 6, '', NULL, '2023-05-21 00:48:10', '2023-05-21 00:48:20', NULL, NULL);
INSERT INTO `sys_menu` VALUES (88, 2, '0,1,2', '重置密码', 4, '', NULL, 'sys:user:reset_pwd', 1, 4, '', NULL, '2023-05-21 00:49:18', '2023-05-21 00:49:18', NULL, NULL);
INSERT INTO `sys_menu` VALUES (89, 0, '0', '功能演示', 2, '/function', 'Layout', NULL, 1, 11, 'menu', '', '2022-10-31 09:18:44', '2022-10-31 09:18:47', NULL, NULL);
INSERT INTO `sys_menu` VALUES (90, 89, '0,89', 'Websocket', 1, 'websocket', 'demo/websocket', NULL, 1, 3, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (91, 89, '0,89', '敬请期待...', 2, 'other', 'demo/other', NULL, 1, 4, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, NULL);
INSERT INTO `sys_menu` VALUES (93, 36, '0,36', '签名', 1, 'signature', 'demo/signature', NULL, 1, 6, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (94, 36, '0,36', '表格', 1, 'table', 'demo/table', NULL, 1, 7, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (95, 36, '0,36', '字典组件', 1, 'dict-demo', 'demo/dict', NULL, 1, 4, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (96, 89, '0,89', 'Permission', 1, 'permission', 'demo/permission/page', NULL, 1, 1, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (97, 89, '0,89', 'Icons', 1, 'icon-demo', 'demo/icons', NULL, 1, 2, '', '', '2022-11-20 23:16:30', '2022-11-20 23:16:32', NULL, 1);
INSERT INTO `sys_menu` VALUES (98, 0, '0', 'Table', 2, '/table', 'Layout', NULL, 0, 10, 'table', '', '2023-08-08 20:49:50', '2023-08-08 20:49:50', NULL, NULL);
INSERT INTO `sys_menu` VALUES (99, 98, '0,98', '动态Table', 1, 'dynamic-table', 'table/dynamic-table/index', NULL, 0, 1, '', '', '2023-08-08 20:54:42', '2023-08-08 20:54:42', NULL, 1);
INSERT INTO `sys_menu` VALUES (100, 98, '0,98', '拖拽Table', 1, 'drag-table', 'table/drag-table', NULL, 0, 2, '', '', '2023-08-08 20:54:42', '2023-08-08 20:54:42', NULL, 1);
INSERT INTO `sys_menu` VALUES (101, 98, '0,98', '综合Table', 1, 'complex-table', 'table/complex-table', NULL, 0, 3, '', '', '2023-08-08 20:54:42', '2023-08-08 20:54:42', NULL, 1);
INSERT INTO `sys_menu` VALUES (102, 26, '0,26', '平台文档(内嵌)', 3, 'internal-doc', 'demo/internal-doc', NULL, 1, 1, 'document', '', '2022-02-18 00:01:40', '2022-02-18 00:01:40', NULL, NULL);
INSERT INTO `sys_menu` VALUES (103, 0, '0', '学校管理', 2, '/school', 'Layout', NULL, 1, 2, 'client', '/school/grade', '2024-03-03 18:40:26', '2024-03-27 10:50:28', 1, 0);
INSERT INTO `sys_menu` VALUES (104, 103, '0,103', '年级管理', 1, 'grade', 'school/grade/index', NULL, 1, 1, 'el-icon-Collection', NULL, '2024-03-03 18:41:50', '2024-03-03 18:41:50', NULL, 0);
INSERT INTO `sys_menu` VALUES (105, 104, '0,103,104', '年级新增', 4, '', NULL, 'school:grade:add', 1, 1, '', NULL, '2024-03-03 18:42:48', '2024-03-03 18:44:06', NULL, NULL);
INSERT INTO `sys_menu` VALUES (106, 104, '0,103,104', '年级编辑', 4, '', NULL, 'school:grade:edit', 1, 2, '', NULL, '2024-03-03 18:43:22', '2024-03-03 18:44:19', NULL, NULL);
INSERT INTO `sys_menu` VALUES (107, 104, '0,103,104', '年级删除', 4, '', NULL, 'school:grade:delete', 1, 3, '', NULL, '2024-03-03 18:43:48', '2024-03-03 18:44:30', NULL, NULL);
INSERT INTO `sys_menu` VALUES (108, 1, '0,1', '职务管理', 1, 'position', 'system/position/index', NULL, 1, 6, 'security', NULL, '2024-03-03 19:33:37', '2024-03-14 14:56:42', 0, 0);
INSERT INTO `sys_menu` VALUES (109, 108, '0,1,108', '岗位新增', 4, '', NULL, 'sys:position:add', 1, 1, '', NULL, '2024-03-03 19:34:04', '2024-03-03 19:34:04', NULL, NULL);
INSERT INTO `sys_menu` VALUES (110, 108, '0,1,108', '岗位编辑', 4, '', NULL, 'sys:position:edit', 1, 2, '', NULL, '2024-03-03 19:34:33', '2024-03-03 19:34:33', NULL, NULL);
INSERT INTO `sys_menu` VALUES (111, 108, '0,1,108', '岗位删除', 4, '', NULL, 'sys:position:delete', 1, 3, '', NULL, '2024-03-03 19:35:05', '2024-03-03 19:35:05', NULL, NULL);
INSERT INTO `sys_menu` VALUES (112, 103, '0,103', '职工管理', 1, 'teacher', 'school/teacher/index', NULL, 1, 2, 'peoples', NULL, '2024-03-03 20:55:25', '2024-03-03 20:55:25', 0, 0);
INSERT INTO `sys_menu` VALUES (113, 112, '0,103,112', '职工新增', 4, '', NULL, 'school:teacher:add', 1, 1, '', NULL, '2024-03-03 20:55:48', '2024-03-03 20:55:48', NULL, NULL);
INSERT INTO `sys_menu` VALUES (114, 112, '0,103,112', '职工编辑', 4, '', NULL, 'school:teacher:edit', 1, 2, '', NULL, '2024-03-03 20:56:16', '2024-03-03 20:56:16', NULL, NULL);
INSERT INTO `sys_menu` VALUES (115, 112, '0,103,112', '职工删除', 4, '', NULL, 'school:teacher:delete', 1, 3, '', NULL, '2024-03-03 20:57:05', '2024-03-03 20:57:05', NULL, NULL);
INSERT INTO `sys_menu` VALUES (116, 112, '0,103,112', '职工重置密码', 4, '', NULL, 'school:teacher:reset_pwd', 1, 4, '', NULL, '2024-03-04 11:11:10', '2024-03-04 11:13:08', 0, 0);
INSERT INTO `sys_menu` VALUES (117, 103, '0,103', '班级管理', 1, 'clazz', 'school/clazz/index', NULL, 1, 3, 'user', NULL, '2024-03-04 12:52:32', '2024-03-04 12:52:47', 0, 0);
INSERT INTO `sys_menu` VALUES (118, 117, '0,103,117', '班级新增', 4, '', NULL, 'school:clazz:add', 1, 1, '', NULL, '2024-03-04 12:53:19', '2024-03-04 12:53:19', NULL, NULL);
INSERT INTO `sys_menu` VALUES (119, 117, '0,103,117', '班级编辑', 4, '', NULL, 'school:clazz:edit', 1, 2, '', NULL, '2024-03-04 12:53:45', '2024-03-04 12:53:45', NULL, NULL);
INSERT INTO `sys_menu` VALUES (120, 117, '0,103,117', '班级删除', 4, '', NULL, 'school:clazz:delete', 1, 3, '', NULL, '2024-03-04 12:54:13', '2024-03-04 12:54:13', NULL, NULL);
INSERT INTO `sys_menu` VALUES (121, 103, '0,103', '学生管理', 1, 'student', 'school/student/index', NULL, 1, 4, 'github', NULL, '2024-03-04 14:12:06', '2024-03-04 14:12:06', 0, 0);
INSERT INTO `sys_menu` VALUES (122, 121, '0,103,121', '学生新增', 4, '', NULL, 'school:student:add', 1, 1, '', NULL, '2024-03-04 14:12:35', '2024-03-04 14:12:35', NULL, NULL);
INSERT INTO `sys_menu` VALUES (123, 121, '0,103,121', '学生编辑', 4, '', NULL, 'school:student:edit', 1, 2, '', NULL, '2024-03-04 14:12:56', '2024-03-04 14:12:56', NULL, NULL);
INSERT INTO `sys_menu` VALUES (124, 121, '0,103,121', '学生删除', 4, '', NULL, 'school:student:delete', 1, 3, '', NULL, '2024-03-04 14:13:22', '2024-03-04 14:13:22', NULL, NULL);
INSERT INTO `sys_menu` VALUES (125, 121, '0,103,121', '学生重置密码', 4, '', NULL, 'school:student:reset_pwd', 1, 4, '', NULL, '2024-03-04 14:13:58', '2024-03-04 14:13:58', NULL, NULL);
INSERT INTO `sys_menu` VALUES (126, 121, '0,103,121', '监护人新增', 4, '', NULL, 'school:guarder:add', 1, 5, '', NULL, '2024-03-04 18:30:18', '2024-03-04 18:30:18', 0, 0);
INSERT INTO `sys_menu` VALUES (127, 121, '0,103,121', '监护人编辑', 4, '', NULL, 'school:guarder:edit', 1, 6, '', NULL, '2024-03-04 18:30:52', '2024-03-04 18:30:52', NULL, NULL);
INSERT INTO `sys_menu` VALUES (128, 121, '0,103,121', '监护人删除', 4, '', NULL, 'school:guarder:delete', 1, 7, '', NULL, '2024-03-04 18:31:33', '2024-03-04 18:31:33', NULL, NULL);
INSERT INTO `sys_menu` VALUES (157, 103, '0,103', '课程管理', 1, 'course', 'school/course/index', NULL, 1, 5, 'dict', NULL, '2024-03-16 15:30:05', '2024-03-16 15:30:05', 0, 0);
INSERT INTO `sys_menu` VALUES (158, 157, '0,103,157', '课程新增', 4, '', NULL, 'school:course:add', 1, 1, '', NULL, '2024-03-16 15:30:34', '2024-03-16 15:30:34', NULL, NULL);
INSERT INTO `sys_menu` VALUES (159, 157, '0,103,157', '课程编辑', 4, '', NULL, 'school:course:edit', 1, 2, '', NULL, '2024-03-16 15:31:05', '2024-03-16 15:31:05', NULL, NULL);
INSERT INTO `sys_menu` VALUES (160, 157, '0,103,157', '课程删除', 4, '', NULL, 'school:course:delete', 1, 3, '', NULL, '2024-03-16 15:31:34', '2024-03-16 15:31:34', NULL, NULL);
INSERT INTO `sys_menu` VALUES (161, 117, '0,103,117', '教学安排新增', 4, '', NULL, 'school:arrange:add', 1, 4, '', NULL, '2024-03-17 09:38:13', '2024-03-17 09:38:13', 0, 0);
INSERT INTO `sys_menu` VALUES (162, 117, '0,103,117', '教学安排编辑', 4, '', NULL, 'school:arrange:edit', 1, 5, '', NULL, '2024-03-17 09:38:45', '2024-03-17 09:38:45', NULL, NULL);
INSERT INTO `sys_menu` VALUES (163, 117, '0,103,117', '教学安排删除', 4, '', NULL, 'school:arrange:delete', 1, 6, '', NULL, '2024-03-17 09:39:22', '2024-03-17 09:39:22', NULL, NULL);
INSERT INTO `sys_menu` VALUES (164, 103, '0,103', '考试管理', 1, 'exam', 'school/exam/index', NULL, 1, 6, 'document', NULL, '2024-03-17 16:36:08', '2024-03-17 16:36:08', 0, 0);
INSERT INTO `sys_menu` VALUES (165, 164, '0,103,164', '考试新增', 4, '', NULL, 'school:exam:add', 1, 1, '', NULL, '2024-03-17 16:36:33', '2024-03-17 16:36:33', NULL, NULL);
INSERT INTO `sys_menu` VALUES (166, 164, '0,103,164', '考试编辑', 4, '', NULL, 'school:exam:edit', 1, 2, '', NULL, '2024-03-17 16:36:57', '2024-03-17 16:36:57', NULL, NULL);
INSERT INTO `sys_menu` VALUES (167, 164, '0,103,164', '考试删除', 4, '', NULL, 'school:exam:delete', 1, 3, '', NULL, '2024-03-17 16:37:23', '2024-03-17 16:37:23', NULL, NULL);
INSERT INTO `sys_menu` VALUES (168, 0, '0', '成绩管理', 2, '/score', 'Layout', NULL, 1, 3, 'edit', 'score/score_entry', '2024-03-18 10:37:14', '2024-03-18 10:37:26', 1, NULL);
INSERT INTO `sys_menu` VALUES (169, 168, '0,168', '成绩录入管理', 1, 'score_entry', 'score/score_entry/index', NULL, 1, 1, 'language', NULL, '2024-03-18 10:38:55', '2024-03-18 10:38:55', NULL, 0);
INSERT INTO `sys_menu` VALUES (170, 0, '0', '成绩分析', 2, '/analysis', 'Layout', NULL, 1, 4, 'monitor', '/analysis/clazz_exam_analysis', '2024-03-20 08:35:46', '2024-03-20 08:36:00', 1, NULL);
INSERT INTO `sys_menu` VALUES (171, 170, '0,170', '班级考试维度', 1, 'clazz_exam_analysis', 'analysis/clazz_exam_analysis/index', NULL, 1, 1, 'homepage', NULL, '2024-03-20 08:36:49', '2024-03-20 08:37:13', NULL, 0);
INSERT INTO `sys_menu` VALUES (172, 170, '0,170', '年级考试维度', 1, 'grade_exam_anlysis', 'analysis/grade_exam_analysis/index', NULL, 1, 2, 'cascader', NULL, '2024-03-21 12:11:37', '2024-03-21 12:11:50', 0, 0);
INSERT INTO `sys_menu` VALUES (173, 170, '0,170', '个人成绩分析', 1, 'student_score_analysis', 'analysis/student_score_analysis/index', NULL, 1, 3, 'role', NULL, '2024-03-22 09:41:13', '2024-03-22 09:41:13', NULL, 0);
INSERT INTO `sys_menu` VALUES (174, 0, '0', '问卷管理', 2, '/survey', 'Layout', NULL, 1, 5, 'dict', '/survey/question', '2024-03-23 13:23:07', '2024-03-23 13:26:03', 1, 0);
INSERT INTO `sys_menu` VALUES (175, 174, '0,174', '问题管理', 1, 'question', 'survey/question/index', NULL, 1, 1, 'size', NULL, '2024-03-23 13:24:02', '2024-03-23 13:24:02', NULL, 1);
INSERT INTO `sys_menu` VALUES (176, 175, '0,174,175', '问题新增', 4, '', NULL, 'survey:question:add', 1, 1, '', NULL, '2024-03-23 13:24:29', '2024-03-23 13:24:29', NULL, NULL);
INSERT INTO `sys_menu` VALUES (177, 175, '0,174,175', '问题编辑', 4, '', NULL, 'survey:question:edit', 1, 2, '', NULL, '2024-03-23 13:24:57', '2024-03-23 13:24:57', NULL, NULL);
INSERT INTO `sys_menu` VALUES (178, 175, '0,174,175', '问题删除', 4, '', NULL, 'survey:question:delete', 1, 3, '', NULL, '2024-03-23 13:25:32', '2024-03-23 13:25:32', NULL, NULL);
INSERT INTO `sys_menu` VALUES (179, 175, '0,174,175', '问题选项新增', 4, '', NULL, 'survey:questionOption:add', 1, 4, '', NULL, '2024-03-23 15:07:42', '2024-03-23 15:07:42', 0, 0);
INSERT INTO `sys_menu` VALUES (180, 175, '0,174,175', '问题选项编辑', 4, '', NULL, 'survey:questionOption:edit', 1, 5, '', NULL, '2024-03-23 15:08:15', '2024-03-23 15:08:15', NULL, NULL);
INSERT INTO `sys_menu` VALUES (181, 175, '0,174,175', '问题选项删除', 4, '', NULL, 'survey:questionOption:delete', 1, 6, '', NULL, '2024-03-23 15:08:44', '2024-03-23 15:08:44', NULL, NULL);
INSERT INTO `sys_menu` VALUES (182, 174, '0,174', '问卷管理', 1, 'investigation', 'survey/investigation/index', NULL, 1, 2, 'captcha', NULL, '2024-03-23 19:42:23', '2024-03-23 19:42:23', 0, 0);
INSERT INTO `sys_menu` VALUES (183, 182, '0,174,182', '问卷新增', 4, '', NULL, 'survey:investigation:add', 1, 1, '', NULL, '2024-03-23 19:42:49', '2024-03-23 19:42:49', NULL, NULL);
INSERT INTO `sys_menu` VALUES (184, 182, '0,174,182', '问卷编辑', 4, '', NULL, 'survey:investigation:edit', 1, 2, '', NULL, '2024-03-23 19:43:16', '2024-03-23 19:43:16', NULL, NULL);
INSERT INTO `sys_menu` VALUES (185, 182, '0,174,182', '问卷删除', 4, '', NULL, 'survey:investigation:delete', 1, 3, '', NULL, '2024-03-23 19:43:49', '2024-03-23 19:43:49', NULL, NULL);
INSERT INTO `sys_menu` VALUES (186, 174, '0,174', '问卷填写', 1, 'investigation-student', 'survey/investigation-student/index', NULL, 1, 3, 'document', NULL, '2024-03-25 10:47:47', '2024-03-25 10:47:47', 0, 0);
INSERT INTO `sys_menu` VALUES (187, 174, '0,174', '问卷分析', 1, 'investigation-analysis', 'survey/investigation-analysis/index', NULL, 1, 4, 'monitor', NULL, '2024-03-26 10:31:22', '2024-03-26 10:31:22', 0, 0);
INSERT INTO `sys_menu` VALUES (188, 0, '0', '错题管理', 2, '/problems', 'Layout', NULL, 1, 6, 'captcha', '/problems/topic-type', '2024-03-27 10:52:17', '2024-03-27 10:52:29', 1, 0);
INSERT INTO `sys_menu` VALUES (189, 188, '0,188', '题型管理', 1, 'topic-type', 'problems/topic-type/index', NULL, 1, 1, 'cascader', NULL, '2024-03-27 10:53:20', '2024-03-27 10:53:20', NULL, 0);
INSERT INTO `sys_menu` VALUES (190, 189, '0,188,189', '题型新增', 4, '', NULL, 'problems:topicType:add', 1, 1, '', NULL, '2024-03-27 10:54:04', '2024-03-27 10:54:04', NULL, NULL);
INSERT INTO `sys_menu` VALUES (191, 189, '0,188,189', '题型编辑', 4, '', NULL, 'problems:topicType:edit', 1, 2, '', NULL, '2024-03-27 10:54:38', '2024-03-27 10:54:38', NULL, NULL);
INSERT INTO `sys_menu` VALUES (192, 189, '0,188,189', '题型删除', 4, '', NULL, 'problems:topicType:delete', 1, 3, '', NULL, '2024-03-27 10:55:10', '2024-03-27 10:55:10', NULL, NULL);
INSERT INTO `sys_menu` VALUES (193, 188, '0,188', '错题管理', 1, 'topic', 'problems/topic/index', NULL, 1, 2, 'publish', NULL, '2024-03-27 12:24:11', '2024-03-27 12:24:11', 0, 0);
INSERT INTO `sys_menu` VALUES (194, 193, '0,188,193', '错题新增', 4, '', NULL, 'problems:topic:add', 1, 1, '', NULL, '2024-03-27 12:24:40', '2024-03-27 12:24:40', NULL, NULL);
INSERT INTO `sys_menu` VALUES (195, 193, '0,188,193', '错题编辑', 4, '', NULL, 'problems:topic:edit', 1, 2, '', NULL, '2024-03-27 12:25:04', '2024-03-27 12:25:04', NULL, NULL);
INSERT INTO `sys_menu` VALUES (196, 193, '0,188,193', '错题删除', 4, '', NULL, 'problems:topic:delete', 1, 3, '', NULL, '2024-03-27 12:25:28', '2024-03-27 12:25:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (197, 0, '0', '配置管理', 2, '/configuration', 'Layout', NULL, 1, 7, 'cascader', '/configuration/hobbies', '2024-03-28 15:39:14', '2024-03-28 15:42:47', 1, 0);
INSERT INTO `sys_menu` VALUES (198, 197, '0,197', '兴趣爱好管理', 1, 'hobbies', 'configuration/hobbies/index', NULL, 1, 1, 'eye-open', NULL, '2024-03-28 15:40:09', '2024-03-28 16:30:51', NULL, 0);
INSERT INTO `sys_menu` VALUES (199, 198, '0,197,198', '兴趣爱好新增', 4, '', NULL, 'configuration:hobbies:add', 1, 1, '', NULL, '2024-03-28 15:40:43', '2024-03-28 15:40:43', NULL, NULL);
INSERT INTO `sys_menu` VALUES (200, 198, '0,197,198', '兴趣爱好编辑', 4, '', NULL, 'configuration:hobbies:edit', 1, 2, '', NULL, '2024-03-28 15:41:26', '2024-03-28 15:41:26', NULL, NULL);
INSERT INTO `sys_menu` VALUES (201, 198, '0,197,198', '兴趣爱好删除', 4, '', NULL, 'configuration:hobbies:delete', 1, 3, '', NULL, '2024-03-28 15:42:00', '2024-03-28 15:42:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (202, 197, '0,197', '目标院校管理', 1, 'university', 'configuration/university/index', NULL, 1, 2, 'security', NULL, '2024-03-28 16:30:34', '2024-03-28 16:31:02', 0, 0);
INSERT INTO `sys_menu` VALUES (203, 202, '0,197,202', '目标院校新增', 4, '', NULL, 'configuration:university:add', 1, 1, '', NULL, '2024-03-28 16:31:37', '2024-03-28 16:31:37', NULL, NULL);
INSERT INTO `sys_menu` VALUES (204, 202, '0,197,202', '目标院校编辑', 4, '', NULL, 'configuration:university:edit', 1, 2, '', NULL, '2024-03-28 16:32:29', '2024-03-28 16:32:29', NULL, NULL);
INSERT INTO `sys_menu` VALUES (205, 202, '0,197,202', '目标院校删除', 4, '', NULL, 'configuration:university:delete', 1, 3, '', NULL, '2024-03-28 16:33:30', '2024-03-28 16:33:30', NULL, NULL);
INSERT INTO `sys_menu` VALUES (206, 0, '0', '个人管理', 2, '/personal', 'Layout', NULL, 1, 8, 'peoples', '/personal/archives', '2024-03-29 09:52:06', '2024-03-29 09:52:20', 1, 0);
INSERT INTO `sys_menu` VALUES (207, 206, '0,206', '学情档案管理', 1, 'archives', 'personal/archives/index', NULL, 1, 1, 'document', NULL, '2024-03-29 09:53:17', '2024-03-29 09:53:17', NULL, 0);
INSERT INTO `sys_menu` VALUES (208, 207, '0,206,207', '学情档案新增', 4, '', NULL, 'personal:archives:add', 1, 1, '', NULL, '2024-03-29 09:53:47', '2024-03-29 09:53:47', NULL, NULL);
INSERT INTO `sys_menu` VALUES (209, 207, '0,206,207', '学情档案编辑', 4, '', NULL, 'personal:archives:edit', 1, 2, '', NULL, '2024-03-29 09:54:13', '2024-03-29 09:54:13', NULL, NULL);
INSERT INTO `sys_menu` VALUES (210, 207, '0,206,207', '学情档案删除', 4, '', NULL, 'personal:archives:delete', 1, 3, '', NULL, '2024-03-29 09:54:46', '2024-03-29 09:54:46', NULL, NULL);
INSERT INTO `sys_menu` VALUES (211, 207, '0,206,207', '学情档案查询', 4, '', NULL, 'personal:archives:query', 1, 0, '', NULL, '2024-03-29 16:04:15', '2024-03-29 16:04:15', 0, 0);

-- ----------------------------
-- Table structure for sys_position
-- ----------------------------
DROP TABLE IF EXISTS `sys_position`;
CREATE TABLE `sys_position`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '岗位名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '岗位状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_position
-- ----------------------------
INSERT INTO `sys_position` VALUES (1, '校长', 'No.1', 1, 1, 0, '2024-02-12 04:30:49', '2024-03-23 15:10:57');
INSERT INTO `sys_position` VALUES (2, '主任', 'No.2', 2, 1, 0, '2024-02-12 04:31:06', '2024-03-23 15:11:38');
INSERT INTO `sys_position` VALUES (3, '秘书', 'No.3', 3, 1, 0, '2024-02-12 04:31:33', '2024-03-23 15:11:31');
INSERT INTO `sys_position` VALUES (4, '职工', 'No.4', 4, 1, 0, '2024-02-12 04:31:55', '2024-03-23 15:11:51');
INSERT INTO `sys_position` VALUES (6, '门卫', 'No.5', 6, 1, 0, '2024-03-03 19:36:43', '2024-03-23 15:12:07');

-- ----------------------------
-- Table structure for sys_question
-- ----------------------------
DROP TABLE IF EXISTS `sys_question`;
CREATE TABLE `sys_question`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '问题名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题编码',
  `ask` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '具体问题',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '问题状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_question
-- ----------------------------
INSERT INTO `sys_question` VALUES (7, '您对本课程教学大纲的了解程度', 'No.1', NULL, 1, 1, 0, '2024-03-23 15:12:51', '2024-03-23 15:12:51');
INSERT INTO `sys_question` VALUES (8, '您对本课程的课程目标认知程度', 'No.2', NULL, 2, 1, 0, '2024-03-23 15:13:22', '2024-03-24 13:30:31');
INSERT INTO `sys_question` VALUES (9, '您对教学大纲中知识点的掌握程度', 'No.3', '选择“大部分未掌握”的请给出您认为的原因', 3, 1, 0, '2024-03-23 15:13:37', '2024-03-24 13:30:35');
INSERT INTO `sys_question` VALUES (10, '您认为对课程目标中相应能力、素质的具备程度', 'No.4', '选择“未具备”的请给出未具备的能力、素质以及原因', 4, 1, 0, '2024-03-23 15:13:52', '2024-03-24 13:30:39');
INSERT INTO `sys_question` VALUES (11, '您认为本课程的教学手段是否合适', 'No.5', '选择“不合适”的请给出您的建议', 5, 1, 0, '2024-03-23 15:14:10', '2024-03-24 13:30:44');
INSERT INTO `sys_question` VALUES (12, '您认为各课程目标对应的评价方法是否合适', 'No.6', '选择“不合适”的请明确并给出您的建议', 6, 1, 0, '2024-03-23 16:10:31', '2024-03-24 13:30:53');
INSERT INTO `sys_question` VALUES (13, '您认为已达到的课程目标', 'No.7', NULL, 7, 1, 0, '2024-03-23 16:11:38', '2024-03-24 13:30:58');
INSERT INTO `sys_question` VALUES (14, '您对自己课程学习的总体评价', 'No.8', '选择“未达到预期学习成果”的请给出您认为的原因', 8, 1, 0, '2024-03-23 16:12:49', '2024-03-24 13:31:04');
INSERT INTO `sys_question` VALUES (15, '您看重该课程对于您的意义', 'No.9', NULL, 9, 1, 0, '2024-03-23 16:13:45', '2024-03-24 13:31:11');
INSERT INTO `sys_question` VALUES (16, '您认为实现该课程目标的关键', 'No.10', NULL, 10, 1, 0, '2024-03-23 16:14:39', '2024-03-24 13:31:19');

-- ----------------------------
-- Table structure for sys_question_option
-- ----------------------------
DROP TABLE IF EXISTS `sys_question_option`;
CREATE TABLE `sys_question_option`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) NULL DEFAULT NULL COMMENT '问题ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '问题选项名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题选项编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '问题选项状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问题选项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_question_option
-- ----------------------------
INSERT INTO `sys_question_option` VALUES (8, 7, '非常了解', 'a', 1, 1, 0, '2024-03-23 15:39:25', '2024-03-23 15:39:25');
INSERT INTO `sys_question_option` VALUES (9, 7, '一般了解', 'b', 1, 1, 0, '2024-03-23 15:39:40', '2024-03-23 15:39:40');
INSERT INTO `sys_question_option` VALUES (10, 7, '不了解', 'c', 1, 1, 0, '2024-03-23 15:39:49', '2024-03-23 15:39:49');
INSERT INTO `sys_question_option` VALUES (11, 7, '不知道去哪里了解', 'd', 1, 1, 0, '2024-03-23 15:40:00', '2024-03-23 15:40:00');
INSERT INTO `sys_question_option` VALUES (12, 8, '熟悉并认可', 'a', 1, 1, 0, '2024-03-23 16:06:22', '2024-03-23 16:06:22');
INSERT INTO `sys_question_option` VALUES (13, 8, '一般熟悉并认可', 'b', 1, 1, 0, '2024-03-23 16:06:37', '2024-03-23 16:06:37');
INSERT INTO `sys_question_option` VALUES (14, 8, '不熟知', 'c', 1, 1, 0, '2024-03-23 16:06:48', '2024-03-23 16:06:48');
INSERT INTO `sys_question_option` VALUES (15, 8, '不认可', 'd', 1, 1, 0, '2024-03-23 16:07:01', '2024-03-23 16:07:01');
INSERT INTO `sys_question_option` VALUES (16, 9, '全部掌握', 'a', 1, 1, 0, '2024-03-23 16:07:27', '2024-03-23 16:07:27');
INSERT INTO `sys_question_option` VALUES (17, 9, '大部门掌握', 'b', 1, 1, 0, '2024-03-23 16:07:38', '2024-03-23 16:07:38');
INSERT INTO `sys_question_option` VALUES (18, 9, '大部分未掌握', 'c', 1, 1, 0, '2024-03-23 16:07:58', '2024-03-23 16:07:58');
INSERT INTO `sys_question_option` VALUES (19, 10, '全部具备', 'a', 1, 1, 0, '2024-03-23 16:08:56', '2024-03-23 16:08:56');
INSERT INTO `sys_question_option` VALUES (20, 10, '部分具备', 'b', 1, 1, 0, '2024-03-23 16:09:10', '2024-03-23 16:09:10');
INSERT INTO `sys_question_option` VALUES (21, 10, '未具备', 'c', 1, 1, 0, '2024-03-23 16:09:22', '2024-03-23 16:09:22');
INSERT INTO `sys_question_option` VALUES (22, 11, '非常合适', 'a', 1, 1, 0, '2024-03-23 16:09:54', '2024-03-23 16:09:54');
INSERT INTO `sys_question_option` VALUES (23, 11, '合适', 'b', 1, 1, 0, '2024-03-23 16:10:03', '2024-03-23 16:10:03');
INSERT INTO `sys_question_option` VALUES (24, 11, '不合适', 'c', 1, 1, 0, '2024-03-23 16:10:11', '2024-03-23 16:10:11');
INSERT INTO `sys_question_option` VALUES (25, 12, '非常合适', 'a', 1, 1, 0, '2024-03-23 16:10:47', '2024-03-23 16:10:47');
INSERT INTO `sys_question_option` VALUES (26, 12, '合适', 'b', 1, 1, 0, '2024-03-23 16:10:56', '2024-03-23 16:10:56');
INSERT INTO `sys_question_option` VALUES (27, 12, '个别不合适', 'c', 1, 1, 0, '2024-03-23 16:11:09', '2024-03-23 16:11:09');
INSERT INTO `sys_question_option` VALUES (28, 12, '均不合适', 'd', 1, 1, 0, '2024-03-23 16:11:21', '2024-03-23 16:11:21');
INSERT INTO `sys_question_option` VALUES (29, 13, '课程目标1', 'a', 1, 1, 0, '2024-03-23 16:11:50', '2024-03-23 16:11:50');
INSERT INTO `sys_question_option` VALUES (30, 13, '课程目标2', 'b', 1, 1, 0, '2024-03-23 16:12:03', '2024-03-23 16:12:03');
INSERT INTO `sys_question_option` VALUES (31, 13, '课程目标3', 'c', 1, 1, 0, '2024-03-23 16:12:12', '2024-03-23 16:12:12');
INSERT INTO `sys_question_option` VALUES (32, 14, '达到预期学习效果', 'a', 1, 1, 0, '2024-03-23 16:13:06', '2024-03-23 16:13:06');
INSERT INTO `sys_question_option` VALUES (33, 14, '基本达到预期学习效果', 'b', 1, 1, 0, '2024-03-23 16:13:15', '2024-03-23 16:13:15');
INSERT INTO `sys_question_option` VALUES (34, 14, '未达到预期学习成果', 'c', 1, 1, 0, '2024-03-23 16:13:24', '2024-03-23 16:13:24');
INSERT INTO `sys_question_option` VALUES (35, 15, '能力培养', 'a', 1, 1, 0, '2024-03-23 16:13:55', '2024-03-23 16:13:55');
INSERT INTO `sys_question_option` VALUES (36, 15, '分数', 'b', 1, 1, 0, '2024-03-23 16:14:02', '2024-03-23 16:14:02');
INSERT INTO `sys_question_option` VALUES (37, 15, '知识积累', 'c', 1, 1, 0, '2024-03-23 16:14:10', '2024-03-23 16:14:10');
INSERT INTO `sys_question_option` VALUES (38, 15, '无意义（不建议本专业开此课）；', 'd', 1, 1, 0, '2024-03-23 16:14:19', '2024-03-23 16:14:19');
INSERT INTO `sys_question_option` VALUES (39, 16, '老师多教', 'a', 1, 1, 0, '2024-03-23 16:14:50', '2024-03-23 16:14:50');
INSERT INTO `sys_question_option` VALUES (40, 16, '自己多学', 'b', 1, 1, 0, '2024-03-23 16:14:56', '2024-03-23 16:14:56');
INSERT INTO `sys_question_option` VALUES (41, 16, '师生互动', 'c', 1, 1, 0, '2024-03-23 16:15:02', '2024-03-23 16:15:02');
INSERT INTO `sys_question_option` VALUES (42, 16, '明确考试具体范围；', 'd', 1, 1, 0, '2024-03-23 16:15:10', '2024-03-23 16:15:10');

-- ----------------------------
-- Table structure for sys_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_record`;
CREATE TABLE `sys_record`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `investigation_student_id` bigint(20) NOT NULL COMMENT '问卷学生ID',
  `investigation_id` bigint(20) NOT NULL COMMENT '问卷ID',
  `year` int(11) NULL DEFAULT NULL COMMENT '年度',
  `grade_id` bigint(20) NULL DEFAULT NULL COMMENT '年级ID',
  `grade_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年级名称',
  `clazz_id` bigint(20) NULL DEFAULT NULL COMMENT '班级ID',
  `clazz_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级名称',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `question_id` bigint(20) NOT NULL COMMENT '问题ID',
  `question_choice` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题选择',
  `question_ask_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 120 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问卷记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_record
-- ----------------------------
INSERT INTO `sys_record` VALUES (58, 1, 7, 2024, 3, '高一', 3, '一班', 2, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (59, 1, 7, 2024, 3, '高一', 3, '一班', 2, 8, '12', NULL);
INSERT INTO `sys_record` VALUES (60, 1, 7, 2024, 3, '高一', 3, '一班', 2, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (61, 2, 7, 2024, 3, '高一', 3, '一班', 4, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (62, 2, 7, 2024, 3, '高一', 3, '一班', 4, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (63, 2, 7, 2024, 3, '高一', 3, '一班', 4, 9, '18', NULL);
INSERT INTO `sys_record` VALUES (64, 3, 7, 2024, 3, '高一', 3, '一班', 5, 7, '9', NULL);
INSERT INTO `sys_record` VALUES (65, 3, 7, 2024, 3, '高一', 3, '一班', 5, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (66, 3, 7, 2024, 3, '高一', 3, '一班', 5, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (67, 4, 7, 2024, 3, '高一', 3, '一班', 6, 7, '11', NULL);
INSERT INTO `sys_record` VALUES (68, 4, 7, 2024, 3, '高一', 3, '一班', 6, 8, '15', NULL);
INSERT INTO `sys_record` VALUES (69, 4, 7, 2024, 3, '高一', 3, '一班', 6, 9, '18', NULL);
INSERT INTO `sys_record` VALUES (70, 5, 7, 2024, 3, '高一', 3, '一班', 7, 7, '10', NULL);
INSERT INTO `sys_record` VALUES (71, 5, 7, 2024, 3, '高一', 3, '一班', 7, 8, '14', NULL);
INSERT INTO `sys_record` VALUES (72, 5, 7, 2024, 3, '高一', 3, '一班', 7, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (73, 6, 7, 2024, 3, '高一', 3, '一班', 8, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (74, 6, 7, 2024, 3, '高一', 3, '一班', 8, 8, '12', NULL);
INSERT INTO `sys_record` VALUES (75, 6, 7, 2024, 3, '高一', 3, '一班', 8, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (76, 7, 7, 2024, 3, '高一', 3, '一班', 9, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (77, 7, 7, 2024, 3, '高一', 3, '一班', 9, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (78, 7, 7, 2024, 3, '高一', 3, '一班', 9, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (79, 8, 7, 2024, 3, '高一', 3, '一班', 10, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (80, 8, 7, 2024, 3, '高一', 3, '一班', 10, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (81, 8, 7, 2024, 3, '高一', 3, '一班', 10, 9, '18', NULL);
INSERT INTO `sys_record` VALUES (82, 9, 7, 2024, 3, '高一', 3, '一班', 11, 7, '10', NULL);
INSERT INTO `sys_record` VALUES (83, 9, 7, 2024, 3, '高一', 3, '一班', 11, 8, '12', NULL);
INSERT INTO `sys_record` VALUES (84, 9, 7, 2024, 3, '高一', 3, '一班', 11, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (85, 10, 7, 2024, 3, '高一', 3, '一班', 12, 7, '11', NULL);
INSERT INTO `sys_record` VALUES (86, 10, 7, 2024, 3, '高一', 3, '一班', 12, 8, '12', NULL);
INSERT INTO `sys_record` VALUES (87, 10, 7, 2024, 3, '高一', 3, '一班', 12, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (88, 11, 7, 2024, 3, '高一', 3, '一班', 22, 7, '9', NULL);
INSERT INTO `sys_record` VALUES (89, 11, 7, 2024, 3, '高一', 3, '一班', 22, 8, '12', NULL);
INSERT INTO `sys_record` VALUES (90, 11, 7, 2024, 3, '高一', 3, '一班', 22, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (91, 12, 7, 2024, 3, '高一', 6, '二班', 13, 7, '11,9', NULL);
INSERT INTO `sys_record` VALUES (92, 12, 7, 2024, 3, '高一', 6, '二班', 13, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (93, 12, 7, 2024, 3, '高一', 6, '二班', 13, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (94, 13, 7, 2024, 3, '高一', 6, '二班', 14, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (95, 13, 7, 2024, 3, '高一', 6, '二班', 14, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (96, 13, 7, 2024, 3, '高一', 6, '二班', 14, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (97, 14, 7, 2024, 3, '高一', 6, '二班', 15, 7, '10', NULL);
INSERT INTO `sys_record` VALUES (98, 14, 7, 2024, 3, '高一', 6, '二班', 15, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (99, 14, 7, 2024, 3, '高一', 6, '二班', 15, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (100, 15, 7, 2024, 3, '高一', 6, '二班', 16, 7, '11', NULL);
INSERT INTO `sys_record` VALUES (101, 15, 7, 2024, 3, '高一', 6, '二班', 16, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (102, 15, 7, 2024, 3, '高一', 6, '二班', 16, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (103, 16, 7, 2024, 3, '高一', 6, '二班', 17, 7, '9', NULL);
INSERT INTO `sys_record` VALUES (104, 16, 7, 2024, 3, '高一', 6, '二班', 17, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (105, 16, 7, 2024, 3, '高一', 6, '二班', 17, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (106, 17, 7, 2024, 3, '高一', 6, '二班', 18, 7, '10', NULL);
INSERT INTO `sys_record` VALUES (107, 17, 7, 2024, 3, '高一', 6, '二班', 18, 8, '14', NULL);
INSERT INTO `sys_record` VALUES (108, 17, 7, 2024, 3, '高一', 6, '二班', 18, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (109, 18, 7, 2024, 3, '高一', 6, '二班', 19, 7, '9', NULL);
INSERT INTO `sys_record` VALUES (110, 18, 7, 2024, 3, '高一', 6, '二班', 19, 8, '14', NULL);
INSERT INTO `sys_record` VALUES (111, 18, 7, 2024, 3, '高一', 6, '二班', 19, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (112, 19, 7, 2024, 3, '高一', 6, '二班', 20, 7, '10', NULL);
INSERT INTO `sys_record` VALUES (113, 19, 7, 2024, 3, '高一', 6, '二班', 20, 8, '13', NULL);
INSERT INTO `sys_record` VALUES (114, 19, 7, 2024, 3, '高一', 6, '二班', 20, 9, '17', NULL);
INSERT INTO `sys_record` VALUES (115, 20, 9, 2024, 3, '高一', 3, '一班', 2, 7, '8', NULL);
INSERT INTO `sys_record` VALUES (116, 20, 9, 2024, 3, '高一', 3, '一班', 2, 9, '16', NULL);
INSERT INTO `sys_record` VALUES (117, 20, 9, 2024, 3, '高一', 3, '一班', 2, 11, '22', NULL);
INSERT INTO `sys_record` VALUES (118, 20, 9, 2024, 3, '高一', 3, '一班', 2, 13, '29', NULL);
INSERT INTO `sys_record` VALUES (119, 20, 9, 2024, 3, '高一', 3, '一班', 2, 15, '35', NULL);

-- ----------------------------
-- Table structure for sys_record_detail
-- ----------------------------
DROP TABLE IF EXISTS `sys_record_detail`;
CREATE TABLE `sys_record_detail`  (
  `record_id` bigint(20) NOT NULL COMMENT '问卷记录ID',
  `option_id` bigint(20) NOT NULL COMMENT '问卷问题选项ID',
  PRIMARY KEY (`record_id`, `option_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问卷记录详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_record_detail
-- ----------------------------
INSERT INTO `sys_record_detail` VALUES (58, 8);
INSERT INTO `sys_record_detail` VALUES (59, 12);
INSERT INTO `sys_record_detail` VALUES (60, 16);
INSERT INTO `sys_record_detail` VALUES (61, 8);
INSERT INTO `sys_record_detail` VALUES (62, 13);
INSERT INTO `sys_record_detail` VALUES (63, 18);
INSERT INTO `sys_record_detail` VALUES (64, 9);
INSERT INTO `sys_record_detail` VALUES (65, 13);
INSERT INTO `sys_record_detail` VALUES (66, 16);
INSERT INTO `sys_record_detail` VALUES (67, 11);
INSERT INTO `sys_record_detail` VALUES (68, 15);
INSERT INTO `sys_record_detail` VALUES (69, 18);
INSERT INTO `sys_record_detail` VALUES (70, 10);
INSERT INTO `sys_record_detail` VALUES (71, 14);
INSERT INTO `sys_record_detail` VALUES (72, 17);
INSERT INTO `sys_record_detail` VALUES (73, 8);
INSERT INTO `sys_record_detail` VALUES (74, 12);
INSERT INTO `sys_record_detail` VALUES (75, 16);
INSERT INTO `sys_record_detail` VALUES (76, 8);
INSERT INTO `sys_record_detail` VALUES (77, 13);
INSERT INTO `sys_record_detail` VALUES (78, 16);
INSERT INTO `sys_record_detail` VALUES (79, 8);
INSERT INTO `sys_record_detail` VALUES (80, 13);
INSERT INTO `sys_record_detail` VALUES (81, 18);
INSERT INTO `sys_record_detail` VALUES (82, 10);
INSERT INTO `sys_record_detail` VALUES (83, 12);
INSERT INTO `sys_record_detail` VALUES (84, 17);
INSERT INTO `sys_record_detail` VALUES (85, 11);
INSERT INTO `sys_record_detail` VALUES (86, 12);
INSERT INTO `sys_record_detail` VALUES (87, 17);
INSERT INTO `sys_record_detail` VALUES (88, 9);
INSERT INTO `sys_record_detail` VALUES (89, 12);
INSERT INTO `sys_record_detail` VALUES (90, 17);
INSERT INTO `sys_record_detail` VALUES (91, 9);
INSERT INTO `sys_record_detail` VALUES (91, 11);
INSERT INTO `sys_record_detail` VALUES (92, 13);
INSERT INTO `sys_record_detail` VALUES (93, 17);
INSERT INTO `sys_record_detail` VALUES (94, 8);
INSERT INTO `sys_record_detail` VALUES (95, 13);
INSERT INTO `sys_record_detail` VALUES (96, 16);
INSERT INTO `sys_record_detail` VALUES (97, 10);
INSERT INTO `sys_record_detail` VALUES (98, 13);
INSERT INTO `sys_record_detail` VALUES (99, 16);
INSERT INTO `sys_record_detail` VALUES (100, 11);
INSERT INTO `sys_record_detail` VALUES (101, 13);
INSERT INTO `sys_record_detail` VALUES (102, 17);
INSERT INTO `sys_record_detail` VALUES (103, 9);
INSERT INTO `sys_record_detail` VALUES (104, 13);
INSERT INTO `sys_record_detail` VALUES (105, 17);
INSERT INTO `sys_record_detail` VALUES (106, 10);
INSERT INTO `sys_record_detail` VALUES (107, 14);
INSERT INTO `sys_record_detail` VALUES (108, 17);
INSERT INTO `sys_record_detail` VALUES (109, 9);
INSERT INTO `sys_record_detail` VALUES (110, 14);
INSERT INTO `sys_record_detail` VALUES (111, 17);
INSERT INTO `sys_record_detail` VALUES (112, 10);
INSERT INTO `sys_record_detail` VALUES (113, 13);
INSERT INTO `sys_record_detail` VALUES (114, 17);
INSERT INTO `sys_record_detail` VALUES (115, 8);
INSERT INTO `sys_record_detail` VALUES (116, 16);
INSERT INTO `sys_record_detail` VALUES (117, 22);
INSERT INTO `sys_record_detail` VALUES (118, 29);
INSERT INTO `sys_record_detail` VALUES (119, 35);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '角色状态(1-正常；0-停用)',
  `data_scope` tinyint(4) NULL DEFAULT NULL COMMENT '数据权限(0-所有数据；1-部门及子部门数据；2-本部门数据；3-本人数据)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ROOT', 1, 1, 0, 0, '2021-05-21 14:56:51', '2018-12-23 16:00:00');
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'ADMIN', 2, 1, 1, 0, '2021-03-25 12:39:54', NULL);
INSERT INTO `sys_role` VALUES (3, '访问游客', 'GUEST', 3, 1, 2, 0, '2021-05-26 15:49:05', '2019-05-05 16:00:00');
INSERT INTO `sys_role` VALUES (128, '职工', 'TEACHER', 1, 1, 0, 0, '2024-03-13 13:33:21', '2024-03-13 14:40:36');
INSERT INTO `sys_role` VALUES (129, '学生', 'STUDENT', 1, 1, 0, 0, '2024-03-13 13:34:52', '2024-03-13 14:40:41');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 31);
INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 88);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 70);
INSERT INTO `sys_role_menu` VALUES (2, 71);
INSERT INTO `sys_role_menu` VALUES (2, 72);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 73);
INSERT INTO `sys_role_menu` VALUES (2, 74);
INSERT INTO `sys_role_menu` VALUES (2, 75);
INSERT INTO `sys_role_menu` VALUES (2, 5);
INSERT INTO `sys_role_menu` VALUES (2, 76);
INSERT INTO `sys_role_menu` VALUES (2, 77);
INSERT INTO `sys_role_menu` VALUES (2, 78);
INSERT INTO `sys_role_menu` VALUES (2, 6);
INSERT INTO `sys_role_menu` VALUES (2, 79);
INSERT INTO `sys_role_menu` VALUES (2, 81);
INSERT INTO `sys_role_menu` VALUES (2, 84);
INSERT INTO `sys_role_menu` VALUES (2, 85);
INSERT INTO `sys_role_menu` VALUES (2, 86);
INSERT INTO `sys_role_menu` VALUES (2, 87);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 113);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 117);
INSERT INTO `sys_role_menu` VALUES (2, 118);
INSERT INTO `sys_role_menu` VALUES (2, 119);
INSERT INTO `sys_role_menu` VALUES (2, 120);
INSERT INTO `sys_role_menu` VALUES (2, 161);
INSERT INTO `sys_role_menu` VALUES (2, 162);
INSERT INTO `sys_role_menu` VALUES (2, 163);
INSERT INTO `sys_role_menu` VALUES (2, 121);
INSERT INTO `sys_role_menu` VALUES (2, 122);
INSERT INTO `sys_role_menu` VALUES (2, 123);
INSERT INTO `sys_role_menu` VALUES (2, 124);
INSERT INTO `sys_role_menu` VALUES (2, 125);
INSERT INTO `sys_role_menu` VALUES (2, 126);
INSERT INTO `sys_role_menu` VALUES (2, 127);
INSERT INTO `sys_role_menu` VALUES (2, 128);
INSERT INTO `sys_role_menu` VALUES (2, 157);
INSERT INTO `sys_role_menu` VALUES (2, 158);
INSERT INTO `sys_role_menu` VALUES (2, 159);
INSERT INTO `sys_role_menu` VALUES (2, 160);
INSERT INTO `sys_role_menu` VALUES (2, 164);
INSERT INTO `sys_role_menu` VALUES (2, 165);
INSERT INTO `sys_role_menu` VALUES (2, 166);
INSERT INTO `sys_role_menu` VALUES (2, 167);
INSERT INTO `sys_role_menu` VALUES (2, 168);
INSERT INTO `sys_role_menu` VALUES (2, 169);
INSERT INTO `sys_role_menu` VALUES (2, 170);
INSERT INTO `sys_role_menu` VALUES (2, 171);
INSERT INTO `sys_role_menu` VALUES (2, 172);
INSERT INTO `sys_role_menu` VALUES (2, 173);
INSERT INTO `sys_role_menu` VALUES (2, 174);
INSERT INTO `sys_role_menu` VALUES (2, 175);
INSERT INTO `sys_role_menu` VALUES (2, 176);
INSERT INTO `sys_role_menu` VALUES (2, 177);
INSERT INTO `sys_role_menu` VALUES (2, 178);
INSERT INTO `sys_role_menu` VALUES (2, 179);
INSERT INTO `sys_role_menu` VALUES (2, 180);
INSERT INTO `sys_role_menu` VALUES (2, 181);
INSERT INTO `sys_role_menu` VALUES (2, 182);
INSERT INTO `sys_role_menu` VALUES (2, 183);
INSERT INTO `sys_role_menu` VALUES (2, 184);
INSERT INTO `sys_role_menu` VALUES (2, 185);
INSERT INTO `sys_role_menu` VALUES (2, 186);
INSERT INTO `sys_role_menu` VALUES (2, 187);
INSERT INTO `sys_role_menu` VALUES (2, 188);
INSERT INTO `sys_role_menu` VALUES (2, 189);
INSERT INTO `sys_role_menu` VALUES (2, 190);
INSERT INTO `sys_role_menu` VALUES (2, 191);
INSERT INTO `sys_role_menu` VALUES (2, 192);
INSERT INTO `sys_role_menu` VALUES (2, 193);
INSERT INTO `sys_role_menu` VALUES (2, 194);
INSERT INTO `sys_role_menu` VALUES (2, 195);
INSERT INTO `sys_role_menu` VALUES (2, 196);
INSERT INTO `sys_role_menu` VALUES (2, 197);
INSERT INTO `sys_role_menu` VALUES (2, 198);
INSERT INTO `sys_role_menu` VALUES (2, 199);
INSERT INTO `sys_role_menu` VALUES (2, 200);
INSERT INTO `sys_role_menu` VALUES (2, 201);
INSERT INTO `sys_role_menu` VALUES (2, 202);
INSERT INTO `sys_role_menu` VALUES (2, 203);
INSERT INTO `sys_role_menu` VALUES (2, 204);
INSERT INTO `sys_role_menu` VALUES (2, 205);
INSERT INTO `sys_role_menu` VALUES (2, 206);
INSERT INTO `sys_role_menu` VALUES (2, 207);
INSERT INTO `sys_role_menu` VALUES (2, 211);
INSERT INTO `sys_role_menu` VALUES (2, 208);
INSERT INTO `sys_role_menu` VALUES (2, 209);
INSERT INTO `sys_role_menu` VALUES (2, 210);
INSERT INTO `sys_role_menu` VALUES (128, 168);
INSERT INTO `sys_role_menu` VALUES (128, 169);
INSERT INTO `sys_role_menu` VALUES (128, 170);
INSERT INTO `sys_role_menu` VALUES (128, 171);
INSERT INTO `sys_role_menu` VALUES (128, 172);
INSERT INTO `sys_role_menu` VALUES (128, 173);
INSERT INTO `sys_role_menu` VALUES (128, 174);
INSERT INTO `sys_role_menu` VALUES (128, 175);
INSERT INTO `sys_role_menu` VALUES (128, 176);
INSERT INTO `sys_role_menu` VALUES (128, 177);
INSERT INTO `sys_role_menu` VALUES (128, 178);
INSERT INTO `sys_role_menu` VALUES (128, 179);
INSERT INTO `sys_role_menu` VALUES (128, 180);
INSERT INTO `sys_role_menu` VALUES (128, 181);
INSERT INTO `sys_role_menu` VALUES (128, 182);
INSERT INTO `sys_role_menu` VALUES (128, 183);
INSERT INTO `sys_role_menu` VALUES (128, 184);
INSERT INTO `sys_role_menu` VALUES (128, 185);
INSERT INTO `sys_role_menu` VALUES (128, 186);
INSERT INTO `sys_role_menu` VALUES (128, 187);
INSERT INTO `sys_role_menu` VALUES (128, 188);
INSERT INTO `sys_role_menu` VALUES (128, 189);
INSERT INTO `sys_role_menu` VALUES (128, 190);
INSERT INTO `sys_role_menu` VALUES (128, 191);
INSERT INTO `sys_role_menu` VALUES (128, 192);
INSERT INTO `sys_role_menu` VALUES (128, 193);
INSERT INTO `sys_role_menu` VALUES (128, 194);
INSERT INTO `sys_role_menu` VALUES (128, 195);
INSERT INTO `sys_role_menu` VALUES (128, 196);
INSERT INTO `sys_role_menu` VALUES (128, 206);
INSERT INTO `sys_role_menu` VALUES (128, 207);
INSERT INTO `sys_role_menu` VALUES (128, 211);
INSERT INTO `sys_role_menu` VALUES (128, 208);
INSERT INTO `sys_role_menu` VALUES (128, 209);
INSERT INTO `sys_role_menu` VALUES (128, 210);
INSERT INTO `sys_role_menu` VALUES (129, 170);
INSERT INTO `sys_role_menu` VALUES (129, 173);
INSERT INTO `sys_role_menu` VALUES (129, 174);
INSERT INTO `sys_role_menu` VALUES (129, 186);
INSERT INTO `sys_role_menu` VALUES (129, 188);
INSERT INTO `sys_role_menu` VALUES (129, 193);
INSERT INTO `sys_role_menu` VALUES (129, 194);
INSERT INTO `sys_role_menu` VALUES (129, 195);
INSERT INTO `sys_role_menu` VALUES (129, 196);
INSERT INTO `sys_role_menu` VALUES (129, 206);
INSERT INTO `sys_role_menu` VALUES (129, 207);
INSERT INTO `sys_role_menu` VALUES (129, 211);

-- ----------------------------
-- Table structure for sys_score
-- ----------------------------
DROP TABLE IF EXISTS `sys_score`;
CREATE TABLE `sys_score`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `exam_id` bigint(20) NULL DEFAULT NULL COMMENT '考试ID',
  `grade_id` bigint(20) NULL DEFAULT NULL COMMENT '年级ID',
  `grade_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年级名称',
  `clazz_id` bigint(20) NULL DEFAULT NULL COMMENT '班级ID',
  `clazz_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级名称',
  `student_id` bigint(20) NULL DEFAULT NULL COMMENT '学生ID',
  `course_id` bigint(20) NULL DEFAULT NULL COMMENT '课程ID',
  `teacher_id` bigint(20) NULL DEFAULT NULL COMMENT '教师ID',
  `score` double NULL DEFAULT NULL COMMENT '分数',
  `degree` int(11) NULL DEFAULT NULL COMMENT '等级',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 247 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '成绩表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_score
-- ----------------------------
INSERT INTO `sys_score` VALUES (1, 13, 3, '高一', 3, '一班', 2, 7, 2, 108, 3, 0, '2024-03-18 17:31:15', '2024-03-18 17:31:15');
INSERT INTO `sys_score` VALUES (2, 13, 3, '高一', 3, '一班', 4, 7, 2, 102, 4, 0, '2024-03-18 17:38:22', '2024-03-18 17:38:22');
INSERT INTO `sys_score` VALUES (3, 13, 3, '高一', 3, '一班', 5, 7, 2, 98, 4, 0, '2024-03-18 17:38:23', '2024-03-18 17:38:23');
INSERT INTO `sys_score` VALUES (4, 13, 3, '高一', 3, '一班', 6, 7, 2, 110, 3, 0, '2024-03-18 17:38:23', '2024-03-18 17:38:23');
INSERT INTO `sys_score` VALUES (5, 13, 3, '高一', 3, '一班', 7, 7, 2, 127, 2, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (6, 13, 3, '高一', 3, '一班', 8, 7, 2, 145, 1, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (7, 13, 3, '高一', 3, '一班', 9, 7, 2, 25, 5, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (8, 13, 3, '高一', 3, '一班', 10, 7, 2, 139, 1, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (9, 13, 3, '高一', 3, '一班', 11, 7, 2, 56, 5, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (10, 13, 3, '高一', 3, '一班', 12, 7, 2, 132, 2, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (11, 13, 3, '高一', 3, '一班', 22, 7, 2, 78, 5, 0, '2024-03-18 18:16:48', '2024-03-18 18:16:48');
INSERT INTO `sys_score` VALUES (12, 13, 3, '高一', 3, '一班', 2, 8, 3, 132, 2, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (13, 13, 3, '高一', 3, '一班', 4, 8, 3, 121, 2, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (14, 13, 3, '高一', 3, '一班', 5, 8, 3, 131, 2, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (15, 13, 3, '高一', 3, '一班', 6, 8, 3, 141, 1, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (16, 13, 3, '高一', 3, '一班', 7, 8, 3, 45, 5, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (17, 13, 3, '高一', 3, '一班', 8, 8, 3, 98, 4, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (18, 13, 3, '高一', 3, '一班', 9, 8, 3, 66, 5, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (19, 13, 3, '高一', 3, '一班', 10, 8, 3, 77, 5, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (20, 13, 3, '高一', 3, '一班', 11, 8, 3, 121, 2, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (21, 13, 3, '高一', 3, '一班', 12, 8, 3, 132, 2, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (22, 13, 3, '高一', 3, '一班', 22, 8, 3, 150, 1, 0, '2024-03-18 22:21:09', '2024-03-18 22:21:09');
INSERT INTO `sys_score` VALUES (23, 13, 3, '高一', 3, '一班', 2, 9, 4, 111, 3, 0, '2024-03-18 22:22:31', '2024-03-18 22:22:31');
INSERT INTO `sys_score` VALUES (24, 13, 3, '高一', 3, '一班', 4, 9, 4, 98, 4, 0, '2024-03-18 22:22:31', '2024-03-18 22:22:31');
INSERT INTO `sys_score` VALUES (25, 13, 3, '高一', 3, '一班', 5, 9, 4, 143, 1, 0, '2024-03-18 22:22:31', '2024-03-18 22:22:31');
INSERT INTO `sys_score` VALUES (26, 13, 3, '高一', 3, '一班', 6, 9, 4, 90, 4, 0, '2024-03-18 22:22:31', '2024-03-18 22:22:31');
INSERT INTO `sys_score` VALUES (27, 13, 3, '高一', 3, '一班', 7, 9, 4, 85, 5, 0, '2024-03-18 22:22:31', '2024-03-18 22:22:31');
INSERT INTO `sys_score` VALUES (28, 13, 3, '高一', 3, '一班', 8, 9, 4, 76, 5, 0, '2024-03-18 22:22:31', '2024-03-18 22:22:31');
INSERT INTO `sys_score` VALUES (29, 13, 3, '高一', 3, '一班', 9, 9, 4, 120, 2, 0, '2024-03-18 22:22:32', '2024-03-18 22:22:32');
INSERT INTO `sys_score` VALUES (30, 13, 3, '高一', 3, '一班', 10, 9, 4, 121, 2, 0, '2024-03-18 22:22:32', '2024-03-18 22:22:32');
INSERT INTO `sys_score` VALUES (31, 13, 3, '高一', 3, '一班', 11, 9, 4, 132, 2, 0, '2024-03-18 22:22:32', '2024-03-18 22:22:32');
INSERT INTO `sys_score` VALUES (32, 13, 3, '高一', 3, '一班', 12, 9, 4, 24, 5, 0, '2024-03-18 22:22:32', '2024-03-18 22:22:32');
INSERT INTO `sys_score` VALUES (33, 13, 3, '高一', 3, '一班', 22, 9, 4, 57, 5, 0, '2024-03-18 22:22:32', '2024-03-18 22:22:32');
INSERT INTO `sys_score` VALUES (34, 13, 3, '高一', 3, '一班', 2, 15, 7, 85, 2, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (35, 13, 3, '高一', 3, '一班', 4, 15, 7, 76, 3, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (36, 13, 3, '高一', 3, '一班', 5, 15, 7, 88, 2, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (37, 13, 3, '高一', 3, '一班', 6, 15, 7, 75, 3, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (38, 13, 3, '高一', 3, '一班', 7, 15, 7, 90, 1, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (39, 13, 3, '高一', 3, '一班', 8, 15, 7, 87, 2, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (40, 13, 3, '高一', 3, '一班', 9, 15, 7, 86, 2, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (41, 13, 3, '高一', 3, '一班', 10, 15, 7, 90, 1, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (42, 13, 3, '高一', 3, '一班', 11, 15, 7, 86, 2, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (43, 13, 3, '高一', 3, '一班', 12, 15, 7, 98, 1, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (44, 13, 3, '高一', 3, '一班', 22, 15, 7, 87, 2, 0, '2024-03-18 22:22:57', '2024-03-18 22:22:57');
INSERT INTO `sys_score` VALUES (45, 13, 3, '高一', 3, '一班', 2, 13, 5, 90, 1, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (46, 13, 3, '高一', 3, '一班', 4, 13, 5, 99, 1, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (47, 13, 3, '高一', 3, '一班', 5, 13, 5, 89, 2, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (48, 13, 3, '高一', 3, '一班', 6, 13, 5, 95, 1, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (49, 13, 3, '高一', 3, '一班', 7, 13, 5, 55, 5, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (50, 13, 3, '高一', 3, '一班', 8, 13, 5, 56, 5, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (51, 13, 3, '高一', 3, '一班', 9, 13, 5, 76, 3, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (52, 13, 3, '高一', 3, '一班', 10, 13, 5, 53, 5, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (53, 13, 3, '高一', 3, '一班', 11, 13, 5, 56, 5, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (54, 13, 3, '高一', 3, '一班', 12, 13, 5, 88, 2, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (55, 13, 3, '高一', 3, '一班', 22, 13, 5, 98, 1, 0, '2024-03-18 22:23:21', '2024-03-18 22:23:21');
INSERT INTO `sys_score` VALUES (56, 13, 3, '高一', 3, '一班', 2, 14, 4, 98, 1, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (57, 13, 3, '高一', 3, '一班', 4, 14, 4, 76, 3, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (58, 13, 3, '高一', 3, '一班', 5, 14, 4, 86, 2, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (59, 13, 3, '高一', 3, '一班', 6, 14, 4, 78, 3, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (60, 13, 3, '高一', 3, '一班', 7, 14, 4, 79, 3, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (61, 13, 3, '高一', 3, '一班', 8, 14, 4, 98, 1, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (62, 13, 3, '高一', 3, '一班', 9, 14, 4, 92, 1, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (63, 13, 3, '高一', 3, '一班', 10, 14, 4, 85, 2, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (64, 13, 3, '高一', 3, '一班', 11, 14, 4, 90, 1, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (65, 13, 3, '高一', 3, '一班', 12, 14, 4, 99, 1, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (66, 13, 3, '高一', 3, '一班', 22, 14, 4, 57, 5, 0, '2024-03-18 22:23:49', '2024-03-18 22:23:49');
INSERT INTO `sys_score` VALUES (67, 13, 3, '高一', 6, '二班', 13, 7, 2, 123, 2, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (68, 13, 3, '高一', 6, '二班', 14, 7, 2, 124, 2, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (69, 13, 3, '高一', 6, '二班', 15, 7, 2, 110, 3, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (70, 13, 3, '高一', 6, '二班', 16, 7, 2, 132, 2, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (71, 13, 3, '高一', 6, '二班', 17, 7, 2, 90, 4, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (72, 13, 3, '高一', 6, '二班', 18, 7, 2, 87, 5, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (73, 13, 3, '高一', 6, '二班', 19, 7, 2, 56, 5, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (74, 13, 3, '高一', 6, '二班', 20, 7, 2, 99, 4, 0, '2024-03-18 22:28:01', '2024-03-18 22:28:01');
INSERT INTO `sys_score` VALUES (75, 13, 3, '高一', 6, '二班', 13, 8, 3, 78, 5, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (76, 13, 3, '高一', 6, '二班', 14, 8, 3, 97, 4, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (77, 13, 3, '高一', 6, '二班', 15, 8, 3, 89, 5, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (78, 13, 3, '高一', 6, '二班', 16, 8, 3, 78, 5, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (79, 13, 3, '高一', 6, '二班', 17, 8, 3, 98, 4, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (80, 13, 3, '高一', 6, '二班', 18, 8, 3, 78, 5, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (81, 13, 3, '高一', 6, '二班', 19, 8, 3, 96, 4, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (82, 13, 3, '高一', 6, '二班', 20, 8, 3, 76, 5, 0, '2024-03-18 22:28:20', '2024-03-18 22:28:20');
INSERT INTO `sys_score` VALUES (83, 13, 3, '高一', 6, '二班', 13, 9, 4, 112, 3, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (84, 13, 3, '高一', 6, '二班', 14, 9, 4, 143, 1, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (85, 13, 3, '高一', 6, '二班', 15, 9, 4, 145, 1, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (86, 13, 3, '高一', 6, '二班', 16, 9, 4, 64, 5, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (87, 13, 3, '高一', 6, '二班', 17, 9, 4, 89, 5, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (88, 13, 3, '高一', 6, '二班', 18, 9, 4, 97, 4, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (89, 13, 3, '高一', 6, '二班', 19, 9, 4, 76, 5, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (90, 13, 3, '高一', 6, '二班', 20, 9, 4, 90, 4, 0, '2024-03-18 22:28:41', '2024-03-18 22:28:41');
INSERT INTO `sys_score` VALUES (91, 13, 3, '高一', 6, '二班', 13, 10, 5, 87, 2, 0, '2024-03-18 22:29:04', '2024-03-18 22:29:04');
INSERT INTO `sys_score` VALUES (92, 13, 3, '高一', 6, '二班', 14, 10, 5, 89, 2, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (93, 13, 3, '高一', 6, '二班', 15, 10, 5, 56, 5, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (94, 13, 3, '高一', 6, '二班', 16, 10, 5, 98, 1, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (95, 13, 3, '高一', 6, '二班', 17, 10, 5, 87, 2, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (96, 13, 3, '高一', 6, '二班', 18, 10, 5, 90, 1, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (97, 13, 3, '高一', 6, '二班', 19, 10, 5, 80, 2, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (98, 13, 3, '高一', 6, '二班', 20, 10, 5, 66, 4, 0, '2024-03-18 22:29:05', '2024-03-18 22:29:05');
INSERT INTO `sys_score` VALUES (99, 13, 3, '高一', 6, '二班', 13, 11, 7, 98, 1, 0, '2024-03-18 22:29:17', '2024-03-18 22:29:17');
INSERT INTO `sys_score` VALUES (100, 13, 3, '高一', 6, '二班', 14, 11, 7, 76, 3, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (101, 13, 3, '高一', 6, '二班', 15, 11, 7, 76, 3, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (102, 13, 3, '高一', 6, '二班', 16, 11, 7, 89, 2, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (103, 13, 3, '高一', 6, '二班', 17, 11, 7, 76, 3, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (104, 13, 3, '高一', 6, '二班', 18, 11, 7, 89, 2, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (105, 13, 3, '高一', 6, '二班', 19, 11, 7, 56, 5, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (106, 13, 3, '高一', 6, '二班', 20, 11, 7, 76, 3, 0, '2024-03-18 22:29:18', '2024-03-18 22:29:18');
INSERT INTO `sys_score` VALUES (107, 13, 3, '高一', 6, '二班', 13, 12, 7, 87, 2, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (108, 13, 3, '高一', 6, '二班', 14, 12, 7, 89, 2, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (109, 13, 3, '高一', 6, '二班', 15, 12, 7, 79, 3, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (110, 13, 3, '高一', 6, '二班', 16, 12, 7, 99, 1, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (111, 13, 3, '高一', 6, '二班', 17, 12, 7, 98, 1, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (112, 13, 3, '高一', 6, '二班', 18, 12, 7, 78, 3, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (113, 13, 3, '高一', 6, '二班', 19, 12, 7, 67, 4, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (114, 13, 3, '高一', 6, '二班', 20, 12, 7, 89, 2, 0, '2024-03-18 22:30:09', '2024-03-18 22:30:09');
INSERT INTO `sys_score` VALUES (115, 15, 3, '高一', 3, '一班', 2, 7, 2, 125, 2, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (116, 15, 3, '高一', 3, '一班', 4, 7, 2, 98, 4, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (117, 15, 3, '高一', 3, '一班', 5, 7, 2, 99, 4, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (118, 15, 3, '高一', 3, '一班', 6, 7, 2, 101, 4, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (119, 15, 3, '高一', 3, '一班', 7, 7, 2, 102, 4, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (120, 15, 3, '高一', 3, '一班', 8, 7, 2, 110, 3, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (121, 15, 3, '高一', 3, '一班', 9, 7, 2, 145, 1, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (122, 15, 3, '高一', 3, '一班', 10, 7, 2, 111, 3, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (123, 15, 3, '高一', 3, '一班', 11, 7, 2, 85, 5, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (124, 15, 3, '高一', 3, '一班', 12, 7, 2, 94, 4, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (125, 15, 3, '高一', 3, '一班', 22, 7, 2, 56, 5, 0, '2024-03-21 20:24:07', '2024-03-21 20:24:07');
INSERT INTO `sys_score` VALUES (126, 15, 3, '高一', 3, '一班', 2, 8, 3, 56, 5, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (127, 15, 3, '高一', 3, '一班', 4, 8, 3, 78, 5, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (128, 15, 3, '高一', 3, '一班', 5, 8, 3, 98, 4, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (129, 15, 3, '高一', 3, '一班', 6, 8, 3, 90, 4, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (130, 15, 3, '高一', 3, '一班', 7, 8, 3, 132, 2, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (131, 15, 3, '高一', 3, '一班', 8, 8, 3, 23, 5, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (132, 15, 3, '高一', 3, '一班', 9, 8, 3, 150, 1, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (133, 15, 3, '高一', 3, '一班', 10, 8, 3, 54, 5, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (134, 15, 3, '高一', 3, '一班', 11, 8, 3, 67, 5, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (135, 15, 3, '高一', 3, '一班', 12, 8, 3, 123, 2, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (136, 15, 3, '高一', 3, '一班', 22, 8, 3, 135, 1, 0, '2024-03-21 20:24:35', '2024-03-21 20:24:35');
INSERT INTO `sys_score` VALUES (137, 15, 3, '高一', 3, '一班', 2, 9, 4, 89, 5, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (138, 15, 3, '高一', 3, '一班', 4, 9, 4, 120, 2, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (139, 15, 3, '高一', 3, '一班', 5, 9, 4, 122, 2, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (140, 15, 3, '高一', 3, '一班', 6, 9, 4, 111, 3, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (141, 15, 3, '高一', 3, '一班', 7, 9, 4, 90, 4, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (142, 15, 3, '高一', 3, '一班', 8, 9, 4, 87, 5, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (143, 15, 3, '高一', 3, '一班', 9, 9, 4, 67, 5, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (144, 15, 3, '高一', 3, '一班', 10, 9, 4, 90, 4, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (145, 15, 3, '高一', 3, '一班', 11, 9, 4, 134, 2, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (146, 15, 3, '高一', 3, '一班', 12, 9, 4, 125, 2, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (147, 15, 3, '高一', 3, '一班', 22, 9, 4, 78, 5, 0, '2024-03-21 20:25:07', '2024-03-21 20:25:07');
INSERT INTO `sys_score` VALUES (148, 15, 3, '高一', 3, '一班', 2, 15, 7, 89, 2, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (149, 15, 3, '高一', 3, '一班', 4, 15, 7, 87, 2, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (150, 15, 3, '高一', 3, '一班', 5, 15, 7, 17, 5, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (151, 15, 3, '高一', 3, '一班', 6, 15, 7, 87, 2, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (152, 15, 3, '高一', 3, '一班', 7, 15, 7, 99, 1, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (153, 15, 3, '高一', 3, '一班', 8, 15, 7, 78, 3, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (154, 15, 3, '高一', 3, '一班', 9, 15, 7, 56, 5, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (155, 15, 3, '高一', 3, '一班', 10, 15, 7, 76, 3, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (156, 15, 3, '高一', 3, '一班', 11, 15, 7, 89, 2, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (157, 15, 3, '高一', 3, '一班', 12, 15, 7, 87, 2, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (158, 15, 3, '高一', 3, '一班', 22, 15, 7, 79, 3, 0, '2024-03-21 20:25:31', '2024-03-21 20:25:31');
INSERT INTO `sys_score` VALUES (159, 15, 3, '高一', 3, '一班', 2, 13, 5, 89, 2, 0, '2024-03-21 20:25:50', '2024-03-21 20:25:50');
INSERT INTO `sys_score` VALUES (160, 15, 3, '高一', 3, '一班', 4, 13, 5, 76, 3, 0, '2024-03-21 20:25:50', '2024-03-21 20:25:50');
INSERT INTO `sys_score` VALUES (161, 15, 3, '高一', 3, '一班', 5, 13, 5, 86, 2, 0, '2024-03-21 20:25:50', '2024-03-21 20:25:50');
INSERT INTO `sys_score` VALUES (162, 15, 3, '高一', 3, '一班', 6, 13, 5, 76, 3, 0, '2024-03-21 20:25:50', '2024-03-21 20:25:50');
INSERT INTO `sys_score` VALUES (163, 15, 3, '高一', 3, '一班', 7, 13, 5, 56, 5, 0, '2024-03-21 20:25:50', '2024-03-21 20:25:50');
INSERT INTO `sys_score` VALUES (164, 15, 3, '高一', 3, '一班', 8, 13, 5, 99, 1, 0, '2024-03-21 20:25:50', '2024-03-21 20:25:50');
INSERT INTO `sys_score` VALUES (165, 15, 3, '高一', 3, '一班', 9, 13, 5, 98, 1, 0, '2024-03-21 20:25:51', '2024-03-21 20:25:51');
INSERT INTO `sys_score` VALUES (166, 15, 3, '高一', 3, '一班', 10, 13, 5, 90, 1, 0, '2024-03-21 20:25:51', '2024-03-21 20:25:51');
INSERT INTO `sys_score` VALUES (167, 15, 3, '高一', 3, '一班', 11, 13, 5, 75, 3, 0, '2024-03-21 20:25:51', '2024-03-21 20:25:51');
INSERT INTO `sys_score` VALUES (168, 15, 3, '高一', 3, '一班', 12, 13, 5, 56, 5, 0, '2024-03-21 20:25:51', '2024-03-21 20:25:51');
INSERT INTO `sys_score` VALUES (169, 15, 3, '高一', 3, '一班', 22, 13, 5, 75, 3, 0, '2024-03-21 20:25:51', '2024-03-21 20:25:51');
INSERT INTO `sys_score` VALUES (170, 15, 3, '高一', 3, '一班', 2, 14, 4, 67, 4, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (171, 15, 3, '高一', 3, '一班', 4, 14, 4, 75, 3, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (172, 15, 3, '高一', 3, '一班', 5, 14, 4, 54, 5, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (173, 15, 3, '高一', 3, '一班', 6, 14, 4, 67, 4, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (174, 15, 3, '高一', 3, '一班', 7, 14, 4, 86, 2, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (175, 15, 3, '高一', 3, '一班', 8, 14, 4, 65, 4, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (176, 15, 3, '高一', 3, '一班', 9, 14, 4, 54, 5, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (177, 15, 3, '高一', 3, '一班', 10, 14, 4, 65, 4, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (178, 15, 3, '高一', 3, '一班', 11, 14, 4, 76, 3, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (179, 15, 3, '高一', 3, '一班', 12, 14, 4, 53, 5, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (180, 15, 3, '高一', 3, '一班', 22, 14, 4, 100, 1, 0, '2024-03-21 20:26:19', '2024-03-21 20:26:19');
INSERT INTO `sys_score` VALUES (181, 16, 3, '高一', 3, '一班', 2, 7, 2, 105, 3, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (182, 16, 3, '高一', 3, '一班', 4, 7, 2, 123, 2, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (183, 16, 3, '高一', 3, '一班', 5, 7, 2, 78, 5, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (184, 16, 3, '高一', 3, '一班', 6, 7, 2, 55, 5, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (185, 16, 3, '高一', 3, '一班', 7, 7, 2, 99, 4, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (186, 16, 3, '高一', 3, '一班', 8, 7, 2, 121, 2, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (187, 16, 3, '高一', 3, '一班', 9, 7, 2, 123, 2, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (188, 16, 3, '高一', 3, '一班', 10, 7, 2, 134, 2, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (189, 16, 3, '高一', 3, '一班', 11, 7, 2, 21, 5, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (190, 16, 3, '高一', 3, '一班', 12, 7, 2, 56, 5, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (191, 16, 3, '高一', 3, '一班', 22, 7, 2, 88, 5, 0, '2024-03-22 12:08:32', '2024-03-22 12:08:32');
INSERT INTO `sys_score` VALUES (192, 16, 3, '高一', 3, '一班', 2, 8, 3, 98, 4, 0, '2024-03-22 12:09:07', '2024-03-22 12:09:07');
INSERT INTO `sys_score` VALUES (193, 16, 3, '高一', 3, '一班', 4, 8, 3, 76, 5, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (194, 16, 3, '高一', 3, '一班', 5, 8, 3, 86, 5, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (195, 16, 3, '高一', 3, '一班', 6, 8, 3, 100, 4, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (196, 16, 3, '高一', 3, '一班', 7, 8, 3, 150, 1, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (197, 16, 3, '高一', 3, '一班', 8, 8, 3, 43, 5, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (198, 16, 3, '高一', 3, '一班', 9, 8, 3, 134, 2, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (199, 16, 3, '高一', 3, '一班', 10, 8, 3, 121, 2, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (200, 16, 3, '高一', 3, '一班', 11, 8, 3, 145, 1, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (201, 16, 3, '高一', 3, '一班', 12, 8, 3, 109, 3, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (202, 16, 3, '高一', 3, '一班', 22, 8, 3, 135, 1, 0, '2024-03-22 12:09:08', '2024-03-22 12:09:08');
INSERT INTO `sys_score` VALUES (203, 16, 3, '高一', 3, '一班', 2, 9, 4, 102, 4, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (204, 16, 3, '高一', 3, '一班', 4, 9, 4, 123, 2, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (205, 16, 3, '高一', 3, '一班', 5, 9, 4, 113, 3, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (206, 16, 3, '高一', 3, '一班', 6, 9, 4, 109, 3, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (207, 16, 3, '高一', 3, '一班', 7, 9, 4, 43, 5, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (208, 16, 3, '高一', 3, '一班', 8, 9, 4, 56, 5, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (209, 16, 3, '高一', 3, '一班', 9, 9, 4, 98, 4, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (210, 16, 3, '高一', 3, '一班', 10, 9, 4, 123, 2, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (211, 16, 3, '高一', 3, '一班', 11, 9, 4, 126, 2, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (212, 16, 3, '高一', 3, '一班', 12, 9, 4, 26, 5, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (213, 16, 3, '高一', 3, '一班', 22, 9, 4, 95, 4, 0, '2024-03-22 12:09:40', '2024-03-22 12:09:40');
INSERT INTO `sys_score` VALUES (214, 16, 3, '高一', 3, '一班', 2, 15, 7, 78, 3, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (215, 16, 3, '高一', 3, '一班', 4, 15, 7, 65, 4, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (216, 16, 3, '高一', 3, '一班', 5, 15, 7, 76, 3, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (217, 16, 3, '高一', 3, '一班', 6, 15, 7, 88, 2, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (218, 16, 3, '高一', 3, '一班', 7, 15, 7, 98, 1, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (219, 16, 3, '高一', 3, '一班', 8, 15, 7, 75, 3, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (220, 16, 3, '高一', 3, '一班', 9, 15, 7, 65, 4, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (221, 16, 3, '高一', 3, '一班', 10, 15, 7, 76, 3, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (222, 16, 3, '高一', 3, '一班', 11, 15, 7, 87, 2, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (223, 16, 3, '高一', 3, '一班', 12, 15, 7, 96, 1, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (224, 16, 3, '高一', 3, '一班', 22, 15, 7, 67, 4, 0, '2024-03-22 12:10:03', '2024-03-22 12:10:03');
INSERT INTO `sys_score` VALUES (225, 16, 3, '高一', 3, '一班', 2, 13, 5, 57, 5, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (226, 16, 3, '高一', 3, '一班', 4, 13, 5, 75, 3, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (227, 16, 3, '高一', 3, '一班', 5, 13, 5, 68, 4, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (228, 16, 3, '高一', 3, '一班', 6, 13, 5, 98, 1, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (229, 16, 3, '高一', 3, '一班', 7, 13, 5, 86, 2, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (230, 16, 3, '高一', 3, '一班', 8, 13, 5, 76, 3, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (231, 16, 3, '高一', 3, '一班', 9, 13, 5, 88, 2, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (232, 16, 3, '高一', 3, '一班', 10, 13, 5, 87, 2, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (233, 16, 3, '高一', 3, '一班', 11, 13, 5, 76, 3, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (234, 16, 3, '高一', 3, '一班', 12, 13, 5, 80, 2, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (235, 16, 3, '高一', 3, '一班', 22, 13, 5, 89, 2, 0, '2024-03-22 12:10:28', '2024-03-22 12:10:28');
INSERT INTO `sys_score` VALUES (236, 16, 3, '高一', 3, '一班', 2, 14, 4, 98, 1, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (237, 16, 3, '高一', 3, '一班', 4, 14, 4, 87, 2, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (238, 16, 3, '高一', 3, '一班', 5, 14, 4, 98, 1, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (239, 16, 3, '高一', 3, '一班', 6, 14, 4, 86, 2, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (240, 16, 3, '高一', 3, '一班', 7, 14, 4, 89, 2, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (241, 16, 3, '高一', 3, '一班', 8, 14, 4, 64, 4, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (242, 16, 3, '高一', 3, '一班', 9, 14, 4, 65, 4, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (243, 16, 3, '高一', 3, '一班', 10, 14, 4, 87, 2, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (244, 16, 3, '高一', 3, '一班', 11, 14, 4, 76, 3, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (245, 16, 3, '高一', 3, '一班', 12, 14, 4, 89, 2, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');
INSERT INTO `sys_score` VALUES (246, 16, 3, '高一', 3, '一班', 22, 14, 4, 76, 3, 0, '2024-03-22 12:10:44', '2024-03-22 12:10:44');

-- ----------------------------
-- Table structure for sys_student
-- ----------------------------
DROP TABLE IF EXISTS `sys_student`;
CREATE TABLE `sys_student`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学生编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '学生姓名',
  `sex` tinyint(1) NOT NULL COMMENT '性别',
  `status` tinyint(1) NOT NULL COMMENT '状态（正常、停用）',
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `birth_day` date NULL DEFAULT NULL COMMENT '出生日期',
  `year` int(11) NULL DEFAULT NULL COMMENT '入学年份',
  `position_id` bigint(20) NULL DEFAULT NULL COMMENT '职务',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学生表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_student
-- ----------------------------
INSERT INTO `sys_student` VALUES (2, 'lisi', '$2a$10$7QK.3.XEpagm6S3S9KR3IOIuw1zGazW1HM64I4/PWZlcZgOBayOKK', 'lisi', '李四', 1, 1, '	110101199001013793', '2022-12-01', 2024, 1, '17377778888', '浙江省杭州市', 'http://127.0.0.1:9005/big-event/20240304/bd7d4e1879c04c2986c14052ffdc6d60.jpg', 0, '2024-03-04 08:57:04', '2024-03-11 20:41:27', '代发费大');
INSERT INTO `sys_student` VALUES (4, 'zhangsan123', '$2a$10$tLCLBMJKvhtNtDFgxf08N.EiIOUw1wpqU5mzl9TJo0GCf21.LjO6O', 'zhangsan', '张三', 2, 1, NULL, '2024-03-05', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240309/810be0d4314e47b3a03c42e40c96afdc.jpg', 0, '2024-03-04 21:02:40', '2024-03-09 08:32:10', NULL);
INSERT INTO `sys_student` VALUES (5, 'wangwu', '$2a$10$PI7wxc.QuD5eEnIQqqkJZucarMp8grVQuRtXtmw8.hzTg4Y4Ncp.G', 'wangwu', '王五', 1, 1, NULL, '2021-01-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240309/b378325c1e7342e4afffa963bab19dfb.jpg', 0, '2024-03-09 08:33:04', '2024-03-09 08:41:59', NULL);
INSERT INTO `sys_student` VALUES (6, 'zhaoliu', '$2a$10$GCpXMbpnp10tS8k1A68/rOhKWNcBLOtfeU7w6fGafA4ENCQ/hGFty', 'zhaoliu', '赵六', 1, 1, NULL, '2020-02-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240309/83d0f180509041fa84c7fbd305de2dae.png', 0, '2024-03-09 08:33:28', '2024-03-09 08:42:45', NULL);
INSERT INTO `sys_student` VALUES (7, 'kiko', '$2a$10$gkwZFhi/sMcTQyUAqlU0.ubWAwvWb.CqhIZz.Mf.qJoBLEyJdgN/K', 'kiko', '王胜男', 2, 1, NULL, '2020-01-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240313/43d40d5b42b742d4ae7b53d88c1cc6fb.jpg', 0, '2024-03-09 09:00:26', '2024-03-09 09:00:26', NULL);
INSERT INTO `sys_student` VALUES (8, 'huangzongwei', '$2a$10$FQFf6RvKy8wjJJ4kHLvwJ.hz4r2CPi1HIvL4et1VzQfMgs6k72Kua', 'huangzongwei', '黄宗伟', 1, 1, NULL, '2018-11-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/3ae9e62a9e324c6fb2ded82208a28524.jpg', 0, '2024-03-09 21:12:04', '2024-03-14 15:49:38', NULL);
INSERT INTO `sys_student` VALUES (9, 'zhaoyounan', '$2a$10$LCnb8K5i4Fdim.52ntiACerYn7gnf3KImQi1kfTo/KfI5.10hv5LG', 'zhaoyounan', '赵友男', 2, 1, NULL, '2017-03-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/4242b5a28ca44879b28c1af8504eb86f.jpg', 0, '2024-03-09 21:13:08', '2024-03-14 15:49:22', NULL);
INSERT INTO `sys_student` VALUES (10, 'adong', '$2a$10$OaNBFqqOYndqIeZW.Jsc7.nMGamQAl0oD9JQeT5ZCyXk2f2EO2XVW', 'adong', '阿东', 1, 1, NULL, '2022-06-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/f79b3aaf0ded4ddb91c8060fa3733963.jpg', 0, '2024-03-09 21:14:17', '2024-03-14 15:49:08', NULL);
INSERT INTO `sys_student` VALUES (11, 'yealan', '$2a$10$A.u7SipV1uQ0MgQrEDfwO.Hm5M2P3CZATOED8Th/A0hMbeQz2yuny', 'yealan', '叶阿兰', 2, 1, NULL, '2022-03-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/d1bd5faa29364a379347cd10cf90f7cc.webp', 0, '2024-03-09 21:14:52', '2024-03-14 15:48:55', NULL);
INSERT INTO `sys_student` VALUES (12, 'duanminghong', '$2a$10$FpxE3RSyJSFXEj5Yu/eZc.sgsTXXSVwo6OgeAkDnGtAdZhFOwjPI6', 'duanminghong', '段明宏', 1, 1, NULL, '2024-03-08', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/b3c100c25c5e42e8af9a462e0d542a54.jpg', 0, '2024-03-09 21:16:13', '2024-03-14 15:47:23', NULL);
INSERT INTO `sys_student` VALUES (13, 'jiangxuening', '$2a$10$1Z2W9jKubooDdblSV3EVvenUHgFM8fMfSy4nuMLK59I0ZpOy6qlpO', 'jiangxuening', '姜雪宁', 2, 1, NULL, '2020-03-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/ccc8c1aeae2e4ee7be09b4904216a5c4.webp', 0, '2024-03-11 20:43:04', '2024-03-14 15:46:56', NULL);
INSERT INTO `sys_student` VALUES (14, 'xiewei', '$2a$10$2ZdnCSPpOKeCL2wiyDc9A.4V8jRDcilO7mSct3MNo0zTR3mPQGRyu', 'xiewei', '谢危', 1, 1, NULL, '2020-02-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/bbfa59fcbd6340f3b10a160a84e04a59.webp', 0, '2024-03-11 20:43:42', '2024-03-14 15:47:11', NULL);
INSERT INTO `sys_student` VALUES (15, 'zhangzhe', '$2a$10$B11TK2OcuACs2/ZZ/gT10.MCl.U2LwplrRYoLklNVIc.mymhAqWXy', 'zhangzhe', '张遮', 1, 1, NULL, '2022-01-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/04813a0fad754c8fa622e5037b55f555.webp', 0, '2024-03-11 20:44:24', '2024-03-14 15:46:42', NULL);
INSERT INTO `sys_student` VALUES (16, 'yanlin', '$2a$10$fy8AOFyI3HzKjyrGGoCIn.wBcoT.QLHL5ekiQ8ptuE6RGnbYxdbHy', 'yanlin', '燕临', 1, 1, NULL, '2020-10-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/e309fe86a22347b99c12788d1fc015b2.webp', 0, '2024-03-11 20:45:18', '2024-03-14 15:46:28', NULL);
INSERT INTO `sys_student` VALUES (17, 'shenzhiyi', '$2a$10$o/lHsKzreeoPuAb7jUJ6gOzAdvtWppfGDRt3sDR15kfu86DsSs49G', 'shenzhiyi', '沈芷衣', 2, 1, NULL, '2021-03-11', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/a106415c3e5043d1b1eb4efb2952fb0b.jpg', 0, '2024-03-11 20:45:52', '2024-03-14 15:44:32', NULL);
INSERT INTO `sys_student` VALUES (18, 'xueshu', '$2a$10$/cgalvjyXjANMFDF2L9wsuEDp/ZOMyGei8AWyW5a5BG/YzyuPq90i', 'xueshu', '薛姝', 2, 1, NULL, '2021-02-01', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/d0262f1ad2d647a096a7d394bd5471ab.jpg', 0, '2024-03-11 20:46:41', '2024-03-14 15:44:09', NULL);
INSERT INTO `sys_student` VALUES (19, 'youfangyin', '$2a$10$VmqMf58vvzU9A.5zDvlwrOSxY55vD7k3Hm1tq4ZRcbWxxKNmtDg06', 'youfangyin', '尤芳吟', 2, 1, NULL, '2021-02-02', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/1f447204f9f24fadbbd5b85a90bdec22.webp', 0, '2024-03-11 20:47:48', '2024-03-14 15:43:57', NULL);
INSERT INTO `sys_student` VALUES (20, 'xueyuan', '$2a$10$9cWdmxcDRMzhipWv6u6ijuJpWZeP65W.FLWT3FLYsdAgXc3bBgvou', 'xueyuan', '薛远', 1, 1, NULL, '2019-02-02', 2024, NULL, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/e0e44a1900374dc6a19f3b510708d82d.jpg', 0, '2024-03-11 20:48:21', '2024-03-14 15:43:45', NULL);
INSERT INTO `sys_student` VALUES (22, 'pengxi', '$2a$10$mL5xyoGExa5qaROnPs/G2eAfrxBahf6P7ANBBHWKCOCL44nMcaKq2', 'pengxi', '彭西', 2, 1, '3333', '2018-08-08', 2024, NULL, '17566667777', '北京市东城区', 'http://127.0.0.1:9005/big-event/20240314/b536e3d8cb944253bb079351b641bc8c.webp', 0, '2024-03-14 14:05:27', '2024-03-28 20:28:46', '大放大');

-- ----------------------------
-- Table structure for sys_student_hobbies
-- ----------------------------
DROP TABLE IF EXISTS `sys_student_hobbies`;
CREATE TABLE `sys_student_hobbies`  (
  `student_id` bigint(20) NOT NULL,
  `hobbies_id` bigint(20) NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学生兴趣关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_student_hobbies
-- ----------------------------
INSERT INTO `sys_student_hobbies` VALUES (22, 7);
INSERT INTO `sys_student_hobbies` VALUES (22, 8);

-- ----------------------------
-- Table structure for sys_student_university
-- ----------------------------
DROP TABLE IF EXISTS `sys_student_university`;
CREATE TABLE `sys_student_university`  (
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `university_id` bigint(20) NOT NULL COMMENT '院校ID'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '学生院校关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_student_university
-- ----------------------------
INSERT INTO `sys_student_university` VALUES (22, 7);
INSERT INTO `sys_student_university` VALUES (22, 8);

-- ----------------------------
-- Table structure for sys_teacher
-- ----------------------------
DROP TABLE IF EXISTS `sys_teacher`;
CREATE TABLE `sys_teacher`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '职工编码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '职工姓名',
  `sex` tinyint(1) NOT NULL COMMENT '性别',
  `status` tinyint(1) NOT NULL COMMENT '状态（正常、停用）',
  `id_card` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `birth_day` date NULL DEFAULT NULL COMMENT '出生日期',
  `year` int(11) NULL DEFAULT NULL COMMENT '入职年份',
  `position_id` bigint(20) NULL DEFAULT NULL COMMENT '职务',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '职工表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_teacher
-- ----------------------------
INSERT INTO `sys_teacher` VALUES (2, 'sunwukong', '$2a$10$TDcG2GAsqoBDhQ7EpbEyKuHN4YYoJSmKUdeWs78LMFdXg8INH0hXW', 'sunwukong123', '孙悟空', 1, 1, '	110101199001013793', '2000-03-01', 2024, 1, '17377778888', '浙江省杭州市', 'http://127.0.0.1:9005/big-event/20240313/494c3b2ffadc4239ba6c702ec4fb52a3.jpg', 0, '2024-03-04 08:57:04', '2024-03-06 08:53:13', '代发费大');
INSERT INTO `sys_teacher` VALUES (3, 'zhubajie', '$2a$10$qgrj6X5/whnkAtwHWeoaeOi9MckGDWENZ4ljT1EATsSoysG7ZY5iO', 'zhubajie', '猪八戒', 1, 1, '564356', '2020-01-02', 2025, 1, '17777555777', '安徽省合肥市', 'http://127.0.0.1:9005/big-event/20240304/1b34056198084cda885f3c1488560b95.jpg', 0, '2024-03-04 08:59:50', '2024-03-14 15:42:59', '发大水发大水');
INSERT INTO `sys_teacher` VALUES (4, 'tangren', '$2a$10$UwmdAWOPavCECnvFW04I7uRdkydDpLuMbqpWBT01JySO/ec0qLU6.', '10001', '唐仁', 1, 1, NULL, '2021-03-04', NULL, 2, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/3561b50123894934bff975b01498859b.webp', 0, '2024-03-08 09:37:06', '2024-03-14 15:42:49', NULL);
INSERT INTO `sys_teacher` VALUES (5, 'linshaochun', '$2a$10$ZQd9YPZy9lxXaJ56SldF1..tGLOqh.MtwiFYtG6KIhAZnnnIWFE2a', '10002', '林少春', 2, 1, NULL, '2000-03-01', NULL, 2, NULL, NULL, 'http://127.0.0.1:9005/big-event/20240314/f3d426da763849b2810e370c40303964.webp', 0, '2024-03-08 09:38:17', '2024-03-14 15:42:17', NULL);
INSERT INTO `sys_teacher` VALUES (7, 'liudehua', '$2a$10$NlwiTAiPWK9zEbK9FxVTje3HxApwss2B8S5hwGk0WkbcR1LixAEt6', '10000', '刘德华', 1, 1, '5556556', '1991-08-08', 2014, NULL, '17712345678', '浙江省杭州市', 'http://127.0.0.1:9005/big-event/20240314/b3ccb928529d46dcb06c2a4ecbdcc03b.jpg', 0, '2024-03-14 11:00:42', '2024-03-21 08:46:37', '对对对');

-- ----------------------------
-- Table structure for sys_topic
-- ----------------------------
DROP TABLE IF EXISTS `sys_topic`;
CREATE TABLE `sys_topic`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `year` int(11) NULL DEFAULT NULL COMMENT '年度',
  `course_id` bigint(20) NULL DEFAULT NULL COMMENT '课程ID',
  `exam_id` bigint(20) NULL DEFAULT NULL COMMENT '考试ID',
  `grade_id` bigint(20) NULL DEFAULT NULL COMMENT '年级ID',
  `grade_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年级名称',
  `clazz_id` bigint(20) NULL DEFAULT NULL COMMENT '班级ID',
  `clazz_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '班级名称',
  `topic_type_id` bigint(20) NULL DEFAULT NULL COMMENT '题型ID',
  `teacher_id` bigint(20) NULL DEFAULT NULL COMMENT '教师ID',
  `student_id` bigint(20) NULL DEFAULT NULL COMMENT '学生ID',
  `admin_id` bigint(20) NULL DEFAULT NULL COMMENT '管理员ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '错题名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错题编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '错题状态(1-正常；0-停用)',
  `remark` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '错题表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_topic
-- ----------------------------
INSERT INTO `sys_topic` VALUES (7, 2024, 8, 15, 3, NULL, NULL, NULL, 7, NULL, NULL, 2, 'fadsfdas', 'dfasfd', 1, 1, '<p>发大水发大水放大噶第三方高大上<img src=\"http://127.0.0.1:9005/big-event/20240328/027d1b57427c4393b47207b9f72f52d4.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>', 0, '2024-03-27 12:27:07', '2024-03-28 22:05:23');
INSERT INTO `sys_topic` VALUES (8, 2024, 7, 13, 3, NULL, NULL, NULL, 8, NULL, NULL, 2, '刚发施工方答复', '发大水发大水', 1, 1, NULL, 0, '2024-03-27 16:24:25', '2024-03-27 19:22:54');
INSERT INTO `sys_topic` VALUES (9, 2024, 9, 13, 3, NULL, NULL, NULL, 7, 7, NULL, NULL, '发地方大', '发大水发大水地方', 1, 1, NULL, 0, '2024-03-27 20:47:45', '2024-03-27 20:47:45');

-- ----------------------------
-- Table structure for sys_topic_picture
-- ----------------------------
DROP TABLE IF EXISTS `sys_topic_picture`;
CREATE TABLE `sys_topic_picture`  (
  `topic_id` bigint(20) NOT NULL COMMENT '错题ID',
  `picture_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片地址'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '错题图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_topic_picture
-- ----------------------------
INSERT INTO `sys_topic_picture` VALUES (8, 'http://127.0.0.1:9005/big-event/20240327/d46148ea31b04392a7da2f80f0ac77d9.jpg');
INSERT INTO `sys_topic_picture` VALUES (9, 'http://127.0.0.1:9005/big-event/20240327/7c8ba882ec354ab880e9355b9486242c.jpg');
INSERT INTO `sys_topic_picture` VALUES (9, 'http://127.0.0.1:9005/big-event/20240327/b8e8916425fd4b7b8d930a7ef5c2063b.jpg');
INSERT INTO `sys_topic_picture` VALUES (7, 'http://127.0.0.1:9005/big-event/20240327/8433f520a7d64cae82cf94db07778989.jpg');
INSERT INTO `sys_topic_picture` VALUES (7, 'http://127.0.0.1:9005/big-event/20240327/33c3901ddab549ffad8023a0f31dc7cf.jpg');

-- ----------------------------
-- Table structure for sys_topic_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_topic_type`;
CREATE TABLE `sys_topic_type`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '题型名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '题型编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '题型状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '题型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_topic_type
-- ----------------------------
INSERT INTO `sys_topic_type` VALUES (7, '选择题', 'choice', 1, 1, 0, '2024-03-27 10:55:47', '2024-03-27 10:55:47');
INSERT INTO `sys_topic_type` VALUES (8, '简答题', 'answer', 1, 1, 0, '2024-03-27 14:04:49', '2024-03-27 14:04:49');

-- ----------------------------
-- Table structure for sys_university
-- ----------------------------
DROP TABLE IF EXISTS `sys_university`;
CREATE TABLE `sys_university`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '院校名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '院校编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '院校状态(1-正常；0-停用)',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '目标院校表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_university
-- ----------------------------
INSERT INTO `sys_university` VALUES (7, '北京大学', 'BeijingUniversity', 1, 1, 0, '2024-03-28 16:36:35', '2024-03-28 16:36:35');
INSERT INTO `sys_university` VALUES (8, '清华大学', 'QingHuaUniversity', 1, 1, 0, '2024-03-28 16:36:53', '2024-03-28 16:36:53');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) NULL DEFAULT 1 COMMENT '性别((1:男;2:女))',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '部门ID',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户头像',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '用户状态((1:正常;0:禁用))',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `login_name`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 288 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'root', '有来技术', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://oss.youlai.tech/youlai-boot/2023/05/16/811270ef31f548af9cffc026dfc3777b.gif', '17621590365', 1, 'youlaitech@163.com', 0, NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'admin', '系统管理员', 1, '$2a$10$nOWHSuqKNXmMCE6aHJKuKuM1V6avQl7T3L7Qygx5rzHHJCCepDQDq', 1, 'http://127.0.0.1:9005/big-event/20240313/0072839f84424199acde4c80a060ae12.jpeg', '17621210366', 1, '', 0, '2019-10-10 13:41:22', '2024-03-05 09:49:16');
INSERT INTO `sys_user` VALUES (3, 'test', '测试小用户', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 3, 'http://127.0.0.1:9005/big-event/20240314/3bb3c2e59ca04283b1e827e4edd107e6.webp', '17621210366', 1, 'youlaitech@163.com', 0, '2021-06-05 01:31:29', '2024-03-14 15:41:45');
INSERT INTO `sys_user` VALUES (287, '123', '123', 1, '$2a$10$mVoBVqm1837huf7kcN0wS.GVYKEFv0arb7GvzfFXoTyqDlcRzT.6i', 1, '', NULL, 1, NULL, 1, '2023-05-21 14:11:19', '2023-05-21 14:11:25');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);
INSERT INTO `sys_user_role` VALUES (287, 2);

SET FOREIGN_KEY_CHECKS = 1;
