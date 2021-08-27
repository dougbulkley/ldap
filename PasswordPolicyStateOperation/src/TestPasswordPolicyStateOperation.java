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
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateOperation;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import static com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateOperation.createClearAuthenticationFailureTimesOperation;

public final class TestPasswordPolicyStateOperation {

  public static void main(String[] args) throws Exception {

    int port = 2389;
    String address = "10.5.1.114";
    String bindDN = "cn=Directory Manager";
    String password = "password";
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

    // This test requires you create a new password policy and assign it
    // to the userDN the extended requests is being executed against
    PasswordPolicyStateOperation[] ops =
        new PasswordPolicyStateOperation[]
            {
                createClearAuthenticationFailureTimesOperation()
            };

    PasswordPolicyStateExtendedRequest req =
        new PasswordPolicyStateExtendedRequest(userDN, ops);

    try
    {
      conn.connect(address, port);
      BindResult bindResult = conn.bind(bindRequest);
      System.out.println("Bind Result: " + bindResult.toString());
      PasswordPolicyStateExtendedResult res =
          (PasswordPolicyStateExtendedResult)conn.processExtendedOperation(req);
      System.out.println("Extended Result Code: " + res.getResultCode());
      System.out.println("Diagnostic Message: " + res.getDiagnosticMessage());
      System.out.println("User DN: " + res.getUserDN());
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


