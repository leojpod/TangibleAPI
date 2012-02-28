/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols.device_speficic.sifteo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import tangible.devices.SifteoCubeDevice;
import tangible.gateway.SiftDriver;
import tangible.protocols.AbsJsonTCPProtocol;
import tangible.protocols.TangibleGatewayCommunicationProtocol;
import utils.ColorHelper;

/**
 *
 * @author leo
 */
public class SiftDriverCommunicationProtocol 
    extends AbsJsonTCPProtocol
    implements TangibleGatewayCommunicationProtocol<SifteoCubeDevice>{
  
  private SiftDriver _driver;
  
  public SiftDriverCommunicationProtocol(SiftDriver driver, Socket s) throws IOException{
    super(s);
    _driver = driver;
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
  
  
}
