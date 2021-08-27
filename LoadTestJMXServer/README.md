# Simple JMX Test
A simple java class using JMX

*Note: The **LoadTestJMXServer** application uses [Apache Maven](https://maven.apache.org/)*

## Documentation
 * Install an LDAP server
 * Enable the jxm handler on the default port of 1689:
   bin/dsconfig set-connection-handler-prop --handler-name "JMX Connection Handler" --set enabled:true
 * Create a user with the necessary jmx user privileges:
   bin/ldapmodify --hostname localhost --port 1389 --bindDN "cn=Directory Manager" --bindPassword rootpassword --defaultAdd --filename jmxUser.ldif
 * To build: mvn package
 * To execute: java -cp target/LoadTestJMXServer-1.0-SNAPSHOT-jar-with-dependencies.jar com.doug.jmx.LoadTestJMXServer
