package ch.qos.logback.core.status;

import java.util.Iterator;

public interface Status {
   int INFO = 0;
   int WARN = 1;
   int ERROR = 2;

   int getLevel();

   int getEffectiveLevel();

   Object getOrigin();

   String getMessage();

   Throwable getThrowable();

   Long getDate();

   boolean hasChildren();

   void add(Status var1);

   boolean remove(Status var1);

   Iterator<Status> iterator();
}
