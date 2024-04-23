package com.vathanakmao.solarnoon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.vathanakmao.solarnoon.util.NetworkUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(AndroidJUnit4.class)
public class HttpUrlConnectionTest {

    @Before
    public void setUp() {
        if (!NetworkUtil.isConnectedToInternet()) {
            assumeTrue(false);
        }
    }

    @Test
    public void downloadWebsite() throws Exception {
        String urlString = "https://google.com";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method as GET
        connection.setRequestMethod("GET");

        // Optionally set request headers (e.g., User-Agent)
        connection.setRequestProperty("User-Agent", "My Java Client");

        // Connect to the server (implicit for some methods like getInputStream())
        connection.connect();

        // Check the response code
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);

        // Read the response body
        StringBuilder response = new StringBuilder();
        assertTrue(response.length() == 0);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        reader.close();

        assertTrue(response.length() > 0);
    }
}
