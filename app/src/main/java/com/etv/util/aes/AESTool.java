package com.etv.util.aes;

import com.etv.config.ApiInfo;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @ClassName: AESTool
 * @Description: TODO
 * @author: xya
 * @date: 2021年6月24日 下午2:34:27
 */
public class AESTool {

    /***
     * 加密
     * @return
     */
    public static byte[] EncryptString(byte[] message) {
        String key = ApiInfo.getAESCodeKey().trim();
        byte[] f_encrypted = null;
        try {
            byte[] iv = new byte[16];//Initialization vector
            String hexStr = toHex(key.getBytes());
            byte[] encodedKey = toByteArray(hexStr);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            byte[][] pt, encrypted = null;
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            pt = padString(message, 16); //Store plain text in block of 16 bytes
            encrypted = new byte[pt.length][16];
            iv = (new SecureRandom()).generateSeed(16);
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            encrypted[0] = xor(cipher.doFinal(iv), pt[0]);//First block encryption with initialization vector
            for (int i = 1; i < pt.length; i++) {
                encrypted[i] = xor(pt[i], cipher.doFinal(encrypted[i - 1]));//Encrypt block with xor of the previous
            }
            byte[] temp = flatten(encrypted);
            // String 转 byte
//          f_encrypted = new byte[message.getBytes().length];
            f_encrypted = new byte[message.length];
            System.arraycopy(temp, 0, f_encrypted, 0, f_encrypted.length);//Truncate to original length of plain text
            int y = 0;
            byte[] new_encrypted = new byte[f_encrypted.length + 16];
            System.arraycopy(f_encrypted, 0, new_encrypted, 16, f_encrypted.length);
            for (int i = 0; i < 16; i++) {
                new_encrypted[i] = iv[y];
                y++;
            }
            f_encrypted = new_encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f_encrypted;
    }


    /***
     * 解密生成 byte[]
     * @param encrypt
     * @return
     */
    public static byte[] DecryptByteGroup(byte[] encrypt) {
        String key = ApiInfo.getAESCodeKey();
        byte[] f_decrypted = null;
        try {
            int y = 0;
            byte[] iv = new byte[16];
            byte[][] ct = null;
            Cipher cipher = null;
            for (int i = 0; y < 16; i++) {
                iv[y] = encrypt[i];
                y++;
            }
            byte[] message = new byte[encrypt.length - 16];
            System.arraycopy(encrypt, 16, message, 0, encrypt.length - 16);
            ct = padBytes(message, 16); //Store cipher text in block of 16 bytes
            key = key.trim();
            String hexStr = toHex(key.getBytes());
            byte[] encodedKey = toByteArray(hexStr);
            SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            ct = padBytes(message, 16); //Store cipher text in block of 16 bytes
            cipher = Cipher.getInstance("AES/CFB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, originalKey, new IvParameterSpec(iv));
            {
                try {
                    f_decrypted = cipher.doFinal(flatten(ct));
                } catch (IllegalBlockSizeException ex) {
                    Logger.getLogger(AESTool.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BadPaddingException ex) {
                    Logger.getLogger(AESTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            byte[] temp = f_decrypted;
            f_decrypted = new byte[message.length];     // [-101, 3, -24, -13, -127, 57, -3, -42, 88]
            System.arraycopy(temp, 0, f_decrypted, 0, message.length);  //Truncate to original length of plain text
            if (f_decrypted == null) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f_decrypted;
    }

    /***
     * 解密
     * @param encrypt
     * @return
     */
    public static String DecryptString(byte[] encrypt) {
        String deCode = "";
        byte[] f_decrypted = DecryptByteGroup(encrypt);
        if (f_decrypted.length < 2) {
            return deCode;
        }
        for (byte numInfo : f_decrypted) {
            deCode = deCode + Byte.toString(numInfo);
        }
        return deCode;
    }


    private static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static byte[] flatten(byte[][] arr) {
        List<Byte> list = new ArrayList<Byte>();
        for (byte[] arr1 : arr) {
            for (int j = 0; j < arr1.length; j++) {
                list.add(arr1[j]);
            }
        }
        byte[] vector = new byte[list.size()];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = list.get(i);
        }
        return vector;
    }


    /**
     * *
     * Pad cipher text in blocks
     *
     * @param source
     * @param block_size
     * @return
     */
    private static byte[][] padBytes(byte[] source, int block_size) {
        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) block_size)][block_size];
        int len = source.length % block_size;
        int start = 0;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source, start, start + block_size);
            start += block_size;
        }
        if (source.length % block_size != 0) {
            try {
                padWithLen(ret[ret.length - 1], len, block_size - len);
            } catch (ShortBufferException ex) {
                Logger.getLogger(AESTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }


    /**
     * Adds the given number of padding bytes to the data input. The value of
     * the padding bytes is determined by the specific padding mechanism that
     * implements this interface.
     *
     * @param in  the input buffer with the data to pad
     * @param off the offset in <code>in</code> where the padding bytes are
     *            appended
     * @param len the number of padding bytes to add
     * @throws ShortBufferException if <code>in</code> is too small to hold
     *                              the padding bytes
     */
    private static void padWithLen(byte[] in, int off, int len)
            throws ShortBufferException {
        if (in == null) {
            return;
        }
        if ((off + len) > in.length) {
            throw new ShortBufferException("Buffer too small to hold padding");
        }
        byte paddingOctet = (byte) (len & 0xff);
        for (int i = 0; i < len; i++) {
            in[i + off] = paddingOctet;
        }
    }


    /**
     * *
     * Pad String message into byte array blocks.
     *
     * @param block_size
     * @return
     */
    private static byte[][] padString(byte[] source, int block_size) {
        int len = source.length % block_size;
        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) block_size)][block_size];
        int start = 0;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source, start, start + block_size);
            start += block_size;
        }
        if (source.length % block_size != 0) {
            try {
                padWithLen(ret[ret.length - 1], len, block_size - len);
            } catch (ShortBufferException ex) {
                Logger.getLogger(AESTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }


    /**
     * *
     * Encode to hex a byte array
     *
     * @param input- the byte array to be parsed
     * @return the resulting String
     */
    private static String toHex(byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }
        int inputLength = input.length;
        StringBuilder output = new StringBuilder(inputLength * 2);
        for (int i = 0; i < inputLength; i++) {
            int next = input[i] & 0xff;
            if (next < 0x10) {
                output.append("0");
            }
            output.append(Integer.toHexString(next));
        }
        return output.toString();
    }


    /**
     * Xor function for two arrays of bytes
     *
     * @param array_1
     * @param array_2
     * @return
     */
    private static byte[] xor(byte[] array_1, byte[] array_2) {
        byte[] result = new byte[array_1.length];
        int i = 0;
        for (byte b : array_1) {
            result[i] = (byte) (b ^ array_2[i++]);
        }
        return result;
    }
}
