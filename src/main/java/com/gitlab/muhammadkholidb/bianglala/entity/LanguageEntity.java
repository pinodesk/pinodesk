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
@Table(name = LanguageEntity.TABLE_NAME)
public class LanguageEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_language";

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

}
