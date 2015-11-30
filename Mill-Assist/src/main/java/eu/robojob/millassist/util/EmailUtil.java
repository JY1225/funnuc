package eu.robojob.millassist.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.user.User;
import eu.robojob.millassist.util.PropertyManager.Setting;

public class EmailUtil {


    private static final Logger logger = LogManager.getLogger(EmailUtil.class.getName());

    public enum EMailEvent {
        ERROR, BATCH_END;
    }
    public static void sendMailTest(final User user) {
        sendMail("Testing RoboJob mail option.", "Your mail option is correctly configured.", Collections.singletonList(user.getEmail()));
    }

    public static void sendMailToAllUsers(final EMailEvent event) {
        switch (event) {
        case ERROR:
            String message = "Error occurred on one of the RoboJob systems.";
            String subject = "Error occurred";
            Set<User> users = null;
            try {
                users= GeneralMapper.getAllUsers();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Set<String> recipients = new HashSet<>();
            for(User user: users) {
                if(user.getEmailSettings().isEmailAtError()) {
                    recipients.add(user.getEmail());
                }
            }
            // TODO delay for the given time
            sendMail(subject, message, recipients);
            break;
        case BATCH_END:
            logger.debug("Going to send emails to batch subscribed users.");
            String messageBE = "Batch finished.";
            String subjectBE = "Batch finished";
            Set<User> usersBE = null;
            try {
                usersBE= GeneralMapper.getAllUsers();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Set<String> recipientsBE = new HashSet<>();
            for(User user: usersBE) {
                if(user.getEmailSettings().isEmailAtBatchEnd()) {
                    recipientsBE.add(user.getEmail());
                }
            }
            sendMail(subjectBE, messageBE , recipientsBE);
            break;
        default:
            break;
        }
    }

    private static void sendMail(final String subject, final String message, final Collection<String> recipients) {

        // Sender's email ID needs to be mentioned
        String from = PropertyManager.getValue(Setting.FROM_EMAIL_ADDRESS);

        // Assuming you are sending email from localhost
        String host = "localhost";
        String port = "1025";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);


        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage mMessage = new MimeMessage(session);

            // Set From: header field of the header.
            mMessage.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            for(String email: recipients) {
                mMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            }

            // Set Subject: header field
            mMessage.setSubject(subject);

            // Now set the actual message
            mMessage.setText(message);

            // Send message
            Transport.send(mMessage, mMessage.getAllRecipients());
            logger.debug("Sent mail to [ "+mMessage.getAllRecipients()+" ]");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
