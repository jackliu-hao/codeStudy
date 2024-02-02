package com.mysql.cj.xdevapi;

import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.protocol.x.XMessageBuilder;
import com.mysql.cj.protocol.x.XProtocolError;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public abstract class PreparableStatement<RES_T> {
   protected int preparedStatementId = 0;
   protected PreparedState preparedState;
   protected MysqlxSession mysqlxSession;

   public PreparableStatement() {
      this.preparedState = PreparableStatement.PreparedState.UNPREPARED;
   }

   protected XMessageBuilder getMessageBuilder() {
      return (XMessageBuilder)this.mysqlxSession.getMessageBuilder();
   }

   protected void resetPrepareState() {
      if (this.preparedState != PreparableStatement.PreparedState.PREPARED && this.preparedState != PreparableStatement.PreparedState.REPREPARE) {
         if (this.preparedState == PreparableStatement.PreparedState.PREPARE) {
            this.preparedState = PreparableStatement.PreparedState.UNPREPARED;
         }
      } else {
         this.preparedState = PreparableStatement.PreparedState.DEALLOCATE;
      }

   }

   protected void setReprepareState() {
      if (this.preparedState == PreparableStatement.PreparedState.PREPARED) {
         this.preparedState = PreparableStatement.PreparedState.REPREPARE;
      }

   }

   public RES_T execute() {
      while(true) {
         switch (this.preparedState) {
            case UNSUPPORTED:
               return this.executeStatement();
            case UNPREPARED:
               RES_T result = this.executeStatement();
               this.preparedState = PreparableStatement.PreparedState.PREPARE;
               return result;
            case SUSPENDED:
               if (!this.mysqlxSession.supportsPreparedStatements()) {
                  this.preparedState = PreparableStatement.PreparedState.UNSUPPORTED;
                  break;
               } else {
                  if (this.mysqlxSession.readyForPreparingStatements()) {
                     this.preparedState = PreparableStatement.PreparedState.PREPARE;
                     break;
                  }

                  return this.executeStatement();
               }
            case PREPARE:
               this.preparedState = this.prepareStatement() ? PreparableStatement.PreparedState.PREPARED : PreparableStatement.PreparedState.SUSPENDED;
               break;
            case PREPARED:
               return this.executePreparedStatement();
            case DEALLOCATE:
               this.deallocatePrepared();
               this.preparedState = PreparableStatement.PreparedState.UNPREPARED;
               break;
            case REPREPARE:
               this.deallocatePrepared();
               this.preparedState = PreparableStatement.PreparedState.PREPARE;
         }
      }
   }

   protected abstract RES_T executeStatement();

   protected abstract XMessage getPrepareStatementXMessage();

   private boolean prepareStatement() {
      if (!this.mysqlxSession.supportsPreparedStatements()) {
         return false;
      } else {
         try {
            this.preparedStatementId = this.mysqlxSession.getNewPreparedStatementId(this);
            this.mysqlxSession.query(this.getPrepareStatementXMessage(), new UpdateResultBuilder());
            return true;
         } catch (XProtocolError var2) {
            if (this.mysqlxSession.failedPreparingStatement(this.preparedStatementId, var2)) {
               this.preparedStatementId = 0;
               return false;
            } else {
               this.preparedStatementId = 0;
               throw var2;
            }
         } catch (Throwable var3) {
            this.preparedStatementId = 0;
            throw var3;
         }
      }
   }

   protected abstract RES_T executePreparedStatement();

   protected void deallocatePrepared() {
      if (this.preparedState == PreparableStatement.PreparedState.PREPARED || this.preparedState == PreparableStatement.PreparedState.DEALLOCATE || this.preparedState == PreparableStatement.PreparedState.REPREPARE) {
         try {
            this.mysqlxSession.query(this.getMessageBuilder().buildPrepareDeallocate(this.preparedStatementId), new UpdateResultBuilder());
         } finally {
            this.mysqlxSession.freePreparedStatementId(this.preparedStatementId);
            this.preparedStatementId = 0;
         }
      }

   }

   public static class PreparableStatementFinalizer extends PhantomReference<PreparableStatement<?>> {
      int prepredStatementId;

      public PreparableStatementFinalizer(PreparableStatement<?> referent, ReferenceQueue<? super PreparableStatement<?>> q, int preparedStatementId) {
         super(referent, q);
         this.prepredStatementId = preparedStatementId;
      }

      public int getPreparedStatementId() {
         return this.prepredStatementId;
      }
   }

   protected static enum PreparedState {
      UNSUPPORTED,
      UNPREPARED,
      SUSPENDED,
      PREPARED,
      PREPARE,
      DEALLOCATE,
      REPREPARE;
   }
}
