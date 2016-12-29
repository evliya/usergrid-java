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


import static org.apache.usergrid.java.client.utils.ObjectUtils.isEmpty;

import java.util.Map;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.usergrid.java.client.UsergridEnums.UsergridHttpMethod;
import org.apache.usergrid.java.client.auth.UsergridAppAuth;
import org.apache.usergrid.java.client.auth.UsergridAuth;
import org.apache.usergrid.java.client.auth.UsergridUserAuth;
import org.apache.usergrid.java.client.model.UsergridUser;
import org.apache.usergrid.java.client.response.UsergridResponse;
import org.jetbrains.annotations.NotNull;

public class UsergridRequestManager {

    @NotNull public static String USERGRID_USER_AGENT = "usergrid-java-client/v" + Usergrid.UsergridSDKVersion;

    @NotNull private final UsergridClient usergridClient;

    @NotNull private final UsergridHttpConfig httpConfig;
    
    public UsergridRequestManager(@NotNull final UsergridClient usergridClient) {
        this.usergridClient = usergridClient;
        this.httpConfig = new UsergridHttpConfig();
    }
    
    public UsergridRequestManager(@NotNull final UsergridClient usergridClient, @NotNull final UsergridHttpConfig httpConfig) {
        this.usergridClient = usergridClient;
        this.httpConfig = httpConfig;
    }

    @NotNull
    public UsergridResponse performRequest(@NotNull final UsergridRequest usergridRequest) {
    	UsergridResponse usergridResponse;
        try {
        	Request request = usergridRequest.buildRequest(httpConfig);
            Response response = request.execute();
            usergridResponse = UsergridResponse.fromResponse(this.usergridClient,usergridRequest,response);
        } catch( Exception exception ) {
            usergridResponse = UsergridResponse.fromException(this.usergridClient,exception);
        }
        return usergridResponse;
    }

    @NotNull
    private UsergridResponse authenticate(@NotNull final UsergridAuth auth) {
        Map<String, String> credentials = auth.credentialsMap();
        String url = this.usergridClient.clientAppUrl();
        if ( auth instanceof UsergridUserAuth){

            UsergridUserAuth userAuth = (UsergridUserAuth) auth;
            if( userAuth.isAdminUser()){

                url = this.usergridClient.managementUrl();
            }

        }

        UsergridRequest request = new UsergridRequest(UsergridHttpMethod.POST, UsergridRequest.APPLICATION_JSON_MEDIA_TYPE, url, null, credentials, this.usergridClient.authForRequests(), "token");
        UsergridResponse response = performRequest(request);
        if (!isEmpty(response.getAccessToken()) && !isEmpty(response.getExpires())) {
            auth.setAccessToken(response.getAccessToken());
            auth.setExpiry(System.currentTimeMillis() + response.getExpires() - 5000);
        }
        return response;
    }

    @NotNull
    public UsergridResponse authenticateApp(@NotNull final UsergridAppAuth appAuth) {
        return this.authenticate(appAuth);
    }

    @NotNull
    public UsergridResponse authenticateUser(@NotNull final UsergridUserAuth userAuth) {
        UsergridResponse response = this.authenticate(userAuth);
        UsergridUser responseUser = response.user();
        if ( response.ok() && responseUser != null) {
            responseUser.setUserAuth(userAuth);
        }
        return response;
    }
}
