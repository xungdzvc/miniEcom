package com.web.enums;

public enum Role{
    USER("USER"),
    STAFF("STAFF"),
    ADMIN("ADMIN");

    public final String value;

    Role(String value) { this.value = value; }
    public String roleName() {
        return this.value;
    }

}