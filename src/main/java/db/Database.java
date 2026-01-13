package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private final Map<String, User> users = new java.util.concurrent.ConcurrentHashMap<>();

    public void addUser(User user) {
        users.put(user.userId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}
