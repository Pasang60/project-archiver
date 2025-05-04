/*
 * @author Pasang Gelbu Sherpa *
 */
package com.pasang.projectarchiver.auth.repository;

import com.pasang.projectarchiver.auth.entity.Token;
import com.pasang.projectarchiver.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByAccessToken(String accessToken);


    Optional<Token> findByUser(Users user);
}
