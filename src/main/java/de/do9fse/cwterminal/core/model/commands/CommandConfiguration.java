package de.do9fse.cwterminal.core.model.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.responses.EmptyResponse;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandConfiguration {
    WinKeyProtocolVersion[] allowedProtocols();
    Class<?> responseType() default EmptyResponse.class;
}