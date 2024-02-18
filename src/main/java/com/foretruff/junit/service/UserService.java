package com.foretruff.junit.service;

import com.foretruff.junit.dto.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class UserService {

    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User... users) {
        return this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }
        return users
                .stream()
                .filter(user -> user.getUsername().equals(username)
                                && user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users
                .stream()
                .collect(toMap(User::getId, Function.identity()));
    }
}
