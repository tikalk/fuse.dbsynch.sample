-- MySql schema
--for xa transactions must be innoDB engine

DROP TABLE `producer`.`source_table`;
CREATE TABLE  `producer`.`source_table` (
  `id` bigint(20) NOT NULL,
  `desc` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=latin1;


DROP TABLE `collector`.`dest_table`;
CREATE TABLE  `collector`.`dest_table` (
  `id` bigint(20) NOT NULL,
  `desc` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=latin1;
