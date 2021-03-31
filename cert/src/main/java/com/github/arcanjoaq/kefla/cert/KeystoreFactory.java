package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.io.CharStreams;

public class KeystoreFactory {

  private static final int DEFAULT_VALIDITY = 365;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(KeystoreFactory.class);
  
  private KeystoreFactory() { }
  
  public static KeyStore createKeystore(final String alias, final String password, 
      final String dn,
      final String file)
      throws IOException, InterruptedException, NoSuchAlgorithmException, 
      CertificateException, KeyStoreException {
    final String command = String.format("keytool -genkeypair -dname %s"
        + " -keyalg RSA -alias %s -keypass %s -keystore %s -storepass %s -validity %d", 
        dn, alias, password, file, password, DEFAULT_VALIDITY);
    LOGGER.info("Command: '{}'", command);
    
    final Process process = Runtime.getRuntime().exec(command);
    final String error = CharStreams.toString(new InputStreamReader(process.getErrorStream()));
    final String input = CharStreams.toString(new InputStreamReader(process.getInputStream()));
    process.waitFor();
    process.destroy();
    LOGGER.error("Error: '{}'", error);
    LOGGER.debug("Input: '{}'", input);
    
    Security.addProvider(new BouncyCastleProvider());
    final KeyStore keyStore = KeyStore.getInstance("JKS");    
    try (final InputStream is = new FileInputStream(file)) {
      keyStore.load(is, password.toCharArray());
    }
    return keyStore;
  }
}
