package de.do9fse.cwterminal.core.model.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfoAnnotation {
    WinKeyProtocolVersion[] allowedProtocols();
    Class<?> resultType() default Void.class;
}