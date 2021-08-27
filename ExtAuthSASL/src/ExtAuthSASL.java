// Test program that uses the LDAP SDK to make a connection
// and perform some action against a running ldap server.

import java.util.LinkedHashMap;
import java.util.List;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.unboundidds.
            UnboundIDExternallyProcessedAuthenticationBindRequest;
import com.unboundid.ldap.sdk.unboundidds.controls.
            GetPasswordPolicyStateIssuesRequestControl;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public final class ExtAuthSASL {

  public static void main(String[] args) throws Exception {

    // We need to configure Directory Manager properly so he
    // is permitted to use external authentication like so or
    // this will throw an exception: "auth method not supported"
    // dsconfig set-root-dn-user-prop --user-name "Directory Manager" --set privilege:permit-externally-processed-authentication
    
    int port = 1390;
    String address = "127.0.0.1";
    String bindDN = "cn=Directory Manager";
    String password = "password";
    String userDN = "";
    if (args.length != 0)
    {
      userDN = "uid=" + args[0] + ",ou=people,dc=example,dc=com";
    }
    else
    {
      userDN = "uid=user.0,ou=people,dc=example,dc=com";
    }

    String authID = "dn:" + userDN;
    //String authID = "u:user.5";
    //String clientIP = "there is no validation here....";
    String clientIP = "fluffy";
    String failureReason = null;

    Boolean passwordBased = false;
    Boolean secureAuth = true;
    Boolean wasAuthSuccessful = true;

    LinkedHashMap<String,String> propertyMap = new LinkedHashMap<String,String>();

    propertyMap.put("attr1", "value1");
    propertyMap.put("attr2", "value2");
    propertyMap.put("`~!@#$%^&*()-+{}[]|'", "`~!@#$%^&*()-+{}[]|'"); 

    //LDAPConnection conn = new com.unboundid.ldap.sdk.LDAPConnection(address, port);
    SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    LDAPConnection conn = new com.unboundid.ldap.sdk.LDAPConnection(sslUtil.createSSLSocketFactory(), address, port);
    conn.bind("cn=Directory Manager", "password");

    final UnboundIDExternallyProcessedAuthenticationBindRequest bindRequest =
            new UnboundIDExternallyProcessedAuthenticationBindRequest(authID,
            "doBind for " + authID, wasAuthSuccessful, failureReason, passwordBased,
            secureAuth, clientIP, propertyMap,
            new GetPasswordPolicyStateIssuesRequestControl());
 	
    BindResult bindResult;

    try
    {
      bindResult = conn.bind(bindRequest);
      System.out.println("Bind Result: " + bindResult.toString());
    }
    catch (LDAPException le)
    {
      System.out.println("Exception: " + le.toString());
    }

  }
}


