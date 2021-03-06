/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.client;

import static com.github.tomakehurst.wiremock.client.ValueMatchingStrategy.toValuePattern;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.List;
import java.util.Map;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.mapping.RequestPattern;

public class RequestPatternBuilder {

	private RequestMethod method;
	private UrlMatchingStrategy urlMatchingStrategy;
	private Map<String, ValueMatchingStrategy> headers = newLinkedHashMap();
	private List<ValueMatchingStrategy> bodyPatterns = newArrayList();
	
	public RequestPatternBuilder(RequestMethod method,
			UrlMatchingStrategy urlMatchingStrategy) {
		this.method = method;
		this.urlMatchingStrategy = urlMatchingStrategy;
	}
	
	public RequestPatternBuilder withHeader(String key, ValueMatchingStrategy headerMatchingStrategy) {
		headers.put(key, headerMatchingStrategy);
		return this;
	}
	
	public RequestPatternBuilder withRequestBody(ValueMatchingStrategy bodyMatchingStrategy) {
		bodyPatterns.add(bodyMatchingStrategy);
		return this;
	}
	
	public RequestPattern build() {
		RequestPattern requestPattern = new RequestPattern();
		requestPattern.setMethod(method);
		urlMatchingStrategy.contributeTo(requestPattern);
		for (Map.Entry<String, ValueMatchingStrategy> header: headers.entrySet()) {
			requestPattern.addHeader(header.getKey(), header.getValue().asValuePattern());
		}
		
		if (!bodyPatterns.isEmpty()) {
			requestPattern.setBodyPatterns(newArrayList(transform(bodyPatterns, toValuePattern)));
		}
		
		return requestPattern;
	}
}
