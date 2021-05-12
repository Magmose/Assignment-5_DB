package dk.cphbusiness.mrv.twitterclone.impl;

import dk.cphbusiness.mrv.twitterclone.contract.UserManagement;
import dk.cphbusiness.mrv.twitterclone.dto.UserCreation;
import dk.cphbusiness.mrv.twitterclone.dto.UserOverview;
import dk.cphbusiness.mrv.twitterclone.dto.UserUpdate;
import dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserManagementImpl implements UserManagement {
    //127.0.0.1:6379
    private Jedis jedis;

    public UserManagementImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public boolean createUser(UserCreation userCreation) {
        var response = jedis.sismember("users", userCreation.username);
        if (response) return false;
        try (var tran = jedis.multi()) {
            tran.sadd("users", userCreation.username);
            //Tilføj følger til set
            //tran.sadd("numFollowers:");
            //tran.sadd("numFollowing:");
            Map<String, String> map = Map.of("firstname", userCreation.firstname,
                    "lastname", userCreation.lastname,
                    "birthday", userCreation.birthday,
                    "passwordHash", userCreation.passwordHash,
                    "numFollowers", "0",
                    "numFollowing", "0");
            tran.hset("user:" + userCreation.username, map);
            tran.exec();
        }

        return true;

    }

    @Override
    public UserOverview getUserOverview(String username) {
        var response = jedis.sismember("users", username);
        if (!response) return null;
        UserOverview userOverview = new UserOverview();
        List<String> l = jedis.hmget("user:" + username, "firstname", "lastname", "numFollowers", "numFollowing");
        userOverview.firstname = l.get(0);
        userOverview.lastname = l.get(1);
        userOverview.numFollowers = Integer.parseInt(l.get(2));
        userOverview.numFollowing = Integer.parseInt(l.get(3));

        return userOverview;
    }

    @Override
    public boolean updateUser(UserUpdate userUpdate) {
        var response = jedis.sismember("users", userUpdate.username);
        if (!response) return false;
        try (var tran = jedis.multi()) {
            Map<String, String> map = new HashMap<>();
            if(userUpdate.firstname!=null)map.put("firstname",userUpdate.firstname);
            if(userUpdate.lastname!=null)map.put("lastname",userUpdate.lastname);
            if(userUpdate.birthday!=null)map.put("birthday",userUpdate.birthday);
            tran.hset("user:" + userUpdate.username, map);
            tran.exec();
        }
        return true;
    }

    @Override
    public boolean followUser(String username, String usernameToFollow) {
        var response = jedis.sismember("users", usernameToFollow);
        if (!response) return false;
        var response1 = jedis.sismember("users", username);
        if (!response1) return false;
        try (var tran = jedis.multi()) {
            tran.sadd(username + ":following", usernameToFollow);
            tran.hincrBy("user:"+username,"numFollowing",1);
            tran.sadd(usernameToFollow+":followed", username);
            tran.hincrBy("user:"+usernameToFollow,"numFollowers",1);
            tran.exec();
        }

        return true;
    }

    @Override
    public boolean unfollowUser(String username, String usernameToUnfollow) {
        var response = jedis.sismember("users", username);
        if (!response) return false;

        try (var tran = jedis.multi()) {
            tran.srem(username + ":following", usernameToUnfollow);
            tran.hincrBy("user:"+username,"numFollowing",-1);
            tran.srem(usernameToUnfollow+":followed", username);
            tran.hincrBy("user:"+usernameToUnfollow,"numFollowers",-1);
            tran.exec();
        }

        return true;
    }

    @Override
    public Set<String> getFollowedUsers(String username) {
        var response = jedis.sismember("users", username);
        if (!response) return null;
        return jedis.smembers(username+":following");
    }

    @Override
    public Set<String> getUsersFollowing(String username) {
        var response = jedis.sismember("users", username);
        if (!response) return null;
        return jedis.smembers(username+":followed");
    }

}
