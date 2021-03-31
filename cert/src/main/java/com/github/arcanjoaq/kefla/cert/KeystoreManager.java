package com.github.arcanjoaq.kefla.cert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.google.common.base.Strings;

public class KeystoreManager {

  private char[] password;
  private String alias;
  private KeyStore keyStore;

  KeystoreManager(final InputStream is, final String alias, 
      final String password) throws Exception {
    this.password = Strings.isNullOrEmpty(password) ? null : password.toCharArray();
    this.alias = alias;
    Security.addProvider(new BouncyCastleProvider());
    this.keyStore = KeyStore.getInstance("JKS");
    this.keyStore.load(is, this.password);    
  }

  public PrivateKey getPrivateKey() throws Exception {
    return (PrivateKey) this.keyStore.getKey(alias, this.password);
  }

  public PublicKey getPublicKey() throws Exception {
    return (PublicKey) this.keyStore.getCertificate(this.alias).getPublicKey();
  }

  public String getPublicKeyAsString() throws Exception {
    final PublicKey publicKey = getPublicKey();
    return "-----BEGIN CERTIFICATE-----\n"
      + Base64.getMimeEncoder().encodeToString(publicKey.getEncoded())
      + "\n-----END CERTIFICATE-----";
  }

  public Certificate[] getCertificateChain() throws KeyStoreException {
    return this.keyStore.getCertificateChain(alias);
  }

  public static KeystoreManager fromPath(final String path, final String alias,
      final String password) throws Exception {
    return fromPath(new File(path), alias, password);
  }

  public static KeystoreManager fromPath(final File file, final String alias,
      final String password) throws Exception {
    try (final InputStream is = new FileInputStream(file)) {
      return new KeystoreManager(is, alias, password);
    }
  }

  public static KeystoreManager fromByteArray(final byte[] keystore, final String alias, 
      final String password) throws Exception {
    try (final InputStream is = new ByteArrayInputStream(keystore)) {
      return new KeystoreManager(is, alias, password);
    }
  }
}