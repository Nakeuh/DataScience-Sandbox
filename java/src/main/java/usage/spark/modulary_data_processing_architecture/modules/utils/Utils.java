package usage.spark.modulary_data_processing_architecture.modules.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

public class Utils {

	private static boolean logEnabled = false;

	public static OAuthAuthorization getAuthorization() {

		// TODO
		String accessToken = "3935644037-RI5yovb75S6kw6IkC0wsD5ELCh4UGykb6F0nudn";
		String accessTokenSecret = "xMcKbqyDIrAcgrfQxbkYys09YA2m6pI3uQhBX7haXqLcM";
		String consumerKey = "2jSpmUxdDEvPv5p5EsfKnZonH";
		String consumerSecret = "Nz6m9QNxQbWzMbmm0gLnh4BiAu9Ppqx15s70BtLiko72ZzeT5D";


		/*String accessToken = "";
		String accessTokenSecret = "";
		String consumerKey = "";
		String consumerSecret = "";*/


		System.setProperty("twitter4j.oauth.consumerKey", consumerKey);
		System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret);
		System.setProperty("twitter4j.oauth.accessToken", accessToken);
		System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret);

		ConfigurationBuilder oauthConf = new ConfigurationBuilder();
		oauthConf.setOAuthAccessToken(accessToken);
		oauthConf.setOAuthAccessTokenSecret(accessTokenSecret);
		oauthConf.setOAuthConsumerKey(consumerKey);
		oauthConf.setOAuthConsumerSecret(consumerSecret);

		OAuthAuthorization auth = new OAuthAuthorization(oauthConf.build());
		return auth;
	}

	/**
	 * http POST of a json at a given endpoint
	 * @param url : endpoint
	 * @param jsonMessage : the json
	 * @return http return code
	 */
	public static String postJson(String url, String jsonMessage) {
		String id = "";
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(jsonMessage);
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			if (logEnabled) {
				System.out.println("POST to URL " + url + " : " + jsonMessage);
				System.out.println("Response code :" + response.toString());
			}
			HttpEntity resEntityGet = response.getEntity();
			if (resEntityGet != null) {
				id = EntityUtils.toString(resEntityGet);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return id;
	}

	/**
	 * http PUT of a json at a given endpoint
	 * @param url : endpoint
	 * @param jsonMessage : the json
	 */
	public static void putJson(String url, String jsonMessage) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			HttpPut request = new HttpPut(url);
			StringEntity params = new StringEntity(jsonMessage);
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			
			if (logEnabled) {
				System.out.println("PUT to URL " + url + " : " + jsonMessage);
				System.out.println("Response code :" + response.toString());
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * http GET of a json from a given endpoint
	 * @param url : endpoint
	 * @return the json
	 */
	public static String getJson(String url) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		String data = "";
		try {
			HttpGet request = new HttpGet(url);
			request.addHeader("content-type", "application/json");
			HttpResponse response = httpClient.execute(request);

			HttpEntity resEntityGet = response.getEntity();
			if (resEntityGet != null) {
				data = EntityUtils.toString(resEntityGet);
			}

			if (logEnabled) {
				System.out.println("GET from URL " + url + " : " + data);
				System.out.println("Response code :" + response.toString());
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

}
