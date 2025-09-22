package com.sb_ecom.backend_ecom.service;


import com.sb_ecom.backend_ecom.payload.AuthenticationResult;
import com.sb_ecom.backend_ecom.payload.UserResponse;
import com.sb_ecom.backend_ecom.security.request.LoginRequest;
import com.sb_ecom.backend_ecom.security.request.SignupRequest;
import com.sb_ecom.backend_ecom.security.response.MessageResponse;
import com.sb_ecom.backend_ecom.security.response.UserInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<MessageResponse> register(SignupRequest signUpRequest);

    UserInfoResponse getCurrentUserDetails(Authentication authentication);

    ResponseCookie logoutUser();

    UserResponse getAllSellers(Pageable pageable);
}
