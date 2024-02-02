package com.mysql.cj.xdevapi;

import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.ResultStreamer;
import com.mysql.cj.protocol.x.StatementExecuteOk;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.result.BufferedRowList;
import com.mysql.cj.result.RowList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public abstract class AbstractDataResult<T> implements ResultStreamer, Iterator<T>, Result {
   protected int position = -1;
   protected int count = -1;
   protected RowList rows;
   protected Supplier<ProtocolEntity> completer;
   protected StatementExecuteOk ok;
   protected ProtocolEntityFactory<T, XMessage> rowToData;
   protected List<T> all;

   public AbstractDataResult(RowList rows, Supplier<ProtocolEntity> completer, ProtocolEntityFactory<T, XMessage> rowToData) {
      this.rows = rows;
      this.completer = completer;
      this.rowToData = rowToData;
   }

   public T next() {
      if (this.all != null) {
         throw new WrongArgumentException("Cannot iterate after fetchAll()");
      } else {
         com.mysql.cj.result.Row r = (com.mysql.cj.result.Row)this.rows.next();
         if (r == null) {
            throw new NoSuchElementException();
         } else {
            ++this.position;
            return this.rowToData.createFromProtocolEntity(r);
         }
      }
   }

   public List<T> fetchAll() {
      if (this.position > -1) {
         throw new WrongArgumentException("Cannot fetchAll() after starting iteration");
      } else {
         if (this.all == null) {
            this.all = new ArrayList((int)this.count());
            this.rows.forEachRemaining((r) -> {
               this.all.add(this.rowToData.createFromProtocolEntity(r));
            });
            this.all = Collections.unmodifiableList(this.all);
         }

         return this.all;
      }
   }

   public long count() {
      this.finishStreaming();
      return (long)this.count;
   }

   public boolean hasNext() {
      return this.rows.hasNext();
   }

   public StatementExecuteOk getStatementExecuteOk() {
      this.finishStreaming();
      return this.ok;
   }

   public void finishStreaming() {
      if (this.ok == null) {
         BufferedRowList remainingRows = new BufferedRowList(this.rows);
         this.count = 1 + this.position + remainingRows.size();
         this.rows = remainingRows;
         this.ok = (StatementExecuteOk)this.completer.get();
      }

   }

   public long getAffectedItemsCount() {
      return this.getStatementExecuteOk().getAffectedItemsCount();
   }

   public int getWarningsCount() {
      return this.getStatementExecuteOk().getWarningsCount();
   }

   public Iterator<Warning> getWarnings() {
      return this.getStatementExecuteOk().getWarnings();
   }
}
