package com.carson.quicker.codec;


import com.carson.quicker.logger.QLogger;
import com.carson.quicker.utils.Base64;
import com.carson.quicker.utils.QStrings;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by carson on 2018/3/9.
 */

public class QAESCoder {


    // AESCoder uses CBC and PKCS7Padding，如果使用ECB（电码本）模式，存在加密数据被破解的风险
    static final String AES_CBC = "AES/CBC/PKCS7Padding";
    static final String AES_ECB = "AES/ECB/PKCS5Padding";

    // AESCoder uses SHA-256 (and so a 256-bit key)
    private static final String HASH_ALGORITHM = "SHA-256";

    // AESCoder-ObjC uses blank IV (not the best security, but the aim here is
    // compatibility)
    private static final byte[] ivBytes = {0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36,
            0x30, 0x37, 0x30, 0x38};

    private QAESCoder() {
    }

    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    private static SecretKeySpec generateKey(final String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    /**
     * Encrypt and encode message using 256-bit AES with key generated from
     * password.
     *
     * @param password used to generated key
     * @param message  the thing you want to encrypt assumed String UTF-8
     * @return Base64 encoded CipherText
     * @throws GeneralSecurityException if problems occur during encryption
     */
    public static String encrypt(final String password, String message) {

        try {
            final SecretKeySpec key = generateKey(password);
            byte[] cipherText = encrypt(key, ivBytes, message.getBytes(QStrings.UTF_8));
            // NO_WRAP is important as was getting \n at the end
            // String encoded = Base64.encodeToString(cipherText, Base64.NO_WRAP);
            String encoded = Base64.encode(cipherText);
            return encoded;
        } catch (UnsupportedEncodingException e) {
        } catch (GeneralSecurityException e) {
        }
        return QStrings.EMPTY;
    }

    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key     AES key typically 128, 192 or 256 bit
     * @param iv      Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    private static byte[] encrypt(final SecretKeySpec key, final byte[] iv, final byte[] message)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_CBC);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(message);

        return cipherText;
    }

    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from
     * password
     *
     * @param password                used to generated key
     * @param base64EncodedCipherText the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    public static String decrypt(final String password, String base64EncodedCipherText) {

        try {
            final SecretKeySpec key = generateKey(password);
            byte[] decodedCipherText = Base64.decode(base64EncodedCipherText);
            byte[] decryptedBytes = decrypt(key, ivBytes, decodedCipherText);
            String message = new String(decryptedBytes, QStrings.UTF_8);
            return message;
        } catch (UnsupportedEncodingException e) {
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return QStrings.EMPTY;
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    private static byte[] decrypt(final SecretKeySpec key, final byte[] iv, final byte[] decodedCipherText)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_CBC);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        return decryptedBytes;
    }


    ////////下面的模式是ECB模式:固定内容加密出来的结果是不改变的

    /*****************************************************
     * AES加密
     * @param content 加密内容
     * @param password 加密密码，由字母或数字组成
    此方法使用AES-128-ECB加密模式，key需要为16位
    加密解密key必须相同，如：abcd1234abcd1234
     * @return 加密密文
     ****************************************************/

    public static String encodeECB(String password, String content) {
        if (password == null || "".equals(password)) {
            return null;
        }
        if (password.length() != 16) {
            QLogger.error("encodeECB password length!=16");
            return null;
        }
        try {
            byte[] raw = password.getBytes();  //获得密码的字节数组
            SecretKeySpec skey = new SecretKeySpec(raw, "AES"); //根据密码生成AES密钥
            Cipher cipher = Cipher.getInstance(AES_ECB);  //根据指定算法ALGORITHM自成密码器
            cipher.init(Cipher.ENCRYPT_MODE, skey); //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥
            byte[] byte_content = content.getBytes("utf-8"); //获取加密内容的字节数组(设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] encode_content = cipher.doFinal(byte_content); //密码器加密数据
            return Base64.encode(encode_content); //将加密后的数据转换为字符串返回
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decodeECB(String password, String content) {
        if (password == null || "".equals(password)) {
            return null;
        }
        if (password.length() != 16) {
            QLogger.error("decodeECB password length!=16");
            return null;
        }
        try {
            byte[] raw = password.getBytes();  //获得密码的字节数组
            SecretKeySpec skey = new SecretKeySpec(raw, "AES"); //根据密码生成AES密钥
            Cipher cipher = Cipher.getInstance(AES_ECB);  //根据指定算法ALGORITHM自成密码器
            cipher.init(Cipher.DECRYPT_MODE, skey); //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥
            byte[] encode_content = Base64.decode(content); //把密文字符串转回密文字节数组
            byte[] byte_content = cipher.doFinal(encode_content); //密码器解密数据
            return new String(byte_content, "utf-8"); //将解密后的数据转换为字符串返回
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}


