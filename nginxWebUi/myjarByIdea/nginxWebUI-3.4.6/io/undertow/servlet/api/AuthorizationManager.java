package io.undertow.servlet.api;

import io.undertow.security.idm.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface AuthorizationManager {
   boolean isUserInRole(String var1, Account var2, ServletInfo var3, HttpServletRequest var4, Deployment var5);

   boolean canAccessResource(List<SingleConstraintMatch> var1, Account var2, ServletInfo var3, HttpServletRequest var4, Deployment var5);

   TransportGuaranteeType transportGuarantee(TransportGuaranteeType var1, TransportGuaranteeType var2, HttpServletRequest var3);
}
