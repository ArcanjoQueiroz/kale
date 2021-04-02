package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;

public class KeytoolKeystoreFactory implements KeystoreFactory {

  private static final int DEFAULT_VALIDITY = 365;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(KeytoolKeystoreFactory.class);

  public KeyStore createKeystore(final String alias, final String password, 
      final String dn,
      final String file) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(alias));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(dn));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(file));
    final String command = String.format("keytool -genkeypair -dname %s"
        + " -keyalg %s -alias %s -keypass %s -keystore %s"
        + " -storepass %s -validity %d -sigalg %s -keysize %d", 
        dn, Constants.ALGORITHM, alias, password, file, password, 
        DEFAULT_VALIDITY, Constants.SIGNATURE_ALGORITHM, Constants.KEY_SIZE);
    LOGGER.info("Command: '{}'", command);
    try {
      final Process process = Runtime.getRuntime().exec(command);
      final String error = CharStreams.toString(new InputStreamReader(process.getErrorStream()));
      final String input = CharStreams.toString(new InputStreamReader(process.getInputStream()));
      process.waitFor();
      process.destroy();
      LOGGER.error("Error: '{}'", error);
      LOGGER.debug("Input: '{}'", input);

      final KeyStore keyStore = KeyStore.getInstance(Constants.KEYSTORE_TYPE);    
      try (final InputStream is = new FileInputStream(file)) {
        keyStore.load(is, password.toCharArray());
      }
      return keyStore;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
