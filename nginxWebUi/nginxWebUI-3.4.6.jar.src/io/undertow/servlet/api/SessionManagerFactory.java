package io.undertow.servlet.api;

import io.undertow.server.session.SessionManager;

public interface SessionManagerFactory {
  SessionManager createSessionManager(Deployment paramDeployment);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SessionManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */