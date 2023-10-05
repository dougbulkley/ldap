import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import java.util.ArrayList;
import java.util.List;

// Simple test to search for a deleted user in
// Active Directory using the unboundid ldap sdk.


public class SearchADDeletedUsers
{
  private static LDAPConnectionPool ADConnectionPool;

  public static void main(String[] args) throws Exception
  {
   LDAPConnection conn = null;

   try {
       SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
       conn = new LDAPConnection(sslUtil.createSSLSocketFactory(), "AD HOSTNAME", 636);
       conn.bind("AD ADMIN DN", "AD ADMIN PASSWORD");
       ADConnectionPool = new LDAPConnectionPool(conn, 10);

       Filter filter = Filter.create("(&(objectClass=user)(cn=Doug*))");
       SearchRequest req = new SearchRequest("AD BASE DN", SearchScope.SUB, filter, "cn");

       // Add the following Active Directory specific controls needed for deleted objects.       
       // Show Deleted Objects (1.2.840.113556.1.4.417)
       Control control1 = new Control("1.2.840.113556.1.4.417");
       // Show Deactivated Links (1.2.840.113556.1.4.2065)
       Control control2 = new Control ("1.2.840.113556.1.4.2065");
       req.addControls(control1, control2);

       SearchResult res = ADConnectionPool.search(req);
       ResultCode rc = res.getResultCode();

       for (SearchResultEntry entry : res.getSearchEntries()) {
          System.out.println("Found deleted user: " + entry.getParsedDN().toString());
       }
   }
   catch (LDAPException e) {
       if (conn != null) {
          conn.close();
       }
   }
   finally {
       ADConnectionPool.close();
   }
  }
}
