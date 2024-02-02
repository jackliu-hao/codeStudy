package javax.servlet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpMethodConstraint {
  String value();
  
  ServletSecurity.EmptyRoleSemantic emptyRoleSemantic() default ServletSecurity.EmptyRoleSemantic.PERMIT;
  
  ServletSecurity.TransportGuarantee transportGuarantee() default ServletSecurity.TransportGuarantee.NONE;
  
  String[] rolesAllowed() default {};
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\annotation\HttpMethodConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */