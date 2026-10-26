package frame.server.config.mybatis;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Mybatis注解增强设置或略的字段
 * 
 * @author Administrator
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Invisible {

}
