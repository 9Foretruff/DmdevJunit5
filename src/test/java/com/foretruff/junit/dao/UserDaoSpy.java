package com.foretruff.junit.dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {
    private final UserDao userDao;
    private Map<Integer, Boolean> userAnswers = new HashMap<>();

    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }
//    private Answer1<Integer,Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
        return userAnswers.getOrDefault(userId, userDao.delete(userId));
    }
}
