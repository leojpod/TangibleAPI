/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.streaming;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tangible.protocols.AbsJsonTCPProtocol;
import tangible.utils.CallBack;

/**
 *
 * @author leo
 */
public class StreamingThread extends Thread {

  public static class AlreadySetupException extends RuntimeException{
    private static final long serialVersionUID = 1L;
  }
  private interface EventStreaming {
    public void sendEvent(JsonElement event);
    public void sendEvent(Object event);
  }
  private class EventStreamingStacker implements CallBack<Void, Void>, EventStreaming{

    private List<JsonElement> _waitingEvents;
    public EventStreamingStacker(){
      _waitingEvents = new LinkedList<JsonElement>();
    }
    
    @Override
    public Void callback(Void arg) {
      //now that the end app is connected, let's send it all the messages it missed
      for (JsonElement event : _waitingEvents) {
        StreamingThread.this._talk.sendEvent(event);  
      }
      return null;
    }

    @Override
    public void sendEvent(JsonElement event) {
      //while waiting for the end app to connect, we still stack the events
      //althought this feature might not be wishable... 
      _waitingEvents.add(event);
    }

    @Override
    public void sendEvent(Object event) {
      this.sendEvent(new Gson().toJsonTree(event));
    }
    
  }
  private static class EventStreamingProtocol extends AbsJsonTCPProtocol implements EventStreaming{

    public EventStreamingProtocol(Socket s) throws IOException {
      super(s);
    }

    @Override
    public void sendEvent(JsonElement event) {
      super.sendJsonEventMsg(event);
    }

    @Override
    public void sendEvent(Object event) {
      this.sendEvent(new Gson().toJsonTree(event));
    }
  }
  private ServerSocket _sock;
  private EventStreaming _talk;
  private boolean _setup;
  private List<CallBack<Void,Void>> _listeners;

  public StreamingThread() throws IOException {
    _sock = new ServerSocket(0);
    _sock.setSoTimeout(5000);//TODO_LATER add this value in the properties!
    _setup = false;
    _listeners = new ArrayList<CallBack<Void, Void>>();
    EventStreamingStacker stacker = new EventStreamingStacker();
    _talk = stacker;
    _listeners.add(stacker);
  }

  @Override
  public void run() {
    try {
      Socket clientSock = _sock.accept();
      System.out.println("Socket.accept worked!!!! hurra");
      _talk = new EventStreamingProtocol(clientSock);
      _setup = true;
      for (CallBack<Void, Void> callBack : _listeners) {
        callBack.callback(null);
      }
    } catch (SocketTimeoutException ex) {
      Logger.getLogger(StreamingThread.class.getName()).log(Level.INFO, "StreamingThread timed out!");
    } catch (IOException ex) {
      Logger.getLogger(StreamingThread.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public boolean isReady() {
    return _setup;
  }

  public void sendEvent(JsonElement event) {
    this._talk.sendEvent(event);
  }

  public void sendEvent(Object event) {
    this._talk.sendEvent(event);
  }
  
  public void addCallBack(CallBack<Void, Void> cb) throws AlreadySetupException{
    _listeners.add(cb);
  }
  
  public int getPort(){
    return _sock.getLocalPort();
  }
}
