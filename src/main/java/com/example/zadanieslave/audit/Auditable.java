package com.example.zadanieslave.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String action();                  // например "UPLOAD_DOCUMENT", "VIEW_DOCUMENTS"
    String entityType() default "";   // "Document", "Project", ...
    String details() default "";      // дополнительные сведения
}