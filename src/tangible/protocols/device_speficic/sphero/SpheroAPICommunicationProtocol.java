/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tangible.protocols.device_speficic.sphero;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;
import restful.streaming.AbstractStreamingThread;
import tangible.devices.SpheroDevice;
import tangible.gateway.SpheroDriver;
import tangible.protocols.AbsJsonTCPProtocol;
import tangible.protocols.TangibleGatewayCommunicationProtocol;
import tangible.utils.JsonMessageReadingThread;
import utils.ColorHelper;

/**
 *
 * @author Nicklas Gavelin, nicklas.gavelin@gmail.com, Luleå University of
 * Technology
 */
public class SpheroAPICommunicationProtocol extends AbsJsonTCPProtocol implements TangibleGatewayCommunicationProtocol<SpheroDevice>
{
    private SpheroDriver _driver;
    private JsonMessageReadingThread _readingThread;


    public SpheroAPICommunicationProtocol( SpheroDriver driver, Socket s ) throws IOException
    {
        super( s );
        s.setSoTimeout( 0 );
        _driver = driver;
        _readingThread = new JsonMessageReadingThread( this.getInput() );
    }


    @Override
    public void showColor( int r, int g, int b, String[] ids )
    {
        JsonObject obj = new JsonObject();
        obj.addProperty( "command", "show_color" );

        JsonObject rgb = new JsonObject();
        rgb.addProperty( "r", r );
        rgb.addProperty( "g", g );
        rgb.addProperty( "b", b );

        JsonObject param = new JsonObject();
        param.add( "color", rgb );
        param.add( "spheros", new Gson().toJsonTree( ids ) );
        obj.add( "params", param );

        this.sendJsonEventMsg( obj );
    }


    private String[] getDeviceIds( SpheroDevice[] devs )
    {
        String[] ids = new String[ devs.length ];
        for ( int i = 0; i < devs.length; i++ )
            ids[ i] = devs[ i].id;

        return ids;
    }


    @Override
    public void showColor( int r, int g, int b, SpheroDevice[] devs )
    {
        this.showColor( r, g, b, getDeviceIds( devs ) );
    }


    @Override
    public void showColor( int color, String[] ids )
    {
        int[] rgb = ColorHelper.decompose( color );
        this.showColor( rgb[0], rgb[1], rgb[2], ids );
    }


    @Override
    public void showColor( int color, SpheroDevice[] devs )
    {
        this.showColor( color, getDeviceIds( devs ) );
    }


    @Override
    public void addAllEventsNotification( AbstractStreamingThread sTh, String[] devs )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public void addAllEventsNotification( AbstractStreamingThread sTh, SpheroDevice[] devs )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }


    @Override
    public void showPicture( BufferedImage img, String[] devs )
    {
        throw new UnsupportedOperationException( "Is not supported on this kind of device." );
    }


    @Override
    public void showPicture( BufferedImage img, SpheroDevice[] devs )
    {
        throw new UnsupportedOperationException( "Is not supported on this kind of device." );
    }
}
