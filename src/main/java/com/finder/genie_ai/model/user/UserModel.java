package com.finder.genie_ai.model.user;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "user_id", length = 60, nullable = false, unique = true)
    private String userId;
    @Column(name = "passwd", length = 200, nullable = false)
    private String passwd;
    @Column(name = "salt", length = 60, nullable = false)
    private String salt;
    @Column(name = "user_name", length = 40, nullable = false)
    private String userName;
    @Column(name = "email", length = 60, nullable = false)
    private String email;
    @Column(name = "birth", columnDefinition = "date", nullable = false)
    private LocalDate birth;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "introduce", length = 128)
    private String introduce;

    @PrePersist
    public void persist() {
        this.createdAt = LocalDateTime.now();
    }

}
