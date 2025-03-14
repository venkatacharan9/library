package com.library.management.controller;

import com.library.management.dto.JwtResponse;
import com.library.management.dto.LoginDto;
import com.library.management.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto) {

        String token = authService.login(loginDto);

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
}