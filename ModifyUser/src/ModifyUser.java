// Test program that uses the LDAP SDK to make a connection
// and perform some action against a running ldap server.


import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressOperationalAttributeUpdateRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressType;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public final class ModifyUser {

  public static void main(String[] args) throws Exception {

    int port = 1389;
    String address = "127.0.0.1";
    String bindDN = "cn=Directory Manager";
    String password = "rootpassword";
    String userDN = "";
    if (args.length == 2)
    {
      userDN = "uid=" + args[0] + ",ou=people,dc=example,dc=com";
      port = Integer.parseInt(args[1]);
    }
    else
    {
      userDN = "uid=user.12,ou=people,dc=example,dc=com";
    }

    LDAPConnection conn = new LDAPConnection();
    //SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    //LDAPConnection conn = new LDAPConnection(sslUtil.createSSLSocketFactory());
    SimpleBindRequest bindRequest = new SimpleBindRequest(bindDN, password); 

    // This control prevents the modifyTimestamp from
    // being updated when an attribute modification is made.
    // Note if this connection were going through a proxy
    // instead of directly to DS, you would have to add the
    // control OID to the ACIs and possibly set a request
    // processor property: 1.3.6.1.4.1.30221.2.5.27
    SuppressOperationalAttributeUpdateRequestControl soaur
            = new SuppressOperationalAttributeUpdateRequestControl(true, SuppressType.LASTMOD);

    Modification mod = new Modification(ModificationType.REPLACE, "st", "TX");
    ModifyRequest modifyRequest = new ModifyRequest(userDN, mod);
    modifyRequest.addControl(soaur);

    try
    {
      conn.connect(address, port);
      BindResult bindResult = conn.bind(bindRequest);
      System.out.println("Bind Result: " + bindResult.toString());
      LDAPResult ldapResult = conn.modify(modifyRequest);
      System.out.println("Modifying user: " + userDN);
      System.out.println("Modify Result: " + ldapResult.toString());
    }
    catch (LDAPException le)
    {
      System.out.println("Exception: " + le.toString());
    }
    finally
    {
      conn.close();
    }
  }
}


