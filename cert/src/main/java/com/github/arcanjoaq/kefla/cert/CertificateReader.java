package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
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

}