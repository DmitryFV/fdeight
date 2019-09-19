package com.fdeight.miscellaneous.simple;

public class FizzBuzz {
    public static void main(final String[] args) {
        for (int number = 1; number <= 100; number++) {
            if (number % 3 == 0 || number % 5 == 0) {
                if (number % 3 == 0 && number % 5 == 0) {
                    System.out.println("FizzBuzz");
                } else if (number % 3 == 0) {
                    System.out.println("Fizz");
                } else {
                    System.out.println("Buzz");
                }
            } else {
                System.out.println(number);
            }
        }
    }
}
