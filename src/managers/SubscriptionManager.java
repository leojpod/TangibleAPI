/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import java.util.UUID;
import restful.streaming.StreamingThread;
import restful.utils.ApplicationException;

/**
 *
 * @author leo
 */
public interface SubscriptionManager {
  public static class NoSuchSocket extends ApplicationException{
    private static final long serialVersionUID = 1L;

    public NoSuchSocket(String appuuid) {
      super(appuuid);
    }
    
    @Override
    public String getMessage() {
      return super.getMessage()+"\n\t-> there is no streaming socket for this application";
    }
    
  }
  public static class AlreadyExistingSocket extends ApplicationException{
    private static final long serialVersionUID = 1L;

    public AlreadyExistingSocket(String appuuid) {
      super(appuuid);
    }
    @Override
    public String getMessage() {
      return super.getMessage()+"\n\t-> a socket already exist for this application!";
    }
  }
  
  boolean existsStreaming(UUID appuuid);
  StreamingThread getStreamingSocket(UUID appuuid) throws NoSuchSocket;
  StreamingThread createStreamingSocket(UUID appuuid) throws AlreadyExistingSocket;
  
  void addEventSubscription(UUID appuuid, String device, String[] events);
  void removeEventSubscription(UUID appuuid, String device, String[] events);
  
}
