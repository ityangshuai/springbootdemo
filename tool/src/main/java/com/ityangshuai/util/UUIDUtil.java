package com.ityangshuai.util;

import java.util.UUID;

/**
 * 
 * UUID工具类
 * 
 * @author Gejiankui
 */
public class UUIDUtil
{
    public static String generateUUID()
    {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0,8) + uuid.substring(9,13) + uuid.substring(14,18) + uuid.substring(19,23) + uuid.substring(24);
    }
}
