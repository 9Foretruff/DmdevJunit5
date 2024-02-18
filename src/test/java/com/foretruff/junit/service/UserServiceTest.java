package com.foretruff.junit.service;

import com.foretruff.junit.dto.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {
    // ctrl + alt + v || ctrl + alt + c
    private static final User IVAN = User.of(1, "Ivan", "777");
    private static final User VASYA = User.of(2, "Vasya", "123");
    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("Before all:");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this.toString());
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this.toString());
        var users = userService.getAll();

        assertTrue(users.isEmpty(), () -> "User list should be empty");
        // input -> [box == func] -> actual output
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this.toString());
        userService.add(IVAN);
        userService.add(VASYA);

        var users = userService.getAll();

        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    void loginFailedIfPasswordIsNotCorrect() {
        userService.add(IVAN);

        var maybeUser = userService.login(IVAN.getUsername(), "oops");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailedIfUserDoesNotExist() {
        userService.add(IVAN);

        var maybeUser = userService.login("oops", "oops");

        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this.toString());
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("After all: ");
    }

}
