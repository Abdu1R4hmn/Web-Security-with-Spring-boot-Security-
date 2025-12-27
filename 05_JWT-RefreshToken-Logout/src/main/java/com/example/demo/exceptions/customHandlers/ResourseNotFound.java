package com.example.demo.exceptions.customHandlers;

import com.example.demo.exceptions.BussinessException;

public class ResourseNotFound extends BussinessException {

    public ResourseNotFound(String resourse) {
        super(resourse + " Not Found");
    }
}
