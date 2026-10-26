-- MySQL dump 10.13  Distrib 5.7.42, for Win64 (x86_64)
--
-- Host: localhost    Database: crush
-- ------------------------------------------------------
-- Server version	5.7.42-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `crush`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `crush` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `crush`;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  `user_id` varchar(128) NOT NULL,
  `name` varchar(255) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `is_cancel_application` bit(1) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `approval_status` tinyint(4) NOT NULL,
  `approval_instructions` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

--
-- Table structure for table `application_detail`
--

DROP TABLE IF EXISTS `application_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_detail` (
  `id` varchar(128) NOT NULL,
  `application_id` varchar(128) NOT NULL,
  `consumable_id` varchar(128) NOT NULL,
  `applicate_count` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `is_cancel_application` bit(1) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `approval_status` tinyint(4) NOT NULL,
  `approval_instructions` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_detail`
--

--
-- Table structure for table `consumable`
--

DROP TABLE IF EXISTS `consumable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumable` (
  `id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  `type_id` varchar(128) NOT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `count` int(11) NOT NULL,
  `safety_count` int(11) NOT NULL,
  `price` double(11,2) NOT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `is_lock` bit(1) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `supplier_id` varchar(128) DEFAULT NULL COMMENT '供应商ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumable`
--

--
-- Table structure for table `consumable_inbound_batch`
--

DROP TABLE IF EXISTS `consumable_inbound_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumable_inbound_batch` (
  `id` varchar(128) NOT NULL,
  `consumable_id` varchar(128) NOT NULL,
  `count` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumable_inbound_batch`
--

--
-- Table structure for table `consumable_operation_detail`
--

DROP TABLE IF EXISTS `consumable_operation_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumable_operation_detail` (
  `id` varchar(128) NOT NULL,
  `consumable_operation_history_id` varchar(128) NOT NULL,
  `consumable_id` varchar(128) NOT NULL,
  `operation_type` int(11) NOT NULL,
  `operation_count` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumable_operation_detail`
--

--
-- Table structure for table `consumable_operation_history`
--

DROP TABLE IF EXISTS `consumable_operation_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumable_operation_history` (
  `id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  `user_id` varchar(128) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumable_operation_history`
--

--
-- Table structure for table `consumable_reference_information`
--

DROP TABLE IF EXISTS `consumable_reference_information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumable_reference_information` (
  `id` varchar(128) NOT NULL,
  `consumable_id` varchar(128) NOT NULL,
  `reference_key` varchar(255) DEFAULT NULL,
  `reference_value` varchar(255) DEFAULT NULL,
  `display_order` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumable_reference_information`
--

--
-- Table structure for table `operation`
--

DROP TABLE IF EXISTS `operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operation` (
  `id` varchar(128) NOT NULL COMMENT '操作履历ID',
  `user_id` varchar(128) DEFAULT NULL COMMENT '用户ID',
  `operation` varchar(4000) NOT NULL COMMENT '操作',
  `ip` varchar(128) NOT NULL COMMENT 'IP地址',
  `create_time` datetime NOT NULL COMMENT '操作日期',
  `method` varchar(4000) NOT NULL COMMENT '操作请求路径',
  `params` varchar(8192) DEFAULT NULL COMMENT '操作请求参数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operation`
--

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission` (
  `id` varchar(128) NOT NULL COMMENT '权限ID',
  `name` varchar(50) NOT NULL COMMENT '权限名',
  `str` varchar(50) NOT NULL COMMENT '权限标识',
  `display_order` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

--
-- Table structure for table `reference_information_template`
--

DROP TABLE IF EXISTS `reference_information_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_information_template` (
  `id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `is_system` bit(1) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reference_information_template`
--

--
-- Table structure for table `reference_information_template_detail`
--

DROP TABLE IF EXISTS `reference_information_template_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_information_template_detail` (
  `id` varchar(128) NOT NULL,
  `reference_information_template_id` varchar(255) NOT NULL,
  `reference_key` varchar(255) NOT NULL,
  `display_order` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reference_information_template_detail`
--

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` varchar(128) NOT NULL COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名',
  `str` varchar(50) NOT NULL COMMENT '角色标识',
  `type` int(11) NOT NULL COMMENT '角色类型',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建日期',
  `is_stop` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否停用',
  `is_delete` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_permission` (
  `role_id` varchar(128) NOT NULL COMMENT '角色ID',
  `permission_id` varchar(128) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

--
-- Table structure for table `role_warehouse`
--

DROP TABLE IF EXISTS `role_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_warehouse` (
  `role_id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  PRIMARY KEY (`role_id`,`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_warehouse`
--

--
-- Table structure for table `scrap`
--

DROP TABLE IF EXISTS `scrap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scrap` (
  `id` varchar(64) NOT NULL,
  `warehouse_id` varchar(64) DEFAULT NULL,
  `consumable_id` varchar(64) DEFAULT NULL,
  `quantity` int(11) DEFAULT '0',
  `reason` varchar(500) DEFAULT NULL,
  `operator` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scrap`
--

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `supplier` (
  `id` varchar(128) NOT NULL COMMENT '供应商ID',
  `name` varchar(50) NOT NULL COMMENT '供应商名称',
  `contact_person` varchar(20) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `qualification` varchar(255) DEFAULT NULL COMMENT '供应商资质',
  `supply_category` varchar(100) DEFAULT NULL COMMENT '供应种类',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

--
-- Table structure for table `type`
--

DROP TABLE IF EXISTS `type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `type` (
  `id` varchar(128) NOT NULL,
  `name` varchar(200) NOT NULL,
  `remarks` varchar(64) DEFAULT NULL,
  `parent_id` varchar(128) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `is_delete` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type`
--

--
-- Table structure for table `type_warehouse`
--

DROP TABLE IF EXISTS `type_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `type_warehouse` (
  `type_id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  PRIMARY KEY (`type_id`,`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type_warehouse`
--

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` varchar(128) NOT NULL,
  `account` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(20) NOT NULL,
  `gender` varchar(2) DEFAULT NULL,
  `avatar` varchar(1024) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(11) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_enable` bit(1) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

--
-- Table structure for table `user_extension`
--

DROP TABLE IF EXISTS `user_extension`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_extension` (
  `user_id` varchar(128) NOT NULL,
  `initialized_password` varchar(255) DEFAULT NULL COMMENT '新增管理员时保存初始化的密码',
  `department` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_extension`
--

--
-- Table structure for table `user_permission`
--

DROP TABLE IF EXISTS `user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_permission` (
  `user_id` varchar(128) NOT NULL,
  `permission_id` varchar(128) NOT NULL,
  PRIMARY KEY (`user_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permission`
--

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `user_id` varchar(128) NOT NULL,
  `role_id` varchar(128) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

--
-- Table structure for table `user_warehouse`
--

DROP TABLE IF EXISTS `user_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_warehouse` (
  `user_id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`,`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_warehouse`
--

--
-- Table structure for table `user_warehouse_application_setting`
--

DROP TABLE IF EXISTS `user_warehouse_application_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_warehouse_application_setting` (
  `user_id` varchar(128) NOT NULL,
  `warehouse_id` varchar(128) NOT NULL,
  PRIMARY KEY (`user_id`,`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_warehouse_application_setting`
--

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse` (
  `id` varchar(128) NOT NULL,
  `code` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `picture_url` varchar(1028) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `display_order` int(11) NOT NULL,
  `is_enable` varchar(255) NOT NULL,
  `is_delete` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse`
--

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-21 12:27:04
