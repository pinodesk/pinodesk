package com.gitlab.muhammadkholidb.bianglala.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.math.BigDecimal;
import java.sql.Date;

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
@Table(name = ProductEntity.TABLE_NAME)
public class ProductEntity extends BaseEntity {

    public static final String TABLE_NAME = "t_product";

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "unit_id", referencedColumnName = "id")
    private UnitEntity unit;

    @Column(name = "unit_label")
    private String unitLabel;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "category_code", referencedColumnName = "code")
    private ProductCategoryEntity category;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "rack_id", referencedColumnName = "id")
    private RackEntity rack;

    @Column(name = "rack_code")
    private String rackCode;

    @Column(name = "rack_name")
    private String rackName;

    @Column(name = "expired_date")
    private Date expiredDate;

}
