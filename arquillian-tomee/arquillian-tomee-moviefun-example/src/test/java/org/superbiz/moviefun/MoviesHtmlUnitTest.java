package org.superbiz.moviefun;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;

import org.apache.commons.io.FileUtils;
import org.apache.tomee.embedded.EmbeddedTomEEContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MoviesHtmlUnitTest {
	
	private static EJBContainer container;
    private static File webApp;

    @BeforeClass public static void start() throws IOException {
        webApp = createWebApp();
        Properties p = new Properties();
        p.setProperty(EJBContainer.APP_NAME, "moviefun");
        p.setProperty(EJBContainer.PROVIDER, "tomee-embedded"); // need web feature
        p.setProperty(EJBContainer.MODULES, webApp.getAbsolutePath());
        p.setProperty(EmbeddedTomEEContainer.TOMEE_EJBCONTAINER_HTTP_PORT, "9999");
        container = EJBContainer.createEJBContainer(p);
    }

    @AfterClass public static void stop() {
        if (container != null) {
            container.close();
        }
        if (webApp != null) {
            try {
                FileUtils.forceDelete(webApp);
            } catch (IOException e) {
                FileUtils.deleteQuietly(webApp);
            }
        }
    }
    
    private static File createWebApp() throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/tomee-" + Math.random());
        if (!file.mkdirs() && !file.exists()) {
            throw new RuntimeException("can't create " + file.getAbsolutePath());
        }

        FileUtils.copyDirectory(new File("target/classes"), new File(file, "WEB-INF/classes"));
        FileUtils.copyDirectory(new File("src/main/webapp"), file);

        return file;
    }

	@Test
    public void testShouldMakeSureWebappIsWorking() throws Exception {
        WebClient webClient = new WebClient();
        HtmlPage page = webClient.getPage("http://localhost:9999/moviefun/setup.jsp");

        assertMoviesPresent(page);

        page = webClient.getPage("http://localhost:9999/moviefun/faces/movie/List.xhtml");

        assertMoviesPresent(page);
        webClient.closeAllWindows();
    }

    private void assertMoviesPresent(HtmlPage page) {
        String pageAsText = page.asText();
        assertTrue(pageAsText.contains("Wedding Crashers"));
        assertTrue(pageAsText.contains("Starsky & Hutch"));
        assertTrue(pageAsText.contains("Shanghai Knights"));
        assertTrue(pageAsText.contains("I-Spy"));
        assertTrue(pageAsText.contains("The Royal Tenenbaums"));
        assertTrue(pageAsText.contains("Zoolander"));
        assertTrue(pageAsText.contains("Shanghai Noon"));
    }
}