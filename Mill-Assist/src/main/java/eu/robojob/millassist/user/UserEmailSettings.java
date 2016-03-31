package eu.robojob.millassist.user;

public class UserEmailSettings {

    private boolean emailAtBatchEnd;
    private boolean emailAtError;
    private int emailErrorDelay;

    public UserEmailSettings(final boolean emailAtBatchEnd, final boolean emailAtError, final int emailErrorDelay) {
        this.emailAtBatchEnd = emailAtBatchEnd;
        this.emailAtError = emailAtError;
        this.emailErrorDelay = emailErrorDelay;
    }

    public boolean isEmailAtBatchEnd() {
        return emailAtBatchEnd;
    }

    public void setEmailAtBatchEnd(final boolean emailAtBatchEnd) {
        this.emailAtBatchEnd = emailAtBatchEnd;
    }

    public boolean isEmailAtError() {
        return emailAtError;
    }

    public void setEmailAtError(final boolean emailAtError) {
        this.emailAtError = emailAtError;
    }

    public int getEmailErrorDelay() {
        return emailErrorDelay;
    }

    public void setEmailErrorDelay(final int emailErrorDelay) {
        this.emailErrorDelay = emailErrorDelay;
    }
}
