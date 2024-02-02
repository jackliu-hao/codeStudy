package io.undertow.servlet.api;

import io.undertow.server.HttpHandler;
import javax.servlet.ServletException;

public interface DeploymentManager {
   void deploy();

   HttpHandler start() throws ServletException;

   void stop() throws ServletException;

   void undeploy();

   State getState();

   Deployment getDeployment();

   public static enum State {
      UNDEPLOYED,
      DEPLOYED,
      STARTED;
   }
}
