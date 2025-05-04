package com.pasang.projectarchiver.auth.entity;
/*
 * @author Pasang Gelbu Sherpa *
 */

import com.pasang.projectarchiver.users.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    private String accessToken;
    @OneToOne
    private Users user;
    private Boolean isExpired;
}
