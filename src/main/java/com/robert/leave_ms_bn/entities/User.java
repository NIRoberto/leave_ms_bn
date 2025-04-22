package com.robert.leave_ms_bn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Size(max = 255)
    @NotNull
    @Column(name = "lastName", nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnDefault("1")
    @JoinColumn(name = "role_id")
    private Role role;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "createdAt")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Size(max = 50)
    @Column(name = "gender", length = 50)
    private String gender;

    @Lob
    @Column(name = "profilePictureUrl")
    private String profilePictureUrl;

    @Size(max = 255)
    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

}