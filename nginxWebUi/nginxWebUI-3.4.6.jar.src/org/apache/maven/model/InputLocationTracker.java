package org.apache.maven.model;

public interface InputLocationTracker {
  InputLocation getLocation(Object paramObject);
  
  void setLocation(Object paramObject, InputLocation paramInputLocation);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\InputLocationTracker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */