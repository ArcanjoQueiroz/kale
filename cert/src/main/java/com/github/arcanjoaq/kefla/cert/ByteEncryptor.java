package com.github.arcanjoaq.kefla.cert;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ByteEncryptor {
  
  private ByteEncryptor() { }
  
  public static byte[] encrypt(final PublicKey key, byte[] plaintext) 
      throws NoSuchAlgorithmException, 
      NoSuchPaddingException, 
      InvalidKeyException, 
      IllegalBlockSizeException, 
      BadPaddingException {    
    final Cipher cipher = Cipher.getInstance(String
        .format("%s/ECB/OAEPWithSHA-256AndMGF1Padding", Constants.ALGORITHM));
    cipher.init(Cipher.ENCRYPT_MODE, key);  
    return cipher.doFinal(plaintext);
  }

}
