/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tangible.protocols.device_speficic.sphero;

import java.awt.image.BufferedImage;
import restful.streaming.AbstractStreamingThread;
import tangible.devices.SpheroDevice;
import tangible.protocols.TangibleDeviceCommunicationProtocol;

/**
 *
 * @author Nicklas Gavelin, nicklas.gavelin@gmail.com, Luleå University of
 * Technology
 */
public class SpheroCommunicationProtocol implements TangibleDeviceCommunicationProtocol<SpheroDevice>
{
    private final String _spheroId;
    private final String[] _id_in_array;
    private final SpheroAPICommunicationProtocol _driverTalk;

    public SpheroCommunicationProtocol( SpheroAPICommunicationProtocol driver, String spheroId )
    {
        this._driverTalk = driver;
        this._spheroId = spheroId;
        this._id_in_array = new String[]{ spheroId };
    }


    @Override
    public boolean isConnected()
    {
        return _driverTalk.isConnected();
    }


    @Override
    public void showColor( int r, int g, int b )
    {
        _driverTalk.showColor( r, g, b, _id_in_array );
    }


    @Override
    public void showColor( int color )
    {
        _driverTalk.showColor( color, _id_in_array );
    }


    @Override
    public void showPicture( BufferedImage img )
    {
        _driverTalk.showPicture( img, _id_in_array );
    }


    @Override
    public void addAllEventsNotification( AbstractStreamingThread sTh )
    {
        _driverTalk.addAllEventsNotification( sTh, _id_in_array );
    }

	@Override
	public void showText(String msg) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void showText(String msg, int color) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void fadeColor(int color) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
