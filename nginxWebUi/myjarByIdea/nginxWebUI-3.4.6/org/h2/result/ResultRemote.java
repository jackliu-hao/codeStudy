package org.h2.result;

import java.io.IOException;
import java.util.ArrayList;
import org.h2.engine.SessionRemote;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class ResultRemote extends FetchedResult {
   private int fetchSize;
   private SessionRemote session;
   private Transfer transfer;
   private int id;
   private final ResultColumn[] columns;
   private long rowCount;
   private long rowOffset;
   private ArrayList<Value[]> result;
   private final Trace trace;

   public ResultRemote(SessionRemote var1, Transfer var2, int var3, int var4, int var5) throws IOException {
      this.session = var1;
      this.trace = var1.getTrace();
      this.transfer = var2;
      this.id = var3;
      this.columns = new ResultColumn[var4];
      this.rowCount = var2.readRowCount();

      for(int var6 = 0; var6 < var4; ++var6) {
         this.columns[var6] = new ResultColumn(var2);
      }

      this.rowId = -1L;
      this.fetchSize = var5;
      if (this.rowCount >= 0L) {
         var5 = (int)Math.min(this.rowCount, (long)var5);
         this.result = new ArrayList(var5);
      } else {
         this.result = new ArrayList();
      }

      synchronized(var1) {
         try {
            if (this.fetchRows(var5)) {
               this.rowCount = (long)this.result.size();
            }
         } catch (IOException var9) {
            throw DbException.convertIOException(var9, (String)null);
         }

      }
   }

   public boolean isLazy() {
      return this.rowCount < 0L;
   }

   public String getAlias(int var1) {
      return this.columns[var1].alias;
   }

   public String getSchemaName(int var1) {
      return this.columns[var1].schemaName;
   }

   public String getTableName(int var1) {
      return this.columns[var1].tableName;
   }

   public String getColumnName(int var1) {
      return this.columns[var1].columnName;
   }

   public TypeInfo getColumnType(int var1) {
      return this.columns[var1].columnType;
   }

   public boolean isIdentity(int var1) {
      return this.columns[var1].identity;
   }

   public int getNullable(int var1) {
      return this.columns[var1].nullable;
   }

   public void reset() {
      if (this.rowCount >= 0L && this.rowOffset <= 0L) {
         this.rowId = -1L;
         this.currentRow = null;
         this.nextRow = null;
         this.afterLast = false;
         if (this.session != null) {
            synchronized(this.session) {
               this.session.checkClosed();

               try {
                  this.session.traceOperation("RESULT_RESET", this.id);
                  this.transfer.writeInt(6).writeInt(this.id).flush();
               } catch (IOException var4) {
                  throw DbException.convertIOException(var4, (String)null);
               }

            }
         }
      } else {
         throw DbException.get(90128);
      }
   }

   public int getVisibleColumnCount() {
      return this.columns.length;
   }

   public long getRowCount() {
      if (this.rowCount < 0L) {
         throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
      } else {
         return this.rowCount;
      }
   }

   public boolean hasNext() {
      if (this.afterLast) {
         return false;
      } else {
         if (this.nextRow == null && (this.rowCount < 0L || this.rowId < this.rowCount - 1L)) {
            long var1 = this.rowId + 1L;
            if (this.session != null) {
               this.remapIfOld();
               if (var1 - this.rowOffset >= (long)this.result.size()) {
                  this.fetchAdditionalRows();
               }
            }

            int var3 = (int)(var1 - this.rowOffset);
            this.nextRow = var3 < this.result.size() ? (Value[])this.result.get(var3) : null;
         }

         return this.nextRow != null;
      }
   }

   private void sendClose() {
      if (this.session != null) {
         try {
            synchronized(this.session) {
               this.session.traceOperation("RESULT_CLOSE", this.id);
               this.transfer.writeInt(7).writeInt(this.id);
            }
         } catch (IOException var8) {
            this.trace.error(var8, "close");
         } finally {
            this.transfer = null;
            this.session = null;
         }

      }
   }

   public void close() {
      this.result = null;
      this.sendClose();
   }

   private void remapIfOld() {
      try {
         if (this.id <= this.session.getCurrentId() - SysProperties.SERVER_CACHED_OBJECTS / 2) {
            int var1 = this.session.getNextId();
            this.session.traceOperation("CHANGE_ID", this.id);
            this.transfer.writeInt(9).writeInt(this.id).writeInt(var1);
            this.id = var1;
         }

      } catch (IOException var2) {
         throw DbException.convertIOException(var2, (String)null);
      }
   }

   private void fetchAdditionalRows() {
      synchronized(this.session) {
         this.session.checkClosed();

         try {
            this.rowOffset += (long)this.result.size();
            this.result.clear();
            int var2 = this.fetchSize;
            if (this.rowCount >= 0L) {
               var2 = (int)Math.min((long)var2, this.rowCount - this.rowOffset);
            } else if (var2 == Integer.MAX_VALUE) {
               var2 = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
            }

            this.session.traceOperation("RESULT_FETCH_ROWS", this.id);
            this.transfer.writeInt(5).writeInt(this.id).writeInt(var2);
            this.session.done(this.transfer);
            this.fetchRows(var2);
         } catch (IOException var4) {
            throw DbException.convertIOException(var4, (String)null);
         }

      }
   }

   private boolean fetchRows(int var1) throws IOException {
      int var2 = this.columns.length;

      for(int var3 = 0; var3 < var1; ++var3) {
         switch (this.transfer.readByte()) {
            case -1:
               throw SessionRemote.readException(this.transfer);
            case 0:
               this.sendClose();
               return true;
            case 1:
               Value[] var4 = new Value[var2];

               for(int var5 = 0; var5 < var2; ++var5) {
                  var4[var5] = this.transfer.readValue(this.columns[var5].columnType);
               }

               this.result.add(var4);
               break;
            default:
               throw DbException.getInternalError();
         }
      }

      if (this.rowCount >= 0L && this.rowOffset + (long)this.result.size() >= this.rowCount) {
         this.sendClose();
      }

      return false;
   }

   public String toString() {
      return "columns: " + this.columns.length + (this.rowCount < 0L ? " lazy" : " rows: " + this.rowCount) + " pos: " + this.rowId;
   }

   public int getFetchSize() {
      return this.fetchSize;
   }

   public void setFetchSize(int var1) {
      this.fetchSize = var1;
   }

   public boolean isClosed() {
      return this.result == null;
   }
}
