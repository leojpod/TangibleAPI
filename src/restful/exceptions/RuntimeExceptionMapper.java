/*
 * Master-Thesis work: see https://sites.google.com/site/sifthesis/
 */
package restful.exceptions;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import java.text.DateFormat;
import java.util.Date;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author leo
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

	@Override
	public Response toResponse(RuntimeException e) {
		onlineLog(e);
		
		return Response.serverError().build();
	}
	public void onlineLog(RuntimeException e) {
		try {
			Client client = Client.create();
			WebResource r = client.resource("http://sifthesis.webuda.com/web_logger.php");
			Form params = new Form();
			DateFormat dateFormat = DateFormat.getTimeInstance();
			params.add("time", dateFormat.format(new Date()));
			String message = e.getLocalizedMessage() + "<br/>";
			StackTraceElement[] stack = e.getStackTrace();
			for (int i = 0; i < stack.length; i ++) {
				message += stack[i].toString()+ "<br/>";
			}
			params.add("message", message);
			params.add("origin", "0.0.0.0");
			
			String resp = r.put(String.class, params);
			System.out.println(resp);
		} catch (RuntimeException ex) {
			System.err.println("catch a runtime exception when online logging another exception");
			System.out.println(ex.getLocalizedMessage());
		} catch (Exception ex) {
			System.err.println("catch a IOexception when online logging another exception");
		}
	}
}
