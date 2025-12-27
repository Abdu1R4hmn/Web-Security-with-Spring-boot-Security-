package com.example.demo.exceptions.customHandlers;

import com.example.demo.exceptions.BussinessException;

public class EmailAlreadyExists extends BussinessException {

    public EmailAlreadyExists(String email) {
        super("Email Already Exits: "+email);
    }
}
