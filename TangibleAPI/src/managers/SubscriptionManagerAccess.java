/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import restful.streaming.StreamingThread;

/**
 *
 * @author leo
 */
public enum SubscriptionManagerAccess {

  INSTANCE;
  private SubscriptionManager _singleton;

  private SubscriptionManagerAccess() {
    _singleton = new SubscriptionManagerImpl();
  }

  public static SubscriptionManager getInstance() {
    return INSTANCE._singleton;
  }

  private class SubscriptionManagerImpl implements SubscriptionManager {
    
    private Map<UUID, StreamingThread> _subsSockets;
    
    private SubscriptionManagerImpl() {
      _subsSockets = new HashMap<UUID, StreamingThread>();
    }

    @Override
    public boolean existsStreaming(UUID appuuid) {
      return _subsSockets.containsKey(appuuid);
    }

    @Override
    public StreamingThread getStreamingSocket(UUID appuuid) throws NoSuchSocket {
      if(!existsStreaming(appuuid)){
        throw new NoSuchSocket(appuuid.toString());
      }
      //else
      return _subsSockets.get(appuuid);
    }

    @Override
    public StreamingThread createStreamingSocket(UUID appuuid) throws AlreadyExistingSocket {
      StreamingThread newSocket = null;
      
      
      return newSocket;
    }

    @Override
    public void addEventSubscription(UUID appuuid, String device, String[] events) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeEventSubscription(UUID appuuid, String device, String[] events) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
