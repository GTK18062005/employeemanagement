package com.staffmanagement.model;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    PROJECT_MANAGER("ROLE_PROJECT_MANAGER"),
    STAFF("ROLE_STAFF");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String toAuthority() {
        return authority;
    }

    public static UserRole fromAuthority(String authority) {
        for (UserRole role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role authority: " + authority);
    }
}
