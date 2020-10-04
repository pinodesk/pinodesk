package com.gitlab.muhammadkholidb.bianglala.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    public void beforeCreate() {
        updatedAt = createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
}
