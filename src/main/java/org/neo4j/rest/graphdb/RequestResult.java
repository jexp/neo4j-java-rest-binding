/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.rest.graphdb;

import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.neo4j.rest.graphdb.RecordingRestRequest.RestOperation;

import java.net.URI;
import java.util.Map;


/**
* @author Klemens Burchardi
* @since 03.08.11
*/
public class RequestResult {
    private final int status;
    private final URI location;
    private final String entity;
    private long batchId;
    private boolean batchResult = false;

    
    RequestResult(int status, URI location, String entity) {
        this.status = status;
        this.location = location;
        this.entity = entity;
    }
    
    RequestResult(long batchId) {
       this(0,null,"");
       this.batchResult = true;
       this.batchId = batchId;
    }
    
    public static RequestResult batchResult(RestOperation restOperation){
        return new RequestResult(restOperation.getBatchId());
    }

    public static RequestResult extractFrom(ClientResponse clientResponse) {
        final int status = clientResponse.getStatus();
        final URI location = clientResponse.getLocation();
        final String data = status != Response.Status.NO_CONTENT.getStatusCode() ? clientResponse.getEntity(String.class) : null;
        clientResponse.close();
        return new RequestResult(status, location, data);
    }
    
  

    public int getStatus() {
        return status;
    }

    public URI getLocation() {
        return location;
    }

    public String getEntity() {
        return entity;
    }

    public Object toEntity() {
        return JsonHelper.jsonToSingleValue( getEntity() );        
    }

    public Map<?, ?> toMap() {
        final String json = getEntity();
        return JsonHelper.jsonToMap(json);
    }

    public boolean statusIs( StatusType status ) {
        return getStatus() == status.getStatusCode();
    }

    public boolean statusOtherThan( StatusType status ) {
        return !statusIs(status );
    }
    
    public long getBatchId() {
        return batchId;
    }
    
    public boolean isBatchResult(){
        return batchResult;
    }

}