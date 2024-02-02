package org.codehaus.plexus.util;

import java.io.File;

public interface DirectoryWalkListener {
  void directoryWalkStarting(File paramFile);
  
  void directoryWalkStep(int paramInt, File paramFile);
  
  void directoryWalkFinished();
  
  void debug(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\DirectoryWalkListener.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */