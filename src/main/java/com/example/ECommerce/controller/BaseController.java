package com.example.ECommerce.controller;

import com.example.ECommerce.util.KeycloakUtil;
import com.example.ECommerce.service.KeycloakUserService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {
    
    @Autowired
    protected KeycloakUserService keycloakUserService;

    protected String getCurrentUserId() {
        return KeycloakUtil.getCurrentUser().getId();
    }


    protected boolean isCurrentUserB2B() {
        return KeycloakUtil.isCurrentUserB2B();
    }
} 