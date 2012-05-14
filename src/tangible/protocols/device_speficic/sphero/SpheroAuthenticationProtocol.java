/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tangible.protocols.device_speficic.sphero;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import tangible.devices.SpheroDevice;
import tangible.gateway.SpheroDriver;
import tangible.protocols.DeviceAuthenticationProtocol.DeviceFoundCallBack;
import tangible.protocols.device_speficic.SpecificAuthenticationProtocol;
import tangible.utils.JsonProtocolHelper;
import tangible.utils.exceptions.UnSupportedDeviceType;

/**
 *
 * @author Nicklas Gavelin, nicklas.gavelin@gmail.com, Luleå University of
 * Technology
 */
public class SpheroAuthenticationProtocol implements SpecificAuthenticationProtocol
{
    @Override
    public void authenticateDevices( JsonObject obj, Socket s, DeviceFoundCallBack cb )
    {
        String communicationType = JsonProtocolHelper.assertStringInObject( obj, "type" );
        String protocolVersion = JsonProtocolHelper.assertStringInObject( obj, "protocolVersion" );

        if ( !communicationType.equals( "SpheroDevices" ) )
        {
            throw new UnSupportedDeviceType( communicationType );
        }

        String appId, driverId;
        appId = JsonProtocolHelper.assertStringInObject( obj, "appMgrId" );
        driverId = JsonProtocolHelper.assertStringInObject( obj, "id" );

        SpheroDriver driver = new SpheroDriver( appId, driverId );

        try
        {
            SpheroAPICommunicationProtocol driverTalk = new SpheroAPICommunicationProtocol( driver, s );
            JsonArray spheroIds = JsonProtocolHelper.assertArrayInObject( obj, "spheroId" );

            for ( Iterator<JsonElement> ite = spheroIds.iterator(); ite.hasNext(); )
            {
                String spheroId = JsonProtocolHelper.assertString( ite.next() );
                SpheroDevice sphero = new SpheroDevice( communicationType, protocolVersion, spheroId, driverId );

                if ( !cb.callback( sphero ) )
                    throw new UnSupportedDeviceType( sphero.type + "##" + sphero.id );

                SpheroCommunicationProtocol spheroTalk = new SpheroCommunicationProtocol( driverTalk, sphero.id );
                sphero.attachCommunication( spheroTalk );
            }

//            driverTalk.startReading();
        }
        catch ( IOException e )
        {
            Logger.getLogger( SpheroAuthenticationProtocol.class.getName() ).log( Level.SEVERE, null, e );
        }
    }
}