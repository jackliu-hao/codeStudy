package io.undertow.servlet;

import io.undertow.servlet.api.DeploymentInfo;
import javax.servlet.ServletContext;

public interface ServletExtension {
   void handleDeployment(DeploymentInfo var1, ServletContext var2);
}
