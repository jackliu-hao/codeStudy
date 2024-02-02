package cn.hutool.poi.excel.sax.handler;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import java.util.List;

public abstract class AbstractRowHandler<T> implements RowHandler {
   protected final int startRowIndex;
   protected final int endRowIndex;
   protected Func1<List<Object>, T> convertFunc;

   public AbstractRowHandler(int startRowIndex, int endRowIndex) {
      this.startRowIndex = startRowIndex;
      this.endRowIndex = endRowIndex;
   }

   public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
      Assert.notNull(this.convertFunc);
      if (rowIndex >= (long)this.startRowIndex && rowIndex <= (long)this.endRowIndex) {
         this.handleData(sheetIndex, rowIndex, this.convertFunc.callWithRuntimeException(rowCells));
      }
   }

   public abstract void handleData(int var1, long var2, T var4);
}
