package com.doctusoft.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark things where the API is still under heavy brainstorming and can change completely.
 */
@Retention(RetentionPolicy.CLASS)
@Target({
    ElementType.ANNOTATION_TYPE,
    ElementType.METHOD,
    ElementType.TYPE
})
public @interface Beta {
    
}
