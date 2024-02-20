package com.supplychain.testapp;

public class ClassFile {
    public static void /* ClassFile */ from(byte[] bytes) {
        System.out.println("Debug printing the input bytes:");
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
    }
}
