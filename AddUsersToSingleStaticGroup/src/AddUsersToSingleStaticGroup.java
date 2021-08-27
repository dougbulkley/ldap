import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;

import java.util.ArrayList;
import java.util.List;

public class AddUsersToSingleStaticGroup
{
  private static LDAPConnection ldapConn;
  private static List<String> dnsToAdd;

  public static void main(String[] args)
  {
    int port = 1389;
    final String host = "10.101.28.136";

    dnsToAdd = new ArrayList<String>();

    try
    {
      ldapConn = new LDAPConnection(host, port, "cn=Directory Manager", "rootpassword");

      // Create the Groups OU
      DN newOU = new DN("ou=Groups,dc=acme,dc=com");
      List<Attribute> la = new ArrayList<Attribute>();
      la.add(new Attribute("objectClass", "top", "organizationalUnit")); 
      ldapConn.add(new AddRequest(newOU, la));

      // Search for a set of users
      String baseDN = "ou=People,dc=acme,dc=com";
      //Filter filter = Filter.create("(sn=Anaya*)");
      Filter filter = Filter.create("(sn=E*)");
      SearchRequest req = new SearchRequest(baseDN, SearchScope.SUB, filter, "uid");
      SearchResult res = ldapConn.search(req);
      for (SearchResultEntry entry : res.getSearchEntries())
      {
        dnsToAdd.add(entry.getParsedDN().toString());
      }

      // Create the single group with the searched for users as member
      DN newGroupDN = new DN("cn=Test-Group-1,ou=Groups,dc=acme,dc=com");
      List<Attribute> la2 = new ArrayList<Attribute>();
      la2.add(new Attribute("objectClass", "top", "groupOfNames"));
      la2.add(new Attribute("member", dnsToAdd));
      ldapConn.add(new AddRequest(newGroupDN, la2));

    }
    catch (LDAPException ex)
    {
      System.err.println("LDAP Exception: " + ex.getMessage());
    }
    finally
    {
    ldapConn.close();
    }
  }

}
