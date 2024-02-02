package org.codehaus.plexus.util.cli;

import java.io.File;

public interface Arg {
  void setValue(String paramString);
  
  void setLine(String paramString);
  
  void setFile(File paramFile);
  
  String[] getParts();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\Arg.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */