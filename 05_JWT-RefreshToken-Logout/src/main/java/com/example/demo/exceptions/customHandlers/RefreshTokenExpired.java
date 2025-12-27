package com.example.demo.exceptions.customHandlers;

import com.example.demo.exceptions.BussinessException;

public class RefreshTokenExpired extends BussinessException {
    public RefreshTokenExpired() {
        super("Invalid Refresh Token!");
    }
}
