package mail;

import util.ResourceBundleManager;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by USER on 23.09.2016.
 */
public class MailSender {
    private void setProperties(Properties props) {
        props.put("mail.smtp.host", ResourceBundleManager.getByName("email.smtp.host"));
        props.put("mail.smtp.socketFactory.port", ResourceBundleManager.getByName("email.smtp.port"));
        props.put("mail.smtp.socketFactory.class", ResourceBundleManager.getByName("email.socketFactory"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", ResourceBundleManager.getByName("email.smtp.port"));
    }

    private Session getSession(Properties props, final String username, final String pass) {
        return Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, pass);
                    }
                });
    }

    public Message createMessage(String emailTo, String subject, String text) throws MessagingException {
        Properties properties = new Properties();
        setProperties(properties);
        Message message = new MimeMessage(getSession(properties,
                ResourceBundleManager.getByName("email.username.from"),
                ResourceBundleManager.getByName("email.username.password")));

        message.setFrom(new InternetAddress(ResourceBundleManager.getByName("email.username.from")));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(emailTo));
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    public void sendMessage(Message message) throws MessagingException {
        Transport.send(message);
    }
}
