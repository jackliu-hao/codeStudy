package com.mysql.cj.protocol.a.result;

import com.mysql.cj.protocol.ColumnDefinition;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.result.Row;
import java.util.List;

public class ResultsetRowsStatic extends AbstractResultsetRows implements ResultsetRows {
   private List<Row> rows;

   public ResultsetRowsStatic(List<? extends Row> rows, ColumnDefinition columnDefinition) {
      this.currentPositionInFetchedRows = -1;
      this.rows = rows;
      this.metadata = columnDefinition;
   }

   public void addRow(Row row) {
      this.rows.add(row);
   }

   public void afterLast() {
      if (this.rows.size() > 0) {
         this.currentPositionInFetchedRows = this.rows.size();
      }

   }

   public void beforeFirst() {
      if (this.rows.size() > 0) {
         this.currentPositionInFetchedRows = -1;
      }

   }

   public void beforeLast() {
      if (this.rows.size() > 0) {
         this.currentPositionInFetchedRows = this.rows.size() - 2;
      }

   }

   public Row get(int atIndex) {
      return atIndex >= 0 && atIndex < this.rows.size() ? ((Row)this.rows.get(atIndex)).setMetadata(this.metadata) : null;
   }

   public int getPosition() {
      return this.currentPositionInFetchedRows;
   }

   public boolean hasNext() {
      boolean hasMore = this.currentPositionInFetchedRows + 1 < this.rows.size();
      return hasMore;
   }

   public boolean isAfterLast() {
      return this.currentPositionInFetchedRows >= this.rows.size() && this.rows.size() != 0;
   }

   public boolean isBeforeFirst() {
      return this.currentPositionInFetchedRows == -1 && this.rows.size() != 0;
   }

   public boolean isDynamic() {
      return false;
   }

   public boolean isEmpty() {
      return this.rows.size() == 0;
   }

   public boolean isFirst() {
      return this.currentPositionInFetchedRows == 0;
   }

   public boolean isLast() {
      if (this.rows.size() == 0) {
         return false;
      } else {
         return this.currentPositionInFetchedRows == this.rows.size() - 1;
      }
   }

   public void moveRowRelative(int rowsToMove) {
      if (this.rows.size() > 0) {
         this.currentPositionInFetchedRows += rowsToMove;
         if (this.currentPositionInFetchedRows < -1) {
            this.beforeFirst();
         } else if (this.currentPositionInFetchedRows > this.rows.size()) {
            this.afterLast();
         }
      }

   }

   public Row next() {
      ++this.currentPositionInFetchedRows;
      if (this.currentPositionInFetchedRows > this.rows.size()) {
         this.afterLast();
      } else if (this.currentPositionInFetchedRows < this.rows.size()) {
         Row row = (Row)this.rows.get(this.currentPositionInFetchedRows);
         return row.setMetadata(this.metadata);
      }

      return null;
   }

   public void remove() {
      this.rows.remove(this.getPosition());
   }

   public void setCurrentRow(int newIndex) {
      this.currentPositionInFetchedRows = newIndex;
   }

   public int size() {
      return this.rows.size();
   }

   public boolean wasEmpty() {
      return this.rows != null && this.rows.size() == 0;
   }
}
