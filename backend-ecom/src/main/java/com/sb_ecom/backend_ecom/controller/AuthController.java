package com.sb_ecom.backend_ecom.controller;


import com.sb_ecom.backend_ecom.config.AppConstants;
import com.sb_ecom.backend_ecom.payload.AuthenticationResult;
import com.sb_ecom.backend_ecom.security.request.LoginRequest;
import com.sb_ecom.backend_ecom.security.request.SignupRequest;
import com.sb_ecom.backend_ecom.security.request.TokenRefreshRequest;
import com.sb_ecom.backend_ecom.security.response.MessageResponse;
import com.sb_ecom.backend_ecom.security.response.TokenRefreshResponse;
import com.sb_ecom.backend_ecom.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthenticationResult result = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.getJwtCookie().toString())
                .header(HttpHeaders.SET_COOKIE, result.getRefreshTokenCookie().toString())
                .body(result.getResponse());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.register(signUpRequest);
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        return ResponseEntity.ok().body(authService.getCurrentUserDetails(authentication));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie jwtCookie = authService.logoutUser();
        ResponseCookie refreshTokenCookie = authService.getCleanRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);

        // Generate cookie with new access token
        ResponseCookie jwtCookie = ResponseCookie.from("springBootEcom", response.getAccessToken())
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .secure(false)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    @GetMapping("/sellers")
    public ResponseEntity<?> getAllSellers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber) {

        Sort sortByAndOrder = Sort.by(AppConstants.SORT_USERS_BY).descending();
        Pageable pageDetails = PageRequest.of(pageNumber ,
                Integer.parseInt(AppConstants.PAGE_SIZE), sortByAndOrder);

        return ResponseEntity.ok(authService.getAllSellers(pageDetails));
    }

}

