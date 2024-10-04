package org.clc.common.utils;

import org.clc.common.constant.MessageConstant;


import java.util.concurrent.ThreadLocalRandom;

/**
 * @version 1.0
 * @description: TODO
 */
public class MyRandomStringGenerator {
    private static final int LOWER_BOUND = 0;
    private static final int UPPER_BOUND = 61; // 'A'-'Z', 'a'-'z', '0'-'9'

    /**
     * 生成一个指定长度的随机字符串。
     *
     * @param length 字符串的长度
     * @return 随机生成的字符串
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            int randomIndex = ThreadLocalRandom.current().nextInt(LOWER_BOUND, UPPER_BOUND);
            sb.append(MessageConstant.CHARS_FOR_NAME.charAt(randomIndex));
        }
        return sb.toString();
    }
}
