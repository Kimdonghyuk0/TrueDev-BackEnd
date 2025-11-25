package com.kdh.truedev.user.mapper;

import com.kdh.truedev.user.dto.response.AccountUpdateRes;
import com.kdh.truedev.user.entity.User;


public class UserMapper {
    public static AccountUpdateRes toUpdateUser(User user){
        return new AccountUpdateRes(
                user.getName(),
                user.getEmail(),
                user.getProfileImage()
        );
    }

}
