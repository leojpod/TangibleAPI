/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols.device_speficic.sifteo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.streaming.StreamingThread;
import tangible.devices.SifteoCubeDevice;
import tangible.gateway.SiftDriver;
import tangible.protocols.AbsJsonTCPProtocol;
import tangible.protocols.TangibleGatewayCommunicationProtocol;
import tangible.utils.JsonMessageReadingThread;
import tangible.utils.JsonMessageReadingThread.JsonEventListener;
import tangible.utils.JsonProtocolHelper;
import tangible.utils.exceptions.WrongProtocolJsonSyntaxException;
import utils.ColorHelper;

/**
 *
 * @author leo
 */
public class SiftDriverCommunicationProtocol 
    extends AbsJsonTCPProtocol
    implements TangibleGatewayCommunicationProtocol<SifteoCubeDevice>{
  
  private final class StreamingThreadReporter implements JsonEventListener{

    private StreamingThread _th;
    //TODO_LATER store a list of events to which we subscribed plus a special 
    //    boolean to know when we are reporting everything (hence efficiency)
    public List<String> _followedDevices;

    public StreamingThreadReporter(StreamingThread th) {
      this._th = th;
      _followedDevices = new ArrayList<String>();
    }
    
    
    public StreamingThreadReporter(StreamingThread th, String[] devId) {
      this(th);
      this.addDevices(devId);
    }
    
    public void addEventNotification(String event){
      throw new UnsupportedOperationException("Not supported yet.");
    }
    public void addDevice(String devId){
      _followedDevices.add(devId);
    }
    public void addDevices(String[] devIds){
      for (String id : devIds) {
        this.addDevice(id);
      }
    }
    @Override
    public void callback(JsonObject t) {
      try{
        JsonObject msg = JsonProtocolHelper.assertObjectInObject(t, "msg");
        String event = JsonProtocolHelper.assertStringInObject(msg, "event");
        String devID = JsonProtocolHelper.assertStringInObject(msg, "devId");
        if(_followedDevices.contains(devID)){
          //TODO_LATER check that the event is one of the followed one
          //this is a valid and followed event let's send it!
          _th.sendEvent(t);
        }else{
          Logger.getLogger(StreamingThreadReporter.class.getName()).log(Level.INFO, "no one following this device... ");
        }
      }catch(WrongProtocolJsonSyntaxException ex){
        Logger.getLogger(StreamingThreadReporter.class.getName()).log(Level.INFO, "ignoring a badly formated message: {0}\n\tthe message was: {1}", new Object[]{ex.getMessage(), t.toString()});
      }
    }
    
  }
  
  private SiftDriver _driver;
  private JsonMessageReadingThread _readingThread;
  private List<StreamingThreadReporter> _reporters;
  
  public SiftDriverCommunicationProtocol(SiftDriver driver, Socket s) throws IOException{
    super(s);
    s.setSoTimeout(0);
    _driver = driver;
    _readingThread = new JsonMessageReadingThread(this.getInput());
    //_readingThread.start();
    _reporters = new ArrayList<StreamingThreadReporter>();
  }

  @Override
  public void showColor(int r, int g, int b, String[] ids) {
    JsonObject obj = new JsonObject();
    obj.addProperty("command", "show_color");
    //obj.addProperty("cube", cubeId);
    JsonObject rgb = new JsonObject();
    rgb.addProperty("r", r);
    rgb.addProperty("g", g);
    rgb.addProperty("b", b);
    //TODO_LATER check that the color match the requirement for sifteo
    JsonObject param = new JsonObject();
    param.add("color",rgb);
    param.add("cubes", new Gson().toJsonTree(ids));
    obj.add("param", param);
    
    this.sendJsonEventMsg(obj);
  }
  public void startReading(){
    _readingThread.start();
  }

  @Override
  public void showColor(int r, int g, int b, SifteoCubeDevice[] devs) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void showColor(int color, String[] ids) {
    int[] rgb = ColorHelper.decompose(color);
    this.showColor(rgb[0], rgb[1], rgb[2], ids);
  }

  @Override
  public void showColor(int color, SifteoCubeDevice[] devs) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addAllEventsNotification(StreamingThread sTh, String[] devs) {
    StreamingThreadReporter aReporter = new StreamingThreadReporter(sTh, devs);
    this._reporters.add(aReporter);
    this._readingThread.addEventListener(aReporter);
    //TODO notify the devices that they have to report the events
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "reportAllEvents");//TODO LATER send an array of events to report 
    JsonElement devsArray = new Gson().toJsonTree(devs);
    msg.add("params", devsArray);
    this.sendJsonCtrlMsg(msg);
  }

  @Override
  public void addAllEventsNotification(StreamingThread sTh, SifteoCubeDevice[] devs) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  
}
