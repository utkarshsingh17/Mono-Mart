package com.sb_ecom.backend_ecom.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
