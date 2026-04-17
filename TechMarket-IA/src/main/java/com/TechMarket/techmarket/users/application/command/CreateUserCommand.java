package com.techmarket.techmarket.users.application.command;

public record CreateUserCommand(String firstName, String lastName, String email, String status) {}
