package org.h2.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.h2.api.DatabaseEventListener;
import org.h2.command.Command;
import org.h2.message.DbException;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.Authenticator;
import org.h2.store.fs.FileUtils;
import org.h2.util.MathUtils;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.ThreadDeadlockDetector;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;

public final class Engine {
   private static final Map<String, DatabaseHolder> DATABASES = new HashMap();
   private static volatile long WRONG_PASSWORD_DELAY;
   private static boolean JMX;

   private static SessionLocal openSession(ConnectionInfo var0, boolean var1, boolean var2, String var3) {
      String var4 = var0.getName();
      var0.removeProperty("NO_UPGRADE", false);
      boolean var6 = var0.getProperty("OPEN_NEW", false);
      boolean var7 = false;
      User var8 = null;
      DatabaseHolder var9;
      if (!var0.isUnnamedInMemory()) {
         synchronized(DATABASES) {
            var9 = (DatabaseHolder)DATABASES.computeIfAbsent(var4, (var0x) -> {
               return new DatabaseHolder();
            });
         }
      } else {
         var9 = new DatabaseHolder();
      }

      Database var5;
      synchronized(var9) {
         var5 = var9.database;
         if (var5 == null || var6) {
            if (var0.isPersistent()) {
               String var11 = var0.getProperty("MV_STORE");
               String var12;
               if (var11 == null) {
                  var12 = var4 + ".mv.db";
                  if (!FileUtils.exists(var12)) {
                     throwNotFound(var1, var2, var4);
                     var12 = var4 + ".data.db";
                     if (FileUtils.exists(var12)) {
                        throw DbException.getFileVersionError(var12);
                     }

                     var12 = null;
                  }
               } else {
                  var12 = var4 + ".mv.db";
                  if (!FileUtils.exists(var12)) {
                     throwNotFound(var1, var2, var4);
                     var12 = null;
                  }
               }

               if (var12 != null && !FileUtils.canWrite(var12)) {
                  var0.setProperty("ACCESS_MODE_DATA", "r");
               }
            } else {
               throwNotFound(var1, var2, var4);
            }

            var5 = new Database(var0, var3);
            var7 = true;
            boolean var21 = false;
            Iterator var23 = var5.getAllUsersAndRoles().iterator();

            while(var23.hasNext()) {
               RightOwner var13 = (RightOwner)var23.next();
               if (var13 instanceof User) {
                  var21 = true;
                  break;
               }
            }

            if (!var21) {
               var8 = new User(var5, var5.allocateObjectId(), var0.getUserName(), false);
               var8.setAdmin(true);
               var8.setUserPasswordHash(var0.getUserPasswordHash());
               var5.setMasterUser(var8);
            }

            var9.database = var5;
         }
      }

      if (var7) {
         var5.opened();
      }

      if (var5.isClosing()) {
         return null;
      } else {
         if (var8 == null) {
            if (var5.validateFilePasswordHash(var3, var0.getFilePasswordHash())) {
               if (var0.getProperty("AUTHREALM") == null) {
                  var8 = var5.findUser(var0.getUserName());
                  if (var8 != null && !var8.validateUserPasswordHash(var0.getUserPasswordHash())) {
                     var8 = null;
                  }
               } else {
                  Authenticator var10 = var5.getAuthenticator();
                  if (var10 == null) {
                     throw DbException.get(90144, var4);
                  }

                  try {
                     AuthenticationInfo var22 = new AuthenticationInfo(var0);
                     var8 = var5.getAuthenticator().authenticate(var22, var5);
                  } catch (AuthenticationException var16) {
                     var5.getTrace(2).error(var16, "an error occurred during authentication; user: \"" + var0.getUserName() + "\"");
                  }
               }
            }

            if (var7 && (var8 == null || !var8.isAdmin())) {
               var5.setEventListener((DatabaseEventListener)null);
            }
         }

         if (var8 == null) {
            DbException var20 = DbException.get(28000);
            var5.getTrace(2).error(var20, "wrong user or password; user: \"" + var0.getUserName() + "\"");
            var5.removeSession((SessionLocal)null);
            throw var20;
         } else {
            var0.cleanAuthenticationInfo();
            checkClustering(var0, var5);
            SessionLocal var19 = var5.createSession(var8, var0.getNetworkConnectionInfo());
            if (var19 == null) {
               return null;
            } else {
               if (var0.getProperty("OLD_INFORMATION_SCHEMA", false)) {
                  var19.setOldInformationSchema(true);
               }

               if (var0.getProperty("JMX", false)) {
                  try {
                     Utils.callStaticMethod("org.h2.jmx.DatabaseInfo.registerMBean", var0, var5);
                  } catch (Exception var15) {
                     var5.removeSession(var19);
                     throw DbException.get(50100, var15, "JMX");
                  }

                  JMX = true;
               }

               return var19;
            }
         }
      }
   }

   private static void throwNotFound(boolean var0, boolean var1, String var2) {
      if (var0) {
         throw DbException.get(90146, var2);
      } else if (var1) {
         throw DbException.get(90149, var2);
      }
   }

   public static SessionLocal createSession(ConnectionInfo var0) {
      try {
         SessionLocal var1 = openSession(var0);
         validateUserAndPassword(true);
         return var1;
      } catch (DbException var2) {
         if (var2.getErrorCode() == 28000) {
            validateUserAndPassword(false);
         }

         throw var2;
      }
   }

   private static SessionLocal openSession(ConnectionInfo var0) {
      boolean var1 = var0.removeProperty("IFEXISTS", false);
      boolean var2 = var0.removeProperty("FORBID_CREATION", false);
      boolean var3 = var0.removeProperty("IGNORE_UNKNOWN_SETTINGS", false);
      String var4 = var0.removeProperty("CIPHER", (String)null);
      String var5 = var0.removeProperty("INIT", (String)null);
      long var7 = System.nanoTime();

      while(true) {
         SessionLocal var6 = openSession(var0, var1, var2, var4);
         if (var6 != null) {
            synchronized(var6) {
               var6.setAllowLiterals(true);
               DbSettings var10 = DbSettings.DEFAULT;
               String[] var11 = var0.getKeys();
               int var12 = var11.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  String var14 = var11[var13];
                  if (!var10.containsKey(var14)) {
                     String var15 = var0.getProperty(var14);
                     StringBuilder var16 = (new StringBuilder("SET ")).append(var14).append(' ');
                     if (!ParserUtil.isSimpleIdentifier(var14, false, false)) {
                        if (!var14.equalsIgnoreCase("TIME ZONE")) {
                           throw DbException.get(90113, var14);
                        }

                        StringUtils.quoteStringSQL(var16, var15);
                     } else {
                        var16.append(var15);
                     }

                     try {
                        Command var17 = var6.prepareLocal(var16.toString());
                        var17.executeUpdate((Object)null);
                     } catch (DbException var21) {
                        if (var21.getErrorCode() == 90040) {
                           var6.getTrace().error(var21, "admin rights required; user: \"" + var0.getUserName() + "\"");
                        } else {
                           var6.getTrace().error(var21, "");
                        }

                        if (!var3) {
                           var6.close();
                           throw var21;
                        }
                     }
                  }
               }

               TimeZoneProvider var23 = var0.getTimeZone();
               if (var23 != null) {
                  var6.setTimeZone(var23);
               }

               if (var5 != null) {
                  try {
                     Command var24 = var6.prepareLocal(var5);
                     var24.executeUpdate((Object)null);
                  } catch (DbException var20) {
                     if (!var3) {
                        var6.close();
                        throw var20;
                     }
                  }
               }

               var6.setAllowLiterals(false);
               var6.commit(true);
               return var6;
            }
         }

         if (System.nanoTime() - var7 > 60000000000L) {
            throw DbException.get(90020, "Waited for database closing longer than 1 minute");
         }

         try {
            Thread.sleep(1L);
         } catch (InterruptedException var19) {
            throw DbException.get(90121);
         }
      }
   }

   private static void checkClustering(ConnectionInfo var0, Database var1) {
      String var2 = var0.getProperty(12, (String)null);
      if (!"''".equals(var2)) {
         String var3 = var1.getCluster();
         if (!"''".equals(var3) && !"TRUE".equals(var2) && !Objects.equals(var2, var3)) {
            if (var3.equals("''")) {
               throw DbException.get(90093);
            } else {
               throw DbException.get(90094, var3);
            }
         }
      }
   }

   static void close(String var0) {
      if (JMX) {
         try {
            Utils.callStaticMethod("org.h2.jmx.DatabaseInfo.unregisterMBean", var0);
         } catch (Exception var4) {
            throw DbException.get(50100, var4, "JMX");
         }
      }

      synchronized(DATABASES) {
         DATABASES.remove(var0);
      }
   }

   private static void validateUserAndPassword(boolean var0) {
      int var1 = SysProperties.DELAY_WRONG_PASSWORD_MIN;
      if (var0) {
         long var16 = WRONG_PASSWORD_DELAY;
         if (var16 > (long)var1 && var16 > 0L) {
            Class var4 = Engine.class;
            synchronized(Engine.class) {
               var16 = (long)MathUtils.secureRandomInt((int)var16);

               try {
                  Thread.sleep(var16);
               } catch (InterruptedException var12) {
               }

               WRONG_PASSWORD_DELAY = (long)var1;
            }
         }

      } else {
         Class var2 = Engine.class;
         synchronized(Engine.class){}

         try {
            long var3 = WRONG_PASSWORD_DELAY;
            int var5 = SysProperties.DELAY_WRONG_PASSWORD_MAX;
            if (var5 <= 0) {
               var5 = Integer.MAX_VALUE;
            }

            WRONG_PASSWORD_DELAY += WRONG_PASSWORD_DELAY;
            if (WRONG_PASSWORD_DELAY > (long)var5 || WRONG_PASSWORD_DELAY < 0L) {
               WRONG_PASSWORD_DELAY = (long)var5;
            }

            if (var1 > 0) {
               var3 += Math.abs(MathUtils.secureRandomLong() % 100L);

               try {
                  Thread.sleep(var3);
               } catch (InterruptedException var14) {
               }
            }

            throw DbException.get(28000);
         } finally {
            ;
         }
      }
   }

   private Engine() {
   }

   static {
      WRONG_PASSWORD_DELAY = (long)SysProperties.DELAY_WRONG_PASSWORD_MIN;
      if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
         ThreadDeadlockDetector.init();
      }

   }

   private static final class DatabaseHolder {
      volatile Database database;

      DatabaseHolder() {
      }
   }
}
