
package org.fcrepo.metrics;

import static org.fcrepo.metrics.RegistryService.getMetrics;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * A ServletContextListener to set the ServletContext attributes that the
 * Metrics servlets expect.
 * 
 * @author Edwin Shin
 * @see <a href="http://metrics.codahale.com/manual/servlets/">http://metrics.codahale.com/manual/servlets/</a>
 */
@WebListener
public class MetricsContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        context.setAttribute(
                "com.codahale.metrics.servlets.MetricsServlet.registry",
                getMetrics());

        context.setAttribute(
                "com.codahale.metrics.servlets.HealthCheckServlet.registry",
                new HealthCheckRegistry());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // TODO Auto-generated method stub

    }
}
