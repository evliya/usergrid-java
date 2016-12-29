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

/**
 * Configuration for OK HTTP Client, which is initialized in UsergridRequestManager
 * 
 * @author Eren Yilmaz <evliya@gmail.com>
 *
 */
public class UsergridHttpConfig {

	/**
	 * HttpClient connection timeout, in milliseconds.
	 */
	int connectTimeout = 30000;
	
	/**
	 * HttpClient socket timeout in milliseconds
	 */
	int socketTimeout = 30000;

	public UsergridHttpConfig() {
		// default configuration
	}
	
	/**
	 * Initialize Usergrid Java Client Http Parameters
	 * @param connectTimeout Timeout in milliseconds
	 * @param socketTimeout Timeout in milliseconds
	 */
	public UsergridHttpConfig(int connectTimeout, int socketTimeout) {
		super();
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
}
