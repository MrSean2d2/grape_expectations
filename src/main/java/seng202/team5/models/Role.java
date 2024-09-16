package seng202.team5.models;

/**
 * Class to record user role information. At this stage this only consists of
 * keeping track of whether the user is an admin or not.
 *
 * @author Finn Brown
 * @author Sean Reitsma
 */
public class Role {
    private boolean isAdmin = false;

    /**
     * Toggle's the admin status of the role.
     */
    public void toggleAdmin() {
        isAdmin = !isAdmin;
    }

    /**
     * Returns true if the user is an admin.
     *
     * @return whether the user is an admin
     */
    public boolean adminQuery() {
        return isAdmin;
    }

}
