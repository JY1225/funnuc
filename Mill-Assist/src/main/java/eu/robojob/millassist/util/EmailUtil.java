package eu.robojob.millassist.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.robojob.millassist.db.GeneralMapper;
import eu.robojob.millassist.process.ProcessFlow;
import eu.robojob.millassist.user.UserGroup;
import eu.robojob.millassist.util.PropertyManager.Setting;

public class EmailUtil {


    private static final Logger logger = LogManager.getLogger(EmailUtil.class.getName());

    private static Set<Timer> errorTimers = new HashSet<>();

    public enum EMailEvent {
        ERROR, BATCH_END;
    }

    public static void sendMailTest(final UserGroup userGroup) {
        sendMail("Testing RoboJob mail option.", htmlToString("/html/mail_option_test_mail.html"), userGroup.getEmails());
    }

    public static void sendMailToAllUsers(final EMailEvent event, final ProcessFlow processFlow) {
        switch (event) {
        case ERROR:
            if(errorTimers.size() == 0) {
                logger.debug("Going to send emails to ERROR subscribed users.");
                String message = htmlToString("/html/error_mail.html");
                message = message.replace("{{processName}}", processFlow.getName());
                final String messageF = message;
                final String subject = "Error occurred";
                Set<UserGroup> userGroups = null;
                try {
                    userGroups= GeneralMapper.getAllUserGroups();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for(final UserGroup userGroup: userGroups) {
                    if(userGroup.getEmailSettings().isEmailAtError()) {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendMail(subject, messageF, userGroup.getEmails());
                            }
                        }, 1000 * 60 * userGroup.getEmailSettings().getEmailErrorDelay());
                        errorTimers.add(timer);
                    }
                }
            }
            break;
        case BATCH_END:
            logger.debug("Going to send emails to BATCH subscribed users.");
            String messageBE = htmlToString("/html/batch_end_mail.html");
            messageBE = messageBE.replace("{{processName}}", processFlow.getName());
            String subjectBE = "Batch finished!";
            Set<UserGroup> userGroupsBE = null;
            try {
                userGroupsBE= GeneralMapper.getAllUserGroups();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Set<String> recipientsBE = new HashSet<>();
            for(UserGroup userGroup: userGroupsBE) {
                if(userGroup.getEmailSettings().isEmailAtBatchEnd()) {
                    recipientsBE.addAll(userGroup.getEmails());
                }
            }
            sendMail(subjectBE, messageBE , recipientsBE);
            break;
        default:
            break;
        }
    }

    public static void cancelErrorTimers() {
        for(Timer timer: errorTimers) {
            timer.cancel();
        }
        errorTimers.clear();
    }

    private static void sendMail(final String subject, final String message, final Collection<String> recipients) {

        // Sender's email ID needs to be mentioned
        String from = PropertyManager.getValue(Setting.FROM_EMAIL_ADDRESS);

        // Assuming you are sending email from localhost
        String host = PropertyManager.getValue(Setting.EMAIL_HOST);
        String port = PropertyManager.getValue(Setting.EMAIL_PORT);
        final String username = PropertyManager.getValue(Setting.EMAIL_USERNAME);
        final String password = PropertyManager.getValue(Setting.EMAIL_PASSWORD);
        //        String host = "localhost";
        //        String port = "1025";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", username);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", true);
        properties.put("mail.transport.protocol", "smtps");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // TODO password validation?
                return new PasswordAuthentication(username, password);
            }
        });
        //        session.setDebug(true);
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
            mMessage.setContent(message, "text/html;charset=utf-8");

            // Send message
            Transport transport = session.getTransport("smtps");
            transport.connect(host,username,password);
            transport.sendMessage(mMessage, mMessage.getAllRecipients());
            logger.debug("Sent mail to [ "+mMessage.getAllRecipients()+" ]");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static String htmlToString(final String fileUrl) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            InputStream is = EmailUtil.class.getResourceAsStream(fileUrl);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        String content = contentBuilder.toString();
        return content;
    }
}
