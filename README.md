The Java binding for the Neo4j Server REST API wraps the REST calls behind the well known
[GraphDatabaseService](http://api.neo4j.org/1.2/org/neo4j/graphdb/GraphDatabaseService.html) API.

Currently supports:
___________________
 * all the node and relationship operations
 * the new Index API
 * Basic Http Auth (Digest)
 * preliminary traversal support

Open issues:
____________
 * full traversal support
 * support for exposing server extensions - via an interface based proxy

Usage:
------

Build it locally. Then use the maven / ivy dependency or copy the jar into your app.

    <dependency>
		<groupId>org.neo4j</groupId>
		<artifactId>neo4j-rest-graphdb</artifactId>
		<version>0.1-SNAPSHOT</version>
    </dependency>

    GraphDatabaseService gds = new RestGraphDatabase(new URI("http://localhost:7474/db/data"));
    GraphDatabaseService gds = new RestGraphDatabase(new URI("http://localhost:7474/db/data"),username,password);

    <bean id="graphDbService" class="org.neo4j.rest.graphdb.RestGraphDatabase" destroy-method="shutdown">
        <constructor-arg index="0" value="http://localhost:7474/db/data" />
    </bean>
    </pre>

**Please note: Transactions are not supported over this API.**

Unit Test:
----------
For the "unit" tests to succeed, there has to be a running neo4j server on port 7474. Didn't want to pull in the server dependency and start one on my own.
(Will perhaps end up to do that)


References / Community:
-----------------------

 * [Neo4j community site](http://neo4j.org)
 * [Neo4j REST API](http://components.neo4j.org/neo4j-server/milestone/rest.html)
 * [Neo4j Wiki](http://wiki.neo4j.org)
 * [Neo4j Mailing List](https://lists.neo4j.org/mailman/listinfo/user)
