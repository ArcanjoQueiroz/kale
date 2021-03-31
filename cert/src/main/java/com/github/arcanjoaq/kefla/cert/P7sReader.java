package com.github.arcanjoaq.kefla.cert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class P7sReader {

  private P7sReader() { }
 
  public static List<X509Certificate> getCertificates(final File file)
      throws CertificateException, IOException {
    final List<X509Certificate> certificates = new ArrayList<>();
    try (FileInputStream fis = new FileInputStream(file)) {
      final CertificateFactory cf = CertificateFactory.getInstance("X.509");
      final Collection<?> c = cf.generateCertificates(fis);
      final Iterator<?> i = c.iterator();
      while (i.hasNext()) {
        final X509Certificate cert509 = (X509Certificate) i.next();
        certificates.add(cert509);
      }
    }
    return certificates;
  }
 
}
