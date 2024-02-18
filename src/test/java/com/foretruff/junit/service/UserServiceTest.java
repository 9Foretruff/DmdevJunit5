package com.foretruff.junit.service;

import com.foretruff.junit.dto.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {
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
        userService.add(new User());
        userService.add(new User());

        var users = userService.getAll();

        assertEquals(2, users.size());
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
