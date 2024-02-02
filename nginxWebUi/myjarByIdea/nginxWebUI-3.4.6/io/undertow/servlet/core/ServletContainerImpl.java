package io.undertow.servlet.core;

import io.undertow.servlet.UndertowServletMessages;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ServletContainerImpl implements ServletContainer {
   private final Map<String, DeploymentManager> deployments = Collections.synchronizedMap(new HashMap());
   private final Map<String, DeploymentManager> deploymentsByPath = Collections.synchronizedMap(new HashMap());

   public Collection<String> listDeployments() {
      return new HashSet(this.deployments.keySet());
   }

   public DeploymentManager addDeployment(DeploymentInfo deployment) {
      DeploymentInfo dep = deployment.clone();
      DeploymentManager deploymentManager = new DeploymentManagerImpl(dep, this);
      this.deployments.put(dep.getDeploymentName(), deploymentManager);
      this.deploymentsByPath.put(dep.getContextPath(), deploymentManager);
      return deploymentManager;
   }

   public DeploymentManager getDeployment(String deploymentName) {
      return (DeploymentManager)this.deployments.get(deploymentName);
   }

   public void removeDeployment(DeploymentInfo deploymentInfo) {
      DeploymentManager deploymentManager = (DeploymentManager)this.deployments.get(deploymentInfo.getDeploymentName());
      if (deploymentManager.getState() != DeploymentManager.State.UNDEPLOYED) {
         throw UndertowServletMessages.MESSAGES.canOnlyRemoveDeploymentsWhenUndeployed(deploymentManager.getState());
      } else {
         this.deployments.remove(deploymentInfo.getDeploymentName());
         this.deploymentsByPath.remove(deploymentInfo.getContextPath());
      }
   }

   public DeploymentManager getDeploymentByPath(String path) {
      DeploymentManager exact = (DeploymentManager)this.deploymentsByPath.get(path.isEmpty() ? "/" : path);
      if (exact != null) {
         return exact;
      } else {
         int length = path.length();
         int pos = length;

         while(pos > 1) {
            --pos;
            if (path.charAt(pos) == '/') {
               String part = path.substring(0, pos);
               DeploymentManager deployment = (DeploymentManager)this.deploymentsByPath.get(part);
               if (deployment != null) {
                  return deployment;
               }
            }
         }

         return (DeploymentManager)this.deploymentsByPath.get("/");
      }
   }
}
