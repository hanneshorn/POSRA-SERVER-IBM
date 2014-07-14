SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `POSRA` ;
CREATE SCHEMA IF NOT EXISTS `POSRA` DEFAULT CHARACTER SET big5 ;
USE `POSRA` ;

-- -----------------------------------------------------
-- Table `POSRA`.`Polymer`
-- -----------------------------------------------------
-- bryn changed here used to say 'POSRA'.'Polymer'
-- changed order of polymerID, name
-- changed polymerID --> externalID
DROP TABLE IF EXISTS `Polymer` ;

CREATE  TABLE IF NOT EXISTS `Polymer` (
  `name` VARCHAR(255) NOT NULL ,
  `polymerID` INT(11) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`polymerID`) ,
  UNIQUE INDEX `Polymer_UC` (`name` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = big5;


-- -----------------------------------------------------
-- Table `POSRA`.`ExternalID`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `POSRA`.`ExternalID` ;

CREATE  TABLE IF NOT EXISTS `POSRA`.`ExternalID` (
  `value` VARCHAR(255) NOT NULL ,
  `polymerID` INT(11) NOT NULL ,
  PRIMARY KEY (`value`) ,
  INDEX `ExternalID_FK` (`polymerID` ASC) ,
  CONSTRAINT `ExternalID_FK`
    FOREIGN KEY (`polymerID` )
    REFERENCES `POSRA`.`Polymer` (`polymerID` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = big5;


-- -----------------------------------------------------
-- Table `POSRA`.`RepeatUnit`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `POSRA`.`RepeatUnit` ;

CREATE  TABLE IF NOT EXISTS `POSRA`.`RepeatUnit` (
  `repeatUnitID` INT(11) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`repeatUnitID`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = big5;


-- -----------------------------------------------------
-- Table `POSRA`.`SMILES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `POSRA`.`SMILES` ;

CREATE  TABLE IF NOT EXISTS `POSRA`.`SMILES` (
  `SMILESID` INT(11) NOT NULL AUTO_INCREMENT ,
  `SMILESString` MEDIUMTEXT NOT NULL ,
  PRIMARY KEY (`SMILESID`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = big5;


-- -----------------------------------------------------
-- Table `POSRA`.`Segment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `POSRA`.`Segment` ;

CREATE  TABLE IF NOT EXISTS `POSRA`.`Segment` (
  `segmentID` INT(11) NOT NULL AUTO_INCREMENT ,
  `bondCount` INT(11) NOT NULL ,
  `degree` CHAR(255) NOT NULL ,
  `repeatUnitID` INT(11) NULL DEFAULT NULL ,
  `SMILESID` INT(11) NULL DEFAULT NULL ,
  PRIMARY KEY (`segmentID`) ,
  INDEX `Segment_FK1` (`SMILESID` ASC) ,
  INDEX `Segment_FK2` (`repeatUnitID` ASC) ,
  CONSTRAINT `Segment_FK1`
    FOREIGN KEY (`SMILESID` )
    REFERENCES `POSRA`.`SMILES` (`SMILESID` ),
  CONSTRAINT `Segment_FK2`
    FOREIGN KEY (`repeatUnitID` )
    REFERENCES `POSRA`.`RepeatUnit` (`repeatUnitID` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = big5;


-- -----------------------------------------------------
-- Table `POSRA`.`SegmentAssociation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `POSRA`.`SegmentAssociation` ;

CREATE  TABLE IF NOT EXISTS `POSRA`.`SegmentAssociation` (
  `polymerID` INT(11) NOT NULL ,
  `relationshipType` CHAR(2) NOT NULL ,
  `segmentID1` INT(11) NOT NULL ,
  `segmentID2` INT(11) NOT NULL ,
  PRIMARY KEY (`segmentID1`, `segmentID2`, `relationshipType`, `polymerID`) ,
  INDEX `SegmentAssociation_FK2` (`segmentID2` ASC) ,
  INDEX `SegmentAssociation_FK3` (`polymerID` ASC) ,
  CONSTRAINT `SegmentAssociation_FK1`
    FOREIGN KEY (`segmentID1` )
    REFERENCES `POSRA`.`Segment` (`segmentID` ),
  CONSTRAINT `SegmentAssociation_FK2`
    FOREIGN KEY (`segmentID2` )
    REFERENCES `POSRA`.`Segment` (`segmentID` ),
  CONSTRAINT `SegmentAssociation_FK3`
    FOREIGN KEY (`polymerID` )
    REFERENCES `POSRA`.`Polymer` (`polymerID` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = big5;


-- -----------------------------------------------------
-- Table `POSRA`.`PolymerRepeatUnitSegmentAssociation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `POSRA`.`PolymerRepeatUnitSegmentAssociation` ;

CREATE  TABLE IF NOT EXISTS `POSRA`.`PolymerRepeatUnitSegmentAssociation` (
  `prsID` INT NOT NULL AUTO_INCREMENT ,
  `polymerID` INT NULL DEFAULT NULL COMMENT 'XOR with RepeatUnitID' ,
  `repeatUnitID` INT NULL DEFAULT NULL COMMENT 'XOR with PolymerID' ,
  `segmentID` INT NOT NULL ,
  `segmentType` CHAR NOT NULL ,
  PRIMARY KEY (`prsID`, `polymerID`, `segmentID`, `repeatUnitID`) ,
  UNIQUE INDEX `prsID_UNIQUE` (`prsID` ASC) ,
  INDEX `PolymerRepeatUnitSegmentAssociation_FK1_idx` (`polymerID` ASC) ,
  INDEX `PolymerRepeatUnitSegmentAssociation_FK2_idx` (`repeatUnitID` ASC) ,
  INDEX `PolymerRepeatUnitSegmentAssociation_FK3_idx` (`segmentID` ASC) ,
  CONSTRAINT `PolymerRepeatUnitSegmentAssociation_FK1`
    FOREIGN KEY (`polymerID` )
    REFERENCES `POSRA`.`Polymer` (`polymerID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `PolymerRepeatUnitSegmentAssociation_FK2`
    FOREIGN KEY (`repeatUnitID` )
    REFERENCES `POSRA`.`RepeatUnit` (`repeatUnitID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `PolymerRepeatUnitSegmentAssociation_FK3`
    FOREIGN KEY (`segmentID` )
    REFERENCES `POSRA`.`Segment` (`segmentID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `POSRA` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
