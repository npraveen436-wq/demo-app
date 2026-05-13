package com.demo;

public class App {
    public String greet(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, stranger";
        }
        return "Hello, " + name;
    }

    public static void main(String[] args) {
        System.out.println(new App().greet("World"));
    }
}
