/*
 * @author Pasang Gelbu Sherpa *
 */
package com.pasang.projectarchiver.auth.repository;

import com.pasang.projectarchiver.auth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
