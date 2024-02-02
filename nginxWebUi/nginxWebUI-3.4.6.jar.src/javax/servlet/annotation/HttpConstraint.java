package javax.servlet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpConstraint {
  ServletSecurity.EmptyRoleSemantic value() default ServletSecurity.EmptyRoleSemantic.PERMIT;
  
  ServletSecurity.TransportGuarantee transportGuarantee() default ServletSecurity.TransportGuarantee.NONE;
  
  String[] rolesAllowed() default {};
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\annotation\HttpConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */