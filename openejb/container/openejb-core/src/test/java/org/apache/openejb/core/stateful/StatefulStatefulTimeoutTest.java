package org.apache.openejb.core.stateful;

import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.StatefulBean;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.junit.Module;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.naming.Context;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(ApplicationComposer.class)
public class StatefulStatefulTimeoutTest {
    private Context context;

    @Before
    public void initContext() {
        context = (Context) System.getProperties().get(ApplicationComposer.OPENEJB_APPLICATION_COMPOSER_CONTEXT);
    }

    @Stateful
    @StatefulTimeout(value = 4, unit = TimeUnit.SECONDS)
    public static class TimedOutStateful {
        public void foo() {}
    }

    @Module
    public EnterpriseBean stateful() {
        return new StatefulBean("TimedOutStateful", TimedOutStateful.class);
    }

    @Test @Ignore
    public void checkBeanIsCleaned() throws Exception {
        assertNotNull(context);
        TimedOutStateful stateful = (TimedOutStateful) context.lookup("global/StatefulStatefulTimeoutTest/stateful/TimedOutStateful");
        stateful.foo();
        Thread.sleep(8000);
        try {
            stateful.foo();
            fail();
        } catch (Exception e) {
            System.out.println("got " + e.getClass());
        }
    }
}
