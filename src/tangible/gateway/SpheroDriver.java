/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tangible.gateway;

import com.google.gson.annotations.SerializedName;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import tangible.devices.SpheroDevice;
import tangible.protocols.TangibleGatewayCommunicationProtocol;
import tangible.protocols.device_speficic.sphero.SpheroAPICommunicationProtocol;

/**
 *
 * @author Nicklas Gavelin, nicklas.gavelin@gmail.com, Luleå University of
 * Technology
 */
public class SpheroDriver implements TangibleGateway<SpheroDevice>
{
    // <editor-fold defaultstate="collapsed" desc="json serializable fields">
    @SerializedName( "appMgrId" )
    private String _app_id;
    @SerializedName( "type" )
    private String _type;
    @SerializedName( "id" )
    private String _driver_id;
    @SerializedName( "cubesId" )
    private String[] _cubes_id;
    @SerializedName( "protocolVersion" )
    private String _protocol_version;
    // </editor-fold>

    private transient List<SpheroDevice> _devices;
    private transient SpheroAPICommunicationProtocol _talk;


    public SpheroDriver()
    {
        _devices = new LinkedList<SpheroDevice>();
    }


    public SpheroDriver( String app_id, String driver_id )
    {
        this();
        this._app_id = app_id;
        this._driver_id = driver_id;
    }


    @Override
    public TangibleGatewayCommunicationProtocol<SpheroDevice> getTalk()
    {
        return _talk;
    }


    @Override
    public String[] getDevicesId()
    {
        String[] ids = new String[ this.size() ];
        int i = 0;
        for ( Iterator<SpheroDevice> ite = this.iterator(); ite.hasNext(); i++ )
            ids[ i] = ite.next().id;

        return ids;
    }


    @Override
    public String getId()
    {
        return _driver_id;
    }


    @Override
    public int size()
    {
        return _devices.size();
    }


    @Override
    public boolean isEmpty()
    {
        return _devices.isEmpty();
    }


    @Override
    public boolean contains( Object o )
    {
        return _devices.contains( o );
    }


    @Override
    public Iterator<SpheroDevice> iterator()
    {
        return _devices.iterator();
    }


    @Override
    public Object[] toArray()
    {
        return _devices.toArray();
    }


    @Override
    public <T> T[] toArray( T[] ts )
    {
        return _devices.toArray( ts );
    }


    @Override
    public boolean add( SpheroDevice e )
    {
        return _devices.add( e );
    }


    @Override
    public boolean remove( Object o )
    {
        return _devices.remove( o );
    }


    @Override
    public boolean containsAll( Collection<?> clctn )
    {
        return _devices.containsAll( clctn );
    }


    @Override
    public boolean addAll( Collection<? extends SpheroDevice> clctn )
    {
        return _devices.addAll( clctn );
    }


    @Override
    public boolean removeAll( Collection<?> clctn )
    {
        return _devices.removeAll( clctn );
    }


    @Override
    public boolean retainAll( Collection<?> clctn )
    {
        return _devices.retainAll( clctn );
    }


    @Override
    public void clear()
    {
        _devices.clear();
    }
}
