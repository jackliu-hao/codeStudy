package io.undertow.servlet.api;

import io.undertow.server.session.SessionManager;

public interface SessionManagerFactory {
   SessionManager createSessionManager(Deployment var1);
}
