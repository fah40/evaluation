CREATE TABLE IF NOT EXISTS `budget` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `customer_id` INT NOT NULL,
    `date` DATE NOT NULL,
    `value` DOUBLE NOT NULL CHECK (`value` > 0),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
);

CREATE TABLE IF NOT EXISTS `type_config` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Configuration` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `id_type_config` INT NOT NULL,
    `value` TEXT NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`id_type_config`) REFERENCES `type_config` (`id`)
);

CREATE TABLE IF NOT EXISTS `depense` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `lead_id` INT DEFAULT NULL,
    `ticket_id` INT DEFAULT NULL,
    `date` DATE NOT NULL,
    `value` DOUBLE NOT NULL CHECK (`value` > 0),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`lead_id`) REFERENCES `trigger_lead` (`lead_id`),
    FOREIGN KEY (`ticket_id`) REFERENCES `trigger_ticket` (`ticket_id`),
    CHECK (
        (`lead_id` IS NOT NULL AND `ticket_id` IS NULL) OR 
        (`lead_id` IS NULL AND `ticket_id` IS NOT NULL)
    )
);