package com.github.arcanjoaq.kefla.cert;

import java.security.KeyStore;

@FunctionalInterface
public interface KeystoreFactory {

  public KeyStore createKeystore(final String alias, final String password, 
      final String dn,
      final String file);
}
