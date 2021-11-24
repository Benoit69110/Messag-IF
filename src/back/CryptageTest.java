package back;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptageTest {
    public static void main(String[] args){

        String message="message test c'est cool ca a l'air de fonctionner\r\n" +
                "avec un message de plusieurs lignes aussi\r\n" +
                "plus qu'a integrer ca dans le projet";
        try{
            SecretKeySpec key=new SecretKeySpec("ThisIsMyPassword".getBytes(),"AES");
            String messageEnc=encrypt(message,key);
            String messageDec=decrypt(messageEnc,key);
            System.out.println(messageEnc);
            System.out.println(messageDec);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String encrypt(String valueToEncrypt, SecretKeySpec key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher c=Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal=c.doFinal(valueToEncrypt.getBytes());
        String res=new String(Base64.getEncoder().encode(encVal));
        return res;
    }

    public static String decrypt(String encryptedValue, SecretKeySpec key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher c=Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decryptedVal=Base64.getDecoder().decode(encryptedValue);
        byte[] decvValue= c.doFinal(decryptedVal);
        String decr=new String(decvValue);
        return decr;
    }
}
