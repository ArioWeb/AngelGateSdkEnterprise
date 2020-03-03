package com.angelsgate.sdk.AngelsGateUtils;


import android.content.Context;

import com.angelsgate.sdk.AngelsGateUtils.prefs.AngelGatePreferencesHelper;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;


public class RandomUtils {


    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {


            SecureRandom rand = new SecureRandom();
            int character = rand.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }


    public static int random(int Min, int Max) {



        SecureRandom rand = new SecureRandom();
        return (rand.nextInt((Max - Min) + 1) + Min);
    }


    public static int randomNumByDigit(int digits) {
        int minimum = (int) Math.pow(10, digits - 1); // minimum value with 2 digits is 10 (10^1)
        int maximum = (int) Math.pow(10, digits) - 1; // maximum value with 2 digits is 99 (10^2 - 1)
        return random(minimum, maximum);
    }


    public static String CreatSsalt() {
        int Lenght = random(AngelGateConstants.MintLengthSsalt, AngelGateConstants.MaxLengthSsalt);
        return randomAlphaNumeric(Lenght - 1);
    }


    public static synchronized int CreatSegment(Context ctx) {
        AtomicInteger SegmentAtomic = new AtomicInteger(AngelGatePreferencesHelper.getSegment(ctx));
        int segment = SegmentAtomic.incrementAndGet();
        AngelGatePreferencesHelper.setSegment(segment, ctx);
        return segment;
    }


}
