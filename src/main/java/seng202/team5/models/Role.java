package seng202.team5.models;

import java.util.Arrays;

/**
 * An enum to represent role strings in a way that is easier to verify.
 *
 * @author Sean Reitsma
 */
public enum Role {
    /**
     * The role for admin user accounts.
     */
    ADMIN("admin"),
    /**
     * The role for default user accounts.
     */
    USER("user");

    private final String roleName;

    /**
     * Constructor for Role.
     *
     * @param roleName role
     */
    Role(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Gets role of user.
     *
     * @return role
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Get the first role matching the specified name, defaulting to {@link Role#USER}
     * if not found.
     *
     * @param name the name to match
     * @return the matching role
     */
    public static Role getRoleFromName(String name) {
        return Arrays.stream(values()).filter(
                r -> r.roleName.equals(name)).findFirst().orElse(USER);
    }

}
