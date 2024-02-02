package javax.servlet.descriptor;

import java.util.Collection;

public interface JspPropertyGroupDescriptor {
  Collection<String> getUrlPatterns();
  
  String getElIgnored();
  
  String getPageEncoding();
  
  String getScriptingInvalid();
  
  String getIsXml();
  
  Collection<String> getIncludePreludes();
  
  Collection<String> getIncludeCodas();
  
  String getDeferredSyntaxAllowedAsLiteral();
  
  String getTrimDirectiveWhitespaces();
  
  String getDefaultContentType();
  
  String getBuffer();
  
  String getErrorOnUndeclaredNamespace();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\descriptor\JspPropertyGroupDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */