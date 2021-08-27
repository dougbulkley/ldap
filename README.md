# ldap
Simple standalone code used for testing ldap functionality.

*Note: One exception to the below is **LoadTestJMXServer**, which uses Maven and is described in its own README.md file.*

Each directory is a standalone "application" that uses [ant](https://ant.apache.org/) to build and a **run** script to execute.

Since these are "quick and dirty" test applications, most require a locally running LDAP server and modifications to the src to properly connect.

Some of the applications, such as **TestGetAttributeWithOptions**, utilize a lightweight "in memory" LDAP server, a feature of PingDirectory (a [Ping Identity](https://www.pingidentity.com/) product).

* To build, type **ant** in the directory containing build.xml
* This will take the code contained in the src/ directory and compile it into a classes/ directory using the necessary lib/* files.
* To execute, type **run** in the directory containing build.xml
* If you need to remove the compiled classes/ directory, type **ant clean**
* Reference the ant documentation on the apache website for more help with using ant.
