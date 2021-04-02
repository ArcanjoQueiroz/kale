package com.github.arcanjoaq.kefla.cert;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class PrivateKeyReader {
  
  private PrivateKeyReader() { }

  public static PrivateKey readPrivateKeyFromString(final String string) throws Exception {
    final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(string.getBytes());
    final KeyFactory kf = KeyFactory.getInstance(Constants.ALGORITHM);
    return kf.generatePrivate(spec);
  }
}
