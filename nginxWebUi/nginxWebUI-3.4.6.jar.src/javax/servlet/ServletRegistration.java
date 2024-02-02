package javax.servlet;

import java.util.Collection;
import java.util.Set;

public interface ServletRegistration extends Registration {
  Set<String> addMapping(String... paramVarArgs);
  
  Collection<String> getMappings();
  
  String getRunAsRole();
  
  public static interface Dynamic extends ServletRegistration, Registration.Dynamic {
    void setLoadOnStartup(int param1Int);
    
    Set<String> setServletSecurity(ServletSecurityElement param1ServletSecurityElement);
    
    void setMultipartConfig(MultipartConfigElement param1MultipartConfigElement);
    
    void setRunAsRole(String param1String);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletRegistration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */