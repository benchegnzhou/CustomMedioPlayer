package com.itcast.zbc.mediaplayer.utils;

import java.security.MessageDigest;

/**
 * Created by lenovo on 2016/9/13.
 */
    public class MD5Util {
        /***
         * MD5加密 生成32位md5码
         * @param 待加密字符串
         * @return 返回32位md5码
         */
        public static String md5Encode_32(String inStr) throws Exception {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
                return "";
            }

            byte[] byteArray = inStr.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        }

    /**
     * 16位的MD5加密
     * @param val
     * @return
     * @throws Exception
     */
    public static String md5Encode_16(String val) throws Exception{
               MessageDigest md5 = MessageDigest.getInstance("MD5");
               md5.update(val.getBytes());
             byte[] m = md5.digest();//加密
               return getString(m);
            }
         private static String getString(byte[] b) {
             StringBuffer buf = new StringBuffer();
             for (int i = 0; i < b.length; i++) {
                 int a = b[i];
                 if (a < 0)
                     a += 256;
                 if (a < 16)
                     buf.append("0");
                 buf.append(Integer.toHexString(a));

             }
//                 return buf.toString();  //32位
             return buf.toString().substring(8, 24);    //16位
         }

}
