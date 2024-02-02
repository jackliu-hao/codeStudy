package io.undertow.server.session;

public interface SessionManagerStatistics {
   long getCreatedSessionCount();

   long getMaxActiveSessions();

   default long getHighestSessionCount() {
      return -1L;
   }

   long getActiveSessionCount();

   long getExpiredSessionCount();

   long getRejectedSessions();

   long getMaxSessionAliveTime();

   long getAverageSessionAliveTime();

   long getStartTime();
}
