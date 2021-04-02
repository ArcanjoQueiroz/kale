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
  
  private final char[] password;
  private final String alias;
  private final KeyStore keyStore;

  KeystoreManager(final InputStream is, final String alias, 
      final String password) throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    this.password = Strings.isNullOrEmpty(password) ? null : password.toCharArray();
    this.alias = alias;
    this.keyStore = KeyStore.getInstance(Constants.KEYSTORE_TYPE);
    this.keyStore.load(is, this.password);    
  }

  KeystoreManager(final KeyStore keystore, final String alias, 
      final String password) throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    this.password = Strings.isNullOrEmpty(password) ? null : password.toCharArray();
    this.alias = alias;
    this.keyStore = keystore;
  }

  public PrivateKey getPrivateKey() throws Exception {
    return (PrivateKey) this.keyStore.getKey(alias, this.password);
  }

  public PublicKey getPublicKey() throws Exception {
    return (PublicKey) this.keyStore.getCertificate(this.alias).getPublicKey();
  }

  public String getPublicKeyAsString() throws Exception {
    final String algorithm = getPublicKey().getAlgorithm().toUpperCase();
    final PublicKey publicKey = getPublicKey();
    return String.format("-----BEGIN %s PUBLIC KEY-----\n", algorithm)
      + Base64.getMimeEncoder().encodeToString(publicKey.getEncoded())
      + String.format("\n-----END %s PUBLIC KEY-----", algorithm);
  }

  public Certificate[] getCertificateChain() throws KeyStoreException {
    return this.keyStore.getCertificateChain(alias);
  }

  public static KeystoreManager of(final String path, final String alias,
      final String password) throws Exception {
    return of(new File(path), alias, password);
  }

  public static KeystoreManager of(final File file, final String alias,
      final String password) throws Exception {
    try (final InputStream is = new FileInputStream(file)) {
      return new KeystoreManager(is, alias, password);
    }
  }

  public static KeystoreManager of(final byte[] keystore, final String alias, 
      final String password) throws Exception {
    try (final InputStream is = new ByteArrayInputStream(keystore)) {
      return new KeystoreManager(is, alias, password);
    }
  }

  public static KeystoreManager of(final KeyStore keystore, final String alias, 
      final String password) throws Exception {
    return new KeystoreManager(keystore, alias, password);
  }
}