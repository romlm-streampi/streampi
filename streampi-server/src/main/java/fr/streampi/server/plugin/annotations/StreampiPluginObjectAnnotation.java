package fr.streampi.server.plugin.annotations;

import static fr.streampi.server.plugin.enums.StreampiPluginCategory.DEFAULT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fr.streampi.server.plugin.enums.ExposurePolicy;
import fr.streampi.server.plugin.enums.StreampiPluginCategory;

@Retention(RUNTIME)
@Target(TYPE)
public @interface StreampiPluginObjectAnnotation {

	StreampiPluginCategory category() default DEFAULT;

	ExposurePolicy policy() default ExposurePolicy.EXPOSED;

	String name() default "";

	String description() default "";

}
