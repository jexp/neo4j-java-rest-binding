package org.neo4j.rest.graphdb;

import java.net.URI;

public interface RestRequest {

    RequestResult get(String path);

    RequestResult get(String path, Object data);

    RequestResult delete(String path);

    RequestResult post(String path, Object data);

    RequestResult put(String path, Object data);

    RestRequest with(String uri);

    URI getUri();

}