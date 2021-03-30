package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeystoreManager {
  
  private String path;
  private String password;
  private String alias;
  private KeyStore keyStore;
  
  public KeystoreManager(final String path, final String password, 
      final String alias) throws Exception {
    this.path = path;
    this.password = password;
    this.alias = alias;
    Security.addProvider(new BouncyCastleProvider());
    this.keyStore = KeyStore.getInstance("JKS");
    try (final InputStream is = new FileInputStream(this.path)) {
      this.keyStore.load(is, this.password.toCharArray());
    }
  }
  
  public PrivateKey getPrivateKey() throws Exception {
    return (PrivateKey) this.keyStore.getKey(alias, password.toCharArray());
  }
  
  public Certificate[] getCertificateChain() throws KeyStoreException {
    return this.keyStore.getCertificateChain(alias);
  }
}