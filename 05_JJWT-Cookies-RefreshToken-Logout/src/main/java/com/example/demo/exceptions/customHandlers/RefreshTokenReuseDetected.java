package com.example.demo.exceptions.customHandlers;

import com.example.demo.exceptions.BussinessException;

public class RefreshTokenReuseDetected extends BussinessException {

    public RefreshTokenReuseDetected() {
        super("Invalid refresh token");
    }
}
