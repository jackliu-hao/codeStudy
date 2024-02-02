package com.mysql.cj.protocol.a.result;

import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.result.DefaultColumnDefinition;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.Row;
import java.util.HashMap;

public class NativeResultset implements Resultset {
   protected ColumnDefinition columnDefinition;
   protected ResultsetRows rowData;
   protected Resultset nextResultset = null;
   protected int resultId;
   protected long updateCount;
   protected long updateId = -1L;
   protected String serverInfo = null;
   protected Row thisRow = null;

   public NativeResultset() {
   }

   public NativeResultset(OkPacket ok) {
      this.updateCount = ok.getUpdateCount();
      this.updateId = ok.getUpdateID();
      this.serverInfo = ok.getInfo();
      this.columnDefinition = new DefaultColumnDefinition(new Field[0]);
   }

   public NativeResultset(ResultsetRows rows) {
      this.columnDefinition = rows.getMetadata();
      this.rowData = rows;
      this.updateCount = (long)this.rowData.size();
      if (this.rowData.size() > 0) {
         if (this.updateCount == 1L && this.thisRow == null) {
            this.rowData.close();
            this.updateCount = -1L;
         }
      } else {
         this.thisRow = null;
      }

   }

   public void setColumnDefinition(ColumnDefinition metadata) {
      this.columnDefinition = metadata;
   }

   public ColumnDefinition getColumnDefinition() {
      return this.columnDefinition;
   }

   public boolean hasRows() {
      return this.rowData != null;
   }

   public int getResultId() {
      return this.resultId;
   }

   public void initRowsWithMetadata() {
      if (this.rowData != null) {
         this.rowData.setMetadata(this.columnDefinition);
      }

      this.columnDefinition.setColumnToIndexCache(new HashMap());
   }

   public synchronized void setNextResultset(Resultset nextResultset) {
      this.nextResultset = nextResultset;
   }

   public synchronized Resultset getNextResultset() {
      return this.nextResultset;
   }

   public synchronized void clearNextResultset() {
      this.nextResultset = null;
   }

   public long getUpdateCount() {
      return this.updateCount;
   }

   public long getUpdateID() {
      return this.updateId;
   }

   public String getServerInfo() {
      return this.serverInfo;
   }

   public ResultsetRows getRows() {
      return this.rowData;
   }
}
