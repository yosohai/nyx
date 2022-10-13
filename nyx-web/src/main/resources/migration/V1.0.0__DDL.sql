CREATE TABLE `t_visitor` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问者id',
    `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `password` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `salt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `visitortype` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问者类型',
    `description` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `createman` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `createtime` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `updateman` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `updatetime` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `version` int DEFAULT '0' COMMENT '乐观锁,版本号',
    `deleteflag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '0',
    `tenantid` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='访问者表';