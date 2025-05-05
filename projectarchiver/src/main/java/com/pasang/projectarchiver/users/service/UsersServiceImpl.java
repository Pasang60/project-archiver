package com.pasang.projectarchiver.users.service;

import com.pasang.projectarchiver.users.dto.UsersRegistrationRequest;
import com.pasang.projectarchiver.users.dto.UsersResponse;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import com.pasang.projectarchiver.utils.file.FileHandlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService{
    private final UsersRepository usersRepository;
    private final FileHandlerUtil fileHandlerUtil;
    @Override
    public UsersResponse registerUser(UsersRegistrationRequest usersRegistrationRequest) {
        log.info("Registering user: {}", usersRegistrationRequest);
        Users user = new Users();
        user.setFullName(usersRegistrationRequest.getFullName());
        user.setEmail(usersRegistrationRequest.getEmail());
        user.setPhone(usersRegistrationRequest.getPhone());
        user.setAddress(usersRegistrationRequest.getAddress());
        user.setProfilePic(fileHandlerUtil.saveFile(usersRegistrationRequest.getProfilePic(), "profileImages").getFileDownloadUri());
        usersRepository.save(user);
        log.info("User registered: {}", user);

        return null;
    }
}
