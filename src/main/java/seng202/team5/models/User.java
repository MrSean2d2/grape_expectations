package seng202.team5.models;

import java.util.Objects;

public class User {
    private int id;
    private String username = null;
    private String password = null;
    private String role = "user";

    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public int getId() {
    return id;
}
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }
    public boolean getIsAdmin() {
        return (Objects.equals(role, "admin"));
    }
}
