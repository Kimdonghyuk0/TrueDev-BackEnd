package com.kdh.truedev.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;


@Schema(name = "UserReq", description = "사용자 응답")
public record UserReq(
        @Email @NotBlank
        @Schema(example = "user@example.com")
        String email,
        @NotBlank @Size(min = 2, max = 20)
        String name,
        String password
) {}