package com.github.arcanjoaq.kefla.email;

import java.io.Serializable;
import java.util.Properties;

public class MailPropertiesBuilder implements Serializable {

  private static final long serialVersionUID = -3256103200089816823L;
  
  private Boolean debug;
  private int timeout;
  private String protocol;
  private String protocols;
  private int port;
  private String host;
  private Boolean starttls;
  private Boolean auth;
 
  public MailPropertiesBuilder() {
    this.auth = true;
    this.starttls = true;
    this.host = "localhost";
    this.protocol = "smtp";
    this.port = 8345;
    this.timeout = 5000;
    this.debug = false;
  }
  
  public MailPropertiesBuilder debug(final boolean debug) {
    this.debug = debug;
    return this;
  }
  
  public MailPropertiesBuilder timeout(final int timeout) {
    this.timeout = timeout;
    return this;
  }
  
  public MailPropertiesBuilder protocol(final String protocol) {
    this.protocol = protocol;
    return this;
  }
  
  public MailPropertiesBuilder protocols(final String protocols) {
    this.protocols = protocols;
    return this;
  }
  
  public MailPropertiesBuilder port(final int port) {
    this.port = port;
    return this;
  }  
  
  public MailPropertiesBuilder host(final String host) {
    this.host = host;
    return this;
  }
  
  public MailPropertiesBuilder starttls(final boolean starttls) {
    this.starttls = starttls;
    return this;
  }
  
  public MailPropertiesBuilder auth(final boolean auth) {
    this.auth = auth;
    return this;
  }  
  
  public Properties build() {
    final Properties props = new Properties();
    props.put("mail.smtp.auth", Boolean.toString(this.auth));
    props.put("mail.smtp.starttls.enable", Boolean.toString(this.starttls));
    props.put("mail.smtp.host", this.host);
    props.put("mail.smtp.port", Integer.toString(this.port));
    props.put("mail.smtp.ssl.protocols", this.protocols);
    props.put("mail.smtp.ssl.trust", this.host);
    props.put("mail.transport.protocol", this.protocol);
    props.put("mail.smtp.timeout", Integer.toString(this.timeout));
    props.put("mail.debug", Boolean.toString(this.debug));
    
    return props;
  }
}
