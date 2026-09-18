package com.mycompany.main;

import java.util.Scanner;

class Login {

    static String storedUser;
    static String storedPassword;

    public static boolean checkUsername(String username) {
        boolean hasUnderScore = false;
        boolean lessThanFive = false;

        if (username.length() < 5) {
            lessThanFive = true;
        }

        if (username.contains("_")) {
            hasUnderScore = true;
        }

        return hasUnderScore && lessThanFive;
    }

    public static boolean checkPasswordComplexity(String password) {
        boolean hasDigit = false;
        boolean hasSpecial = false;
        boolean hasCapital = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            }

            if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasSpecial && hasDigit && hasCapital;
    }

    public static boolean checkCellPhoneNumber(String number) {
        boolean isValid = false;
        boolean hasCode = false;

        if (number.length() == 12) {
            isValid = true;
        }

        if (number.startsWith("+27")) {
            hasCode = true;
        }

        return hasCode && isValid;
    }

    public static void registerUser() {
        Scanner input = new Scanner(System.in);

        System.out.println("Please Create Username");
        String user = input.nextLine();

        System.out.println("Please Create a Password");
        String password = input.nextLine();
        
        System.out.println("Please Enter CellPhone Number");
        String number = input.nextLine();

        while (!checkUsername(user)) {
            System.out.println("Username invalid");

            System.out.println("Please Re-create Username");
            user = input.nextLine();
        }
        
        while (!checkPasswordComplexity(password)) {
            System.out.println("Password invalid");
            
            System.out.println("Please Re-create a Password");
            password = input.nextLine();
        }
        
        while (!checkCellPhoneNumber(number)) {
            System.out.println("Cellphone Number invalid");
            
            System.out.println("Please Enter a Valid CellPhone Number");
            number = input.nextLine();
        }
        
        storedUser = user;
        storedPassword = password;
        System.out.println("You have been successfully been registered");
    }

    public static void loginUser() {
        boolean hasLoggedIn = false;

        System.out.println("Login to your Chat App");
        System.out.println("---------------------------------------------------");
        Scanner input = new Scanner(System.in);

        System.out.println("Please Enter Your Username");
        String user = input.nextLine();

        System.out.println("Please Enter Your Password");
        String enterPass = input.nextLine();

        do {
            if (user.equals(storedUser) && enterPass.equals(storedPassword)) {
                System.out.println("Welcome to Your Chatapp");
                hasLoggedIn = true;
            } else {
                System.out.println("Login unsuccessful");
                System.out.println("Please Enter Your Username");
                user = input.nextLine();
                System.out.println("Please Enter Your Password");
                enterPass = input.nextLine();
            }
        } while (!hasLoggedIn);
    }
}

public class Main {

    public static void main(String[] args) {

        Login.registerUser();
        Login.loginUser();
    }
}
