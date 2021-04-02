package com.github.arcanjoaq.kefla.cert;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class P7sSigner {

  private final String keystorePath;
  private final String keystorePassword;
  private final String keystoreAlias;
  private final String signatureAlgorithm;

  public P7sSigner(final String keystorePath, final String keystoreAlias, 
      final String keystorePassword,
      final String signatureAlgorithm) {
    this.keystorePath = keystorePath;
    this.keystorePassword = keystorePassword;
    this.keystoreAlias = keystoreAlias;
    this.signatureAlgorithm = signatureAlgorithm;
  }
 
  public P7sSigner(final String keystorePath, final String keystoreAlias, 
      final String keystorePassword) {
    this.keystorePath = keystorePath;
    this.keystorePassword = keystorePassword;
    this.keystoreAlias = keystoreAlias;
    this.signatureAlgorithm = Constants.SIGNATURE_ALGORITHM;
  }

  public KeyStore loadKeyStore() throws Exception {
    final KeyStore keystore = KeyStore.getInstance(Constants.KEYSTORE_TYPE);
    try (final InputStream is = new FileInputStream(this.keystorePath)) {
      keystore.load(is, this.keystorePassword.toCharArray());
    }
    return keystore;
  }

  public CMSSignedDataGenerator setUpProvider(final KeyStore keystore) throws Exception {
    Security.addProvider(new BouncyCastleProvider());

    final Certificate[] certchain = (Certificate[]) keystore
        .getCertificateChain(this.keystoreAlias);

    final List<Certificate> certlist = new ArrayList<Certificate>();
    for (int i = 0, length = certchain == null ? 0 : certchain.length; i < length; i++) {
      certlist.add(certchain[i]);
    }

    final JcaCertStore certstore = new JcaCertStore(certlist);

    final Certificate cert = keystore.getCertificate(this.keystoreAlias);

    final PrivateKey privateKey = (PrivateKey) (keystore
        .getKey(this.keystoreAlias, this.keystorePassword.toCharArray()));
    
    final ContentSigner signer = new JcaContentSignerBuilder(this.signatureAlgorithm)
        .setProvider("BC")
        .build(privateKey);

    final CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

    generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
        new JcaDigestCalculatorProviderBuilder()
        .setProvider("BC")
        .build())
        .build(signer, (X509Certificate) cert));

    generator.addCertificates(certstore);

    return generator;
  }

  public byte[] signPkcs7(final byte[] content, 
      final CMSSignedDataGenerator generator) throws Exception {
    final CMSTypedData cmsdata = new CMSProcessableByteArray(content);
    final CMSSignedData signeddata = generator.generate(cmsdata, true);
    return signeddata.getEncoded();
  }

  public byte[] signPkcs7(final byte[] content) throws Exception {
    final KeyStore keyStore = loadKeyStore();
    final CMSSignedDataGenerator signatureGenerator = setUpProvider(keyStore);

    final CMSTypedData cmsdata = new CMSProcessableByteArray(content);
    final CMSSignedData signeddata = signatureGenerator.generate(cmsdata, true);
    return signeddata.getEncoded();
  }
 
}