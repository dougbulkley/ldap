// Test program that uses the LDAP SDK to make a connection
// and perform some action against a running ldap server.

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.RouteToBackendSetRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressOperationalAttributeUpdateRequestControl;
import com.unboundid.ldap.sdk.unboundidds.controls.SuppressType;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateErrorBehavior;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateExtendedResult;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateOperation;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public final class PasswordModifyExtended {

  public static void main(String[] args) throws Exception {

    int port = 1389;
    String host = "127.0.0.1";
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
    userDN = "uid=user.4,ou=people,dc=example,dc=com";
    //userDN = "CN=user.4,OU=Employees,dc=QA1,dc=local";
    }

    LDAPConnection conn = new LDAPConnection();
    //SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    //LDAPConnection conn = new LDAPConnection(sslUtil.createSSLSocketFactory());
    SimpleBindRequest bindRequest = new SimpleBindRequest(bindDN, password); 

    // This test validates password history (if enabled on the policy)
    try
    {
      conn.connect(host, port);
      BindResult bindResult = conn.bind(bindRequest);
      System.out.println("Bind Result: " + bindResult.toString());

      // Modify Password to new Password - Expect Success
      PasswordModifyExtendedResult res =
           (PasswordModifyExtendedResult)
           conn.processExtendedOperation(new PasswordModifyExtendedRequest(
                "dn:" + userDN, null, "password1"));

      System.out.println("Extended Result Code: " + res.getResultCode());
      System.out.println("Diagnostic Message: " + res.getDiagnosticMessage());
      System.out.println("Matched DN: " + res.getMatchedDN());
/*
      // Modify Password to previous Password - Expect Failure (if history enabled)
      res =
           (PasswordModifyExtendedResult)
           conn.processExtendedOperation(new PasswordModifyExtendedRequest(
                "dn:" + userDN, null, "password"));

      System.out.println("Extended Result Code: " + res.getResultCode());
      System.out.println("Diagnostic Message: " + res.getDiagnosticMessage());
      System.out.println("Matched DN: " + res.getMatchedDN());
*/
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


