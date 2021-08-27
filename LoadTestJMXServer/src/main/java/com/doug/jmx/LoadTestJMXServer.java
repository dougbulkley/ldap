package com.doug.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


@SuppressWarnings("deprecation")
public final class LoadTestJMXServer {

    public static void main(String[] args) throws IOException {
        new LoadTestJMXServer().doMain(args);
    }

    public void doMain(String[] args) throws IOException {

        connectTest();

    }

    public void connectTest() {
        try {
            while (true) {
                String jmxURL = "service:jmx:rmi:///jndi/rmi://localhost:1689/com.unboundid.directory.server.protocols.jmx.client-unknown";
                JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);
                Map map = new HashMap();
                String[] credentials = new String[]{"cn=jmxUser", "password"};
                map.put("jmx.remote.credentials", credentials);

                //JXM connector
                JMXConnector connector = JMXConnectorFactory.connect(serviceURL, map);

                //MBean server connection
                MBeanServerConnection  mbsc = connector.getMBeanServerConnection();

                //Object name
                ObjectName connHandler = new ObjectName("com.unboundid.directory.server:name=\"jmx connection handler 1689\",type=\"ds-connectionhandler-monitor-entry\"");

                //Object name
                ObjectName clientConns = new ObjectName("com.unboundid.directory.server:name=\"client connections\",type=\"ds-client-connection-monitor-entry\"");

                //  Get and print attribute value
                System.out.println( "Number of jmx connections: " + mbsc.getAttribute(connHandler, "ds-connectionhandler-num-connections"));
                System.out.println( "Total connection count: " + mbsc.getAttribute(clientConns, "total-connection-count-since-startup"));

                //close the connection
                connector.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
