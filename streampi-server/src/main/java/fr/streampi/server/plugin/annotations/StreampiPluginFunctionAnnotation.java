package fr.streampi.server.plugin.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface StreampiPluginFunctionAnnotation {

	String name() default "";

	String description() default "";

}
