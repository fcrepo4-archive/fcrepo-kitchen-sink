/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.integration;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditIT {

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

    protected static final String HOSTNAME = "localhost";

    protected static final String BASE_URL = "http://" + HOSTNAME + ":" +
            SERVER_PORT;

    protected static HttpClient client;

    protected static final PoolingClientConnectionManager connectionManager =
            new PoolingClientConnectionManager();

    static {
        connectionManager.setMaxTotal(Integer.MAX_VALUE);
        connectionManager.setDefaultMaxPerRoute(5);
        client = new DefaultHttpClient(connectionManager);
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp() throws Exception {
        assertEquals(201, getStatus(new HttpPost(BASE_URL + CONTEXT_PATH +
            "/rest/objects/fcr:new")));
    }

    @Test
    public void test() throws SQLException {
        Connection con = DriverManager.getConnection(
            "jdbc:hsqldb:file:/tmp/audit.db;shutdown=true","sa","");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select count(*) from logging_event");
        assertTrue(rs.next());
        int rowCount = rs.getInt(1);
        logger.warn("Audit events in db: "  + rowCount);
        assertTrue("No audit events found", rowCount > 0);
    }

    protected int getStatus(HttpUriRequest method)
        throws ClientProtocolException, IOException {
        return client.execute(method).getStatusLine().getStatusCode();
    }

}
