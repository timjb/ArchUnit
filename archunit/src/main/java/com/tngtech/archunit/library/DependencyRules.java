package com.tngtech.archunit.library;

import com.tngtech.archunit.PublicAPI;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.PublicAPI.Usage.ACCESS;

public class DependencyRules {

    @PublicAPI(usage = ACCESS)
    public static ArchCondition<JavaClass> accessClassesThatResideInAnUpperPackage() {
        return new AccessClassesThatResideInAnUpperPackageCondition();
    }

    private static class AccessClassesThatResideInAnUpperPackageCondition extends ArchCondition<JavaClass> {
        AccessClassesThatResideInAnUpperPackageCondition() {
            super("access classes that reside in an upper package");
        }

        @Override
        public void check(final JavaClass clazz, final ConditionEvents events) {
            for (JavaAccess<?> access : clazz.getAccessesFromSelf()) {
                boolean callToSuperPackage = isCallToSuperPackage(access.getOriginOwner(), access.getTargetOwner());
                events.add(new SimpleConditionEvent(access, callToSuperPackage, access.getDescription()));
            }
        }

        private boolean isCallToSuperPackage(JavaClass origin, JavaClass target) {
            String originPackageName = getOutermostEnclosingClass(origin).getPackageName();
            String targetSubPackagePrefix = getOutermostEnclosingClass(target).getPackageName() + ".";
            return originPackageName.startsWith(targetSubPackagePrefix);
        }

        private JavaClass getOutermostEnclosingClass(JavaClass javaClass) {
            while (javaClass.getEnclosingClass().isPresent()) {
                javaClass = javaClass.getEnclosingClass().get();
            }
            return javaClass;
        }
    }
}
