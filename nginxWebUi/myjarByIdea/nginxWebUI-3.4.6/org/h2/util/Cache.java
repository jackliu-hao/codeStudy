package org.h2.util;

import java.util.ArrayList;

public interface Cache {
   ArrayList<CacheObject> getAllChanged();

   void clear();

   CacheObject get(int var1);

   void put(CacheObject var1);

   CacheObject update(int var1, CacheObject var2);

   boolean remove(int var1);

   CacheObject find(int var1);

   void setMaxMemory(int var1);

   int getMaxMemory();

   int getMemory();
}
