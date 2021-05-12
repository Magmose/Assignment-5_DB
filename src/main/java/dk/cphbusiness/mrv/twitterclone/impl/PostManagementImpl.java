package dk.cphbusiness.mrv.twitterclone.impl;

import dk.cphbusiness.mrv.twitterclone.contract.PostManagement;
import dk.cphbusiness.mrv.twitterclone.dto.Post;
import dk.cphbusiness.mrv.twitterclone.util.Time;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostManagementImpl implements PostManagement {
    private Jedis jedis;
    private Time time;

    public PostManagementImpl(Jedis jedis, Time time) {
        this.jedis = jedis;
        this.time = time;
    }

    @Override
    public boolean createPost(String username, String message) {
        var response = jedis.sismember("users", username);
        if (!response) return false;
        jedis.zadd(username+":tweets",time.getCurrentTimeMillis(),message);
        return true;
    }

    @Override
    public List<Post> getPosts(String username) {
        var posts = jedis.zrange(username+":tweets",0,-1);
        return posts.stream().map(post -> {
            Post p = new Post(100,post);
            return p;
        }).collect(Collectors.toList());

        }

    @Override
    public List<Post> getPostsBetween(String username, long timeFrom, long timeTo) {
        var posts = jedis.zrange(username+":tweets",timeFrom,timeTo);
        return posts.stream().map(post -> {
            Post p = new Post(100,post);
            return p;
        }).collect(Collectors.toList());
    }
}
