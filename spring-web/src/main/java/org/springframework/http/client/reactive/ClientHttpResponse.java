/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http.client.reactive;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * Represents a client-side reactive HTTP response.
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @since 5.0
 */
public interface ClientHttpResponse extends ReactiveHttpInputMessage {

	/**
	 * Return an id that represents the underlying connection, if available,
	 * or the request for the purpose of correlating log messages.
	 * @since 5.3.5
	 */
	default String getId() {
		return ObjectUtils.getIdentityHexString(this);
	}

	/**
	 * Return the HTTP status code as an {@link HttpStatusCode}.
	 * @return the HTTP status as {@code HttpStatusCode} value (never {@code null})
	 */
	HttpStatusCode getStatusCode();

	/**
	 * Return the HTTP status code as an integer.
	 * @return the HTTP status as an integer value
	 * @since 5.0.6
	 * @see #getStatusCode()
	 * @deprecated in favor of {@link #getStatusCode()}, for removal in 7.0
	 */
	@Deprecated(since = "6.0", forRemoval = true)
	default int getRawStatusCode() {
		return getStatusCode().value();
	}

	/**
	 * Return a read-only map of response cookies received from the server.
	 */
	MultiValueMap<String, ResponseCookie> getCookies();

	static long mergeMaxAgeAndExpires(@Nullable String maxAgeAttribute, @Nullable String expiresAttribute) {
		if (maxAgeAttribute != null) {
			return Long.parseLong(maxAgeAttribute);
		}
		else if (expiresAttribute != null) {
			ZonedDateTime expiresDate = HttpHeaders.getZonedDateTime(expiresAttribute);

			// Verify that the input date is in the future
			ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
			if (expiresDate == null || expiresDate.isBefore(now)) {
				return -1;
			}
			else {
				// Calculate the difference in seconds
				return ChronoUnit.SECONDS.between(now, expiresDate);
			}
		}
		return -1;
	}

}
