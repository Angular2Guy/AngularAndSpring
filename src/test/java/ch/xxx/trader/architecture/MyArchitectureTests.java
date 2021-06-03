package ch.xxx.trader.architecture;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.GeneralCodingRules;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@AnalyzeClasses(packages = "ch.xxx.trader", importOptions = { DoNotIncludeTests.class })
public class MyArchitectureTests {
	private JavaClasses importedClasses = new ClassFileImporter().importPackages("ch.xxx.trader");
	
	@ArchTest
	static final ArchRule clean_architecture_respected = Architectures.onionArchitecture().domainModels("..domain..")
			.applicationServices("..usecase..").adapter("rest", "..adapter.controller..")
			.adapter("cron", "..adapter.cron..").adapter("repo", "..adapter.repository..")
			.adapter("config", "..adapter.config..").adapter("clients", "..adapter.clients..").withOptionalLayers(true);

	@ArchTest
	static final ArchRule cyclesDomain = SlicesRuleDefinition.slices().matching("..domain.(*)..").should()
			.beFreeOfCycles();

	@ArchTest
	static final ArchRule cyclesUseCases = SlicesRuleDefinition.slices().matching("..usecase..").should()
			.beFreeOfCycles();

	@ArchTest
	static final ArchRule cyclesAdapter = SlicesRuleDefinition.slices().matching("..adapter.(*)..").should()
			.beFreeOfCycles();

	@Test
	public void ruleControllerAnnotations() {
	
		ArchRule beAnnotatedWith = ArchRuleDefinition.classes().that().resideInAPackage("..adapter.controller..")
				.should().beAnnotatedWith(RestController.class);
		beAnnotatedWith.check(this.importedClasses);
	}

	@Test
	public void ruleExceptionsType() {
		ArchRule exceptionType = ArchRuleDefinition.classes().that().resideInAPackage("..domain.exceptions..").should()
				.beAssignableTo(RuntimeException.class).orShould().beAssignableTo(DefaultErrorAttributes.class);
		exceptionType.check(this.importedClasses);
	}
	
	@Test
	public void ruleCronJobMethodsAnnotations() {
		ArchRule exceptionType = ArchRuleDefinition.methods()
			    .that().arePublic()
			    .and().areDeclaredInClassesThat().resideInAPackage("..adapter.cron.ScheduledTask").should()
				.beAnnotatedWith(PostConstruct.class).orShould().beAnnotatedWith(Scheduled.class).andShould().beAnnotatedWith(SchedulerLock.class); 
		exceptionType.check(this.importedClasses);
	}
	
	@Test
	public void ruleGeneralCodingRulesLoggers() {
		ArchRuleDefinition.fields().that().haveRawType(Logger.class)
        .should().bePrivate()
        .andShould().beStatic()
        .andShould().beFinal()
        .because("we agreed on this convention")
        .check(this.importedClasses);
	}	
}