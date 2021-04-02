package com.github.arcanjoaq.kefla.cert;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.security.KeyStore;
import java.security.cert.Certificate;
import org.junit.Before;
import org.junit.Test;

public class KeytoolKeystoreFactoryTest {

  private KeystoreFactory keystoreFactory;
  
  @Before
  public void setUp() {
    keystoreFactory = new BouncyCastleKeystoreFactory();
  }
  
  @Test
  public void shouldCreateKeytoolCA() throws Exception {
    final String alias = "foo";
    final String password = "changeit"; 
    final String file = "target" + File.separator + "keystore_keytool";
    final String dn = "CN=localhost,ou=Home,c=BR";
    
    final File f = new File(file);
    if (f.exists()) {
      f.delete();
    }
    assertThat(f).doesNotExist();
    final KeyStore keyStore = keystoreFactory.createKeystore(alias, password, dn, file);
    assertThat(keyStore).isNotNull();
    assertThat(f).exists();
    
    final KeystoreManager keystoreManager = KeystoreManager.fromPath(file, alias, password);
    
    final Certificate[] certificateChain = keystoreManager.getCertificateChain();    
    assertThat(certificateChain).isNotNull().isNotEmpty();
    assertThat(keystoreManager.getPrivateKey()).isNotNull();
    assertThat(keystoreManager.getPublicKey()).isNotNull();
    assertThat(keystoreManager.getPublicKeyAsString()).isNotNull().isNotEmpty();
  }

}
