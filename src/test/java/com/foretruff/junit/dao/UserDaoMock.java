package com.foretruff.junit.dao;

import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDaoMock extends UserDao { // userDao не должен быть FINAL!!!
    private Map<Integer, Boolean> userAnswers = new HashMap<>();
    private Answer1<Integer,Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
        return userAnswers.getOrDefault(userId, false);
    }
}
