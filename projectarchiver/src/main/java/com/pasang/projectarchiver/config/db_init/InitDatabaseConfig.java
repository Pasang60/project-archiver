package com.pasang.projectarchiver.config.db_init;

import com.pasang.projectarchiver.constant.SystemMessage;
import com.pasang.projectarchiver.role.entity.Role;
import com.pasang.projectarchiver.role.repository.RoleRepository;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitDatabaseConfig {
    private final RoleRepository roleRepository;
    private final UsersRepository userRepository;
    private static final String PASSWORD = "Admin@123";

    @PostConstruct
    public void initDatabase() {
        log.info("Initializing database");
        if(roleRepository.findAll().isEmpty()) {
            log.info("Creating roles");
            List<Role> roles = List.of(
                    new Role(null, "ROLE_SUPER_ADMIN", "Super Admin Role"),
                    new Role(null, "ROLE_ADMIN", "Admin Role")
            );
            roleRepository.saveAll(roles);
            log.info("Roles created");
        }

        if(userRepository.findAll().isEmpty()) {
            log.info("Creating super admin role user");
            Users user = new Users();
            user.setFullName("Super Admin");
            user.setEmail("admin@gmail.com");
            user.setPhone("9840757252");
            user.setAddress("Kathmandu, Nepal");
            user.setPassword(new BCryptPasswordEncoder().encode(PASSWORD));
            Role role = roleRepository.findByName(Role.ROLE_SUPER_ADMIN).orElseThrow(
                    () -> new RuntimeException(SystemMessage.ROLE_NOT_FOUND)
            );
            user.setRole(List.of(role));
            user.setIsActive(true);
            user.setIsDeleted(false);
            userRepository.save(user);
            log.info("Super admin role user created");
        }

    }
}

