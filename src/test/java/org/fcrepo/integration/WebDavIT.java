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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class WebDavIT {

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

    protected static final String davEndpoint = serverAddress +
            "webdav/repo/default/";


    @Test
    public void testWebDav() throws Exception {
        String folderUri =
                davEndpoint + "davFolder-" + UUID.randomUUID().toString();
        String fileUri = folderUri + "/davFile";
        String testContent = "How davvy!";

        Sardine sardine = SardineFactory.begin();
        sardine.createDirectory(folderUri);
        assertTrue(sardine.exists(folderUri));

        try {
            sardine.put(fileUri, testContent.getBytes());
        } catch (IOException e) {
            // I don't know why, but creating the file is throwing a
            // SardineException with an Unexpected Response / 404 Not Found,
            // even though the file is being created (as the subsequent
            // sardine.exists(fileUri) shows).
            logger.debug(e.getMessage());
        }

        assertTrue(sardine.exists(fileUri));
        InputStream davResource = sardine.get(fileUri);
        assertEquals(testContent, convertStreamToString(davResource));
    }

    /**
     * Converts an InputStream to a String.
     * <p>
     * "\A" is the "beginning of the input boundary" token, so we only get one
     * token for the entire contents of the stream.
     * 
     * @param is
     * @param charset The encoding type used to convert bytes from the stream
     *        into characters. If not specified, defaults to UTF-8
     * @return the inputstream as a String
     * @throws IOException
     * @see http
     *      ://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
     */
    public static String convertStreamToString(InputStream is, String... charset)
        throws IOException {
        String encoding;
        if (charset.length == 0) {
            encoding = UTF_8.name();
        } else {
            encoding = charset[0];
        }
        Scanner scanner = new Scanner(is, encoding);
        scanner.useDelimiter("\\A");
        String s = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return s;
    }
}
