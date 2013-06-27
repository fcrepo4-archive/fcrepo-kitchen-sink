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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.StandardAuthenticationProvider;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

public class CmisIT {

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
        connectionManager.closeIdleConnections(3, TimeUnit.SECONDS);
        client = new DefaultHttpClient(connectionManager);
    }

    private Session session;

    @Before
    public void setUp() throws Exception {
        assertEquals(201,
 getStatus(new HttpPost(BASE_URL + CONTEXT_PATH +
                "/rest/objects/fcr:new")));

        // default factory implementation
        SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        //parameter.put(SessionParameter.USER, null);
        //parameter.put(SessionParameter.PASSWORD, null);

        // connection settings
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES
                .value());
        parameter.put(SessionParameter.WEBSERVICES_ACL_SERVICE, BASE_URL +
                "/services/ACLService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, BASE_URL +
                "/services/DiscoveryService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE,
                BASE_URL + "/services/MultiFilingService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE,
                BASE_URL + "/services/NavigationService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, BASE_URL +
                "/services/ObjectService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, BASE_URL +
                "/services/PolicyService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE,
                BASE_URL + "/services/RelationshipService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE,
                BASE_URL + "/services/RepositoryService?wsdl");
        parameter.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE,
                BASE_URL + "/services/VersioningService?wsdl");

        parameter.put(SessionParameter.REPOSITORY_ID, "repo");

        // create session
        session =
                factory.createSession(parameter, null,
                        new StandardAuthenticationProvider() {

                            private static final long serialVersionUID = 1L;

                            @Override
                            public Element getSOAPHeaders(Object portObject) {
                                //Place headers here
                                return super.getSOAPHeaders(portObject);
                            };
                        }, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        Folder root = session.getRootFolder();
        System.out.println("Root: " + root);
    }

    protected int getStatus(HttpUriRequest method)
            throws ClientProtocolException, IOException {
        return client.execute(method).getStatusLine().getStatusCode();
    }

}
