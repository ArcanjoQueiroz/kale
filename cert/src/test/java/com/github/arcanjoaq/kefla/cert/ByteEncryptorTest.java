package com.github.arcanjoaq.kefla.cert;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ByteEncryptorTest {

  @Test
  public void test() throws Exception {
    final String alias = "foo";
    final String password = "changeit"; 
    final String file = "target" + File.separator + "keystore_encrypt";
    final String dn = "CN=localhost,ou=Home,c=BR";
    final String myText = "MyText";
        
    final File f = new File(file);
    if (f.exists()) {
      f.delete();
    }
    assertThat(f).doesNotExist();    
    final KeyStore keyStore = new BouncyCastleKeystoreFactory()
        .createKeystore(alias, password, dn, file);
    assertThat(keyStore).isNotNull();
    assertThat(f).exists();
    
    final KeystoreManager keystoreManager = KeystoreManager.of(keyStore, alias, password);
    assertThat(keystoreManager).isNotNull();

    final PrivateKey privateKey = keystoreManager.getPrivateKey();
    assertThat(privateKey).isNotNull();
    
    final PublicKey publicKey = keystoreManager.getPublicKey();
    assertThat(publicKey).isNotNull();
    
    final byte[] encrypt = ByteEncryptor.encrypt(publicKey, myText.getBytes());
    assertThat(encrypt).isNotNull().isNotEmpty();

    final byte[] decrypt = ByteDecryptor.decrypt(privateKey, encrypt);
    assertThat(decrypt).isNotNull().isNotEmpty();
    assertThat(new String(decrypt)).isNotNull().isNotEmpty().isEqualTo(myText);
    
    final String publicKeyAsString = keystoreManager.getPublicKeyAsString();
    assertThat(publicKeyAsString).isNotNull().isNotEmpty();
    
    final PublicKey publicKey2 = PublicKeyReader
        .read(publicKeyAsString);
    assertThat(publicKey2).isNotNull();
    
    final byte[] encrypt2 = ByteEncryptor.encrypt(publicKey2, myText.getBytes());
    assertThat(encrypt2).isNotNull().isNotEmpty();
    
    final byte[] decrypt2 = ByteDecryptor.decrypt(privateKey, encrypt2);
    assertThat(decrypt2).isNotNull().isNotEmpty();
    assertThat(new String(decrypt2)).isNotNull().isNotEmpty().isEqualTo(myText);
  }

}
