// Test program that uses the LDAP SDK to make a connection
// and perform some action against a running ldap server.

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.CompareRequest;
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
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

public final class TestExtendedRequest {

  public static void main(String[] args) throws Exception {

    int port = 4389;
    String address = "10.5.1.114";
    String bindDN = "cn=Directory Manager";
    String password = "password";
    String userDN = "";
    String entryBalancingRequestProcessor = "ou_people_dc_example_dc_com-eb-req-processor";
    String backendSet = "ou_people_dc_example_dc_comServer_Set_1-req-processor";

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

    //Control control = new SubtreeDeleteRequestControl(false);

    List<LDAPRequest> requests = Arrays.<LDAPRequest>asList(
        new ModifyRequest(
            "dn: uid=user.0,ou=People,dc=example,dc=com",
            "changetype: modify",
            "replace: description",
            "description: " + UUID.randomUUID().toString()));
        //new AddRequest(
        //    "dn: uid=user.XX,ou=people,dc=example,dc=com",
        //    "objectClass: top",
        //    "objectClass: person",
        //    "objectClass: organizationalPerson",
        //    "objectClass: inetOrgPerson",
        //    "uid: user.XX",
        //    "givenName: User",
        //    "sn: XX",
        //    "cn: User XX",
        //    "userPassword: password"));
        ///new DeleteRequest(
        //   "uid=user.XX,ou=people,dc=example,dc=com"));

    MultiUpdateExtendedRequest req = new MultiUpdateExtendedRequest(MultiUpdateErrorBehavior.ATOMIC,
                                     requests, 
                                     (RouteToBackendSetRequestControl.createAbsoluteRoutingRequest(true, entryBalancingRequestProcessor, backendSet)));

    //CompareRequest req = new CompareRequest("uid=user.1,ou=people,dc=example,dc=com",
    //                 "sn", "",
    //                 new Control[] {RouteToBackendSetRequestControl.createAbsoluteRoutingRequest(true, entryBalancingRequestProcessor, backendSet)});

    try
    {
      conn.connect(address, port);
      BindResult bindResult = conn.bind(bindRequest);
      System.out.println("Bind Result: " + bindResult.toString());
      MultiUpdateExtendedResult multiUpdateResult =
          (MultiUpdateExtendedResult)conn.processExtendedOperation(req); 
      System.out.println("Extended Result Code: " + multiUpdateResult.getResultCode());
      System.out.println("Changes Applied: " + multiUpdateResult.getChangesApplied());
      System.out.println("Diagnostic Message: " + multiUpdateResult.getDiagnosticMessage());
      //System.out.println("Result: " + conn.processOperation(req).toString());
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


