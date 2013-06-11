
package org.fcrepo.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.update.GraphStore;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.fcrepo.RdfLexicon;
import org.fcrepo.test.util.TestHelpers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KitchenSinkIT {

    /**
     * The server port of the application, set as system property by 
     * maven-failsafe-plugin.
     */
    private static final String SERVER_PORT = System.getProperty("test.port");

    /**
    * The context path of the application (including the leading "/"), set as 
    * system property by maven-failsafe-plugin.
    */
    private static final String CONTEXT_PATH = System
            .getProperty("test.context.path");

    protected Logger logger;

    @Before
    public void setLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    protected static final String HOSTNAME = "localhost";

    protected static final String serverAddress = "http://" + HOSTNAME + ":" +
            SERVER_PORT + CONTEXT_PATH;

    protected static final PoolingClientConnectionManager connectionManager =
            new PoolingClientConnectionManager();

    protected static HttpClient client;

    static {
        connectionManager.setMaxTotal(Integer.MAX_VALUE);
        connectionManager.setDefaultMaxPerRoute(5);
        connectionManager.closeIdleConnections(3, TimeUnit.SECONDS);
        client = new DefaultHttpClient(connectionManager);
    }

    @Test
    public void doASanityCheck() throws IOException {
        assertEquals(200, getStatus(new HttpGet(serverAddress +
                "rest/")));
    }

    @Test
    public void shouldContainOptionalServicesLinks() throws IOException {
        final HttpGet httpGet = new HttpGet(serverAddress + "rest/");
        final HttpResponse response = client.execute(httpGet);

        assertEquals(200, response.getStatusLine().getStatusCode());

        final GraphStore graphStore = TestHelpers.parseTriples(response.getEntity().getContent());

        assertTrue("expected to find fcr:rss link", graphStore.contains(Node.ANY, Node.createURI(serverAddress + "rest/"), RdfLexicon.HAS_FEED.asNode(),  Node.createURI(serverAddress + "rest/fcr:rss")));
        assertTrue("expected to find fcr:webhooks link", graphStore.contains(Node.ANY, Node.createURI(serverAddress + "rest/"), RdfLexicon.HAS_SUBSCRIPTION_SERVICE.asNode(),  Node.createURI(serverAddress + "rest/fcr:webhooks")));

    }

    @Test
    public void doAnRssSanityCheck() throws IOException {
        assertEquals(200, getStatus(new HttpGet(serverAddress + "rest/fcr:rss")));
    }

    @Test
    public void doWebhooksSanityCheck() throws IOException {
        assertEquals(200, getStatus(new HttpGet(serverAddress + "rest/fcr:webhooks")));
    }

    @Test
    public void doV3SanityCheck() throws IOException {
        assertEquals(200, getStatus(new HttpGet(serverAddress + "rest/v3/describe")));
    }

    protected int getStatus(final HttpUriRequest method)
            throws ClientProtocolException, IOException {
        logger.debug("Executing: " + method.getMethod() + " to " +
                method.getURI());
        return client.execute(method).getStatusLine().getStatusCode();
    }
}
