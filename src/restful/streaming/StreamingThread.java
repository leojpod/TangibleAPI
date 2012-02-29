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
  private static class EventStreamingProtocol extends AbsJsonTCPProtocol {

    public EventStreamingProtocol(Socket s) throws IOException {
      super(s);
    }

    public void sendEvent(JsonElement event) {
      super.sendJsonEventMsg(event);
    }

    public void sendEvent(Object event) {
      this.sendEvent(new Gson().toJsonTree(event));
    }
  }
  private ServerSocket _sock;
  private EventStreamingProtocol _talk;
  private boolean _setup;
  private List<CallBack<Void,Void>> _listeners;

  public StreamingThread() throws IOException {
    _sock = new ServerSocket(0);
    _sock.setSoTimeout(5000);//TODO_LATER add this value in the properties!
    _setup = false;
    _listeners = new ArrayList<CallBack<Void, Void>>();
  }

  @Override
  public void run() {
    try {
      Socket clientSock = _sock.accept();
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
