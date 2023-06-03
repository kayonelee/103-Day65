package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserDatabase userDatabase;

    private UserService userService;
    private User testUser;

//TEST SETUP
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService();
        userService.setUserDatabase(userDatabase);
        testUser = new User("testUser", "testPassword", "test@google.com");
    }

// POSITIVE CASE: REGISTERING USER FOR THE FIRST TIME
    @Test
    public void testRegisterUser_Success() {
        when(userDatabase.containsKey(testUser.getUsername())).thenReturn(false); //MOCK BEHAVIOR. WHEN METHOD CALL-RETURN FALSE

        boolean result = userService.registerUser(testUser);

        Assertions.assertTrue(result, "User registration should be successful"); //VERIFY REGISTRATION IS SUCCESSFUL
        verify(userDatabase).containsKey(testUser.getUsername());
    }

// POSITIVE CASE: REGISTER USER FOR THE FIRST TIME, THEN REGISTERING THE SAME USER AGAIN
    @Test
    public void testRegisterUser_Failure() {
        when(userDatabase.containsKey(testUser.getUsername())).thenReturn(false, true); //MOCK BEHAVIOR. WHEN METHOD CALL-RETURN FALSE THEN TRUE

        boolean result = userService.registerUser(testUser);
        Assertions.assertTrue(result, "User registration successful"); //VERIFY SUCCESS

        boolean duplicateResult = userService.registerUser(testUser);
        Assertions.assertFalse(duplicateResult, "User registration fail-duplicate user"); //VERIFY FAILURE

        verify(userDatabase, times(2)).containsKey(testUser.getUsername());
    }

// POSITIVE CASE: REGISTER USER AND LOGGING IN WITH CORRECT CREDENTIALS
    @Test
    public void testLoginUser_Success() {
        when(userDatabase.get(testUser.getUsername())).thenReturn(testUser); //MOCK BEHAVIOR. WHEN METHOD CALL RETURN TESTUSER OBJECT

        User loggedInUser = userService.loginUser(testUser.getUsername(), testUser.getPassword());
        Assertions.assertNotNull(loggedInUser, "User login successful");  //VERIFY VARIABLE IS NOT NULL, LOGIN SUCCESSFUL

        verify(userDatabase).get(testUser.getUsername());
    }

// POSITIVE CASE: REGISTER USER AND LOGGING IN WITH WRONG USERNAME (NO MOCKING)
    @Test
    public void testLoginUser_WrongUsername() {
        User loggedInUser = userService.loginUser("wrongUsername", testUser.getPassword());
        Assertions.assertNull(loggedInUser, "User login fail- wrong username"); //VERIFY FAILURE DUE TO WRONG USERNAME

        verify(userDatabase).get("wrongUsername");
    }

// POSITIVE CASE: REGISTER USER AND LOGGING IN WITH WRONG PASSWORD
    @Test
    public void testLoginUser_WrongPassword() {
        when(userDatabase.get(testUser.getUsername())).thenReturn(testUser);  ///MOCK BEHAVIOR. WHEN METHOD CALL RETURN TESTUSER OBJECT

        User loggedInUser = userService.loginUser(testUser.getUsername(), "wrongPassword");
        Assertions.assertNull(loggedInUser, "User login fail- wrong password"); //VERIFY FAILURE DUE TO WRONG PW

        verify(userDatabase).get(testUser.getUsername());
    }

// POSITIVE CASE: REGISTER USER AND UPDATING PROFILE WITH NEW USERNAME, PW, EMAIL
    @Test
    public void testUpdateUserProfile_Success() {
        when(userDatabase.containsKey(testUser.getUsername())).thenReturn(false);

        boolean result = userService.updateUserProfile(testUser, "somethingUsername", "Password123", "hello@google.com");
        Assertions.assertTrue(result, "User profile updated successfully");

        verify(userDatabase).containsKey("newUsername");
        verify(userDatabase).put("newUsername", testUser);
    }

// POSITIVE CASE: REGISTER USER AND UPDATE PROFILE WITH A NEW USERNAME THAT ALREADY EXISTS
    @Test
    public void testUpdateUserProfile_Failure() {
        when(userDatabase.containsKey(testUser.getUsername())).thenReturn(false, true);

        boolean result = userService.updateUserProfile(testUser, testUser.getUsername(), "Password123", "hello@google.com");
        Assertions.assertFalse(result, "User profile update fail-duplicate username");

        verify(userDatabase, times(2)).containsKey(testUser.getUsername());
        verify(userDatabase, never()).put(anyString(), any(User.class));
    }

// POSITIVE CASE: REGISTER USER AND UPDATE PROFILE WITH EMPTY VALUES FOR USERNAME, PW, EMAIL
    @Test
    public void testUpdateUserProfile_EdgeCase() {
        when(userDatabase.containsKey(testUser.getUsername())).thenReturn(false);

        boolean result = userService.updateUserProfile(testUser, "", "", "");
        Assertions.assertTrue(result, "User profile updated with empty values");

        verify(userDatabase).containsKey("");
        verify(userDatabase).put("", testUser);
    }
}



//
//CODE WITHOUT MOCKING OBJECTS (TEST-SUCCESSFUL)
//package org.example;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class UserServiceTest {
//    private UserService userService;
//    private User testUser;
//
//    //TEST SETUP
//    @BeforeEach
//    public void setUp() {
//        userService = new UserService();
//        testUser = new User("testUser", "testPassword", "test@example.com");
//    }
//
//    // POSITIVE CASE: REGISTERING USER FOR THE FIRST TIME
//    @Test
//    public void testRegisterUser_Success() {
//        boolean result = userService.registerUser(testUser);
//        Assertions.assertTrue(result, "User registration successful");
//    }
//
//    // POSITIVE CASE: REGISTER USER FOR THE FIRST TIME, THEN REGISTERING THE SAME USER AGAIN
//    @Test
//    public void testRegisterUser_Failure() {
//        boolean result = userService.registerUser(testUser);
//        Assertions.assertTrue(result, "User registration successful");
//
//        boolean duplicateResult = userService.registerUser(testUser);
//        Assertions.assertFalse(duplicateResult, "User registration fail-duplicate user");
//    }
//
//    // POSITIVE CASE: REGISTER USER AND LOGGING IN WITH CORRECT CREDENTIALS
//    @Test
//    public void testLoginUser_Success() {
//
//        userService.registerUser(testUser);
//
//        User loggedInUser = userService.loginUser(testUser.getUsername(), testUser.getPassword());
//        Assertions.assertNotNull(loggedInUser, "User login successful");
//    }
//
//    // POSITIVE CASE: REGISTER USER AND LOGGING IN WITH WRONG USERNAME
//    @Test
//    public void testLoginUser_WrongUsername() {
//        userService.registerUser(testUser);
//
//        User loggedInUser = userService.loginUser("wrongUsername", testUser.getPassword());
//        Assertions.assertNull(loggedInUser, "User login fail- wrong username");
//
//    }
//
//    // POSITIVE CASE: REGISTER USER AND LOGGING IN WITH WRONG PASSWORD
//    @Test
//    public void testLoginUser_WrongPassword() {
//        userService.registerUser(testUser);
//
//        User loggedInUser = userService.loginUser(testUser.getUsername(), "wrongPassword");
//        Assertions.assertNull(loggedInUser, "User login fail -wrong password");
//    }
//    // POSITIVE CASE: REGISTER USER AND UPDATING PROFILE WITH NEW USERNAME, PW, EMAIL
//    @Test
//    public void testUpdateUserProfile_Success() {
//        userService.registerUser(testUser);
//
//        boolean result = userService.updateUserProfile(testUser, "newUsername", "Password123", "hello@google.com");
//        Assertions.assertTrue(result, "User profile updated successfully");
//    }
//
//    // POSITIVE CASE: REGISTER USER AND UPDATE PROFILE WITH A NEW USERNAME THAT ALREADY EXISTS
//    @Test
//    public void testUpdateUserProfile_Failure() {
//        userService.registerUser(testUser);
//
//        boolean result = userService.updateUserProfile(testUser, testUser.getUsername(), "Password123", "hello@google.com");
//        Assertions.assertFalse(result, "User profile update fail-duplicate username");
//    }
//
//    // POSITIVE CASE: REGISTER USER AND UPDATE PROFILE WITH EMPTY VALUES FOR USERNAME, PW, EMAIL
//    @Test
//    public void testUpdateUserProfile_EdgeCase() {
//        userService.registerUser(testUser);
//
//        boolean result = userService.updateUserProfile(testUser, "", "", "");
//        Assertions.assertTrue(result, "User profile updated with empty values");
//    }
//}