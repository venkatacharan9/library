package com.library.management.service;

import com.library.management.dto.LoginDto;

public interface AuthService {

    String login(LoginDto loginDto);
}
