package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User testUser1;
    User testUser1LowerCase;
    User testUser2;

    User testUser1Clone;
    User testNullUser;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testUser1 = new User("Bertil");
        testUser1LowerCase = new User("bertil");
        testUser2 = new User("Sven");

        //Used to test if users with same name is equal to each other
        testUser1Clone = new User("Bertil");

        //Used to test null in equal method
        testNullUser = null;
    }

    /**
     * Tests if getName() returns the correct name
     */
    @Test
    void getName() {
        String testName = testUser1.getName();

        assertEquals("Bertil", testName);
        assertNotEquals("Sven", testName);
    }


    /**
     * Tests if equals()
     * returns true when comparing:
     *      - Users with identical names A = B
     *      - Identical Objects A = A
     *
     * returns false when comparing:
     *      - Users with different names
     *      - User with a null value
     *      - User with another class
     */

    @Test
    void testEquals() {

        boolean shouldBeEqualClone = testUser1.equals(testUser1Clone);
        boolean shouldBeEqualReflection = testUser1.equals(testUser1);

        assertTrue(shouldBeEqualClone);
        assertTrue(shouldBeEqualReflection);

        boolean shouldNotBeEqual = testUser1.equals(testUser2);
        boolean upperCompareWithLowerCaseShouldNotBeEqual = testUser1.equals(testUser1LowerCase);
        boolean compareWithNullTestShouldNotBeEqual = testUser1.equals(testNullUser);
        boolean compareWithStringTestShouldNotBeEqual = testUser1.equals("Bertil");


        assertFalse(shouldNotBeEqual);
        assertFalse(upperCompareWithLowerCaseShouldNotBeEqual);
        assertFalse(compareWithNullTestShouldNotBeEqual);
        assertFalse(compareWithStringTestShouldNotBeEqual);

    }


    /**
     * Tests if User has a unique hashCode
     */

    @Test
    void testHashCode() {
        assertNotEquals(testUser1.hashCode(), testUser2.hashCode());
        assertNotEquals(0, testUser1.hashCode());
        assertNotNull(testUser1.hashCode());
    }

    /**
     * Tests if toString() returns the name of the User
     */

    @Test
    void testToString() {
        String testName = testUser1.getName();

        assertEquals("Bertil", testName);
        assertNotEquals("Sven", testName);
    }
}
