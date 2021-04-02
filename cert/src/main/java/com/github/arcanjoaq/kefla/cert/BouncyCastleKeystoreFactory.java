package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class BouncyCastleKeystoreFactory implements KeystoreFactory {

  private static final int DEFAULT_VALIDITY = 365;

  public KeyStore createKeystore(final String alias, final String password, 
      final String dn,
      final String file) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(alias));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(dn));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(file));
    Security.addProvider(new BouncyCastleProvider());
    try {
      final java.security.KeyPairGenerator keyPairGenerator = KeyPairGenerator
          .getInstance(Constants.ALGORITHM, "BC");
      keyPairGenerator.initialize(Constants.KEY_SIZE, new SecureRandom());
      final KeyPair keyPair = keyPairGenerator.generateKeyPair();
      final PrivateKey privateKey = keyPair.getPrivate();
      final X509Certificate selfCert = createSelfSignedX509Certificate(dn, keyPair);

      final java.security.cert.Certificate[] outChain = { selfCert };
      final KeyStore outStore = KeyStore.getInstance(Constants.KEYSTORE_TYPE);

      final char[] passwd = password.toCharArray();
      outStore.load(null, passwd);
      outStore.setKeyEntry(alias, privateKey, passwd,
          outChain);
      try (OutputStream outputStream = new FileOutputStream(file)) {
        outStore.store(outputStream, passwd);
      }

      final KeyStore inStore = KeyStore.getInstance(Constants.KEYSTORE_TYPE);
      inStore.load(new FileInputStream(file), passwd);
      return inStore;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private X509Certificate createSelfSignedX509Certificate(final String dn, 
      final KeyPair keyPair) throws InvalidKeyException, IllegalStateException,
      NoSuchProviderException, 
      NoSuchAlgorithmException, 
      SignatureException, 
      CertificateException, 
      OperatorCreationException {
    final X500Name issuer = new X500Name(dn);

    final BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

    final LocalDateTime now = LocalDateTime.now();
    
    final Date before = Date.from(now.plusSeconds(1)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    
    final Date after = Date.from(now.plusDays(DEFAULT_VALIDITY)
        .atZone(ZoneId.systemDefault())
        .toInstant());
    
    final JcaX509v3CertificateBuilder certificateBuilder = 
        new JcaX509v3CertificateBuilder(issuer, serial, before, 
            after, issuer, keyPair.getPublic());

    final ContentSigner signer = new JcaContentSignerBuilder(Constants.SIGNATURE_ALGORITHM)
        .build(keyPair.getPrivate());

    final X509CertificateHolder certHolder = certificateBuilder.build(signer);

    final X509Certificate cert = new JcaX509CertificateConverter()
        .getCertificate(certHolder);
    cert.verify(keyPair.getPublic());
    return cert;
  }
}
