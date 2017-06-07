package br.com.alm.workflow.alm;

import br.com.alm.workflow.infraestructure.Base64Encoder;
import br.com.alm.workflow.infraestructure.Response;
import br.com.alm.workflow.infraestructure.RestConnector;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class AlmConnector {

	public AlmConnector(final String serverUrl, final String domain, final String project) {
		this.con = RestConnector.getInstance().init(new HashMap<String, String>(), serverUrl, domain, project);
	}

	public AlmConnector() {
		this.con = RestConnector.getInstance();
	}

	public boolean login(String username, String password) throws Exception {

		String authenticationPoint = this.isAuthenticated();

		if (authenticationPoint != null) {
			return this.login(authenticationPoint, username, password);
		}

		return true;
	}

	private boolean login(String loginUrl, String username, String password) throws Exception {

		byte[] credBytes = (username + ":" + password).getBytes();
		String credEncodedString = "Basic " + Base64Encoder.encode(credBytes);

		Map<String, String> map = new HashMap<String, String>();
		map.put("Authorization", credEncodedString);

		Response response = con.httpGet(loginUrl, null, map);

		boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;

		return ret;
	}

	public boolean logout() throws Exception {

		Response response = con.httpGet(con.buildUrl("authentication-point/logout"), null, null);

		return (response.getStatusCode() == HttpURLConnection.HTTP_OK);
	}

	public String isAuthenticated() throws Exception {
		String isAuthenticateUrl = con.buildUrl("rest/is-authenticated");
		String ret;

		Response response = con.httpGet(isAuthenticateUrl, null, null);
		int responseCode = response.getStatusCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			ret = null;
		} else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
			ret = con.buildUrl("authentication-point/authenticate");
		} else {
			throw response.getFailure();
		}

		return ret;
	}

	private RestConnector con;
}
