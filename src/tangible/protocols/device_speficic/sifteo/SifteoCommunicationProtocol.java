/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols.device_speficic.sifteo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.streaming.StreamingThread;
import tangible.devices.SifteoCubeDevice;
import tangible.protocols.TangibleDeviceCommunicationProtocol;
import utils.ColorHelper;

/**
 *
 * @author leo
 */
public class SifteoCommunicationProtocol
        implements TangibleDeviceCommunicationProtocol<SifteoCubeDevice> {

  private class SifteoPicture {
    SifteoColorBlocks[] pictureBlocks;
    transient List<SifteoColorBlocks> _blocks;
    public SifteoPicture() {
      _blocks = new ArrayList<SifteoColorBlocks>();
    }
    public void addColorBlock(SifteoColorBlocks cb){
      _blocks.add(cb);
    }
    public void flush(){
      pictureBlocks = _blocks.toArray(pictureBlocks);
    }
  }

  private class SifteoColorBlocks {
    SifteoBlocks[] blocks;
    transient List<SifteoBlocks> _blocks;
    SifteoColor color;

    public SifteoColorBlocks() {
      _blocks = new ArrayList<SifteoBlocks>();
    }
    public void addBlock(SifteoBlocks b){
      _blocks.add(b);
    }
    public void flush(){
      blocks = _blocks.toArray(blocks);
    }
  }
  private class SifteoColor{
    int r, g, b;

    public SifteoColor(int c) {
      int[] rgb = ColorHelper.decompose(c);
      r = rgb[0]; g = rgb[1]; b = rgb[2];
    }
    
  }
  private class SifteoBlocks{
    int x, y, w, h;
  }
  private SiftDriverCommunicationProtocol _driverTalk;
  private final String _cubeId;
  private final String[] _id_in_array;

  public SifteoCommunicationProtocol(SiftDriverCommunicationProtocol talk, String cubeId) {
    this._driverTalk = talk;
    _cubeId = cubeId;
    _id_in_array = new String[1];
    _id_in_array[0] = _cubeId;
  }

  @Override
  public boolean isConnected() {
    return _driverTalk.isConnected();
  }

  @Override
  public void showColor(int r, int g, int b) {
    System.out.println("SifteoCommunicationProtocol: Show color -> _cubeId = " + _cubeId);
    _driverTalk.showColor(r, g, b, _id_in_array);
  }

  @Override
  public void showColor(int color) {

    System.out.println("SifteoCommunicationProtocol: Show color -> _cubeId = " + _cubeId);
    _driverTalk.showColor(color, _id_in_array);
  }

  @Override
  public void addAllEventsNotification(StreamingThread sTh) {
    _driverTalk.addAllEventsNotification(sTh, _id_in_array);
  }

  @Override
  public void showPicture(BufferedImage img) {
    BufferedImage scaled;
    int h = img.getHeight();
    int w = img.getWidth();
    
    float ratio_x = 128/w;
    float ratio_y = 128/h;
    float ratio =  (ratio_x < 1 || ratio_y < 1)? ((ratio_x < ratio_y)? ratio_x: ratio_y): ((ratio_x < ratio_y)? ratio_y: ratio_x);
    
    AffineTransform transform = new AffineTransform();
    transform.scale(ratio, ratio);
    AffineTransformOp scaleOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    scaled = scaleOp.filter(img, null);
    
    
    for(int x = 0; x < 128; x ++){
      for(int y = 0; y < 128; y++){
        int c = scaled.getRGB(x, y);
        
      }
    }
  }
}
