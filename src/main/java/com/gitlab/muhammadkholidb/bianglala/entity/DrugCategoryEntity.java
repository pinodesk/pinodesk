package com.gitlab.muhammadkholidb.bianglala.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = DrugCategoryEntity.TABLE_NAME)
public class DrugCategoryEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_drug_category";

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "category_base_id", referencedColumnName = "id")
    private DrugCategoryBaseEntity drugCategoryBase;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
