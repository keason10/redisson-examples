package com.hw.redis.keason10resubmit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);
    private MD5Utils() {
    }

    public static String getMd5Val(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            String md5Val = new BigInteger(1, m.digest()).toString(16);
            return md5Val;
        } catch (NoSuchAlgorithmException e) {
            logger.error("org.redisson.example.locks.util.MD5Utils.getMd5Val error", e);
            return "";
        }
    }


}
