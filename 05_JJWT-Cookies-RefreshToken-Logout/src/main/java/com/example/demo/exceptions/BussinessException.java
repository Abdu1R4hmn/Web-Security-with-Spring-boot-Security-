package com.example.demo.exceptions;

public abstract class BussinessException extends RuntimeException {

    protected BussinessException(String message){
        super(message);
    }
}
