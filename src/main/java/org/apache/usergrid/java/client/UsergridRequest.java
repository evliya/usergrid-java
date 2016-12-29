/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.usergrid.java.client;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.auth.UsergridAuth;
import org.apache.usergrid.java.client.query.UsergridQuery;
import org.apache.usergrid.java.client.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@SuppressWarnings("unused")
public class UsergridRequest {
	@NotNull public static final ContentType APPLICATION_JSON_MEDIA_TYPE = ContentType.parse("application/json; charset=utf-8");

    @NotNull private UsergridHttpMethod method;
    @NotNull private String baseUrl;
    @NotNull private ContentType contentType;

    @Nullable private UsergridQuery query;
    @Nullable private Map<String, Object> headers;
    @Nullable private Map<String, Object> parameters;
    @Nullable private Object data;
    @Nullable private UsergridAuth auth;
    @Nullable private String[] pathSegments;

    @NotNull
    public UsergridHttpMethod getMethod() { return method; }
    public void setMethod(@NotNull final UsergridHttpMethod method) { this.method = method; }

    @NotNull
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(@NotNull final String baseUrl) { this.baseUrl = baseUrl; }

    @NotNull
    public ContentType getContentType() { return contentType; }
    public void setContentType(@NotNull final ContentType contentType) { this.contentType = contentType; }

    @Nullable
    public UsergridQuery getQuery() { return query; }
    public void setQuery(@Nullable final UsergridQuery query) { this.query = query; }

    @Nullable
    public Map<String,Object> getHeaders() { return headers; }
    public void setHeaders(@Nullable final Map<String,Object> headers) { this.headers = headers; }

    @Nullable
    public Map<String,Object> getParameters() { return parameters; }
    public void setParameters(@Nullable final Map<String,Object> parameters) { this.parameters = parameters; }

    @Nullable
    public Object getData() { return data; }
    public void setData(@Nullable final Object data) { this.data = data; }

    @Nullable
    public UsergridAuth getAuth() { return auth; }
    public void setAuth(@Nullable final UsergridAuth auth) { this.auth = auth; }

    @Nullable
    public String[] getPathSegments() { return pathSegments; }
    public void setPathSegments(@Nullable final String[] pathSegments) { this.pathSegments = pathSegments; }

    private UsergridRequest() {}

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final ContentType contentType,
                           @NotNull final String url,
                           @Nullable final UsergridQuery query,
                           @Nullable final UsergridAuth auth,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.query = query;
        this.auth = auth;
        this.pathSegments = pathSegments;
    }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final ContentType contentType,
                           @NotNull final String url,
                           @Nullable final UsergridAuth auth,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.auth = auth;
        this.pathSegments = pathSegments;
    }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final ContentType contentType,
                           @NotNull final String url,
                           @Nullable final Map<String, Object> params,
                           @Nullable final Object data,
                           @Nullable final UsergridAuth auth,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.parameters = params;
        this.data = data;
        this.headers = null;
        this.query = null;
        this.auth = auth;
        this.pathSegments = pathSegments;
    }

    public UsergridRequest(@NotNull final UsergridHttpMethod method,
                           @NotNull final ContentType contentType,
                           @NotNull final String url,
                           @Nullable final Map<String, Object> params,
                           @Nullable final Object data,
                           @Nullable final Map<String, Object> headers,
                           @Nullable final UsergridQuery query,
                           @Nullable final UsergridAuth auth,
                           @Nullable final String... pathSegments) {
        this.method = method;
        this.contentType = contentType;
        this.baseUrl = url;
        this.parameters = params;
        this.data = data;
        this.headers = headers;
        this.query = query;
        this.auth = auth;
        this.pathSegments = pathSegments;
    }

    @NotNull
    public Request buildRequest(UsergridHttpConfig httpConfig) throws URISyntaxException {
    	
    	Request r;
    	URI uri = constructUrl();
    	switch(this.method) {
    	case PUT:
    		r = Request.Put(uri)
    			.connectTimeout(httpConfig.getConnectTimeout())
    			.socketTimeout(httpConfig.getSocketTimeout())
    			.useExpectContinue()
    			;
    		r.body(constructRequestBody());
    		break;
    	case POST:
    		r = Request.Post(uri)
				.connectTimeout(httpConfig.getConnectTimeout())
				.socketTimeout(httpConfig.getSocketTimeout())
				.useExpectContinue()
				;
    		r.body(constructRequestBody());
    		break;
    	case DELETE:
    		r = Request.Delete(uri)
				.connectTimeout(httpConfig.getConnectTimeout())
				.socketTimeout(httpConfig.getSocketTimeout())
				.useExpectContinue()
				;
    		break;
    	case GET:
    	default:
    		r = Request.Get(uri)
				.connectTimeout(httpConfig.getConnectTimeout())
				.socketTimeout(httpConfig.getSocketTimeout())
				.useExpectContinue()
				;
    		break;
    	}
    	
        this.addHeaders(r);
        return r;
    }

    @NotNull
    protected URI constructUrl() throws URISyntaxException {
        String url = this.baseUrl;

        if( this.pathSegments != null ) {
            for( String path : this.pathSegments ) {
            	//uriBuilder.setPath(path);
            	url += "/" + path;
            }
        }
        if( this.query != null ) {
            url += this.query.build(true);
        }
        
        URIBuilder uriBuilder = new URIBuilder(url);
        if( this.parameters != null ) {
            for (Map.Entry<String, Object> param : this.parameters.entrySet()) {
            	uriBuilder.addParameter(param.getKey(), param.getValue().toString());
            }
        }
        return uriBuilder.build();
    }

    protected void addHeaders(@NotNull final Request request) {
        request.addHeader("User-Agent", UsergridRequestManager.USERGRID_USER_AGENT);
        if (this.auth != null ) {
            String accessToken = this.auth.getAccessToken();
            if( accessToken != null ) {
                request.addHeader("Authorization", "Bearer " + accessToken);
            }
        }
        if( this.headers != null ) {
            for( Map.Entry<String,Object> header : this.headers.entrySet() ) {
                request.addHeader(header.getKey(),header.getValue().toString());
            }
        }
    }

    @Nullable
    protected StringEntity constructRequestBody() {
    	StringEntity requestBody = null;
        if (method == UsergridHttpMethod.POST || method == UsergridHttpMethod.PUT) {
            String jsonString = "";
            if( this.data != null ) {
                jsonString = JsonUtils.toJsonString(this.data);
            }
            requestBody = new StringEntity(jsonString, this.contentType);
        }
        return requestBody;
    }
}
