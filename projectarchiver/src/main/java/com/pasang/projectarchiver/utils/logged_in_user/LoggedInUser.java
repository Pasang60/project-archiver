package com.pasang.projectarchiver.utils.logged_in_user;


import com.pasang.projectarchiver.auth.repository.TokenRepository;
import com.pasang.projectarchiver.role.entity.Role;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoggedInUser {
    private final UsersRepository userRepository;
    private final TokenRepository tokenRepository;

    public Users getLoggedInUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("No user found with email: " + email)
        );
    }

    public List<Role> getLoggedInUserRole() {
        Users user = getLoggedInUser();
        return user.getRole();
    }


    public String getLoggedInUserAccessToken(){
        Users user = getLoggedInUser();
        return tokenRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("No refresh token found for user: " + user.getEmail())
        ).getAccessToken();
    }
}
