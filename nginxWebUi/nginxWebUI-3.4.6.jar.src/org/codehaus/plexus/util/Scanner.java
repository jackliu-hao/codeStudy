package org.codehaus.plexus.util;

import java.io.File;

public interface Scanner {
  void setIncludes(String[] paramArrayOfString);
  
  void setExcludes(String[] paramArrayOfString);
  
  void addDefaultExcludes();
  
  void scan();
  
  String[] getIncludedFiles();
  
  String[] getIncludedDirectories();
  
  File getBasedir();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\Scanner.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */