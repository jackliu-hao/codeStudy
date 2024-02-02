package io.undertow.servlet.api;

import io.undertow.security.idm.Account;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface AuthorizationManager {
  boolean isUserInRole(String paramString, Account paramAccount, ServletInfo paramServletInfo, HttpServletRequest paramHttpServletRequest, Deployment paramDeployment);
  
  boolean canAccessResource(List<SingleConstraintMatch> paramList, Account paramAccount, ServletInfo paramServletInfo, HttpServletRequest paramHttpServletRequest, Deployment paramDeployment);
  
  TransportGuaranteeType transportGuarantee(TransportGuaranteeType paramTransportGuaranteeType1, TransportGuaranteeType paramTransportGuaranteeType2, HttpServletRequest paramHttpServletRequest);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\AuthorizationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */