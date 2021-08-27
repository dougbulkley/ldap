// Test program that uses the LDAP SDK to make a connection
// and perform some action against a running ldap server.

import java.util.List;

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            GetPasswordQualityRequirementsExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            GetPasswordQualityRequirementsExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            GetPasswordQualityRequirementsTargetType;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public final class PassQualityReqs {

  public static void main(String[] args) throws Exception {

    int port = 1389;
    String address = "127.0.0.1";
    String bindDN = "cn=Directory Manager";
    String password = "rootpassword";
    String userDN = "";
    if (args.length != 0)
    {
      userDN = "uid=" + args[0] + ",ou=people,dc=example,dc=com";
    }
    else
    {
      userDN = "uid=user.12,ou=people,dc=example,dc=com";
    }

    LDAPConnection conn = new LDAPConnection();
    //SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
    //LDAPConnection conn = new LDAPConnection(sslUtil.createSSLSocketFactory());
    SimpleBindRequest bindRequest = new SimpleBindRequest(bindDN, password); 
    GetPasswordQualityRequirementsExtendedResult result = null;

    try
    {
      conn.connect(address, port);
      BindResult bindResult = conn.bind(bindRequest);
      System.out.println("Bind Result: " + bindResult.toString());

      // Get password quality requirements.
      GetPasswordQualityRequirementsExtendedRequest request =
         GetPasswordQualityRequirementsExtendedRequest.
            createAddWithSpecifiedPasswordPolicyRequest("cn=Secure Password Policy,cn=Password Policies,cn=config");

      result = (GetPasswordQualityRequirementsExtendedResult)
         conn.processExtendedOperation(request);
    }
    catch (LDAPException le)
    {
      System.out.println("Exception: " + le.toString());
    }
    finally
    { 
      List l = result.getPasswordRequirements();
      for (int i=0; i < l.size(); i++) {
        System.out.println("Requirement: " + l.get(i));
      }
      System.out.println("Result Code: " + result.getResultCode());
      conn.close();
    }
  }
}


