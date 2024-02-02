package io.undertow.servlet.util;

import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.api.SessionPersistenceManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionPersistence implements SessionPersistenceManager {
   private static final Map<String, Map<String, SessionEntry>> data = new ConcurrentHashMap();

   public void persistSessions(String deploymentName, Map<String, SessionPersistenceManager.PersistentSession> sessionData) {
      try {
         Map<String, SessionEntry> serializedData = new HashMap();
         Iterator var4 = sessionData.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, SessionPersistenceManager.PersistentSession> sessionEntry = (Map.Entry)var4.next();
            Map<String, byte[]> data = new HashMap();
            Iterator var7 = ((SessionPersistenceManager.PersistentSession)sessionEntry.getValue()).getSessionData().entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry<String, Object> sessionAttribute = (Map.Entry)var7.next();

               try {
                  ByteArrayOutputStream out = new ByteArrayOutputStream();
                  ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                  objectOutputStream.writeObject(sessionAttribute.getValue());
                  objectOutputStream.close();
                  data.put(sessionAttribute.getKey(), out.toByteArray());
               } catch (Exception var11) {
                  UndertowServletLogger.ROOT_LOGGER.failedToPersistSessionAttribute((String)sessionAttribute.getKey(), sessionAttribute.getValue(), (String)sessionEntry.getKey(), var11);
               }
            }

            serializedData.put(sessionEntry.getKey(), new SessionEntry(((SessionPersistenceManager.PersistentSession)sessionEntry.getValue()).getExpiration(), data));
         }

         InMemorySessionPersistence.data.put(deploymentName, serializedData);
      } catch (Exception var12) {
         UndertowServletLogger.ROOT_LOGGER.failedToPersistSessions(var12);
      }

   }

   public Map<String, SessionPersistenceManager.PersistentSession> loadSessionAttributes(String deploymentName, ClassLoader classLoader) {
      try {
         long time = System.currentTimeMillis();
         Map<String, SessionEntry> data = (Map)InMemorySessionPersistence.data.remove(deploymentName);
         if (data != null) {
            Map<String, SessionPersistenceManager.PersistentSession> ret = new HashMap();
            Iterator var7 = data.entrySet().iterator();

            while(true) {
               Map.Entry sessionEntry;
               do {
                  if (!var7.hasNext()) {
                     return ret;
                  }

                  sessionEntry = (Map.Entry)var7.next();
               } while(((SessionEntry)sessionEntry.getValue()).expiry.getTime() <= time);

               Map<String, Object> session = new HashMap();
               Iterator var10 = ((SessionEntry)sessionEntry.getValue()).data.entrySet().iterator();

               while(var10.hasNext()) {
                  Map.Entry<String, byte[]> sessionAttribute = (Map.Entry)var10.next();
                  ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream((byte[])sessionAttribute.getValue()));
                  session.put(sessionAttribute.getKey(), in.readObject());
               }

               ret.put(sessionEntry.getKey(), new SessionPersistenceManager.PersistentSession(((SessionEntry)sessionEntry.getValue()).expiry, session));
            }
         }
      } catch (Exception var13) {
         UndertowServletLogger.ROOT_LOGGER.failedtoLoadPersistentSessions(var13);
      }

      return null;
   }

   public void clear(String deploymentName) {
   }

   static final class SessionEntry {
      private final Date expiry;
      private final Map<String, byte[]> data;

      private SessionEntry(Date expiry, Map<String, byte[]> data) {
         this.expiry = expiry;
         this.data = data;
      }

      // $FF: synthetic method
      SessionEntry(Date x0, Map x1, Object x2) {
         this(x0, x1);
      }
   }
}
