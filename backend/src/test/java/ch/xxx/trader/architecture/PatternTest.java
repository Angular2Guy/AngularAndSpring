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
package ch.xxx.trader.architecture;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.importer.Location;

import ch.xxx.trader.architecture.MyArchitectureTests.DoNotIncludeAotGenerated;

public class PatternTest {
	private List<String> testStrings = List.of(
			"ch.xxx.trader.adapter.controller.StatisticsController__BeanDefinitions.class",
			"file:/home/sven/git/AngularAndSpring/backend/target/classes/ch/xxx/trader/TraderApplication$$SpringCGLIB$$0.class",
			"ch.xxx.maps.usecase.service.CompanySiteService__TestContext001_BeanDefinitions.class");
	private final DoNotIncludeAotGenerated importOption = new MyArchitectureTests.DoNotIncludeAotGenerated();

	@Test
	public void testMatcherBeanDefinition() throws URISyntaxException {
		Assertions.assertFalse(importOption.includes(Location.of(Path.of(testStrings.get(0)))));
	}

	@Test
	public void testMatcherCgLib() throws URISyntaxException {
		Assertions.assertFalse(importOption.includes(Location.of(Path.of(testStrings.get(1)))));
	}
	
	@Test
	public void testMatcherTests() throws URISyntaxException {
		Assertions.assertFalse(importOption.includes(Location.of(Path.of(testStrings.get(2)))));
	}
}
