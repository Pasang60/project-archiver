package com.pasang.projectarchiver.users.entity;

/*
 * @author Pasang Gelbu Sherpa *
 */

import com.pasang.projectarchiver.global.Auditable;
import com.pasang.projectarchiver.role.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "FULL_NAME")
    private String fullName;

    @Column(nullable = false, name = "EMAIL_ID", unique = true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PHONE")
    private String phone;
    private String profilePic;

    @Column(name = "ADDRESS")
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> role;
    private Boolean isActive = true;
    private Boolean isDeleted = false;
    private Boolean isVerified = false;

}
