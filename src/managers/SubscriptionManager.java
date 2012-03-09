/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import java.io.IOException;
import java.util.UUID;
import restful.streaming.StreamingThread;
import restful.utils.ApplicationException;
import tangible.utils.exceptions.DeviceNotFoundException;

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
    public NoSuchSocket(UUID appuuid){
      this(appuuid.toString());
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
    public AlreadyExistingSocket(UUID appuuid){
      this(appuuid.toString());
    }
    
    @Override
    public String getMessage() {
      return super.getMessage()+"\n\t-> a socket already exist for this application!";
    }
  }
  
  boolean existsStreaming(UUID appuuid);
  StreamingThread getStreamingSocket(UUID appuuid) throws NoSuchSocket;
  StreamingThread createStreamingSocket(UUID appuuid) throws AlreadyExistingSocket,IOException;
  
  void addEventSubscription(UUID appuuid, String device, String[] events) throws NoSuchSocket, DeviceNotFoundException;
  void removeEventSubscription(UUID appuuid, String device, String[] events) throws NoSuchSocket, DeviceNotFoundException;
  void addEventsSubscription(UUID appuuid, String device) throws NoSuchSocket, DeviceNotFoundException;
  void removeEventsSubscription(UUID appuuid, String device) throws NoSuchSocket, DeviceNotFoundException;
  
}
