package br.com.alexandre.kale.spring.web;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;

public class ResponseEntityFactory {

  private static final Logger logger = LoggerFactory.getLogger(ResponseEntityFactory.class);
 
  private ResponseEntityFactory() { }
  
  public static ResponseEntity<InputStreamResource> createResponseEntity(final byte[] byteArray,
      final MediaType mediaType, final String name) {
    try {
      return createResponseEntity(ByteSource.wrap(byteArray).openStream(), mediaType, name);
    } catch (final IOException e) {
      throw new ResponseEntityFactoryException(e);
    }
  }    

  public static ResponseEntity<InputStreamResource> createResponseEntity(
      final InputStream inputStream,
      final MediaType mediaType, 
      final String fileName) {
    checkArgument(!Strings.isNullOrEmpty(fileName));
    checkArgument(mediaType != null);
    
    final HttpHeaders headers = new HttpHeaders();
    headers.put("Content-Type", Arrays.asList(mediaType.toString()));
    
    headers.add("Content-Disposition", String.format("inline; filename=%s", fileName));
    final ResponseEntity.BodyBuilder bodyBuilder =
        ResponseEntity.ok().headers(headers);
    logger.debug("Returning response file {}", fileName);
    return (inputStream != null)
        ? bodyBuilder.body(new InputStreamResource(inputStream))
        : bodyBuilder.build();
  }


  public static class ResponseEntityFactoryException extends RuntimeException {

    private static final long serialVersionUID = 2649933753201458605L;

    ResponseEntityFactoryException(final Throwable cause) {
      super(cause);
    }
    
  }

}
