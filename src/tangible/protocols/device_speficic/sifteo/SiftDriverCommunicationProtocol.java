/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package tangible.protocols.device_speficic.sifteo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import commons.ApiException;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import restful.streaming.AbstractStreamingThread;
import tangible.devices.SifteoCubeDevice;
import tangible.gateway.SiftDriver;
import tangible.protocols.AbsJsonTCPProtocol;
import tangible.protocols.TangibleGatewayCommunicationProtocol;
import tangible.utils.JsonMessageReadingThread;
import tangible.utils.JsonMessageReadingThread.JsonEventListener;
import tangible.utils.JsonProtocolHelper;
import tangible.utils.Listener;
import tangible.utils.exceptions.WrongProtocolJsonSyntaxException;
import utils.ColorHelper;
import utils.ColorHelper.InvalidColorException;

/**
 *
 * @author leo
 */
public class SiftDriverCommunicationProtocol
		extends AbsJsonTCPProtocol
		implements TangibleGatewayCommunicationProtocol<SifteoCubeDevice> {

	private class SifteoPicture {

		SifteoColorBlocks[] pictureBlocks;
		transient SortedMap<SifteoColor, SifteoColorBlocks> _blocks;

		public SifteoPicture() {
			_blocks = new TreeMap<SifteoColor, SifteoColorBlocks>();
		}

		public void addColorBlocks(SifteoColorBlocks cb) {
			_blocks.put(cb.color, cb);
		}

		public void addSimpleBlock(SifteoColor c, SifteoBlock b) {
			SifteoColorBlocks cb = _blocks.get(c);
			if (cb == null) {
				cb = new SifteoColorBlocks();
				cb.color = c;
				addColorBlocks(cb);
			}
			cb.addBlock(b);
		}

		public void flush() {
			for (SifteoColorBlocks cb : _blocks.values()) {
				cb.flush();
			}
			pictureBlocks = _blocks.values().toArray(new SifteoColorBlocks[0]);
		}
	}

	private class SifteoColorBlocks implements Comparable<Object> {

		SifteoBlock[] blocks;
		transient List<SifteoBlock> _blocks;
		SifteoColor color;

		public SifteoColorBlocks() {
			_blocks = new ArrayList<SifteoBlock>();
		}

		public void addBlock(SifteoBlock b) {
			_blocks.add(b);
		}

		public void flush() {
			blocks = _blocks.toArray(new SifteoBlock[0]);
		}

		@Override
		public int compareTo(Object o) {
			if (o instanceof SifteoColorBlocks) {
				return compareTo((SifteoColorBlocks) o);
			} else if (o instanceof SifteoColor) {
				return compareTo((SifteoColor) o);
			} else {
				throw new ClassCastException("not comparable with this kind of object");
			}
		}

		public int compareTo(SifteoColorBlocks t) {
			return color.compareTo(t.color);
		}
	}

	private class SifteoColor implements Comparable<SifteoColor> {

		int r, g, b;
		transient int c;

		public SifteoColor(int c) {
			Color color = new Color(c);
			this.c = color.getRGB();
			r = color.getRed();
			g = color.getGreen();
			b = color.getBlue();
			if (!ColorHelper.isValidColor(r, g, b)) {
				throw new InvalidColorException(c);
			}
		}

		@Override
		public int compareTo(SifteoColor that) {
			return this.c - that.c;
		}
	}

	private class SifteoBlock {

		int x, y, w, h;

		public SifteoBlock(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}

	private final class StreamingThreadReporter implements JsonEventListener {

		private AbstractStreamingThread _th;
		//TODO_LATER store a list of events to which we subscribed plus a special
		//    boolean to know when we are reporting everything (hence efficiency)
		public List<String> _followedDevices;

		public StreamingThreadReporter(AbstractStreamingThread th) {
			this._th = th;
			_followedDevices = new ArrayList<String>();
		}

		public StreamingThreadReporter(AbstractStreamingThread th, String[] devId) {
			this(th);
			this.addDevices(devId);
		}

		public void addEventNotification(String event) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void addDevice(String devId) {
			_followedDevices.add(devId);
		}

		public void addDevices(String[] devIds) {
			for (String id : devIds) {
				this.addDevice(id);
			}
		}

		@Override
		public void callback(JsonObject t) {
			try {
				JsonObject msg = JsonProtocolHelper.assertObjectInObject(t, "msg");
				String event = JsonProtocolHelper.assertStringInObject(msg, "event");
				String devID = JsonProtocolHelper.assertStringInObject(msg, "devId");
				if (_followedDevices.contains(devID)) {
					//TODO_LATER check that the event is one of the followed one
					//this is a valid and followed event let's send it!
					_th.sendEvent(t);
				} else {
//          Logger.getLogger(StreamingThreadReporter.class.getName()).log(Level.INFO, "no one following this device... ");
				}
			} catch (WrongProtocolJsonSyntaxException ex) {
				Logger.getLogger(StreamingThreadReporter.class.getName()).log(Level.INFO, "ignoring a badly formated message: {0}\n\tthe message was: {1}", new Object[]{ex.getMessage(), t.toString()});
			}
		}
	}
	private SiftDriver _driver;
	private JsonMessageReadingThread _readingThread;
	private List<StreamingThreadReporter> _reporters;

	public SiftDriverCommunicationProtocol(SiftDriver driver, Socket s) throws IOException {
		super(s);
		s.setSoTimeout(0);
		_driver = driver;
		_readingThread = new JsonMessageReadingThread(this.getInput());
		_readingThread.setStreamOverListener(new Listener<Void>() {

			@Override
			public void callback(Void t) {
				//TODO remove devices!
				//Logger.getLogger(SiftDriverCommunicationProtocol.class.getName()).log(Level.INFO, "TODO: remove the devices that just disconnect from the device list!");
				_driver.handleDisconnection();
			}
		});
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
		param.add("color", rgb);
		param.add("cubes", new Gson().toJsonTree(ids));
		obj.add("params", param);

		this.sendJsonEventMsg(obj);
	}

	public void startReading() {
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
	public void addAllEventsNotification(AbstractStreamingThread sTh, String[] devs) {
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
	public void addAllEventsNotification(AbstractStreamingThread sTh, SifteoCubeDevice[] devs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void showPicture(BufferedImage img, String[] devs) {
		//show_simplePicture(devs);
		String type;
		switch (img.getType()) {
			case BufferedImage.TYPE_INT_ARGB:
				type = "INT_ARGB";
				break;
			case BufferedImage.TYPE_INT_RGB:
				type = "INT_RGB";
				break;
			case BufferedImage.TYPE_3BYTE_BGR:
				type = "3BYTES_BGR";
				break;
			case BufferedImage.TYPE_CUSTOM:
				type = "custom";
				break;
			case BufferedImage.TYPE_INT_ARGB_PRE:
				type = "INT_ARGB_PRE";
				break;
			default:
				type = "other";
				break;
		}
    Logger.getLogger(SiftDriverCommunicationProtocol.class.getName()).log(Level.INFO, "bufferedImage color system is : {0} which is also known as :{1}", new Object[]{img.getType(), type});
		BufferedImage scaled;//= new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		double h = img.getHeight();
		double w = img.getWidth();

		double ratio_x = 128.0 / w;
		double ratio_y = 128.0 / h;
		double ratio = (ratio_x < 1 || ratio_y < 1) ? ((ratio_x < ratio_y) ? ratio_x : ratio_y) : ((ratio_x < ratio_y) ? ratio_y : ratio_x);
//    Logger.getLogger(SiftDriverCommunicationProtocol.class.getName()).log(Level.INFO, "scaling the picture to match the right size");

		AffineTransform transform = new AffineTransform();
		transform.scale(ratio, ratio);
		AffineTransformOp scaleOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		scaled = scaleOp.filter(img, null);
		scaled.flush();
		System.out.println("scaled size is: " + scaled.getWidth()+"x"+scaled.getHeight());

		int[] byteArray = new int[128*128*4];
//
//		for (int y = 0, i = 0 ; y < 128; y++, i += 4) {
//			for (int x = 0; x < 128; x++) {
//				int pixel = scaled.getRGB(x, y);
//				Color color = new Color(pixel);
//				//b
//				byteArray[i] = color.getBlue();
//				//g
//				byteArray[i+1] = color.getGreen();
//				//r
//				byteArray[i+2] = color.getRed();
//				//alpha
//				byteArray[i+3] = color.getAlpha();
//			}
//		}

		
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
		

		JsonElement jsonPic = new Gson().toJsonTree(byteArray);
		JsonObject msg = new JsonObject();
//    msg.addProperty("command", "show_json_picture");
		msg.addProperty("command", "show_picture");
		JsonObject params = new JsonObject();
		params.add("cubes", new Gson().toJsonTree(devs));
		params.add("picture", jsonPic);
		msg.add("params", params);
		this.sendJsonEventMsg(msg);
//    Logger.getLogger(SiftDriverCommunicationProtocol.class.getName()).log(Level.INFO, "picture command sent!");
//    Logger.getLogger(SiftDriverCommunicationProtocol.class.getName()).log(Level.INFO, "the sent command looks like : {0}", msg.toString());
	}

	@Override
	public void showPicture(BufferedImage img, SifteoCubeDevice[] devs) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private void show_simplePicture(String[] devs) {
		SifteoPicture pic = new SifteoPicture();

		SifteoColor c = new SifteoColor(0x00ff00);
		SifteoBlock b = new SifteoBlock(0, 0, 32, 32);
		pic.addSimpleBlock(c, b);

		c = new SifteoColor(0xff00ff);
		b = new SifteoBlock(32, 32, 32, 32);
		pic.addSimpleBlock(c, b);

		c = new SifteoColor(0x0000ff);
		b = new SifteoBlock(64, 64, 32, 32);
		pic.addSimpleBlock(c, b);

		pic.flush();

		JsonElement jsonPic = new Gson().toJsonTree(pic);
		JsonObject msg = new JsonObject();
		msg.addProperty("command", "show_json_picture");
		JsonObject params = new JsonObject();
		params.add("cubes", new Gson().toJsonTree(devs));
		params.add("picture", jsonPic);
		msg.add("params", params);
		this.sendJsonEventMsg(msg);
	}

	@Override
	public void showText(String msg, String[] devs) {
		this.showText(msg, 0, devs);
	}

	@Override
	public void showText(String msg, int color, String[] devs) {
		JsonObject cmd = new JsonObject();
		cmd.addProperty("command", "show_message");
		JsonObject params = new JsonObject();
		params.add("cubes", new Gson().toJsonTree(devs));
		params.addProperty("text_msg", msg);
		int[] rgb_color = ColorHelper.decompose(color);
		JsonObject rgb = new JsonObject();
		rgb.addProperty("r", rgb_color[0]);
		rgb.addProperty("g", rgb_color[1]);
		rgb.addProperty("b", rgb_color[2]);
		params.add("color", rgb);
		cmd.add("params", params);
		this.sendJsonEventMsg(cmd);
	}

	@Override
	public void fadeColor(int color, String[] devs) {
		JsonObject cmd = new JsonObject();
		cmd.addProperty("command", "fade_color");
		JsonObject params = new JsonObject();
		params.add("cubes", new Gson().toJsonTree(devs));
		int[] rgb_color = ColorHelper.decompose(color);
		JsonObject rgb = new JsonObject();
		rgb.addProperty("r", rgb_color[0]);
		rgb.addProperty("g", rgb_color[1]);
		rgb.addProperty("b", rgb_color[2]);
		params.add("color", rgb);
		cmd.add("params", params);
		this.sendJsonEventMsg(cmd);
	}
}
