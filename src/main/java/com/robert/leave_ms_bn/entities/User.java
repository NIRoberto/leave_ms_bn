package com.robert.leave_ms_bn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Size(max = 255)
    @Column(name = "last_name", nullable = false)
    private String last_name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updated_at;

    @Size(max = 50)
    @Column(name = "gender", length = 50)
    private String gender;

    @Lob
    @Column(name = "profile_picture_url")
    private String profile_picture_url;

    @Size(max = 255)
    @NotNull(message = "Phone number is required")
    @Column(name = "phone", nullable = false)
    private String phone;

    @Size(max = 255)
    @NotNull(message = "Email is required")
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;


}
