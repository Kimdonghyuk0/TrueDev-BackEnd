package com.week4.lucas.user.mapper;

import com.week4.lucas.user.dto.request.UserReq;
import com.week4.lucas.user.dto.response.AccountUpdateRes;
import com.week4.lucas.user.entity.User;


public class UserMapper {
    public static AccountUpdateRes toUpdateUser(User user){
        return new AccountUpdateRes(
                user.getName(),
                user.getEmail(),
                user.getProfileImage()
        );
    }

}
