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

// Create a bunch of nested groups for DS-17362

public class NestedGroups
{
  private static String GROUPS_DN = "ou=Groups,dc=example,dc=com";

  private static int maxNestedGroups = 500;
  private static LDAPConnection ldapConn;
  private static List<String> dnsToAdd;

  public static void main(String[] args)
  {
    int port = 1389;
    final String host = "127.0.0.1";

    dnsToAdd = new ArrayList<String>();

    try
    {
      ldapConn = new LDAPConnection(host, port, "cn=Directory Manager", "rootpassword");

      // Cleanup to start fresh
      try
      {
        DeleteRequest deleteRequest =
          new DeleteRequest(GROUPS_DN, new Control[] { new SubtreeDeleteRequestControl() });
        ldapConn.delete(deleteRequest);
      }
      catch (LDAPException e)
      {
        if (!e.getResultCode().equals(ResultCode.NO_SUCH_OBJECT))
        {
          throw e;
        }
      }

      // Create the Groups OU
      DN newOU = new DN(GROUPS_DN);
      List<Attribute> la = new ArrayList<Attribute>();
      la.add(new Attribute("objectClass", "top", "organizationalUnit")); 
      ldapConn.add(new AddRequest(newOU, la));

      String baseDN = "ou=People,dc=example,dc=com";
      Filter filter = Filter.create("(&(objectClass=person)(uid=user*))");
      SearchRequest req = new SearchRequest(baseDN, SearchScope.SUB, filter, "uid");

      SearchResult res = ldapConn.search(req);
      ResultCode rc = res.getResultCode();
      if (rc != ResultCode.SUCCESS)
      {
        System.err.printf("LDAP search error. Result code: %d %s %n", rc.intValue(), rc.getName());
        System.exit(1);
      }

      for (SearchResultEntry entry : res.getSearchEntries())
      {
        dnsToAdd.add(entry.getParsedDN().toString());
      }

      addGroups();

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

  private static void addGroups()
  {
    int groupCounter = maxNestedGroups; 
    while ((groupCounter <= maxNestedGroups) && (groupCounter != 0))
    {
      try
      {
        DN newGroupDN = new DN(String.format("cn=Test-Group-%d,ou=Groups,dc=example,dc=com", groupCounter));
        List<Attribute> la = new ArrayList<Attribute>();
        la.add(new Attribute("objectClass", "top", "groupOfNames"));
        if (groupCounter == maxNestedGroups)
        {
          la.add(new Attribute("member", dnsToAdd));
        }
        else
        {
          la.add(new Attribute("member",
            String.format("cn=Test-Group-%d,ou=Groups,dc=example,dc=com", groupCounter + 1)));
        }
        la.add(new Attribute("cn", String.format("cn=Test-Group-%d", groupCounter)));
        ldapConn.add(new AddRequest(newGroupDN, la));

        System.out.println("Added group " + groupCounter);
        Thread.sleep(100);

        groupCounter--;
      }
      catch(LDAPException ldapEx)
      {
        System.err.println("LDAP exception during group creation: " + ldapEx.getMessage());
      }
      catch(InterruptedException ex)
      {
        System.err.println("Sleep was interrupted.");
      }
    }
  }
}
