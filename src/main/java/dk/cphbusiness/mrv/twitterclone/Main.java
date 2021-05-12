package dk.cphbusiness.mrv.twitterclone;

import dk.cphbusiness.mrv.twitterclone.impl.PostManagementImpl;
import dk.cphbusiness.mrv.twitterclone.impl.UserManagementImpl;
import dk.cphbusiness.mrv.twitterclone.util.TimeImpl;
import redis.clients.jedis.Jedis;


public class Main {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 6379;

        Jedis jedis = new Jedis(host, port);
        TimeImpl time = new TimeImpl();

        PostManagementImpl postManagement = new PostManagementImpl(jedis,time);
        UserManagementImpl userManagement = new UserManagementImpl(jedis);

        /*
        Your task is to fill out the PostManagementImpl and UserManagementImpl classes.
        You must satisfy the unit tests, in order to complete it.

        Run the unit tests by right clicking the Java folder under Test,
        and choose Run All Tests (Ctrl+Shift+F10)
         */
    }

}
