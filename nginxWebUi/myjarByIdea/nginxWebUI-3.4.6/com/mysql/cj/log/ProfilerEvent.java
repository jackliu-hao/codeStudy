package com.mysql.cj.log;

public interface ProfilerEvent {
   byte TYPE_USAGE = 0;
   byte TYPE_OBJECT_CREATION = 1;
   byte TYPE_PREPARE = 2;
   byte TYPE_QUERY = 3;
   byte TYPE_EXECUTE = 4;
   byte TYPE_FETCH = 5;
   byte TYPE_SLOW_QUERY = 6;
   byte NA = -1;

   byte getEventType();

   String getHostName();

   String getDatabase();

   long getConnectionId();

   int getStatementId();

   int getResultSetId();

   long getEventCreationTime();

   long getEventDuration();

   String getDurationUnits();

   String getEventCreationPointAsString();

   String getMessage();

   byte[] pack();
}
