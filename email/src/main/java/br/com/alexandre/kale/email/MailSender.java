package br.com.alexandre.kale.email;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class MailSender {

    private String email;
    private String username;
    private String password;
    private Properties properties;
    private Charset charset;
    
    private Logger logger = LoggerFactory.getLogger(MailSender.class);

    public MailSender(final String email, final String username, final String password, final Properties properties, final Charset charset) {
        checkArgument(!Strings.isNullOrEmpty(email), "Sender e-mail is null or empty");
        checkArgument(!Strings.isNullOrEmpty(username), "Username is null or empty");
        checkArgument(!Strings.isNullOrEmpty(password), "Password is null or empty");
        checkArgument(charset != null, "Charset is null or empty");

        this.email = email;
        this.username = username;
        this.password = password;
        this.properties = (properties != null) ? properties : new Properties();
        this.charset = charset;
    }

    public void send(final String to, final String subject, final String html, final List<Attachment> attachments) {
        checkArgument(!Strings.isNullOrEmpty(to), "Invalid destination message");
        checkArgument(attachments != null && !attachments.isEmpty(), "Attachments are not set");

        final Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                logger.debug("Authenticating using: '{}' and properties are '{}'", username, properties);
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            final MimeBodyPart bodyPart = new MimeBodyPart();
            logger.debug("Set html: '{}'", html);
            bodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
            bodyPart.setHeader("Content-Disposition", "inline");                        
            bodyPart.setContent(Strings.nullToEmpty(html), String.format("text/html; charset=%s", charset));

            final Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);

            attachments.forEach(a -> {
                try {
                    final MimeBodyPart att = new MimeBodyPart();
                    att.setDataHandler(new DataHandler(new ByteArrayDataSource(a.getSource(), a.getMimeType())));
                    att.setFileName(StringUtils.stripAccents(a.getFileName()));
                    logger.debug("Adding attachment '{}' into the message with mime type: '{}'", a.getFileName(), a.getMimeType());
                    multipart.addBodyPart(att);
                } catch (final MessagingException | IOException e) {
                    throw new RuntimeException("Error on adding attachment: " + e.getMessage(), e);
                }                    
            });

            final MimeMessage message = new MimeMessage(session);
          
            message.setFrom(new InternetAddress(email, null, this.charset.toString()));
            logger.debug("Adding from: '{}'", email);

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            logger.debug("Adding to: '{}'", to);

            message.setSubject(Strings.nullToEmpty(subject), this.charset.toString());
            logger.debug("Adding subject: '{}'", subject);

            message.setContent(multipart);

            logger.info("Sending message to: '{}' with subject: '{}' from '{}' ...", to, subject, email);
            Transport.send(message);
            logger.info("Message was successfully sent");
        } catch (final MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error on sending e-mail to '" + to + "': " + e.getMessage(), e);
        }
    }
}
