/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import commons.ApiException;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;
import tangible.enums.Capacity;
import utils.ColorHelper;
import utils.Couple;

/**
 *
 * @author leo
 */
public class TangibleGatewayProtocol extends AbsJsonTCPProtocol{

	
	public static class UnSupportedMethodException extends ApiException {
		private static final long serialVersionUID = 1L;

		public UnSupportedMethodException(Capacity c) {
			super(Response.Status.BAD_REQUEST, 
					"this device does not support this operation: "+c.name());
		}
	}
	private static class ScreenSize  extends Couple<Integer, Integer>{

		public ScreenSize(int heigth, int width) {
			super(heigth, width);
		}
		
		public int getHeight() { return _t; }
		public int getWidth() { return _u; }
	}
	
	
	public final String type;
	public final String protocol_version;
	public final Gson gson = new Gson();
	public List<Capacity> _capacities;
	public ScreenSize _size;
	
	public TangibleGatewayProtocol(Socket s, String type, String protocol_version, List<Capacity> capacities) throws IOException {
		super(s);
		this.type = type;
		this.protocol_version = protocol_version;
		this._capacities = capacities;
	}
	
	public void setScreenSize(int height, int width) {
		_size = new ScreenSize(height, width);
	}
	
	
	public Capacity[] getCapacities() {
		return _capacities.toArray(new Capacity[0]);
	}
	
	public boolean canDo(Capacity c) {
		return _capacities.contains(c);
	}
	private void assertCanDo(Capacity c) {
		if (!canDo(c)) {
			throw new UnSupportedMethodException(c);
		}
	}
	private JsonObject buildCommand(String command, JsonObject params, String[] devIds){
		JsonObject msg = new JsonObject();
		msg.addProperty("command", command);
		params.add("devices", gson.toJsonTree(devIds));
		msg.add("params", params);
		return msg;
	}
	private void sendEventCommand(String command, JsonObject params, String[] devIds){
		JsonObject msg = buildCommand(command, params, devIds);
		this.sendJsonEventMsg(msg);
	}
	
	public void showColor(int r, int g, int b, String[] devs){
		//TODO: should we check the devIds here to make sure they are all from this gateway? 
		//We will assume that there is no need to check such thing here: it has to be performed higer!
		assertCanDo(Capacity.show_color);
		//let's build up the message and send it!
		JsonObject params = new JsonObject();
		params.add("color", ColorHelper.toJson(r,g,b));
		sendEventCommand("show_color", params, devs);
	}
	public void showColor(int color, String[] devs) {
		int[] rgb = ColorHelper.decompose(color);
		this.showColor(rgb[0], rgb[1], rgb[2], devs);
	}
	
	public void showPicture(BufferedImage img, String[] devId) {
		assertCanDo(Capacity.show_picture);
		//now let's talk!
		if (_size == null) {
			throw new ApiException(Response.Status.BAD_REQUEST, "The screen size hasn't been specified!");
		} else {
			BufferedImage scaled;
			double h = img.getHeight();
			double w = img.getWidth();
			
			double ratio_x = _size.getWidth() / w;
			double ratio_y = _size.getHeight() / h;
			double ratio = (ratio_x < 1 || ratio_y < 1) ? ((ratio_x < ratio_y) ? ratio_x : ratio_y) : ((ratio_x < ratio_y) ? ratio_y : ratio_x);
			
			AffineTransform transform = new AffineTransform();
			transform.scale(ratio, ratio);
			AffineTransformOp scaleOperation = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
			scaled = scaleOperation.filter(img, null);
			scaled.flush();
			
			int[] byteArray = new int[_size.getWidth() * _size.getHeight() * 4];
			DataBuffer buffer = scaled.getData().getDataBuffer();
			if (buffer instanceof DataBufferByte) {
				byte[] rgb;
				DataBufferByte dataBufferByte = (DataBufferByte) buffer;
				rgb = dataBufferByte.getData();
				System.out.println("rgb data size is : "+rgb.length);
				for(int i = 0; i < byteArray.length && i < rgb.length; i++){
					byteArray[i] = rgb[i] & 0xff;
					if(rgb[i] != -1){
						System.out.print("  b:"+rgb[i]+"/"+byteArray[i]);
					}
				}
				if(rgb.length < byteArray.length) {
					Arrays.fill(byteArray, rgb.length, byteArray.length, 0xff);
				}
			} else if (buffer instanceof DataBufferInt) {
				DataBufferInt dataBufferInt = (DataBufferInt) buffer;
				int[] rgbInt = dataBufferInt.getData();
				System.out.println("rgbInt data size is : " + rgbInt.length + " compared to byteArray : "+byteArray.length);
				for (int i = 0; i < rgbInt.length && 4*i < byteArray.length; i ++){
					Color color = new Color (rgbInt[i]);
					//alpha
					byteArray[4*i] = color.getAlpha();
					//b
					byteArray[4*i+1] = color.getBlue();
					//g
					byteArray[4*i+2] = color.getGreen();
					//r
					byteArray[4*i+3] = color.getRed();
				}

				if(4* rgbInt.length < byteArray.length) {
					Arrays.fill(byteArray, 4* rgbInt.length, byteArray.length, 0xff);
				}
			} else {
				throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR,
						"picture format not recognized");
			}
			
			JsonElement jsonPic = gson.toJsonTree(byteArray);
			JsonObject params = new JsonObject();
			params.add("picture", jsonPic);
			this.sendEventCommand("show_picture", params, devId);
		}
	}
	
	public void showText(String text_msg, String[] devIds){
		this.showText(text_msg, 0, devIds);
	}
	public void showText(String text_msg, int color, String[] devIds) {
		int[] rgb = ColorHelper.decompose(color);
		this.showText(text_msg, rgb[0], rgb[1], rgb[2], devIds);
	}
	public void showText(String text_msg, int r, int g, int b, String[] devIds) {
		assertCanDo(Capacity.show_text);
		JsonObject params = new JsonObject();
		params.addProperty("text_msg", text_msg);
		params.add("color", ColorHelper.toJson(r,g,b));
		this.sendEventCommand("show_message", params, devIds);
	}
	
	public void fadeColor(int color, String[] devIds){
		int[] rgb = ColorHelper.decompose(color);
		this.fadeColor(rgb[0], rgb[1], rgb[2], devIds);
	}
	public void fadeColor(int r, int g, int b, String[] devIds){
		assertCanDo(Capacity.show_fade);
		JsonObject params = new JsonObject();
		params.add("color", ColorHelper.toJson(r,g,b));
		this.sendEventCommand("fade_color", params, devIds);
	}
	
}