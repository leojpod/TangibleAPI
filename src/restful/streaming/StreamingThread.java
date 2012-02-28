/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.streaming;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import tangible.protocols.AbsJsonTCPProtocol;

/**
 *
 * @author leo
 */
public class StreamingThread extends Thread{
  private static class EventStreamingProtocol extends AbsJsonTCPProtocol{
    public EventStreamingProtocol(Socket s) throws IOException {
      super(s);
    }
  }

  private ServerSocket _sock;
  
  public StreamingThread() throws IOException{
    _sock = new ServerSocket(0);
    _sock.setSoTimeout(5000);//TODO_LATER add this value in the properties!
    
  }
  
  
}
