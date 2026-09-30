package com.web.logging;

import java.util.UUID;
import java.util.regex.Pattern;

public class RequestIdUtil {
    private static final int MAX_LENGTH = 64;
    private static final Pattern VALID_REQUEST_ID = Pattern.compile("^[a-zA-Z0-9._-]+$");

    private RequestIdUtil(){

    }

    public static String resolve(String requestId){
        if(requestId == null || requestId.isBlank()|| requestId.length() > MAX_LENGTH || !VALID_REQUEST_ID.matcher(requestId).matches()){
            return UUID.randomUUID().toString();
        }
        return requestId;
    }

}
