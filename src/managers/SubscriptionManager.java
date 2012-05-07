/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import commons.ApiException;
import java.io.IOException;
import java.util.UUID;
import javax.ws.rs.core.Response;
import restful.streaming.StreamingThread;
import tangible.utils.exceptions.DeviceNotFoundException;

/**
 *
 * @author leo
 */
public interface SubscriptionManager {

  public static class NoSuchSocket extends ApiException{
    private static final long serialVersionUID = 1L;
    public NoSuchSocket(String appuuid) {
      super(Response.Status.CONFLICT, "there is no streaming socket for this application ("+appuuid+")");
    }
    public NoSuchSocket(UUID appuuid){
      this(appuuid.toString());
    }
  }
  public static class AlreadyExistingSocket extends ApiException{
    private static final long serialVersionUID = 1L;
    public AlreadyExistingSocket(String appuuid) {
      super(Response.Status.CONFLICT, "a socket already exist for this application ("+appuuid+")");
    }
    public AlreadyExistingSocket(UUID appuuid){
      this(appuuid.toString());
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
