/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.kaku.practice.encrypt;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author 咖枯
 * @version 1.0 2016/9/25
 */
public class DesUtil {
    // 加密后的结果是字节数组，这些被加密后的字节在码表（例如UTF-8 码表）上找不到对应字符，会出现乱码，当乱码字符串再次转换为字节数组时，长度会变化，导致解密失败，所以转换后的数据是不安全的。

    // 使用Base64 对字节数组进行编码，任何字节都能映射成对应的Base64 字符，之后能恢复到字节数组，利于加密后数据的保存于传输，所以转换是安全的。同样，字节数组转换成16 进制字符串也是安全的。

    //秘钥算法
    private static final String KEY_ALGORITHM = "DES";
    //加密算法：algorithm/mode/padding 算法/工作模式/填充模式
    private static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
    private static final String CIPHER_ALGORITHM_CBC = "DES/CBC/PKCS5Padding";
    //秘钥
    private static final String KEY = "12345678";//DES 秘钥长度必须是8 位或以上
    //private static final String KEY = "1234567890123456";//AES 秘钥长度必须是16 位

    public static void main(String args[]) {
        String data = "加密解密";
        System.out.println("加密数据：" + data);
        byte[] encryptData = encrypt(data);
        System.out.println("加密后的数据：" + new String(encryptData));
        System.out.println("加密后的数据经过base64解密：" + new String(Base64.getDecoder().decode(encryptData)));
        byte[] decryptData = decrypt(encryptData);
        System.out.println("解密后的数据：" + new String(decryptData));

    }

    public static byte[] encrypt(String data) {
        //初始化秘钥
        SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);

/*
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);*/


        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encode(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] data) {
        byte[] resultBase64 = Base64.getDecoder().decode(data);

        //初始化秘钥
        SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] result = cipher.doFinal(resultBase64);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] encryptWithCBCMode(String data) {
        //AES、DES 在CBC 操作模式下需要iv 参数
        IvParameterSpec iv = new IvParameterSpec(KEY.getBytes());

        SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptWithCBCMode(byte[] data) {
        //AES、DES 在CBC 操作模式下需要iv 参数
        IvParameterSpec iv = new IvParameterSpec(KEY.getBytes());

        SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
