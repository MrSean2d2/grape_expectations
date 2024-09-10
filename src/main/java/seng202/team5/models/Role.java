package seng202.team5.models;

public class Role {
    private boolean isAdmin = false;

    public void toggleAdmin() {
        if (isAdmin) {
            isAdmin = false;
        } else {
            isAdmin = true;
        }
    }
    public boolean AdminQuery() {
        if (isAdmin) {
            return true;
        } else {
            return false;
        }
    }

}
