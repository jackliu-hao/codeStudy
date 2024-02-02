package io.undertow.client;

public interface ClientStatistics {
   long getRequests();

   long getRead();

   long getWritten();

   void reset();
}
