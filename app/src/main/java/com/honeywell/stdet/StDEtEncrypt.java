package com.honeywell.stdet;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StDEtEncrypt{
    private static final String ALGORITHM = "AES";
        private static final String KEY = "Stdet22Tetratech";

        public static String encrypt(String value) throws Exception
        {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(StDEtEncrypt.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
            String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
            return encryptedValue64;

        }

        public static String decrypt(String value) throws Exception {
            String decryptedValue = "";
            try {
                Key key = generateKey();
                System.out.println("pwd encr - " + value);
                Cipher cipher = Cipher.getInstance(StDEtEncrypt.ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
                byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
                decryptedValue = new String(decryptedByteValue, "utf-8");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return decryptedValue;

        }

        private static Key generateKey() throws Exception
        {
            Key key = new SecretKeySpec(StDEtEncrypt.KEY.getBytes(),StDEtEncrypt.ALGORITHM);
            return key;
        }
}
