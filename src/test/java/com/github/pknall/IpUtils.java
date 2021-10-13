package com.github.pknall;

public class IpUtils {

    public static long createMask(final int length) {
        long l = 0;
        int shift = 31;
        for (int i = 0; i < length; i++) {
            l |= 1L << shift--;
        }
        return l;
    }

    public static void main(String[] args) {
        System.out.println(createMask(10));
    }
}
