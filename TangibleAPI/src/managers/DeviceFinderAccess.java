/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tangible.devices.TangibleDevice;
import tangible.protocols.DeviceAuthenticationProtocol;
import tangible.utils.AbsLoopingThread;
import tangible.utils.DeviceContainer;
import tangible.utils.exceptions.WrongProtocolException;

/**
 * provide a
 *
 * @author leo
 */
public enum DeviceFinderAccess implements SingletonAccessor<DeviceFinder> {
  INSTANCE;
  
  

  private static class DeviceFinderImpl extends AbsLoopingThread implements DeviceFinder {

    private ServerSocket _listening_sock;
    private DeviceContainer _devices;
    //private DeviceContainer _free;
    private final Object _sync;
    private DeviceFinderProperties properties = null;
    //private boolean _never_tried;

    private DeviceFinderImpl() {
      this.loadProperties();
      try {
        this._listening_sock = new ServerSocket(properties.port());
      } catch (IOException ex) {
        Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.SEVERE, null, ex);
        //TODO decide what should be done in this case
      }

      _devices = new DeviceContainer();
      //_free = new DeviceContainer();
      _sync = new Object();
      //_never_tried = true;
    }

    private void attemptDeviceAuthentication() throws IOException {
      try {
        while (true) {//looping the device authentication protocol to connect as many devices as possible
          Socket sock = _listening_sock.accept();
          try {
            //attemptDeviceAuthenticationV1(sock);
            attemptDeviceAuthenticationV2(sock);
          } catch (WrongProtocolException ex) {
            Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.SEVERE, "!!!Protocol error!!!", ex);
            //TODO throw the device away!
          }

        }
      } catch (SocketTimeoutException ex) {
        //exiting on a timeout Exception 
        //the discovery session is over for now.
        //Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.INFO, "expected socket timeout");
      }
    }
    
    private void attemptDeviceAuthenticationV1(Socket sock) throws IOException{
      DeviceAuthenticationProtocol auth = new DeviceAuthenticationProtocol(sock, 10000);
      TangibleDevice dev = auth.authenticateDevice();
      _devices.add(dev);
      //_free.add(dev);
      dev.attachSocket(sock);
      Logger.getLogger(DeviceFinderImpl.class.getName()).
          log(Level.INFO, "a device was added: hashcode:{0}", dev.getHashCode());
      //TODO_LATER check the device compatibility and so...

      auth.finalizeAuthentication();
      Logger.getLogger(DeviceFinderImpl.class.getName()).
          log(Level.INFO, "the device has been notified of this success");
    }
    private void attemptDeviceAuthenticationV2(Socket sock) throws IOException{
      DeviceAuthenticationProtocol authProc = new DeviceAuthenticationProtocol(sock, 10000, properties.protocolVersion());
      authProc.authenticateDevices(new DeviceAuthenticationProtocol.DeviceFoundCallBack() {
        @Override
        public Boolean callback(TangibleDevice arg) {
          boolean success = _devices.add(arg);
          //TODO_LATER check more compatibility things!
          //nothing else to do here!
          return success;
        }
      });
    }

    @Override
    public void makeManualDeviceAuthenticationAttempt() {
      synchronized (_sync) {
        _sync.notify();
      }
    }

    private void loadProperties() {
      properties = new DeviceFinderProperties();
      String[] propertiesURI = 
        {
          "../tangibleProperties.properties",
          "./tangibleProperties.properties",
          "../../tangibleProperties.properties",
          "../src/tangible/tangibleProperties.properties",
          "/Users/leo/Documents/master-thesis/01-code/02-FirstDesign/TangibleAPI/src/tangible/tangibleProperties.properties"
        };
      boolean success = false;
      for(int i = 0; !success && i < propertiesURI.length; i++){
        try {
          properties.load(this.getClass().getClassLoader().getResourceAsStream(propertiesURI[i]));
          //if no exception occured
          success = true;
        } catch (NullPointerException ex) {
          Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.INFO, "loading <<{0}>> wasn''t a success", propertiesURI[i]);
        } catch (IOException ex) {
          Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.INFO, "loading <<{0}>> wasn''t a success", propertiesURI[i]);
        }
      }
      if(!success){
        loadDefaultProperties();
      }
    }

    private void loadDefaultProperties() {
      //load defaults
      //properties.setProperty("discovery_behaviour", "AT_START_UP");
      properties.setProperty("discovery_behaviour", "ALWAYS");
      properties.setProperty("discovery_port", "60000");
      properties.setProperty("discovery_protocol_version", "0.3");
      properties.setProperty("discovery_timeout", "30");
      //TODO add more property is needed
    }

    @Override
    public String getProperty(String key) {
      return properties.getProperty(key);
    }

    private boolean arePropertiesLoaded() {
      return properties != null;
    }

    @Override
    protected void runningSetup() {
      /*
       * TODO - start the server socket and wait incomming connection from
       * devices for 30 seconds - for each connection create the matching
       * DeviceHandler and add it to the global list of connected devices - then
       * put the thread to sleep, - is waked up: back to the first step!
       */
      Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.INFO, "DeviceFinder starting now");
      try {
        int timeout = properties.timeout() * 1000;
        _listening_sock.setSoTimeout(timeout);
      } catch (SocketException ex) {
        Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    protected void loopingProcess() {
      try {
        DeviceFinderProperties.Behaviour discovery_behaviour = properties.behaviour();
        attemptDeviceAuthentication();
        if (discovery_behaviour == DeviceFinderProperties.Behaviour.AT_START_UP
            || discovery_behaviour == DeviceFinderProperties.Behaviour.MANUAL ) {
          synchronized (_sync) {
            try {
              _sync.wait();
            } catch (InterruptedException ex) {
              //let' s do nothing about this exception
            }
          }
        }
      } catch (IOException ex) {
        Logger.getLogger(DeviceFinderImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @Override
    public List<TangibleDevice> getDevices() {
      List<TangibleDevice> device_list = new LinkedList<TangibleDevice>();
      for (Iterator<TangibleDevice> ite = _devices.iterator(); ite.hasNext();){
        device_list.add(ite.next());
      }
      return device_list;
    }

    @Override
    public boolean reserveDevice(String id) {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean existsDevice(String id) {
      return _devices.containsId(id);
    }

    @Override
    public TangibleDevice getDevice(String device_id) {
      return _devices.getById(device_id);
    }

    @Override
    public DeviceFinderProperties getProperty() {
      return properties;
    }
  }
  
  private DeviceFinder _finder;

  private DeviceFinderAccess() {
    _finder = new DeviceFinderImpl();
  }
  
  public static DeviceFinder getInstance(){
    return INSTANCE._finder;
  }
  
}
