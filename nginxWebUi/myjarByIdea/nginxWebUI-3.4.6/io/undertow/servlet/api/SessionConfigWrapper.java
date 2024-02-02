package io.undertow.servlet.api;

import io.undertow.server.session.SessionConfig;

public interface SessionConfigWrapper {
   SessionConfig wrap(SessionConfig var1, Deployment var2);
}
