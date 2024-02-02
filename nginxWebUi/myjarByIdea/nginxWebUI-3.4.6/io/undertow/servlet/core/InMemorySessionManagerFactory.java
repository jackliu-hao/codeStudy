package io.undertow.servlet.core;

import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionManagerFactory;

public class InMemorySessionManagerFactory implements SessionManagerFactory {
   private final int maxSessions;
   private final boolean expireOldestUnusedSessionOnMax;

   public InMemorySessionManagerFactory() {
      this(-1, false);
   }

   public InMemorySessionManagerFactory(int maxSessions) {
      this(maxSessions, false);
   }

   public InMemorySessionManagerFactory(int maxSessions, boolean expireOldestUnusedSessionOnMax) {
      this.maxSessions = maxSessions;
      this.expireOldestUnusedSessionOnMax = expireOldestUnusedSessionOnMax;
   }

   public SessionManager createSessionManager(Deployment deployment) {
      return new InMemorySessionManager(deployment.getDeploymentInfo().getSessionIdGenerator(), deployment.getDeploymentInfo().getDeploymentName(), this.maxSessions, this.expireOldestUnusedSessionOnMax, deployment.getDeploymentInfo().getMetricsCollector() != null);
   }
}
