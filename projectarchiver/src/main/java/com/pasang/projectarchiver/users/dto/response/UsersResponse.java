package com.pasang.projectarchiver.users.dto.response;

import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.utils.file.FileUrlUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private URI profilePicture;
    private List<RoleResponse> userRole;
    private boolean status;

    public UsersResponse(Users user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhone();
        this.address = user.getAddress();
        this.profilePicture = FileUrlUtil.getFileUri(user.getProfilePic());
        this.status = user.isStatus();
        this.userRole = user.getRole().stream()
                .map(RoleResponse::new)
                .toList();

    }
}
