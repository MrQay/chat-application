package utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user in the chat application.
 * The User have a username and a hashCode
 *
 * @author Baseer
 */
public class User implements Serializable {
    String name;

    private int hashCode;

    /**
     * @pre  name != null
     * @param name the users name
     * @post Creates an instance of User with given name as User.name
     *       Creates a Hashcode of that name.
     *
     */
    public User(String name) {
        this.name = Objects.requireNonNull(name);
        this.hashCode = Objects.hashCode(name) * 31;

    }

    /**
     * @pre None
     * @return the Users name.
     * @post name is unchanged.
     */
    public String getName() {
        return name;
    }

    /** Checks for equality between this user and another object.
     * @pre other != null
     * @param other the object to compare to this.
     * @return True if the other object is a User with the same name as this one; false otherwise.
     * @post other & this is unchanged
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || other.getClass() != this.getClass()) return false;

        User o = (User)other;
        if(o.hashCode != this.hashCode) return false;
        return o.getName().equals(this.name);
    }

    /**
     * @pre None
     * @return returns the hashcode for this user.
     * @post the hashCode is unchanged
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * @pre None
     * @return returns the name for this user.
     * @post the name is unchanged
     */
    @Override
    public String toString() {
        return this.name;
    }

}
