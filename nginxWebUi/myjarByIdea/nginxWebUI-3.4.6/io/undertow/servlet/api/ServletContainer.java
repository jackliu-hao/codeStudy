package io.undertow.servlet.api;

import io.undertow.servlet.core.ServletContainerImpl;
import java.util.Collection;

public interface ServletContainer {
   Collection<String> listDeployments();

   DeploymentManager addDeployment(DeploymentInfo var1);

   DeploymentManager getDeployment(String var1);

   void removeDeployment(DeploymentInfo var1);

   DeploymentManager getDeploymentByPath(String var1);

   public static class Factory {
      public static ServletContainer newInstance() {
         return new ServletContainerImpl();
      }
   }
}
