package javax.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DataSourceDefinitions.class)
public @interface DataSourceDefinition {
   String name();

   String className();

   String description() default "";

   String url() default "";

   String user() default "";

   String password() default "";

   String databaseName() default "";

   int portNumber() default -1;

   String serverName() default "localhost";

   int isolationLevel() default -1;

   boolean transactional() default true;

   int initialPoolSize() default -1;

   int maxPoolSize() default -1;

   int minPoolSize() default -1;

   int maxIdleTime() default -1;

   int maxStatements() default -1;

   String[] properties() default {};

   int loginTimeout() default 0;
}
