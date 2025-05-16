-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: farmacia_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `balconista`
--

DROP TABLE IF EXISTS `balconista`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `balconista` (
  `idbalconista_pk` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(60) NOT NULL,
  `cpf` char(11) NOT NULL,
  `comissao` int NOT NULL,
  `salario` float NOT NULL,
  PRIMARY KEY (`idbalconista_pk`),
  UNIQUE KEY `cpf_UNIQUE` (`cpf`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balconista`
--

LOCK TABLES `balconista` WRITE;
/*!40000 ALTER TABLE `balconista` DISABLE KEYS */;
INSERT INTO `balconista` VALUES (1,'Fernanda Alves','98765432100',10,2800.5),(2,'Ricardo Menezes','87654321098',8,2600);
/*!40000 ALTER TABLE `balconista` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `idcliente_pk` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(60) NOT NULL,
  `telefone` varchar(45) NOT NULL,
  `endereco` varchar(100) NOT NULL,
  `cpf` char(11) NOT NULL,
  PRIMARY KEY (`idcliente_pk`),
  UNIQUE KEY `cpf_UNIQUE` (`cpf`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'Ana Souza','11987654321','Rua das Palmeiras, 100','12345678901'),(2,'Carlos Lima','21999887766','Avenida Brasil, 2000','10987654321');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compra`
--

DROP TABLE IF EXISTS `compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra` (
  `idcompra_pk` int NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `totalpreco` float NOT NULL,
  `nfcompra` int NOT NULL,
  PRIMARY KEY (`idcompra_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compra`
--

LOCK TABLES `compra` WRITE;
/*!40000 ALTER TABLE `compra` DISABLE KEYS */;
INSERT INTO `compra` VALUES (1,'2025-05-16',150.5,12345),(2,'2025-02-18',175,32145);
/*!40000 ALTER TABLE `compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cotacao`
--

DROP TABLE IF EXISTS `cotacao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cotacao` (
  `idcotacao_pk` int NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `precounitario` float NOT NULL,
  `industria_idindustria_pk` int NOT NULL,
  `medicamento_idmedicamento_pk` int NOT NULL,
  `compra_idcompra_pk` int NOT NULL,
  PRIMARY KEY (`idcotacao_pk`),
  KEY `fk_cotacao_industria1_idx` (`industria_idindustria_pk`),
  KEY `fk_cotacao_medicamento1_idx` (`medicamento_idmedicamento_pk`),
  KEY `fk_cotacao_compra1_idx` (`compra_idcompra_pk`),
  CONSTRAINT `fk_cotacao_compra1` FOREIGN KEY (`compra_idcompra_pk`) REFERENCES `compra` (`idcompra_pk`),
  CONSTRAINT `fk_cotacao_industria1` FOREIGN KEY (`industria_idindustria_pk`) REFERENCES `industria` (`idindustria_pk`),
  CONSTRAINT `fk_cotacao_medicamento1` FOREIGN KEY (`medicamento_idmedicamento_pk`) REFERENCES `medicamento` (`idmedicamento_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cotacao`
--

LOCK TABLES `cotacao` WRITE;
/*!40000 ALTER TABLE `cotacao` DISABLE KEYS */;
INSERT INTO `cotacao` VALUES (1,'2025-05-16',4.5,1,2,1),(2,'2025-05-17',3.9,2,2,1);
/*!40000 ALTER TABLE `cotacao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estoque`
--

DROP TABLE IF EXISTS `estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estoque` (
  `idestoque_pk` int NOT NULL AUTO_INCREMENT,
  `locacao` varchar(100) NOT NULL,
  `codigoProduto` varchar(100) NOT NULL,
  `medicamento_idmedicamento_pk` int NOT NULL,
  `farmacia_idfarmacia_pk` int NOT NULL,
  PRIMARY KEY (`idestoque_pk`),
  KEY `fk_estoque_medicamento1_idx` (`medicamento_idmedicamento_pk`),
  KEY `fk_estoque_farmacia1_idx` (`farmacia_idfarmacia_pk`),
  CONSTRAINT `fk_estoque_farmacia1` FOREIGN KEY (`farmacia_idfarmacia_pk`) REFERENCES `farmacia` (`idfarmacia_pk`),
  CONSTRAINT `fk_estoque_medicamento1` FOREIGN KEY (`medicamento_idmedicamento_pk`) REFERENCES `medicamento` (`idmedicamento_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estoque`
--

LOCK TABLES `estoque` WRITE;
/*!40000 ALTER TABLE `estoque` DISABLE KEYS */;
/*!40000 ALTER TABLE `estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmacia`
--

DROP TABLE IF EXISTS `farmacia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farmacia` (
  `idfarmacia_pk` int NOT NULL AUTO_INCREMENT,
  `Nome` varchar(45) NOT NULL,
  `endereco` varchar(100) NOT NULL,
  PRIMARY KEY (`idfarmacia_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmacia`
--

LOCK TABLES `farmacia` WRITE;
/*!40000 ALTER TABLE `farmacia` DISABLE KEYS */;
INSERT INTO `farmacia` VALUES (1,'farmacia uvaranas','rua uvaranas 321'),(2,'farmacia oficinas','rua oficinas 321');
/*!40000 ALTER TABLE `farmacia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historicovenda`
--

DROP TABLE IF EXISTS `historicovenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historicovenda` (
  `idhistoricovenda_pk` int NOT NULL AUTO_INCREMENT,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total` float NOT NULL,
  `balconista_idbalconista_pk` int NOT NULL,
  PRIMARY KEY (`idhistoricovenda_pk`),
  KEY `fk_historicovenda_balconista1_idx` (`balconista_idbalconista_pk`),
  CONSTRAINT `fk_historicovenda_balconista1` FOREIGN KEY (`balconista_idbalconista_pk`) REFERENCES `balconista` (`idbalconista_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historicovenda`
--

LOCK TABLES `historicovenda` WRITE;
/*!40000 ALTER TABLE `historicovenda` DISABLE KEYS */;
INSERT INTO `historicovenda` VALUES (1,'2025-05-16 15:00:00',45,1),(2,'2025-05-16 17:30:00',32.75,2);
/*!40000 ALTER TABLE `historicovenda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `industria`
--

DROP TABLE IF EXISTS `industria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `industria` (
  `idindustria_pk` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(65) NOT NULL,
  `cnpj` char(14) NOT NULL,
  `localizacao` varchar(100) NOT NULL,
  PRIMARY KEY (`idindustria_pk`),
  UNIQUE KEY `cnpj_UNIQUE` (`cnpj`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `industria`
--

LOCK TABLES `industria` WRITE;
/*!40000 ALTER TABLE `industria` DISABLE KEYS */;
INSERT INTO `industria` VALUES (1,'farmacias diogo','12345678000195','ponta grossa'),(2,'farmacias henrique','87654321000195','ponta grossa');
/*!40000 ALTER TABLE `industria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itemvenda`
--

DROP TABLE IF EXISTS `itemvenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itemvenda` (
  `iditemvenda_pk` int NOT NULL AUTO_INCREMENT,
  `quantidade` int NOT NULL,
  `precounitario` float NOT NULL,
  `medicamento_idmedicamento_pk` int NOT NULL,
  `venda_idvenda_pk` int NOT NULL,
  PRIMARY KEY (`iditemvenda_pk`),
  KEY `fk_itemvenda_medicamento1_idx` (`medicamento_idmedicamento_pk`),
  KEY `fk_itemvenda_venda1_idx` (`venda_idvenda_pk`),
  CONSTRAINT `fk_itemvenda_medicamento1` FOREIGN KEY (`medicamento_idmedicamento_pk`) REFERENCES `medicamento` (`idmedicamento_pk`),
  CONSTRAINT `fk_itemvenda_venda1` FOREIGN KEY (`venda_idvenda_pk`) REFERENCES `venda` (`idvenda_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itemvenda`
--

LOCK TABLES `itemvenda` WRITE;
/*!40000 ALTER TABLE `itemvenda` DISABLE KEYS */;
/*!40000 ALTER TABLE `itemvenda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicamento`
--

DROP TABLE IF EXISTS `medicamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamento` (
  `idmedicamento_pk` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `dosagem` varchar(45) NOT NULL,
  `validade` date NOT NULL,
  `precocompra` float NOT NULL,
  `precovenda` float NOT NULL,
  `promocao` tinyint NOT NULL,
  `industria_idindustria_pk` int NOT NULL,
  PRIMARY KEY (`idmedicamento_pk`),
  KEY `fk_medicamento_industria1_idx` (`industria_idindustria_pk`),
  CONSTRAINT `fk_medicamento_industria1` FOREIGN KEY (`industria_idindustria_pk`) REFERENCES `industria` (`idindustria_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicamento`
--

LOCK TABLES `medicamento` WRITE;
/*!40000 ALTER TABLE `medicamento` DISABLE KEYS */;
INSERT INTO `medicamento` VALUES (1,'Paracetamol','500mg','2026-12-31',2.5,5,1,1),(2,'Ibuprofeno','400mg','2025-11-30',3,6,0,2);
/*!40000 ALTER TABLE `medicamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venda`
--

DROP TABLE IF EXISTS `venda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venda` (
  `idvenda_pk` int NOT NULL AUTO_INCREMENT,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total` float NOT NULL,
  `pagamento` varchar(45) NOT NULL,
  `metodoentrega` varchar(45) NOT NULL,
  `cliente_idcliente_pk` int NOT NULL,
  `balconista_idbalconista_pk` int NOT NULL,
  PRIMARY KEY (`idvenda_pk`),
  KEY `fk_venda_cliente1_idx` (`cliente_idcliente_pk`),
  KEY `fk_venda_balconista1_idx` (`balconista_idbalconista_pk`),
  CONSTRAINT `fk_venda_balconista1` FOREIGN KEY (`balconista_idbalconista_pk`) REFERENCES `balconista` (`idbalconista_pk`),
  CONSTRAINT `fk_venda_cliente1` FOREIGN KEY (`cliente_idcliente_pk`) REFERENCES `cliente` (`idcliente_pk`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venda`
--

LOCK TABLES `venda` WRITE;
/*!40000 ALTER TABLE `venda` DISABLE KEYS */;
INSERT INTO `venda` VALUES (1,'2025-05-16 13:30:00',45,'Cartão de Crédito','Entrega Rápida',1,1),(2,'2025-05-16 14:00:00',32.75,'Dinheiro','Retirada no Balcão',2,2);
/*!40000 ALTER TABLE `venda` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-16  8:06:26
