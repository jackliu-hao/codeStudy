package com.mysql.cj;

import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.OperationCancelledException;
import com.mysql.cj.protocol.a.NativeMessageBuilder;
import java.util.TimerTask;

public class CancelQueryTaskImpl extends TimerTask implements CancelQueryTask {
   Query queryToCancel;
   Throwable caughtWhileCancelling = null;
   boolean queryTimeoutKillsConnection = false;

   public CancelQueryTaskImpl(Query cancellee) {
      this.queryToCancel = cancellee;
      NativeSession session = (NativeSession)cancellee.getSession();
      this.queryTimeoutKillsConnection = (Boolean)session.getPropertySet().getBooleanProperty(PropertyKey.queryTimeoutKillsConnection).getValue();
   }

   public boolean cancel() {
      boolean res = super.cancel();
      this.queryToCancel = null;
      return res;
   }

   public void run() {
      Thread cancelThread = new Thread() {
         public void run() {
            Query localQueryToCancel = CancelQueryTaskImpl.this.queryToCancel;
            if (localQueryToCancel != null) {
               NativeSession session = (NativeSession)localQueryToCancel.getSession();
               if (session != null) {
                  try {
                     if (CancelQueryTaskImpl.this.queryTimeoutKillsConnection) {
                        localQueryToCancel.setCancelStatus(Query.CancelStatus.CANCELED_BY_TIMEOUT);
                        session.invokeCleanupListeners(new OperationCancelledException(Messages.getString("Statement.ConnectionKilledDueToTimeout")));
                     } else {
                        synchronized(localQueryToCancel.getCancelTimeoutMutex()) {
                           long origConnId = session.getThreadId();
                           HostInfo hostInfo = session.getHostInfo();
                           String database = hostInfo.getDatabase();
                           String user = hostInfo.getUser();
                           String password = hostInfo.getPassword();
                           NativeSession newSession = null;

                           try {
                              newSession = new NativeSession(hostInfo, session.getPropertySet());
                              newSession.connect(hostInfo, user, password, database, 30000, new TransactionEventHandler() {
                                 public void transactionCompleted() {
                                 }

                                 public void transactionBegun() {
                                 }
                              });
                              newSession.sendCommand((new NativeMessageBuilder(newSession.getServerSession().supportsQueryAttributes())).buildComQuery(newSession.getSharedSendPacket(), "KILL QUERY " + origConnId), false, 0);
                           } finally {
                              try {
                                 newSession.forceClose();
                              } catch (Throwable var28) {
                              }

                           }

                           localQueryToCancel.setCancelStatus(Query.CancelStatus.CANCELED_BY_TIMEOUT);
                        }
                     }
                  } catch (Throwable var31) {
                     CancelQueryTaskImpl.this.caughtWhileCancelling = var31;
                  } finally {
                     CancelQueryTaskImpl.this.setQueryToCancel((Query)null);
                  }

               }
            }
         }
      };
      cancelThread.start();
   }

   public Throwable getCaughtWhileCancelling() {
      return this.caughtWhileCancelling;
   }

   public void setCaughtWhileCancelling(Throwable caughtWhileCancelling) {
      this.caughtWhileCancelling = caughtWhileCancelling;
   }

   public Query getQueryToCancel() {
      return this.queryToCancel;
   }

   public void setQueryToCancel(Query queryToCancel) {
      this.queryToCancel = queryToCancel;
   }
}
