package com.github.arcanjoaq.kefla.s3;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.findify.s3mock.S3Mock;

public class AwsS3StorageServiceTest {

  private static final int S3_PORT = 9192;

  private static final String BUCKET_NAME = "demo-bucket";

  private static final String AWS_ACCESS_KEY = "test";

  private static final String AWS_ACCESS_SECRET = "test";

  private static final String KEY = "geralt";

  private static final String ENDPOINT = String.format("http://localhost:%d", S3_PORT);

  private static S3Mock api;

  @BeforeClass
  public static void setUpBeforeClass() {
    api = new S3Mock.Builder().withPort(S3_PORT).withFileBackend("target").build();
    api.start();
  }
  
  @AfterClass
  public static void tearDownAfterClass() {
    api.shutdown();
  }

  @Test
  public void shouldExecute() throws IOException {
    final URL url = Resources.getResource("whiteWolf.png");
    final byte[] byteArray = Resources.toByteArray(url);

    final AwsS3StorageService awsS3StorageService = 
        new AwsS3StorageService(AWS_ACCESS_KEY, AWS_ACCESS_SECRET, BUCKET_NAME, ENDPOINT, true);

    assertThat(awsS3StorageService.exists(KEY)).isFalse();

    try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
      awsS3StorageService.upload(KEY, inputStream, byteArray.length);
    }

    assertThat(awsS3StorageService.exists(KEY)).isTrue();

    final File to = new File("target" + File.separator + "whiteWolf.png");

    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      awsS3StorageService.download(KEY, outputStream);
      final byte[] array = outputStream.toByteArray();

      Files.write(array, to);
    }

    assertThat(to).exists();

    awsS3StorageService.delete(KEY);

    assertThat(awsS3StorageService.exists(KEY)).isFalse();
  }

}
