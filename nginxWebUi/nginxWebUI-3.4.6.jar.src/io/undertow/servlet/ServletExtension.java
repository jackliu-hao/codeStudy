package io.undertow.servlet;

import io.undertow.servlet.api.DeploymentInfo;
import javax.servlet.ServletContext;

public interface ServletExtension {
  void handleDeployment(DeploymentInfo paramDeploymentInfo, ServletContext paramServletContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\ServletExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */