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
@Table(name = ProductCategoryEntity.TABLE_NAME)
public class ProductCategoryEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_product_category";

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "parent_category_id", referencedColumnName = "id")
    private ProductCategoryEntity parentCategory;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "language_id", referencedColumnName = "id")
    private LanguageEntity language;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
