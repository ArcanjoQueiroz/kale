package com.github.arcanjoaq.kefla.cert;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ByteDecryptor {

  private ByteDecryptor() { }
  
  public static byte[] decrypt(final PrivateKey key, byte[] ciphertext) 
      throws NoSuchAlgorithmException, 
      NoSuchPaddingException, 
      InvalidKeyException, 
      IllegalBlockSizeException, 
      BadPaddingException {    
    final Cipher cipher = Cipher.getInstance(String
        .format("%s/ECB/OAEPWithSHA-256AndMGF1Padding", Constants.ALGORITHM));   
    cipher.init(Cipher.DECRYPT_MODE, key);  
    return cipher.doFinal(ciphertext);
  }
}
