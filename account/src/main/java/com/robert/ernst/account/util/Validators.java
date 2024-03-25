package com.robert.ernst.account.util;

import com.robert.ernst.account.dto.Account;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validators {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^\\+[1-9]\\d{1,14}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
