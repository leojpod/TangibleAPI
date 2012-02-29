/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import restful.streaming.StreamingThread;
import tangible.devices.TangibleDevice;
import tangible.protocols.TangibleDeviceCommunicationProtocol;

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
    private DeviceFinder _devMgr = DeviceFinderAccess.getInstance();
    
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

    @Override
    public void addEventsSubscription(UUID appuuid, String device) {
      if(!existsStreaming(appuuid)){
        throw new NoSuchSocket(appuuid.toString());
      }
      //else
      //find the device, get the talk, add a call back for all event messages
      //NOTE: we assume that the device exists and that the application is associated to it
      TangibleDevice dev = _devMgr.getDevice(device);
      TangibleDeviceCommunicationProtocol<? extends TangibleDevice> talk = dev.getTalk();
      talk.addAllEventsNotification(_subsSockets.get(appuuid));
    }

    @Override
    public void removeEventsSubscription(UUID appuuid, String device) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
