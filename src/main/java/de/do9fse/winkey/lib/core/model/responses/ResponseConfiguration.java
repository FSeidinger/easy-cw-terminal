package de.do9fse.winkey.lib.core.model.responses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResponseConfiguration {
    int expectedResponseBytes() default 0;
}
