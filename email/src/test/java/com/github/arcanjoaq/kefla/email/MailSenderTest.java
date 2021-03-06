package com.github.arcanjoaq.kefla.email;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;

public class MailSenderTest {

  private static final String PROTOCOL = "smtp";

  private static final String HOST = "localhost";

  private static final int PORT = 8345;

  private static GreenMail greenMail;

  private MailSender sender;

  @BeforeClass
  public static void setUpBeforeClass() {
    final ServerSetup setup = new ServerSetup(PORT, HOST, PROTOCOL);

    greenMail =
        new GreenMail(setup)
            .withConfiguration(new GreenMailConfiguration().withUser("foo@foo.com", "foo", "foo"));
    greenMail.start();
  }

  @Before
  public void setUp() throws Exception {
    final Properties props = new MailPropertiesBuilder()
        .auth(true)
        .starttls(true)
        .host(HOST)
        .port(PORT)
        .protocol(PROTOCOL)
        .protocols("TLSv1.2")
        .timeout(5000)
        .debug(true)
        .build();
    sender = new MailSender("foo@foo.com", "foo", "foo", props, Charset.forName("UTF-8"));
  }

  @Test
  public void shouldSendAnEmail() throws MessagingException, IOException {
    sender.send(
        "bar@bar.com",
        "Subject",
        "<h1>Foo</h1>",
        Attachment.from(
            new ByteArrayInputStream("my attachment".getBytes()), "text/plain", "foo.txt"));

    assertThat(greenMail.getReceivedMessages().length).isEqualTo(1);
    assertThat(greenMail.getReceivedMessages()[0].getSubject()).isEqualTo("Subject");
    assertThat(greenMail.getReceivedMessages()[0].getFrom()[0].toString()).isEqualTo("foo@foo.com");

    assertThat(greenMail.getReceivedMessages()[0].getContent()).isInstanceOf(MimeMultipart.class);

    System.out.println(GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]));

    final MimeMultipart mp = (MimeMultipart) greenMail.getReceivedMessages()[0].getContent();

    assertThat(mp.getCount()).isEqualTo(2);

    assertThat(GreenMailUtil.getBody(mp.getBodyPart(1)).trim()).isEqualTo("my attachment");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    greenMail.stop();
  }
}
