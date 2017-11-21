-- MySQL dump 10.13  Distrib 5.7.20, for Win64 (x86_64)
--
-- Host: localhost    Database: MusicLibrary
-- ------------------------------------------------------
-- Server version	5.7.20-log

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
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist` (
  `playlistName` varchar(255) NOT NULL,
  `username` varchar(30) NOT NULL,
  `isUserEditable` tinyint(1) NOT NULL,
  PRIMARY KEY (`playlistName`),
  KEY `username` (`username`),
  CONSTRAINT `playlist_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES ('All Songs','laudo',0),('frankuuu','laudo',1),('Hiii','laudo',1),('pen15','laudo',1);
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlistsong`
--

DROP TABLE IF EXISTS `playlistsong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlistsong` (
  `playlistName` varchar(255) NOT NULL,
  `songID` int(11) NOT NULL,
  PRIMARY KEY (`songID`,`playlistName`),
  KEY `playlistName` (`playlistName`),
  CONSTRAINT `playlistsong_ibfk_1` FOREIGN KEY (`playlistName`) REFERENCES `playlist` (`playlistName`),
  CONSTRAINT `playlistsong_ibfk_2` FOREIGN KEY (`songID`) REFERENCES `song` (`songID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlistsong`
--

LOCK TABLES `playlistsong` WRITE;
/*!40000 ALTER TABLE `playlistsong` DISABLE KEYS */;
INSERT INTO `playlistsong` VALUES ('All Songs',1),('All Songs',2),('All Songs',3),('All Songs',4),('All Songs',5),('All Songs',6),('All Songs',7),('All Songs',8),('All Songs',9),('All Songs',10),('All Songs',11),('All Songs',12),('All Songs',13),('All Songs',14),('frankuuu',1),('frankuuu',2),('frankuuu',3),('Hiii',3),('Hiii',6),('Hiii',7),('Hiii',8),('Hiii',10),('Hiii',12),('pen15',1),('pen15',3),('pen15',5),('pen15',8),('pen15',9),('pen15',11),('pen15',12);
/*!40000 ALTER TABLE `playlistsong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song`
--

DROP TABLE IF EXISTS `song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `song` (
  `songID` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `artist` varchar(255) DEFAULT NULL,
  `album` varchar(255) DEFAULT NULL,
  `filepath` varchar(800) NOT NULL,
  PRIMARY KEY (`songID`),
  UNIQUE KEY `filepath` (`filepath`),
  UNIQUE KEY `filepath_2` (`filepath`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song`
--

LOCK TABLES `song` WRITE;
/*!40000 ALTER TABLE `song` DISABLE KEYS */;
INSERT INTO `song` VALUES (1,'BAD INTERNET RAPPERS','joji',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Franku\\BAD INTERNET RAPPERS.mp3'),(2,'PINK GUY - KILL YOURSELF','joji',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Franku\\PINK GUY - KILL YOURSELF.mp3'),(3,'STFU','joji',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Franku\\STFU.mp3'),(4,'Conservatives and Hitler  Jordan B Peterson','jbp',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\JBP\\Conservatives and Hitler  Jordan B Peterson.mp3'),(5,'How to Manage a High OpennessLow Conscientiousness Personality  Jordan B Peterson','jbp',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\JBP\\How to Manage a High OpennessLow Conscientiousness Personality  Jordan B Peterson.mp3'),(6,'Is My Advice to Young People Predicated on My Own Personality  Jordan B Peterson','jbp',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\JBP\\Is My Advice to Young People Predicated on My Own Personality  Jordan B Peterson.mp3'),(7,'Effeil 65- Blue (KNY Factory Remix)','KNY Factory',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Muzak\\Effeil 65- Blue (KNY Factory Remix).mp3'),(8,'Illenium - Afterlife (feat. ECHOS)','Illenium',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Muzak\\Illenium - Afterlife (feat. ECHOS).mp3'),(9,'Illenium - Leaving','Illenium',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Muzak\\Illenium - Leaving.mp3'),(10,'Stalgia - Euphoria','Stalgia',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\Muzak\\Stalgia - Euphoria.mp3'),(11,'POPPIN4','qfsw',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\POPPIN4.mp3'),(12,'STFU','h3h3',NULL,'C:\\Users\\Nikolai\\Music\\Songs\\STFU.mp3'),(13,'Senritsu','some guys','dethu notu','C:\\Users\\Nikolai\\Music\\06-senritsu.mp3'),(14,'Kodoku','Dark Souls 2 Sir Alonne Remix','null','C:\\Users\\Nikolai\\Music\\Dark Souls 2 Sir Alonne Remix - Kodoku.mp3');
/*!40000 ALTER TABLE `song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` int(11) NOT NULL,
  `foo` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
INSERT INTO `test` VALUES (1,'hiiii lol'),(2,'ayyy lmao');
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `username` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('laudo','laudo_');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-11-21 22:45:33
