package fr.asgard.mimir.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDescription {
    String value();
    String[] tags() default {};
    String category() default "";
    int order() default 0;
    
    /**
     * Description du type de retour (uniquement pour les méthodes)
     */
    String returnType() default "";
    
    /**
     * Liste des exceptions possibles (uniquement pour les méthodes)
     */
    Throws[] throws_() default {};
    
    @interface Throws {
        Class<? extends Throwable> exception();
        String description();
    }
} 