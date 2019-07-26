package com.fdeight.miscellaneous.simple;

public class Simple {

    public static void main(final String[] args) {
        System.out.println("Simple");
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MIN_VALUE);
        System.out.println(Math.abs(Integer.MIN_VALUE));
        System.out.println();
        System.out.println(Integer.toBinaryString(1));
        System.out.println(Integer.toBinaryString(-1));
        System.out.println(Integer.toBinaryString(-0));
        System.out.println();
        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println(Integer.toBinaryString(Integer.MIN_VALUE));
        System.out.println(Integer.toBinaryString(-Integer.MAX_VALUE));
        //System.out.println(Integer.toBinaryString(-Integer.MIN_VALUE));
    }
}
