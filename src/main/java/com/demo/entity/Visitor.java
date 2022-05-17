package com.demo.entity;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

import javax.persistence.*;

@Data
@Entity
@Table(name = "visitor")
public class Visitor {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", columnDefinition = "varchar(20) not null")
    private String username;

    @Column(name = "email", columnDefinition = "varchar(20) not null")
    private String email;

    @Column(name = "password", columnDefinition = "varchar(20) not null")
    private String password;

    @Column(name = "clientid", columnDefinition = "varchar(60) not null")
    private String clientid;

    @Column(name = "activation", columnDefinition = "int(2)")
    private int activation;

    @Column(name = "activationCode", columnDefinition = "varchar(60)")
    private String activationCode;

    @Column(name = "accessToken", columnDefinition = "varchar(60)")
    private String accessToken;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        if (createTime == null) {
            createTime = now;
        }
        if (updateTime == null) {
            updateTime = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = new Date();
    }

    @PreRemove
    public void preRemove() {
        updateTime = new Date();
    }

    public Visitor() {
    }

    public Visitor(String username, String email, String password, String clientid, int activation, String activationCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.clientid = clientid;
        this.activation = activation;
        this.activationCode = activationCode;
    }
}
