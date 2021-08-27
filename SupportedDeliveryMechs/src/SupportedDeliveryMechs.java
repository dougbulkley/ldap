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
import com.unboundid.ldap.sdk.unboundidds.extensions.
            GetSupportedOTPDeliveryMechanismsExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            GetSupportedOTPDeliveryMechanismsExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            SupportedOTPDeliveryMechanismInfo;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public final class SupportedDeliveryMechs {

  public static void main(String[] args) throws Exception {

    String ANSI_RESET = "\u001B[0m";
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";

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
    GetSupportedOTPDeliveryMechanismsExtendedResult result = null;

    try
    {
      conn.connect(address, port);
      BindResult bindResult = conn.bind(bindRequest);
      System.out.println(ANSI_GREEN + "Bind Result: " + bindResult.toString() + ANSI_RESET);

      // Get supported delivery mechanisms
      GetSupportedOTPDeliveryMechanismsExtendedRequest request =
         new GetSupportedOTPDeliveryMechanismsExtendedRequest(userDN); 

      result = (GetSupportedOTPDeliveryMechanismsExtendedResult)
         conn.processExtendedOperation(request);
    }
    catch (LDAPException le)
    {
      System.out.println(ANSI_RED + "Exception: " + le.toString() + ANSI_RESET);
    }
    finally
    {
      for (SupportedOTPDeliveryMechanismInfo mech : result.getDeliveryMechanismInfo())
      {
        System.out.println(ANSI_GREEN + mech.toString() + ANSI_RESET);
      }

      System.out.println(ANSI_GREEN + result.getResultCode() + ANSI_RESET);
      conn.close();
    }
  }
}


