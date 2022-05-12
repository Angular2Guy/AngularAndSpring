/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
// source: https://dzone.com/articles/setters-method-handles-and-java-11
package ch.xxx.trader.usecase.common;

import java.beans.PropertyDescriptor;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DtoUtils {
	public static final String CREATEDAT = "createdAt";
	
	public static PropertyDescriptor createPropertDescriptorApplier(String propertyName, List<PropertyDescriptor> propertyDescriptors) {		
        Function<String, PropertyDescriptor> property = name -> propertyDescriptors.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Not found: " + name));
        return property.apply(propertyName);
	}
	
	public static ObjectMapper produceObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}
	
	@SuppressWarnings("rawtypes")
	public static Function createGetter(final MethodHandles.Lookup lookup, final MethodHandle getter) throws Exception {
		final CallSite site = LambdaMetafactory.metafactory(lookup, "apply", MethodType.methodType(Function.class),
				MethodType.methodType(Object.class, Object.class), // signature of method Function.apply after type
																	// erasure
				getter, getter.type()); // actual signature of getter
		try {
			return (Function) site.getTarget().invokeExact();
		} catch (final Exception e) {
			throw e;
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static BiConsumer createSetter(final MethodHandles.Lookup lookup, final MethodHandle setter)
			throws Exception {
		final CallSite site = LambdaMetafactory.metafactory(lookup, "accept", MethodType.methodType(BiConsumer.class),
				MethodType.methodType(void.class, Object.class, Object.class), // signature of method BiConsumer.accept
																				// after type erasure
				setter, setter.type()); // actual signature of setter
		try {
			return (BiConsumer) site.getTarget().invokeExact();
		} catch (final Exception e) {
			throw e;
		} catch (final Throwable e) {
			throw new Error(e);
		}
	}
}
