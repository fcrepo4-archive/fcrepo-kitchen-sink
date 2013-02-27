
package org.fcrepo.example.cmis;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.StandardAuthenticationProvider;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;



public class CmisIT {

    private Session session;

    private static final String BASE_URL = "http://localhost:8080";

    @Before
    public void setUp() throws Exception {
        // default factory implementation
        SessionFactoryImpl factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // user credentials
        //parameter.put(SessionParameter.USER, user);
        //parameter.put(SessionParameter.PASSWORD, passw);

        // connection settings
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES
                .value());
        parameter
                .put(SessionParameter.WEBSERVICES_ACL_SERVICE,
 BASE_URL +
                "/services/ACLService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE,
 BASE_URL +
                "/services/DiscoveryService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE,
                BASE_URL + "/services/MultiFilingService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE,
                BASE_URL + "/services/NavigationService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_OBJECT_SERVICE,
 BASE_URL +
                "/services/ObjectService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_POLICY_SERVICE,
 BASE_URL +
                "/services/PolicyService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE,
                BASE_URL + "/services/RelationshipService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE,
                BASE_URL + "/services/RepositoryService?wsdl");
        parameter
                .put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE,
                BASE_URL + "/services/VersioningService?wsdl");

        parameter.put(SessionParameter.REPOSITORY_ID, "cmis_repo:default");

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

}
