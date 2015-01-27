package uk.ac.aber.chh57.helloworld;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by Christian on 27/01/2015.
 */
public class executeHttpGet {

    /**
     * Performs an HTTP GET request to the specified url.
     *
     * @return The result of the request
     * @throws Exception
     */
    public static String executeHttpGet()
            throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://185.5.52.204/upload.php"));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static HttpClient getHttpClient() {
        return null;
    }
}