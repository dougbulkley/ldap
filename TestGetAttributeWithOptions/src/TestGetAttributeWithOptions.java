import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.Validator;


final class TestGetAttributeWithOptions
{
  public static void main(final String... args)
         throws Exception
  {
    final InMemoryDirectoryServerConfig dsCfg =
         new InMemoryDirectoryServerConfig("dc=example,dc=com");
    dsCfg.setSchema(Schema.getDefaultStandardSchema());
    final InMemoryDirectoryServer ds = new InMemoryDirectoryServer(dsCfg);
    ds.startListening();

    final LDAPConnectionOptions options = new LDAPConnectionOptions();
    options.setUseSchema(true);

    try (LDAPConnection conn = ds.getConnection(options))
    {
      conn.add(
           "dn: dc=example,dc=com",
           "objectClass: top",
           "objectClass: domain",
           "dc: example");

      conn.add(
           "dn: uid=test.user,dc=example,dc=com",
           "objectClass: top",
           "objectClass: person",
           "objectClass: organizationalPerson",
           "objectClass: inetOrgPerson",
           "uid: test.user",
           "givenName: Test",
           "sn: User",
           "cn: Test User",
           "audio: test",
           "audio;x-opt-1: test1",
           "audio;x-opt-2: test2");

      final SearchResultEntry e =
           conn.getEntry("uid=test.user,dc=example,dc=com");
      Validator.ensureNotNull(e);
      System.out.println("Got search result entry:");
      System.out.println(e.toLDIFString());

      final Attribute a = e.getAttribute("audio;x-opt-2");
      Validator.ensureNotNull(a);
      System.out.println("Got attribute:  " + a);
      System.out.println("Attribute value is:  " + a.getValue());
      Validator.ensureTrue(a.getValue().equals("test2"));
      Validator.ensureTrue(
           e.getAttribute("audio;x-opt-2").getValue().equals("test2"));
      System.out.println("getAttribute tests succeeded");

      final String attributeValue = e.getAttributeValue("audio;x-opt-2");
      System.out.println("Got attribute value " + attributeValue);
      Validator.ensureTrue(attributeValue.equals("test2"));
      Validator.ensureTrue(
           e.getAttributeValue("audio;x-opt-2").equals("test2"));
      System.out.println("getAttributeValue tests succeeded");
    }
    finally
    {
      ds.shutDown(true);
    }
  }
}
