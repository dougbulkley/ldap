
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateErrorBehavior;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.MultiUpdateExtendedResult;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.ObjectPair;

public class MultiUpdateWithSubdelete {

    public static void main(String[] args) {

        LDAPConnection conn = null;

        try {

            // Before test - entries existing in database:
            final String baseDN = "dc=example,dc=com";
            final String baseOU = "ou=People," + baseDN;
            final String ou1 = "ou=data," + baseOU;
            final String ou2 = "ou=config," + ou1;

            conn = new LDAPConnection("localhost", 1389, "cn=Directory Manager", "rootpassword");

            // Prepare multi update extended requests

            AddRequest addRequest1 =
                    new AddRequest(
                            "dn: " + ou1,
                            "objectClass: top",
                            "objectClass: organizationalUnit",
                            "ou: data"
            );

            AddRequest addRequest2 =
                    new AddRequest(
                            "dn: " + ou2,
                            "objectClass: top",
                            "objectClass: organizationalUnit",
                            "ou: config"
                    );

            DeleteRequest delRequest = new DeleteRequest(ou1);
            delRequest.addControl(new SubtreeDeleteRequestControl(true));

            // Execute multi update extended request
            MultiUpdateExtendedRequest mupRequest =
                    new MultiUpdateExtendedRequest(MultiUpdateErrorBehavior.ATOMIC, addRequest1, addRequest2, delRequest);

            MultiUpdateExtendedResult mupResult =
                    (MultiUpdateExtendedResult) conn.processExtendedOperation(mupRequest);

            for (ObjectPair<OperationType, LDAPResult> result : mupResult.getResults()) {
                System.out.printf("%s : %s%n", result.getFirst(), result.getSecond());
            }

            // Do it again
            mupResult =
                    (MultiUpdateExtendedResult) conn.processExtendedOperation(mupRequest);

            System.out.println(mupResult.getResultCode());

            for (ObjectPair<OperationType, LDAPResult> result : mupResult.getResults()) {

                System.out.printf("%s : %s%n", result.getFirst(), result.getSecond());
            }
/*
            // Cleanup
            mupRequest =
                    new MultiUpdateExtendedRequest(MultiUpdateErrorBehavior.ATOMIC, delRequest);

            mupResult =
                    (MultiUpdateExtendedResult) conn.processExtendedOperation(mupRequest);

            for (ObjectPair<OperationType, LDAPResult> result : mupResult.getResults()) {
                System.out.printf("%s : %s%n", result.getFirst(), result.getSecond());
            }
*/
        } catch (LDAPException | LDIFException e) {

            e.printStackTrace();

        } finally {

            if (conn != null) {
                conn.close();
            }
        }
    }
}
