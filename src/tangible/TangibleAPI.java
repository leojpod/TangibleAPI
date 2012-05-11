package tangible;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import managers.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

/**
 *
 * @author leo
 */
public class TangibleAPI {

  /**
	 * @param args the command line arguments
	 * @throws IOException  
   */
  public static void main(String[] args) throws IOException {
    DeviceFinder finder = DeviceFinderAccess.getInstance();
		finder.start();
		//let's initialise all the singleton to avoid delay error later on.. 
		ApplicationManagerAccess.getInstance();
		ReservationManagerAccess.getInstance();
		SubscriptionManagerAccess.getInstance();
//    System.out.println("DeviceFinder is started!");

    //let's start the REST part
    HttpServer restServer = startServer();
//    System.out.println("the Rest Server is started!");
//    System.out.println("the communication is now working with the outside world!");

		
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Press enter to shut down the TangibleAPI daemon");
    try {
      reader.readLine();
      finder.stopASAP();
      restServer.stop();
      System.out.println("The system should turn off soon");
    } catch (IOException ex) {
      Logger.getLogger(TangibleAPI.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost/").port(9998).build();
  }
  public static final URI BASE_URI = getBaseURI();
  protected static HttpServer startServer() throws IOException {
//    System.out.println("Starting grizzly...");
    ResourceConfig rc = new PackagesResourceConfig("restful");

    HttpServer server = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
//    server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("resources\\"), "/resources");
    server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("resources"), "/resources");

    return server;
  }
}
