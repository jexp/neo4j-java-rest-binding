The Java binding for the Neo4j Server REST API wraps the REST calls behind the well known
[GraphDatabaseService](http://api.neo4j.org/1.2/org/neo4j/graphdb/GraphDatabaseService.html) API.

Currently all the node and relationship operations are supported.
The new Index API is also supported.

Open issues:
* traversal support
* support for exposing server extensions - via an interface based proxy



Please note: Transactions are not supported over this API.

