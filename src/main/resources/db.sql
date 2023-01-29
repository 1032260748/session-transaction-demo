CREATE TABLE `product` (
                           `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
                           `code` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
                           `price` int DEFAULT NULL,
                           `description` varchar(200) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
                           PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
