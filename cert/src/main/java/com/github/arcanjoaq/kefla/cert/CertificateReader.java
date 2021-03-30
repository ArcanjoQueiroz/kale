package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;

public class CertificateReader {

  private CertificateReader() { }
  
  public static Certificate[] readCertFromFile(final String file) 
      throws CertificateException, IOException {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    try (final InputStream is = new FileInputStream(file)) {
      final Collection<? extends Certificate> certificates = cf.generateCertificates(is);
      final Certificate[] c = new Certificate[certificates.size()];
      return certificates.toArray(c);
    }
  }

  public static PrivateKey readPrivateKeyFromFile(String filename) throws Exception {
    final byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
    final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    final KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(spec);
  }

  public static PublicKey readPublicKeyFromFile(String filename) throws Exception {
    final byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
    final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    final KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }

}