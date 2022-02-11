-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema job_portal
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `job_portal` ;

-- -----------------------------------------------------
-- Schema job_portal
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `job_portal` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `job_portal` ;

-- -----------------------------------------------------
-- Table `job_portal`.`user_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`user_types` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`user_types` (
  `user_type_id` INT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(16) NULL DEFAULT NULL,
  PRIMARY KEY (`user_type_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`users` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(128) NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `gender` VARCHAR(6) NULL DEFAULT NULL,
  `DOB` DATE NULL DEFAULT NULL,
  `createdAt` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `user_type_id` INT NOT NULL,
  `phone` VARCHAR(13) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  INDEX `fk_users_user_type1_idx` (`user_type_id` ASC) VISIBLE,
  CONSTRAINT `fk_users_user_type1`
    FOREIGN KEY (`user_type_id`)
    REFERENCES `job_portal`.`user_types` (`user_type_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`job_seekers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`job_seekers` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`job_seekers` (
  `job_seeker_id` INT NOT NULL AUTO_INCREMENT,
  `accomplishments` MEDIUMTEXT NULL DEFAULT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`job_seeker_id`),
  INDEX `fk_job_seekers_users_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_job_seekers_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `job_portal`.`users` (`user_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`pays`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`pays` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`pays` (
  `pay_id` INT NOT NULL AUTO_INCREMENT,
  `from` DECIMAL(19,2) NULL DEFAULT '0.00',
  `to` DECIMAL(19,2) NULL DEFAULT '0.00',
  `pay_type` VARCHAR(45) NULL DEFAULT 'MONTHLY',
  PRIMARY KEY (`pay_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`companies`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`companies` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`companies` (
  `company_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `logo` VARCHAR(255) NULL DEFAULT NULL,
  `sector` VARCHAR(255) NULL DEFAULT NULL,
  `industry` VARCHAR(255) NULL DEFAULT NULL,
  `size` INT NULL DEFAULT NULL,
  `founded` INT NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `revenue_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`company_id`),
  INDEX `fk_companies_pays1_idx` (`revenue_id` ASC) VISIBLE,
  CONSTRAINT `fk_companies_pays1`
    FOREIGN KEY (`revenue_id`)
    REFERENCES `job_portal`.`pays` (`pay_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`job_providers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`job_providers` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`job_providers` (
  `job_provider_id` INT NOT NULL AUTO_INCREMENT,
  `designation` VARCHAR(255) NULL DEFAULT 'owner',
  `user_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  PRIMARY KEY (`job_provider_id`),
  INDEX `fk_job_providers_users1_idx` (`user_id` ASC) VISIBLE,
  INDEX `fk_job_providers_companies1_idx` (`company_id` ASC) VISIBLE,
  CONSTRAINT `fk_job_providers_companies1`
    FOREIGN KEY (`company_id`)
    REFERENCES `job_portal`.`companies` (`company_id`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_job_providers_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `job_portal`.`users` (`user_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`jobs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`jobs` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`jobs` (
  `job_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `description` MEDIUMTEXT NULL DEFAULT NULL,
  `location_type` VARCHAR(45) NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `fullOrPartTime` VARCHAR(45) NULL DEFAULT NULL,
  `openings` INT NULL DEFAULT NULL,
  `job_status` VARCHAR(45) NULL DEFAULT 'OPEN',
  `education_level` VARCHAR(45) NULL DEFAULT NULL,
  `candidate_profile` MEDIUMTEXT NULL DEFAULT NULL,
  `postedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `job_provider_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  `pay_id` INT NOT NULL,
  PRIMARY KEY (`job_id`),
  INDEX `fk_jobs_job_providers1_idx` (`job_provider_id` ASC) VISIBLE,
  INDEX `fk_jobs_companies1_idx` (`company_id` ASC) VISIBLE,
  INDEX `fk_jobs_pays1_idx` (`pay_id` ASC) VISIBLE,
  INDEX `idx_jobStatus` (`job_status` ASC) VISIBLE,
  FULLTEXT INDEX `Fidx_jobTitle` (`title`) VISIBLE,
  CONSTRAINT `fk_jobs_companies`
    FOREIGN KEY (`company_id`)
    REFERENCES `job_portal`.`companies` (`company_id`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_jobs_job_providers`
    FOREIGN KEY (`job_provider_id`)
    REFERENCES `job_portal`.`job_providers` (`job_provider_id`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_jobs_pays`
    FOREIGN KEY (`pay_id`)
    REFERENCES `job_portal`.`pays` (`pay_id`)
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 16
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`applications`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`applications` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`applications` (
  `application_id` INT NOT NULL AUTO_INCREMENT,
  `status` VARCHAR(45) NULL DEFAULT 'ACTIVE',
  `appliedAt` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `resume` VARCHAR(255) NULL DEFAULT NULL,
  `job_seeker_id` INT NOT NULL,
  `job_id` INT NOT NULL,
  PRIMARY KEY (`application_id`),
  INDEX `fk_applications_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  INDEX `fk_applications_jobs1_idx` (`job_id` ASC) VISIBLE,
  CONSTRAINT `fk_applications_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_applications_jobs1`
    FOREIGN KEY (`job_id`)
    REFERENCES `job_portal`.`jobs` (`job_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`educations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`educations` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`educations` (
  `education_id` INT NOT NULL AUTO_INCREMENT,
  `education_level` VARCHAR(25) NULL DEFAULT NULL,
  `course` VARCHAR(128) NULL DEFAULT NULL,
  `institution` VARCHAR(255) NULL DEFAULT NULL,
  `course_type` VARCHAR(25) NULL DEFAULT NULL,
  `passout` INT NULL DEFAULT NULL,
  `grade` FLOAT NULL DEFAULT NULL,
  `job_seeker_id` INT NOT NULL,
  PRIMARY KEY (`education_id`),
  INDEX `fk_educations_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  CONSTRAINT `fk_educations_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`employments`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`employments` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`employments` (
  `employment_id` INT NOT NULL AUTO_INCREMENT,
  `organization` VARCHAR(255) NULL DEFAULT NULL,
  `designation` VARCHAR(255) NULL DEFAULT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `end_date` DATE NULL DEFAULT NULL,
  `job_seeker_id` INT NOT NULL,
  PRIMARY KEY (`employment_id`),
  INDEX `fk_employments_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  CONSTRAINT `fk_employments_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`job_schedules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`job_schedules` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`job_schedules` (
  `job_schedule_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`job_schedule_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 17
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`job_job_schedules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`job_job_schedules` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`job_job_schedules` (
  `job_id` INT NOT NULL,
  `job_schedule_id` INT NOT NULL,
  INDEX `fk_job_job_schedules_jobs1_idx` (`job_id` ASC) VISIBLE,
  INDEX `fk_job_job_schedules_job_schedules1_idx` (`job_schedule_id` ASC) VISIBLE,
  CONSTRAINT `fk_job_job_schedules_job_schedules1`
    FOREIGN KEY (`job_schedule_id`)
    REFERENCES `job_portal`.`job_schedules` (`job_schedule_id`),
  CONSTRAINT `fk_job_job_schedules_jobs1`
    FOREIGN KEY (`job_id`)
    REFERENCES `job_portal`.`jobs` (`job_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`job_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`job_types` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`job_types` (
  `job_type_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`job_type_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`job_job_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`job_job_types` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`job_job_types` (
  `job_id` INT NOT NULL,
  `job_type_id` INT NOT NULL,
  PRIMARY KEY (`job_id`, `job_type_id`),
  INDEX `fk_job_job_type_job_types1_idx` (`job_type_id` ASC) VISIBLE,
  CONSTRAINT `fk_job_job_type_job_types1`
    FOREIGN KEY (`job_type_id`)
    REFERENCES `job_portal`.`job_types` (`job_type_id`),
  CONSTRAINT `fk_job_job_type_jobs1`
    FOREIGN KEY (`job_id`)
    REFERENCES `job_portal`.`jobs` (`job_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`key_skills`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`key_skills` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`key_skills` (
  `key_skill_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`key_skill_id`),
  FULLTEXT INDEX `idx_skillName` (`name`) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 36944
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`languages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`languages` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`languages` (
  `language_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(65) NULL DEFAULT NULL,
  PRIMARY KEY (`language_id`),
  FULLTEXT INDEX `idx_languageName` (`name`) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 254
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`projects`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`projects` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`projects` (
  `project_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `status` VARCHAR(255) NULL DEFAULT NULL,
  `client` VARCHAR(255) NULL DEFAULT NULL,
  `start_date` DATE NULL DEFAULT NULL,
  `end_date` DATE NULL DEFAULT NULL,
  `link` MEDIUMTEXT NULL DEFAULT NULL,
  `details` MEDIUMTEXT NULL DEFAULT NULL,
  `job_seeker_id` INT NOT NULL,
  PRIMARY KEY (`project_id`, `job_seeker_id`),
  INDEX `fk_projects_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  CONSTRAINT `fk_projects_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`questions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`questions` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`questions` (
  `question_id` INT NOT NULL AUTO_INCREMENT,
  `question` MEDIUMTEXT NOT NULL,
  `job_id` INT NOT NULL,
  PRIMARY KEY (`question_id`),
  INDEX `fk_questions_jobs1_idx` (`job_id` ASC) VISIBLE,
  CONSTRAINT `fk_questions_jobs1`
    FOREIGN KEY (`job_id`)
    REFERENCES `job_portal`.`jobs` (`job_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 17
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`reviews`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`reviews` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`reviews` (
  `review_id` INT NOT NULL AUTO_INCREMENT,
  `job_title` VARCHAR(255) NULL DEFAULT NULL,
  `ratings` INT NULL DEFAULT NULL,
  `job_status` VARCHAR(45) NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `review` MEDIUMTEXT NULL DEFAULT NULL,
  `pros` MEDIUMTEXT NULL DEFAULT NULL,
  `cons` MEDIUMTEXT NULL DEFAULT NULL,
  `reviewedAt` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `job_seeker_id` INT NOT NULL,
  `company_id` INT NOT NULL,
  PRIMARY KEY (`review_id`),
  INDEX `fk_reviews_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  INDEX `fk_reviews_companies1_idx` (`company_id` ASC) VISIBLE,
  CONSTRAINT `fk_reviews_companies1`
    FOREIGN KEY (`company_id`)
    REFERENCES `job_portal`.`companies` (`company_id`),
  CONSTRAINT `fk_reviews_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`seeker_answers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`seeker_answers` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`seeker_answers` (
  `answer` MEDIUMTEXT NOT NULL,
  `application_id` INT NOT NULL,
  `question_id` INT NOT NULL,
  PRIMARY KEY (`question_id`, `application_id`),
  INDEX `fk_answers_applications1_idx` (`application_id` ASC) VISIBLE,
  INDEX `fk_answers_questions1_idx` (`question_id` ASC) VISIBLE,
  CONSTRAINT `fk_answers_applications1`
    FOREIGN KEY (`application_id`)
    REFERENCES `job_portal`.`applications` (`application_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_answers_questions1`
    FOREIGN KEY (`question_id`)
    REFERENCES `job_portal`.`questions` (`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`seeker_languages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`seeker_languages` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`seeker_languages` (
  `language_id` INT NOT NULL,
  `job_seeker_id` INT NOT NULL,
  PRIMARY KEY (`language_id`, `job_seeker_id`),
  INDEX `fk_seeker_languages_languages1_idx` (`language_id` ASC) VISIBLE,
  INDEX `fk_seeker_languages_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  CONSTRAINT `fk_seeker_languages_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_seeker_languages_languages1`
    FOREIGN KEY (`language_id`)
    REFERENCES `job_portal`.`languages` (`language_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `job_portal`.`seeker_skills`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_portal`.`seeker_skills` ;

CREATE TABLE IF NOT EXISTS `job_portal`.`seeker_skills` (
  `key_skill_id` INT NOT NULL,
  `job_seeker_id` INT NOT NULL,
  PRIMARY KEY (`key_skill_id`, `job_seeker_id`),
  INDEX `fk_seeker_skills_key_skills1_idx` (`key_skill_id` ASC) VISIBLE,
  INDEX `fk_seeker_skills_job_seekers1_idx` (`job_seeker_id` ASC) VISIBLE,
  CONSTRAINT `fk_seeker_skills_job_seekers1`
    FOREIGN KEY (`job_seeker_id`)
    REFERENCES `job_portal`.`job_seekers` (`job_seeker_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_seeker_skills_key_skills1`
    FOREIGN KEY (`key_skill_id`)
    REFERENCES `job_portal`.`key_skills` (`key_skill_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
