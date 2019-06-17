package com.tngtech.archunit.exampletest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.example.ClassViolatingCodingRules;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.DependencyRules.accessClassesThatResideInAnUpperPackage;

@Category(Example.class)
public class DependencyRulesTest {

    private final JavaClasses classes = new ClassFileImporter().importPackagesOf(ClassViolatingCodingRules.class);

    @Test
    public void no_accesses_to_upper_package() {
        noClasses().should(accessClassesThatResideInAnUpperPackage())
                .check(classes);
    }
}
