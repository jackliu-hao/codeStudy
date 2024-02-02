package ch.qos.logback.core.status;

import java.util.List;

public interface StatusManager {
   void add(Status var1);

   List<Status> getCopyOfStatusList();

   int getCount();

   boolean add(StatusListener var1);

   void remove(StatusListener var1);

   void clear();

   List<StatusListener> getCopyOfStatusListenerList();
}
