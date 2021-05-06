package com.github.arcanjoaq.kefla.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Strings;

public class AwsS3StorageService {

  private final AmazonS3 s3client;
  private final String bucket;

  protected AwsS3StorageService(final String accessKey, final String secretKey, 
      final String bucket, 
      final String endpoint, final Regions region, 
      final Boolean pathStyleAccessEnabled) {
    final BasicAWSCredentials credentials = new BasicAWSCredentials(
        accessKey, 
        secretKey
        );
    AmazonS3ClientBuilder builder = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials));

    if (region != null) {
      builder = builder.withRegion(region);
    }    
    if (!Strings.isNullOrEmpty(endpoint)) {
      builder = builder.withEndpointConfiguration(new EndpointConfiguration(endpoint, null));
    }
    if (Boolean.TRUE.equals(pathStyleAccessEnabled)) {
      builder = builder.withPathStyleAccessEnabled(true);
    }
    this.s3client = builder.build();
    if (!this.s3client.doesBucketExistV2(bucket)) {
      this.s3client.createBucket(bucket);
    }
    this.bucket = bucket;
  }

  protected AwsS3StorageService(final String accessKey, final String secretKey, 
      final String bucket, 
      final String endpoint, 
      final boolean pathStyleAccessEnabled) {
    this(accessKey, secretKey, bucket, endpoint, null, pathStyleAccessEnabled);
  }

  public AwsS3StorageService(final String accessKey, final String secretKey, final String bucket, 
      final Regions region) {
    this(accessKey, secretKey, bucket, null, region, null);
  }

  public void upload(final String key, final InputStream file, final long length) {
    final ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(length);
    s3client.putObject(bucket, key, file, metadata);
  }

  public void download(final String key, 
      final OutputStream outputStream) {
    try (final S3Object s3Object = s3client.getObject(bucket, key)) {
      s3Object.getObjectContent().transferTo(outputStream);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(final String key) {
    s3client.deleteObject(bucket, key);
  }

  public boolean exists(final String key) {
    return s3client.doesObjectExist(bucket, key);
  }
}
