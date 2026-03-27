package com.architecturedays.day002.service;

import java.util.List;

public interface UserService {
    List<?> findAllUsers();
    long getPayloadSize();
}
