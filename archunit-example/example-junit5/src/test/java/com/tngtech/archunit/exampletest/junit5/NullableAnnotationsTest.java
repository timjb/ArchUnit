package com.tngtech.archunit.exampletest.junit5;


import com.tngtech.archunit.base.Optional;
import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

@ArchTag("example")
@AnalyzeClasses(packages = "com.tngtech.archunit.example.layers")
public class NullableAnnotationsTest {
    private static ArchCondition<JavaField> onlyHaveJpaFieldAnnotationsWithNullableProperty = new ArchCondition<JavaField>("only have JPA field annotations with 'nullable' property") {
        @Override
        public void check(JavaField field, ConditionEvents events) {
            checkOptionalAnnotation(field, "Column", "javax.persistence.Column", events);
        }

        private void checkOptionalAnnotation(JavaField field, String name, String fullName, ConditionEvents events) {
            Optional<JavaAnnotation> maybeAnnotation = field.tryGetAnnotationOfType(fullName);
            if (maybeAnnotation.isPresent()) {
                JavaAnnotation columnAnnotation = maybeAnnotation.get();
                boolean hasNullableProperty = columnAnnotation.get("nullable").isPresent();
                if (!hasNullableProperty) {
                    String message = String.format("@%s annotation for field %s does not specify 'nullable'", name, field.getFullName());
                    events.add(SimpleConditionEvent.violated(columnAnnotation, message));
                }
            }
        }
    };

    @ArchTest
    static final ArchRule entity_fields_should_explicitly_specify_whether_they_are_nullable = fields().should(onlyHaveJpaFieldAnnotationsWithNullableProperty);
}
