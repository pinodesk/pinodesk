package com.gitlab.muhammadkholidb.bianglala.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author muhammad
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = UnitEntity.TABLE_NAME)
public class UnitEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_unit";

    @Column(name = "name")
    private String name;

    @Column(name = "label")
    private String label;

}
