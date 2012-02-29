/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols;

import restful.streaming.StreamingThread;
import tangible.devices.TangibleDevice;

/**
 *
 * @param <T> supported device type
 * @author leo
 */
public interface TangibleGatewayCommunicationProtocol<T extends TangibleDevice> {
  
  boolean isConnected();
  void showColor(int r, int g, int b, String[] ids);
  void showColor(int r, int g, int b, T[] devs);
  void showColor(int color, String[] ids);
  void showColor(int color, T[] devs);
  void addAllEventsNotification(StreamingThread sTh, String[] devs);
  void addAllEventsNotification(StreamingThread sTh, T[] devs);
}
