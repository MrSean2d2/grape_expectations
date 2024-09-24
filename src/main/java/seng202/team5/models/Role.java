package seng202.team5.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
