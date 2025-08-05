-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         8.0.40 - MySQL Community Server - GPL
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para hardware_store_db
CREATE DATABASE IF NOT EXISTS `hardware_store_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `hardware_store_db`;

-- Volcando estructura para tabla hardware_store_db.article
CREATE TABLE IF NOT EXISTS `article` (
  `id_article` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT '0',
  `price` decimal(10,2) NOT NULL,
  `id_category` int NOT NULL,
  `id_unit` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_article`),
  UNIQUE KEY `code` (`code`),
  KEY `id_category` (`id_category`),
  KEY `id_unit` (`id_unit`),
  KEY `idx_article_code` (`code`),
  KEY `idx_article_name` (`name`),
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`id_category`) REFERENCES `category` (`id_category`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `article_ibfk_2` FOREIGN KEY (`id_unit`) REFERENCES `unit` (`id_unit`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `article_chk_1` CHECK ((`quantity` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.article: ~10 rows (aproximadamente)
INSERT INTO `article` (`id_article`, `name`, `code`, `quantity`, `price`, `id_category`, `id_unit`, `created_at`, `updated_at`) VALUES
	(1, 'Martillo 16oz', 'ART-1001', 50, 0.00, 1, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(2, 'Destornillador Plano', 'ART-1002', 30, 0.00, 1, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(3, 'Pinza de Corte', 'ART-1003', 25, 0.00, 1, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(4, 'Tornillo 3x20mm', 'ART-1004', 1000, 0.00, 2, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(5, 'Tuerca 1/4"', 'ART-1005', 800, 0.00, 2, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, 'Cable Eléctrico 2.5mm', 'ART-1006', 200, 0.00, 3, 3, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, 'Llave Inglesa', 'ART-1007', 15, 0.00, 1, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 'Pintura Blanca 1L', 'ART-1008', 40, 0.00, 5, 4, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 'Brocha 2"', 'ART-1009', 20, 0.00, 5, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 'Cemento 50kg', 'ART-1010', 10, 0.00, 9, 2, '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.article_supplier
CREATE TABLE IF NOT EXISTS `article_supplier` (
  `id_article_supplier` int NOT NULL AUTO_INCREMENT,
  `id_article` int NOT NULL,
  `id_supplier` bigint NOT NULL,
  `unit_cost` decimal(10,2) NOT NULL,
  `lead_time_days` int DEFAULT '0',
  `is_preferred` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_article_supplier`),
  UNIQUE KEY `unique_article_supplier` (`id_article`,`id_supplier`),
  KEY `id_supplier` (`id_supplier`),
  CONSTRAINT `article_supplier_ibfk_1` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_supplier_ibfk_2` FOREIGN KEY (`id_supplier`) REFERENCES `supplier` (`id_supplier`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `article_supplier_chk_1` CHECK ((`unit_cost` > 0)),
  CONSTRAINT `article_supplier_chk_2` CHECK ((`lead_time_days` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.article_supplier: ~10 rows (aproximadamente)
INSERT INTO `article_supplier` (`id_article_supplier`, `id_article`, `id_supplier`, `unit_cost`, `lead_time_days`, `is_preferred`, `created_at`, `updated_at`) VALUES
	(1, 1, 1, 15.99, 3, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(2, 2, 1, 8.50, 3, 0, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(3, 3, 2, 12.75, 5, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(4, 4, 3, 0.25, 2, 0, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(5, 5, 3, 0.15, 2, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, 6, 4, 3.20, 7, 0, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, 7, 5, 18.30, 4, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 8, 6, 7.80, 5, 0, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 9, 7, 4.25, 3, 1, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 10, 8, 12.50, 10, 0, '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.category
CREATE TABLE IF NOT EXISTS `category` (
  `id_category` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_category`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.category: ~16 rows (aproximadamente)
INSERT INTO `category` (`id_category`, `name`, `created_at`, `updated_at`) VALUES
	(1, 'HERRAMIENTAS', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(2, 'TORNILLERIA', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(3, 'ELECTRICIDAD', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(4, 'PLOMERIA', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(5, 'PINTURA', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(6, 'JARDINERIA', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(7, 'ILUMINACION', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 'SEGURIDAD', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 'FERRETERIA', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 'MADERAS', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(11, 'METALES', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(12, 'QUIMICOS', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(13, 'HOGAR', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(14, 'AUTOMOTRIZ', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(15, 'CONSTRUCCION', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(16, 'DECORACION', '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.client
CREATE TABLE IF NOT EXISTS `client` (
  `id_client` bigint NOT NULL AUTO_INCREMENT,
  `document` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `rut` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_client`),
  UNIQUE KEY `document` (`document`),
  UNIQUE KEY `rut` (`rut`),
  KEY `idx_client_document` (`document`),
  KEY `idx_client_rut` (`rut`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.client: ~10 rows (aproximadamente)
INSERT INTO `client` (`id_client`, `document`, `name`, `phone`, `email`, `rut`, `created_at`, `updated_at`) VALUES
	(1, '10000001', 'Cliente Uno', '111111111', 'cliente1@email.com', '11111111-1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(2, '10000002', 'Cliente Dos', '222222222', 'cliente2@email.com', '22222222-2', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(3, '10000003', 'Cliente Tres', '333333333', 'cliente3@email.com', '33333333-3', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(4, '10000004', 'Cliente Cuatro', '444444444', 'cliente4@email.com', '44444444-4', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(5, '10000005', 'Cliente Cinco', '555555555', 'cliente5@email.com', '55555555-5', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, '10000006', 'Cliente Seis', '666666666', 'cliente6@email.com', '66666666-6', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, '10000007', 'Cliente Siete', '777777777', 'cliente7@email.com', '77777777-7', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, '10000008', 'Cliente Ocho', '888888888', 'cliente8@email.com', '88888888-8', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, '10000009', 'Cliente Nueve', '999999999', 'cliente9@email.com', '99999999-9', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, '10000010', 'Cliente Diez', '101010101', 'cliente10@email.com', '10101010-0', '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.employee
CREATE TABLE IF NOT EXISTS `employee` (
  `id_employee` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `id_role` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_employee`),
  UNIQUE KEY `user` (`user`),
  KEY `id_role` (`id_role`),
  KEY `idx_employee_user` (`user`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`id_role`) REFERENCES `role` (`id_role`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.employee: ~11 rows (aproximadamente)
INSERT INTO `employee` (`id_employee`, `name`, `user`, `password`, `id_role`, `created_at`, `updated_at`) VALUES
	(1, 'Administrador', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(2, 'Juan Pérez', 'jperez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(3, 'María Gómez', 'mgomez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(4, 'Carlos López', 'clopez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(5, 'Ana Rodríguez', 'arodriguez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 4, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, 'Luis Martínez', 'lmartinez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 5, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, 'Sofía García', 'sgarcia', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 6, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 'Pedro Sánchez', 'psanchez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 7, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 'Laura Fernández', 'lfernandez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 8, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 'Jorge Ramírez', 'jramirez', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 9, '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(11, 'Mónica Díaz', 'mdiaz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 10, '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.entry
CREATE TABLE IF NOT EXISTS `entry` (
  `id_entry` int NOT NULL,
  `date_entry` date NOT NULL,
  `quantity` int NOT NULL,
  `observations` varchar(100) NOT NULL,
  `id_article` int NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id_entry`),
  KEY `FK_entry_article` (`id_article`),
  CONSTRAINT `FK_entry_article` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para registrar entradas del inventario';

-- Volcando datos para la tabla hardware_store_db.entry: ~0 rows (aproximadamente)

-- Volcando estructura para tabla hardware_store_db.inventory
CREATE TABLE IF NOT EXISTS `inventory` (
  `id_inventory` int NOT NULL AUTO_INCREMENT,
  `current_stock` int NOT NULL DEFAULT '0',
  `minimum_stock` int NOT NULL DEFAULT '0',
  `id_article` int NOT NULL,
  `id_location` int NOT NULL,
  `updating_date` date NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_inventory`),
  UNIQUE KEY `unique_article_location` (`id_article`,`id_location`),
  KEY `idx_inventory_article` (`id_article`),
  KEY `idx_inventory_location` (`id_location`),
  CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `inventory_ibfk_2` FOREIGN KEY (`id_location`) REFERENCES `location` (`id_location`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `inventory_chk_1` CHECK ((`current_stock` >= 0)),
  CONSTRAINT `inventory_chk_2` CHECK ((`minimum_stock` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.inventory: ~10 rows (aproximadamente)
INSERT INTO `inventory` (`id_inventory`, `current_stock`, `minimum_stock`, `id_article`, `id_location`, `updating_date`, `created_at`, `updated_at`) VALUES
	(1, 30, 5, 1, 1, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(2, 15, 3, 2, 1, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(3, 20, 4, 3, 1, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(4, 500, 100, 4, 2, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(5, 400, 80, 5, 2, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, 100, 30, 6, 3, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, 10, 2, 7, 1, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 20, 5, 8, 4, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 15, 3, 9, 4, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 8, 2, 10, 5, '2023-01-15', '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.issue
CREATE TABLE IF NOT EXISTS `issue` (
  `id_issue` bigint NOT NULL,
  `date_issue` date NOT NULL,
  `quantity` int NOT NULL,
  `observations` varchar(100) DEFAULT NULL,
  `id_article` int NOT NULL DEFAULT (0),
  `id_employee` bigint NOT NULL,
  `id_client` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id_issue`),
  KEY `FK_issue_article` (`id_article`),
  KEY `FK_issue_employee` (`id_employee`),
  KEY `FK_issue_client` (`id_client`),
  CONSTRAINT `FK_issue_article` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_issue_client` FOREIGN KEY (`id_client`) REFERENCES `client` (`id_client`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_issue_employee` FOREIGN KEY (`id_employee`) REFERENCES `employee` (`id_employee`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Tabla para registrar movimientos de salida del inventario';

-- Volcando datos para la tabla hardware_store_db.issue: ~0 rows (aproximadamente)

-- Volcando estructura para tabla hardware_store_db.location
CREATE TABLE IF NOT EXISTS `location` (
  `id_location` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_location`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.location: ~14 rows (aproximadamente)
INSERT INTO `location` (`id_location`, `name`, `code`, `created_at`, `updated_at`) VALUES
	(1, 'Almacén Principal', 'ALM-001', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(2, 'Tienda', 'TDA-001', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(3, 'Bodega A', 'BOD-A', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(4, 'Bodega B', 'BOD-B', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(5, 'Estante A1', 'EST-A1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, 'Estante A2', 'EST-A2', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, 'Estante B1', 'EST-B1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 'Estante B2', 'EST-B2', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 'Pasillo 1', 'PAS-1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 'Pasillo 2', 'PAS-2', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(11, 'Sótano', 'SOT-1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(12, 'Trastienda', 'TRA-1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(13, 'Mostrador', 'MOS-1', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(14, 'Exhibición', 'EXH-1', '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.payment
CREATE TABLE IF NOT EXISTS `payment` (
  `id_payment` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `method` varchar(20) NOT NULL,
  `date` datetime NOT NULL,
  `id_sale` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_payment`),
  KEY `id_sale` (`id_sale`),
  KEY `idx_payment_date` (`date`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`id_sale`) REFERENCES `sale` (`id_sale`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `payment_chk_1` CHECK ((`amount` > 0)),
  CONSTRAINT `payment_chk_2` CHECK ((`method` in (_utf8mb4'EFECTIVO',_utf8mb4'TARJETA_CREDITO',_utf8mb4'TARJETA_DEBITO',_utf8mb4'TRANSFERENCIA',_utf8mb4'COMPROBADO')))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.payment: ~10 rows (aproximadamente)
INSERT INTO `payment` (`id_payment`, `amount`, `method`, `date`, `id_sale`, `created_at`, `updated_at`) VALUES
	(1, 35.98, 'EFECTIVO', '2023-01-11 10:00:00', 1, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(2, 25.50, 'EFECTIVO', '2023-01-12 11:00:00', 2, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(3, 38.25, 'EFECTIVO', '2023-01-13 12:00:00', 3, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(4, 5.00, 'EFECTIVO', '2023-01-14 13:00:00', 4, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(5, 3.00, 'EFECTIVO', '2023-01-15 14:00:00', 5, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(6, 12.80, 'EFECTIVO', '2023-01-16 15:00:00', 6, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(7, 36.60, 'EFECTIVO', '2023-01-17 16:00:00', 7, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(8, 15.60, 'EFECTIVO', '2023-01-18 17:00:00', 8, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(9, 8.50, 'EFECTIVO', '2023-01-19 18:00:00', 9, '2025-07-17 17:11:53', '2025-07-17 17:11:53'),
	(10, 25.00, 'EFECTIVO', '2023-01-20 19:00:00', 10, '2025-07-17 17:11:53', '2025-07-17 17:11:53');

-- Volcando estructura para tabla hardware_store_db.product_returns
CREATE TABLE IF NOT EXISTS `product_returns` (
  `id_return` int NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `date` date NOT NULL,
  `id_sale` bigint NOT NULL,
  `id_article` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_return`),
  KEY `id_sale` (`id_sale`),
  KEY `id_article` (`id_article`),
  KEY `idx_returns_date` (`date`),
  CONSTRAINT `product_returns_ibfk_1` FOREIGN KEY (`id_sale`) REFERENCES `sale` (`id_sale`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `product_returns_ibfk_2` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `product_returns_chk_1` CHECK ((`quantity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.product_returns: ~10 rows (aproximadamente)
INSERT INTO `product_returns` (`id_return`, `quantity`, `reason`, `date`, `id_sale`, `id_article`, `created_at`, `updated_at`) VALUES
	(1, 1, 'Producto defectuoso', '2023-01-21', 1, 1, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(2, 1, 'No era lo que esperaba', '2023-01-22', 2, 2, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(3, 1, 'Cambio de opinión', '2023-01-23', 3, 3, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(4, 5, 'Tornillos incorrectos', '2023-01-24', 4, 4, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(5, 5, 'Tuercas incorrectas', '2023-01-25', 5, 5, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(6, 1, 'Cable corto', '2023-01-26', 6, 6, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(7, 1, 'Llave dañada', '2023-01-27', 7, 7, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(8, 1, 'Color incorrecto', '2023-01-28', 8, 8, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(9, 1, 'Brocha deshilachada', '2023-01-29', 9, 9, '2025-07-17 17:12:05', '2025-07-17 17:12:05'),
	(10, 1, 'Bolsas rotas', '2023-01-30', 10, 10, '2025-07-17 17:12:05', '2025-07-17 17:12:05');

-- Volcando estructura para tabla hardware_store_db.purchase
CREATE TABLE IF NOT EXISTS `purchase` (
  `id_purchase` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `date` date NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  `id_supplier` bigint NOT NULL,
  `id_employee` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `unit_price` decimal(10,2) NOT NULL,
  `id_article` int NOT NULL,
  PRIMARY KEY (`id_purchase`),
  KEY `idx_purchase_date` (`date`),
  KEY `idx_purchase_supplier` (`id_supplier`),
  KEY `idx_purchase_employee` (`id_employee`),
  CONSTRAINT `purchase_ibfk_1` FOREIGN KEY (`id_supplier`) REFERENCES `supplier` (`id_supplier`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `purchase_ibfk_2` FOREIGN KEY (`id_employee`) REFERENCES `employee` (`id_employee`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `purchase_chk_1` CHECK ((`quantity` > 0)),
  CONSTRAINT `purchase_chk_2` CHECK ((`total` > 0)),
  CONSTRAINT `purchase_chk_3` CHECK ((`status` in (_utf8mb4'PENDIENTE',_utf8mb4'COMPLETADO',_utf8mb4'CANCELADO')))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.purchase: ~10 rows (aproximadamente)
INSERT INTO `purchase` (`id_purchase`, `quantity`, `date`, `total`, `status`, `id_supplier`, `id_employee`, `created_at`, `updated_at`, `unit_price`, `id_article`) VALUES
	(1, 50, '2023-01-05', 1250.00, 'COMPLETADO', 1, 3, '2023-01-05 14:15:22', '2023-01-05 14:15:22', 0.00, 0),
	(2, 100, '2023-01-12', 850.50, 'COMPLETADO', 2, 3, '2023-01-12 16:30:45', '2023-01-12 16:30:45', 0.00, 0),
	(3, 30, '2023-01-18', 375.75, 'COMPLETADO', 3, 4, '2023-01-18 19:20:10', '2023-01-18 19:20:10', 0.00, 0),
	(4, 75, '2023-02-02', 1125.25, 'COMPLETADO', 1, 3, '2023-02-02 15:45:33', '2023-02-02 15:45:33', 0.00, 0),
	(5, 20, '2023-02-10', 420.00, 'COMPLETADO', 4, 5, '2023-02-10 21:10:18', '2023-02-10 21:30:00', 0.00, 0),
	(6, 45, '2023-02-15', 675.90, 'COMPLETADO', 2, 3, '2023-02-15 13:30:00', '2023-02-15 13:30:00', 0.00, 0),
	(7, 60, '2023-03-01', 1500.00, 'COMPLETADO', 5, 4, '2023-03-01 18:15:42', '2023-03-01 18:15:42', 0.00, 0),
	(8, 25, '2023-03-08', 312.50, 'COMPLETADO', 3, 3, '2023-03-08 14:45:12', '2023-03-08 14:45:12', 0.00, 0),
	(9, 40, '2023-03-15', 800.80, 'COMPLETADO', 1, 5, '2023-03-15 16:20:30', '2023-03-15 16:20:30', 0.00, 0),
	(10, 15, '2023-03-22', 225.25, 'COMPLETADO', 4, 3, '2023-03-22 20:40:05', '2023-03-22 20:40:05', 0.00, 0);

-- Volcando estructura para tabla hardware_store_db.purchase_detail
CREATE TABLE IF NOT EXISTS `purchase_detail` (
  `id_purchase_detail` int NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `id_purchase` bigint NOT NULL,
  `id_article` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_purchase_detail`),
  KEY `id_purchase` (`id_purchase`),
  KEY `id_article` (`id_article`),
  CONSTRAINT `purchase_detail_ibfk_1` FOREIGN KEY (`id_purchase`) REFERENCES `purchase` (`id_purchase`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `purchase_detail_ibfk_2` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `purchase_detail_chk_1` CHECK ((`quantity` > 0)),
  CONSTRAINT `purchase_detail_chk_2` CHECK ((`unit_price` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.purchase_detail: ~10 rows (aproximadamente)
INSERT INTO `purchase_detail` (`id_purchase_detail`, `quantity`, `unit_price`, `id_purchase`, `id_article`, `created_at`, `updated_at`) VALUES
	(1, 10, 15.99, 1, 1, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(2, 20, 8.50, 2, 2, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(3, 15, 12.75, 3, 3, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(4, 1000, 0.25, 4, 4, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(5, 800, 0.15, 5, 5, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(6, 200, 3.20, 6, 6, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(7, 5, 18.30, 7, 7, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(8, 20, 7.80, 8, 8, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(9, 10, 4.25, 9, 9, '2025-07-17 17:09:56', '2025-07-17 17:09:56'),
	(10, 5, 12.50, 10, 10, '2025-07-17 17:09:56', '2025-07-17 17:09:56');

-- Volcando estructura para tabla hardware_store_db.role
CREATE TABLE IF NOT EXISTS `role` (
  `id_role` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_role`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.role: ~14 rows (aproximadamente)
INSERT INTO `role` (`id_role`, `name`, `created_at`, `updated_at`) VALUES
	(1, 'ADMIN', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(2, 'VENDEDOR', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(3, 'COMPRADOR', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(4, 'GERENTE', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(5, 'SUPERVISOR', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(6, 'ALMACENERO', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(7, 'CONTADOR', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(8, 'TECNICO', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(9, 'ATENCION_CLIENTE', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(10, 'MARKETING', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(11, 'LOGISTICA', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(12, 'RRHH', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(13, 'SISTEMAS', '2025-07-17 16:53:08', '2025-07-17 16:53:08'),
	(14, 'MANTENIMIENTO', '2025-07-17 16:53:08', '2025-07-17 16:53:08');

-- Volcando estructura para tabla hardware_store_db.sale
CREATE TABLE IF NOT EXISTS `sale` (
  `id_sale` bigint NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `id_client` bigint NOT NULL,
  `id_employee` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_sale`),
  KEY `idx_sale_date` (`date`),
  KEY `idx_sale_client` (`id_client`),
  KEY `idx_sale_employee` (`id_employee`),
  CONSTRAINT `sale_ibfk_1` FOREIGN KEY (`id_client`) REFERENCES `client` (`id_client`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `sale_ibfk_2` FOREIGN KEY (`id_employee`) REFERENCES `employee` (`id_employee`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `sale_chk_1` CHECK ((`total` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.sale: ~10 rows (aproximadamente)
INSERT INTO `sale` (`id_sale`, `date`, `total`, `id_client`, `id_employee`, `created_at`, `updated_at`) VALUES
	(1, '2023-01-11', 35.98, 1, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(2, '2023-01-12', 25.50, 2, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(3, '2023-01-13', 38.25, 3, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(4, '2023-01-14', 5.00, 4, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(5, '2023-01-15', 3.00, 5, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(6, '2023-01-16', 12.80, 6, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(7, '2023-01-17', 36.60, 7, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(8, '2023-01-18', 15.60, 8, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(9, '2023-01-19', 8.50, 9, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28'),
	(10, '2023-01-20', 25.00, 10, 2, '2025-07-17 17:10:28', '2025-07-17 17:10:28');

-- Volcando estructura para tabla hardware_store_db.sale_detail
CREATE TABLE IF NOT EXISTS `sale_detail` (
  `id_sale_detail` int NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `id_sale` bigint NOT NULL,
  `id_article` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_sale_detail`),
  KEY `id_sale` (`id_sale`),
  KEY `id_article` (`id_article`),
  CONSTRAINT `sale_detail_ibfk_1` FOREIGN KEY (`id_sale`) REFERENCES `sale` (`id_sale`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sale_detail_ibfk_2` FOREIGN KEY (`id_article`) REFERENCES `article` (`id_article`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `sale_detail_chk_1` CHECK ((`quantity` > 0)),
  CONSTRAINT `sale_detail_chk_2` CHECK ((`unit_price` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.sale_detail: ~10 rows (aproximadamente)
INSERT INTO `sale_detail` (`id_sale_detail`, `quantity`, `unit_price`, `id_sale`, `id_article`, `created_at`, `updated_at`) VALUES
	(1, 2, 17.99, 1, 1, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(2, 3, 8.50, 2, 2, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(3, 3, 12.75, 3, 3, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(4, 20, 0.25, 4, 4, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(5, 20, 0.15, 5, 5, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(6, 4, 3.20, 6, 6, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(7, 2, 18.30, 7, 7, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(8, 2, 7.80, 8, 8, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(9, 2, 4.25, 9, 9, '2025-07-17 17:10:35', '2025-07-17 17:10:35'),
	(10, 2, 12.50, 10, 10, '2025-07-17 17:10:35', '2025-07-17 17:10:35');

-- Volcando estructura para tabla hardware_store_db.supplier
CREATE TABLE IF NOT EXISTS `supplier` (
  `id_supplier` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `document` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_supplier`),
  UNIQUE KEY `document` (`document`),
  KEY `idx_supplier_document` (`document`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.supplier: ~10 rows (aproximadamente)
INSERT INTO `supplier` (`id_supplier`, `name`, `phone`, `document`, `created_at`, `updated_at`) VALUES
	(1, 'Proveedor Uno', '111111111', 'P11111111', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(2, 'Proveedor Dos', '222222222', 'P22222222', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(3, 'Proveedor Tres', '333333333', 'P33333333', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(4, 'Proveedor Cuatro', '444444444', 'P44444444', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(5, 'Proveedor Cinco', '555555555', 'P55555555', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(6, 'Proveedor Seis', '666666666', 'P66666666', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(7, 'Proveedor Siete', '777777777', 'P77777777', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 'Proveedor Ocho', '888888888', 'P88888888', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 'Proveedor Nueve', '999999999', 'P99999999', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 'Proveedor Diez', '101010101', 'P10101010', '2025-07-17 16:53:09', '2025-07-17 16:53:09');

-- Volcando estructura para tabla hardware_store_db.unit
CREATE TABLE IF NOT EXISTS `unit` (
  `id_unit` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `abbreviation` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_unit`),
  UNIQUE KEY `abbreviation` (`abbreviation`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Volcando datos para la tabla hardware_store_db.unit: ~16 rows (aproximadamente)
INSERT INTO `unit` (`id_unit`, `name`, `abbreviation`, `created_at`, `updated_at`) VALUES
	(1, 'Unidad', 'UND', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(2, 'Kilogramo', 'KG', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(3, 'Metro', 'M', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(4, 'Litro', 'L', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(5, 'Caja', 'CJA', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(6, 'Paquete', 'PAQ', '2025-07-17 16:46:35', '2025-07-17 16:46:35'),
	(7, 'Gramo', 'GR', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(8, 'Mililitro', 'ML', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(9, 'Pulgada', 'IN', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(10, 'Pie', 'FT', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(11, 'Yarda', 'YD', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(12, 'Galón', 'GAL', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(13, 'Onza', 'OZ', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(14, 'Set', 'SET', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(15, 'Par', 'PAR', '2025-07-17 16:53:09', '2025-07-17 16:53:09'),
	(16, 'Docena', 'DOC', '2025-07-17 16:53:09', '2025-07-17 16:53:09');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
