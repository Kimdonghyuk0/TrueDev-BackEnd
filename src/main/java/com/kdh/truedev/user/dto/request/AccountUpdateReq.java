package com.kdh.truedev.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AccountUpdateReq", description = "회원 정보 수정 요청")
public record AccountUpdateReq(
        @Size(min = 2, max = 20)
        @Nullable
        String name,

        @Email @NotBlank
        @Schema(example = "user@example.com")
        @Nullable
        String email
) {}
