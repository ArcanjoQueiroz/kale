package com.github.arcanjoaq.kefla.cert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PublicKeyReader {

  private PublicKeyReader() { }
  
  public static PublicKey read(final Path filename) throws Exception {
    final byte[] keyBytes = Files.readAllBytes(filename);
    return read(keyBytes, Constants.ALGORITHM);
  }
  
  public static PublicKey read(final String string) throws Exception {
    final String publicKeyPem = string
        .replaceAll(System.lineSeparator(), "")
        .replace(String.format("-----BEGIN %s PUBLIC KEY-----", Constants.ALGORITHM), "")
        .replace(String.format("-----END %s PUBLIC KEY-----", Constants.ALGORITHM), "")
        .trim();

    final byte[] decoded = Base64.getMimeDecoder()
        .decode(publicKeyPem);    
    return read(decoded, Constants.ALGORITHM);
  }

  private static PublicKey read(final byte[] decoded, final String algorithm)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    final KeyFactory kf = KeyFactory.getInstance(algorithm);
    return kf.generatePublic(spec);
  }
}
