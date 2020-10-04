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
@Table(name = DrugDetailEntity.TABLE_NAME)
public class DrugDetailEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_drug_detail";

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductEntity product;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "drug_category_id", referencedColumnName = "id")
    private DrugCategoryEntity drugCategory;

    @Column(name = "drug_category_code")
    private String drugCategoryCode;

    @Column(name = "drug_category_name")
    private String drugCategoryName;

    @Column(name = "indication")
    private String indication;

    @Column(name = "contraindication")
    private String contraindication;

    @Column(name = "prescription_price")
    private String prescriptionPrice;

}
