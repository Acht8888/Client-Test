package org.example;

public class MyConfig {
    public static String username = "john_doe";
    public static String password = "JohnDoe123";

    static Main main = new Main();

    public static void customFunctions() throws Exception {
        main.handleLogin(username, password);
        main.handleAuthInstant();
    }
}
