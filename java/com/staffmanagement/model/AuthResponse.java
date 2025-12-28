// AuthResponse.java
package com.staffmanagement.model;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String name;
    
    // Constructors
    public AuthResponse() {}
    public AuthResponse(String token, String username, String role, String name) {
        this.setToken(token);
        this.setUsername(username);
        this.setRole(role);
        this.setName(name);
    }
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    // Getters and Setters
    // ... (generate all getters and setters)
}