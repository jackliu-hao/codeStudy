package javax.servlet;

import java.util.Map;
import java.util.Set;

public interface Registration {
  String getName();
  
  String getClassName();
  
  boolean setInitParameter(String paramString1, String paramString2);
  
  String getInitParameter(String paramString);
  
  Set<String> setInitParameters(Map<String, String> paramMap);
  
  Map<String, String> getInitParameters();
  
  public static interface Dynamic extends Registration {
    void setAsyncSupported(boolean param1Boolean);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\Registration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */