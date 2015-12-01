package eu.robojob.millassist.user;

import java.util.ArrayList;
import java.util.List;

public class UserGroup {

    private int id;
    private String name;
    private List<String> emails;
    private UserEmailSettings emailSettings;
    private String imageURL;

    public UserGroup(final int id, final String name, final List<String> emails, final String imageURL) {
        this.id = id;
        this.name = name;
        this.emails = emails;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(final List<String> emails) {
        this.emails = emails;
    }

    public UserEmailSettings getEmailSettings() {
        return emailSettings;
    }

    public void addEmail(final String email) {
        if(emails ==null) {
            emails = new ArrayList<>();
        }
        emails.add(email);
    }

    public void setEmailSettings(final UserEmailSettings emailSettings) {
        this.emailSettings = emailSettings;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(final String imageURL) {
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getEmailsString() {
        StringBuilder builder = new StringBuilder();
        for(String email: emails) {
            builder.append(email).append(";");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

}
