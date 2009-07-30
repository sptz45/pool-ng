package com.tzavellas.poolng.jmx;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.DescriptorKey;

/**
 * Annotation used to document the MBeans for management clients (jconsole, etc).
 * 
 * @author spiros
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JmxDescription {
	
	@DescriptorKey("Documentation")
	String value();

}
