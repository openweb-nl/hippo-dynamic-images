package nl.openweb.hippo.dynamicimage;

/**
 * @author Ivor Boers
 *
 */
public class JobExecutionException extends Exception {
    private static final long serialVersionUID = -2932511936657493746L;

    /**
     * @param message the message
     */
    public JobExecutionException(String message) {
        super(message);
    }
    
    /**
     * @param message the message
     * @param e the cause
     */
    public JobExecutionException(String message, Throwable e) {
        super(message, e);
    }
}
