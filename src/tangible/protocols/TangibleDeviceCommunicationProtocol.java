/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols;

import restful.streaming.StreamingThread;
import tangible.devices.TangibleDevice;

/**
 *
 * @param <T> Type of device supported by this Protocol
 * @author leo
 */
public interface TangibleDeviceCommunicationProtocol<T extends TangibleDevice>{
  
  public boolean isConnected();
  public void showColor(int r, int g, int b);
  public void showColor(int color);
  public void addAllEventsNotification(StreamingThread sTh);
}
