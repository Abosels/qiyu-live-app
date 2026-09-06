-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: qiyu_live_user
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `qiyu_live_user`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `qiyu_live_user` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `qiyu_live_user`;

--
-- Table structure for table `t_user_00`
--

DROP TABLE IF EXISTS `t_user_00`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_00` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_00`
--

LOCK TABLES `t_user_00` WRITE;
/*!40000 ALTER TABLE `t_user_00` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_00` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_01`
--

DROP TABLE IF EXISTS `t_user_01`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_01` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_01`
--

LOCK TABLES `t_user_01` WRITE;
/*!40000 ALTER TABLE `t_user_01` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_01` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_02`
--

DROP TABLE IF EXISTS `t_user_02`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_02` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_02`
--

LOCK TABLES `t_user_02` WRITE;
/*!40000 ALTER TABLE `t_user_02` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_02` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_03`
--

DROP TABLE IF EXISTS `t_user_03`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_03` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_03`
--

LOCK TABLES `t_user_03` WRITE;
/*!40000 ALTER TABLE `t_user_03` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_03` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_04`
--

DROP TABLE IF EXISTS `t_user_04`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_04` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_04`
--

LOCK TABLES `t_user_04` WRITE;
/*!40000 ALTER TABLE `t_user_04` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_04` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_05`
--

DROP TABLE IF EXISTS `t_user_05`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_05` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_05`
--

LOCK TABLES `t_user_05` WRITE;
/*!40000 ALTER TABLE `t_user_05` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_05` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_06`
--

DROP TABLE IF EXISTS `t_user_06`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_06` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_06`
--

LOCK TABLES `t_user_06` WRITE;
/*!40000 ALTER TABLE `t_user_06` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_06` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_07`
--

DROP TABLE IF EXISTS `t_user_07`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_07` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_07`
--

LOCK TABLES `t_user_07` WRITE;
/*!40000 ALTER TABLE `t_user_07` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_07` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_08`
--

DROP TABLE IF EXISTS `t_user_08`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_08` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_08`
--

LOCK TABLES `t_user_08` WRITE;
/*!40000 ALTER TABLE `t_user_08` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_08` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_09`
--

DROP TABLE IF EXISTS `t_user_09`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_09` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_09`
--

LOCK TABLES `t_user_09` WRITE;
/*!40000 ALTER TABLE `t_user_09` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_09` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_10`
--

DROP TABLE IF EXISTS `t_user_10`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_10` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_10`
--

LOCK TABLES `t_user_10` WRITE;
/*!40000 ALTER TABLE `t_user_10` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_10` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_11`
--

DROP TABLE IF EXISTS `t_user_11`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_11` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_11`
--

LOCK TABLES `t_user_11` WRITE;
/*!40000 ALTER TABLE `t_user_11` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_11` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_12`
--

DROP TABLE IF EXISTS `t_user_12`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_12` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_12`
--

LOCK TABLES `t_user_12` WRITE;
/*!40000 ALTER TABLE `t_user_12` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_12` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_13`
--

DROP TABLE IF EXISTS `t_user_13`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_13` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_13`
--

LOCK TABLES `t_user_13` WRITE;
/*!40000 ALTER TABLE `t_user_13` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_13` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_14`
--

DROP TABLE IF EXISTS `t_user_14`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_14` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_14`
--

LOCK TABLES `t_user_14` WRITE;
/*!40000 ALTER TABLE `t_user_14` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_14` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_15`
--

DROP TABLE IF EXISTS `t_user_15`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_15` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_15`
--

LOCK TABLES `t_user_15` WRITE;
/*!40000 ALTER TABLE `t_user_15` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_15` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_16`
--

DROP TABLE IF EXISTS `t_user_16`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_16` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_16`
--

LOCK TABLES `t_user_16` WRITE;
/*!40000 ALTER TABLE `t_user_16` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_16` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_17`
--

DROP TABLE IF EXISTS `t_user_17`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_17` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_17`
--

LOCK TABLES `t_user_17` WRITE;
/*!40000 ALTER TABLE `t_user_17` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_17` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_18`
--

DROP TABLE IF EXISTS `t_user_18`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_18` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_18`
--

LOCK TABLES `t_user_18` WRITE;
/*!40000 ALTER TABLE `t_user_18` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_18` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_19`
--

DROP TABLE IF EXISTS `t_user_19`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_19` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_19`
--

LOCK TABLES `t_user_19` WRITE;
/*!40000 ALTER TABLE `t_user_19` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_19` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_20`
--

DROP TABLE IF EXISTS `t_user_20`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_20` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_20`
--

LOCK TABLES `t_user_20` WRITE;
/*!40000 ALTER TABLE `t_user_20` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_20` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_21`
--

DROP TABLE IF EXISTS `t_user_21`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_21` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_21`
--

LOCK TABLES `t_user_21` WRITE;
/*!40000 ALTER TABLE `t_user_21` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_21` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_22`
--

DROP TABLE IF EXISTS `t_user_22`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_22` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_22`
--

LOCK TABLES `t_user_22` WRITE;
/*!40000 ALTER TABLE `t_user_22` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_22` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_23`
--

DROP TABLE IF EXISTS `t_user_23`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_23` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_23`
--

LOCK TABLES `t_user_23` WRITE;
/*!40000 ALTER TABLE `t_user_23` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_23` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_24`
--

DROP TABLE IF EXISTS `t_user_24`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_24` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_24`
--

LOCK TABLES `t_user_24` WRITE;
/*!40000 ALTER TABLE `t_user_24` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_24` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_25`
--

DROP TABLE IF EXISTS `t_user_25`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_25` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_25`
--

LOCK TABLES `t_user_25` WRITE;
/*!40000 ALTER TABLE `t_user_25` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_25` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_26`
--

DROP TABLE IF EXISTS `t_user_26`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_26` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_26`
--

LOCK TABLES `t_user_26` WRITE;
/*!40000 ALTER TABLE `t_user_26` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_26` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_27`
--

DROP TABLE IF EXISTS `t_user_27`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_27` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_27`
--

LOCK TABLES `t_user_27` WRITE;
/*!40000 ALTER TABLE `t_user_27` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_27` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_28`
--

DROP TABLE IF EXISTS `t_user_28`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_28` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_28`
--

LOCK TABLES `t_user_28` WRITE;
/*!40000 ALTER TABLE `t_user_28` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_28` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_29`
--

DROP TABLE IF EXISTS `t_user_29`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_29` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_29`
--

LOCK TABLES `t_user_29` WRITE;
/*!40000 ALTER TABLE `t_user_29` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_29` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_30`
--

DROP TABLE IF EXISTS `t_user_30`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_30` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_30`
--

LOCK TABLES `t_user_30` WRITE;
/*!40000 ALTER TABLE `t_user_30` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_30` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_31`
--

DROP TABLE IF EXISTS `t_user_31`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_31` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_31`
--

LOCK TABLES `t_user_31` WRITE;
/*!40000 ALTER TABLE `t_user_31` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_31` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_32`
--

DROP TABLE IF EXISTS `t_user_32`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_32` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_32`
--

LOCK TABLES `t_user_32` WRITE;
/*!40000 ALTER TABLE `t_user_32` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_32` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_33`
--

DROP TABLE IF EXISTS `t_user_33`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_33` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_33`
--

LOCK TABLES `t_user_33` WRITE;
/*!40000 ALTER TABLE `t_user_33` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_33` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_34`
--

DROP TABLE IF EXISTS `t_user_34`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_34` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_34`
--

LOCK TABLES `t_user_34` WRITE;
/*!40000 ALTER TABLE `t_user_34` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_34` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_35`
--

DROP TABLE IF EXISTS `t_user_35`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_35` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_35`
--

LOCK TABLES `t_user_35` WRITE;
/*!40000 ALTER TABLE `t_user_35` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_35` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_36`
--

DROP TABLE IF EXISTS `t_user_36`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_36` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_36`
--

LOCK TABLES `t_user_36` WRITE;
/*!40000 ALTER TABLE `t_user_36` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_36` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_37`
--

DROP TABLE IF EXISTS `t_user_37`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_37` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_37`
--

LOCK TABLES `t_user_37` WRITE;
/*!40000 ALTER TABLE `t_user_37` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_37` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_38`
--

DROP TABLE IF EXISTS `t_user_38`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_38` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_38`
--

LOCK TABLES `t_user_38` WRITE;
/*!40000 ALTER TABLE `t_user_38` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_38` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_39`
--

DROP TABLE IF EXISTS `t_user_39`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_39` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_39`
--

LOCK TABLES `t_user_39` WRITE;
/*!40000 ALTER TABLE `t_user_39` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_39` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_40`
--

DROP TABLE IF EXISTS `t_user_40`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_40` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_40`
--

LOCK TABLES `t_user_40` WRITE;
/*!40000 ALTER TABLE `t_user_40` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_40` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_41`
--

DROP TABLE IF EXISTS `t_user_41`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_41` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_41`
--

LOCK TABLES `t_user_41` WRITE;
/*!40000 ALTER TABLE `t_user_41` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_41` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_42`
--

DROP TABLE IF EXISTS `t_user_42`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_42` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_42`
--

LOCK TABLES `t_user_42` WRITE;
/*!40000 ALTER TABLE `t_user_42` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_42` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_43`
--

DROP TABLE IF EXISTS `t_user_43`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_43` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_43`
--

LOCK TABLES `t_user_43` WRITE;
/*!40000 ALTER TABLE `t_user_43` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_43` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_44`
--

DROP TABLE IF EXISTS `t_user_44`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_44` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_44`
--

LOCK TABLES `t_user_44` WRITE;
/*!40000 ALTER TABLE `t_user_44` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_44` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_45`
--

DROP TABLE IF EXISTS `t_user_45`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_45` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_45`
--

LOCK TABLES `t_user_45` WRITE;
/*!40000 ALTER TABLE `t_user_45` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_45` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_46`
--

DROP TABLE IF EXISTS `t_user_46`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_46` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_46`
--

LOCK TABLES `t_user_46` WRITE;
/*!40000 ALTER TABLE `t_user_46` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_46` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_47`
--

DROP TABLE IF EXISTS `t_user_47`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_47` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_47`
--

LOCK TABLES `t_user_47` WRITE;
/*!40000 ALTER TABLE `t_user_47` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_47` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_48`
--

DROP TABLE IF EXISTS `t_user_48`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_48` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_48`
--

LOCK TABLES `t_user_48` WRITE;
/*!40000 ALTER TABLE `t_user_48` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_48` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_49`
--

DROP TABLE IF EXISTS `t_user_49`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_49` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_49`
--

LOCK TABLES `t_user_49` WRITE;
/*!40000 ALTER TABLE `t_user_49` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_49` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_50`
--

DROP TABLE IF EXISTS `t_user_50`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_50` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_50`
--

LOCK TABLES `t_user_50` WRITE;
/*!40000 ALTER TABLE `t_user_50` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_50` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_51`
--

DROP TABLE IF EXISTS `t_user_51`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_51` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_51`
--

LOCK TABLES `t_user_51` WRITE;
/*!40000 ALTER TABLE `t_user_51` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_51` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_52`
--

DROP TABLE IF EXISTS `t_user_52`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_52` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_52`
--

LOCK TABLES `t_user_52` WRITE;
/*!40000 ALTER TABLE `t_user_52` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_52` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_53`
--

DROP TABLE IF EXISTS `t_user_53`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_53` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_53`
--

LOCK TABLES `t_user_53` WRITE;
/*!40000 ALTER TABLE `t_user_53` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_53` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_54`
--

DROP TABLE IF EXISTS `t_user_54`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_54` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_54`
--

LOCK TABLES `t_user_54` WRITE;
/*!40000 ALTER TABLE `t_user_54` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_54` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_55`
--

DROP TABLE IF EXISTS `t_user_55`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_55` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_55`
--

LOCK TABLES `t_user_55` WRITE;
/*!40000 ALTER TABLE `t_user_55` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_55` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_56`
--

DROP TABLE IF EXISTS `t_user_56`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_56` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_56`
--

LOCK TABLES `t_user_56` WRITE;
/*!40000 ALTER TABLE `t_user_56` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_56` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_57`
--

DROP TABLE IF EXISTS `t_user_57`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_57` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_57`
--

LOCK TABLES `t_user_57` WRITE;
/*!40000 ALTER TABLE `t_user_57` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_57` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_58`
--

DROP TABLE IF EXISTS `t_user_58`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_58` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_58`
--

LOCK TABLES `t_user_58` WRITE;
/*!40000 ALTER TABLE `t_user_58` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_58` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_59`
--

DROP TABLE IF EXISTS `t_user_59`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_59` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_59`
--

LOCK TABLES `t_user_59` WRITE;
/*!40000 ALTER TABLE `t_user_59` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_59` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_60`
--

DROP TABLE IF EXISTS `t_user_60`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_60` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_60`
--

LOCK TABLES `t_user_60` WRITE;
/*!40000 ALTER TABLE `t_user_60` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_60` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_61`
--

DROP TABLE IF EXISTS `t_user_61`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_61` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_61`
--

LOCK TABLES `t_user_61` WRITE;
/*!40000 ALTER TABLE `t_user_61` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_61` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_62`
--

DROP TABLE IF EXISTS `t_user_62`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_62` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_62`
--

LOCK TABLES `t_user_62` WRITE;
/*!40000 ALTER TABLE `t_user_62` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_62` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_63`
--

DROP TABLE IF EXISTS `t_user_63`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_63` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_63`
--

LOCK TABLES `t_user_63` WRITE;
/*!40000 ALTER TABLE `t_user_63` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_63` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_64`
--

DROP TABLE IF EXISTS `t_user_64`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_64` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_64`
--

LOCK TABLES `t_user_64` WRITE;
/*!40000 ALTER TABLE `t_user_64` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_64` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_65`
--

DROP TABLE IF EXISTS `t_user_65`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_65` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_65`
--

LOCK TABLES `t_user_65` WRITE;
/*!40000 ALTER TABLE `t_user_65` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_65` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_66`
--

DROP TABLE IF EXISTS `t_user_66`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_66` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_66`
--

LOCK TABLES `t_user_66` WRITE;
/*!40000 ALTER TABLE `t_user_66` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_66` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_67`
--

DROP TABLE IF EXISTS `t_user_67`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_67` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_67`
--

LOCK TABLES `t_user_67` WRITE;
/*!40000 ALTER TABLE `t_user_67` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_67` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_68`
--

DROP TABLE IF EXISTS `t_user_68`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_68` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_68`
--

LOCK TABLES `t_user_68` WRITE;
/*!40000 ALTER TABLE `t_user_68` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_68` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_69`
--

DROP TABLE IF EXISTS `t_user_69`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_69` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_69`
--

LOCK TABLES `t_user_69` WRITE;
/*!40000 ALTER TABLE `t_user_69` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_69` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_70`
--

DROP TABLE IF EXISTS `t_user_70`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_70` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_70`
--

LOCK TABLES `t_user_70` WRITE;
/*!40000 ALTER TABLE `t_user_70` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_70` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_71`
--

DROP TABLE IF EXISTS `t_user_71`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_71` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_71`
--

LOCK TABLES `t_user_71` WRITE;
/*!40000 ALTER TABLE `t_user_71` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_71` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_72`
--

DROP TABLE IF EXISTS `t_user_72`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_72` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_72`
--

LOCK TABLES `t_user_72` WRITE;
/*!40000 ALTER TABLE `t_user_72` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_72` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_73`
--

DROP TABLE IF EXISTS `t_user_73`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_73` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_73`
--

LOCK TABLES `t_user_73` WRITE;
/*!40000 ALTER TABLE `t_user_73` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_73` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_74`
--

DROP TABLE IF EXISTS `t_user_74`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_74` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_74`
--

LOCK TABLES `t_user_74` WRITE;
/*!40000 ALTER TABLE `t_user_74` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_74` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_75`
--

DROP TABLE IF EXISTS `t_user_75`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_75` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_75`
--

LOCK TABLES `t_user_75` WRITE;
/*!40000 ALTER TABLE `t_user_75` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_75` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_76`
--

DROP TABLE IF EXISTS `t_user_76`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_76` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_76`
--

LOCK TABLES `t_user_76` WRITE;
/*!40000 ALTER TABLE `t_user_76` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_76` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_77`
--

DROP TABLE IF EXISTS `t_user_77`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_77` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_77`
--

LOCK TABLES `t_user_77` WRITE;
/*!40000 ALTER TABLE `t_user_77` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_77` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_78`
--

DROP TABLE IF EXISTS `t_user_78`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_78` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_78`
--

LOCK TABLES `t_user_78` WRITE;
/*!40000 ALTER TABLE `t_user_78` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_78` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_79`
--

DROP TABLE IF EXISTS `t_user_79`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_79` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_79`
--

LOCK TABLES `t_user_79` WRITE;
/*!40000 ALTER TABLE `t_user_79` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_79` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_80`
--

DROP TABLE IF EXISTS `t_user_80`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_80` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_80`
--

LOCK TABLES `t_user_80` WRITE;
/*!40000 ALTER TABLE `t_user_80` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_80` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_81`
--

DROP TABLE IF EXISTS `t_user_81`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_81` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_81`
--

LOCK TABLES `t_user_81` WRITE;
/*!40000 ALTER TABLE `t_user_81` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_81` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_82`
--

DROP TABLE IF EXISTS `t_user_82`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_82` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_82`
--

LOCK TABLES `t_user_82` WRITE;
/*!40000 ALTER TABLE `t_user_82` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_82` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_83`
--

DROP TABLE IF EXISTS `t_user_83`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_83` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_83`
--

LOCK TABLES `t_user_83` WRITE;
/*!40000 ALTER TABLE `t_user_83` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_83` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_84`
--

DROP TABLE IF EXISTS `t_user_84`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_84` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_84`
--

LOCK TABLES `t_user_84` WRITE;
/*!40000 ALTER TABLE `t_user_84` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_84` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_85`
--

DROP TABLE IF EXISTS `t_user_85`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_85` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_85`
--

LOCK TABLES `t_user_85` WRITE;
/*!40000 ALTER TABLE `t_user_85` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_85` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_86`
--

DROP TABLE IF EXISTS `t_user_86`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_86` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_86`
--

LOCK TABLES `t_user_86` WRITE;
/*!40000 ALTER TABLE `t_user_86` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_86` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_87`
--

DROP TABLE IF EXISTS `t_user_87`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_87` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_87`
--

LOCK TABLES `t_user_87` WRITE;
/*!40000 ALTER TABLE `t_user_87` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_87` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_88`
--

DROP TABLE IF EXISTS `t_user_88`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_88` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_88`
--

LOCK TABLES `t_user_88` WRITE;
/*!40000 ALTER TABLE `t_user_88` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_88` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_89`
--

DROP TABLE IF EXISTS `t_user_89`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_89` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_89`
--

LOCK TABLES `t_user_89` WRITE;
/*!40000 ALTER TABLE `t_user_89` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_89` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_90`
--

DROP TABLE IF EXISTS `t_user_90`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_90` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_90`
--

LOCK TABLES `t_user_90` WRITE;
/*!40000 ALTER TABLE `t_user_90` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_90` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_91`
--

DROP TABLE IF EXISTS `t_user_91`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_91` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_91`
--

LOCK TABLES `t_user_91` WRITE;
/*!40000 ALTER TABLE `t_user_91` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_91` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_92`
--

DROP TABLE IF EXISTS `t_user_92`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_92` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_92`
--

LOCK TABLES `t_user_92` WRITE;
/*!40000 ALTER TABLE `t_user_92` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_92` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_93`
--

DROP TABLE IF EXISTS `t_user_93`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_93` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_93`
--

LOCK TABLES `t_user_93` WRITE;
/*!40000 ALTER TABLE `t_user_93` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_93` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_94`
--

DROP TABLE IF EXISTS `t_user_94`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_94` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_94`
--

LOCK TABLES `t_user_94` WRITE;
/*!40000 ALTER TABLE `t_user_94` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_94` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_95`
--

DROP TABLE IF EXISTS `t_user_95`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_95` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_95`
--

LOCK TABLES `t_user_95` WRITE;
/*!40000 ALTER TABLE `t_user_95` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_95` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_96`
--

DROP TABLE IF EXISTS `t_user_96`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_96` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_96`
--

LOCK TABLES `t_user_96` WRITE;
/*!40000 ALTER TABLE `t_user_96` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_96` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_97`
--

DROP TABLE IF EXISTS `t_user_97`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_97` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_97`
--

LOCK TABLES `t_user_97` WRITE;
/*!40000 ALTER TABLE `t_user_97` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_97` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_98`
--

DROP TABLE IF EXISTS `t_user_98`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_98` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_98`
--

LOCK TABLES `t_user_98` WRITE;
/*!40000 ALTER TABLE `t_user_98` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_98` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_user_99`
--

DROP TABLE IF EXISTS `t_user_99`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user_99` (
  `user_id` bigint NOT NULL DEFAULT '-1' COMMENT '用户id',
  `nick_name` varchar(35) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `true_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '真实姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0男, 1女',
  `born_date` datetime DEFAULT NULL COMMENT '出生时间',
  `work_city` int DEFAULT NULL COMMENT '工作地',
  `born_city` int DEFAULT NULL COMMENT '出生地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user_99`
--

LOCK TABLES `t_user_99` WRITE;
/*!40000 ALTER TABLE `t_user_99` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_user_99` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-21 16:41:06
