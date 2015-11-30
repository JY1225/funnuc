package eu.robojob.millassist.user;

public class User {

    private int id;
    private String name;
    private String email;
    private UserEmailSettings emailSettings;
    private String imageURL;

    public User(final int id, final String name, final String email, final String imageURL) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public UserEmailSettings getEmailSettings() {
        return emailSettings;
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

}
