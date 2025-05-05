package com.pasang.projectarchiver.exception.custom;

public class InvalidPhoneFormat extends RuntimeException{
    public InvalidPhoneFormat(String message){
        super(message);
    }
}
