package seng202.team5.models;

import java.util.Arrays;

/**
 * An enum to represent role strings in a way that is easier to verify.
 *
 * @author Sean Reitsma
 */
public enum Role {
    ADMIN("admin"),
    USER("user");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

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
