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
@Table(name = RackEntity.TABLE_NAME)
public class RackEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_rack";

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
