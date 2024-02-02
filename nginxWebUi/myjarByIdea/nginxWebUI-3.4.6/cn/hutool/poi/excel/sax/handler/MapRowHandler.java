package cn.hutool.poi.excel.sax.handler;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import java.util.Map;

public abstract class MapRowHandler extends AbstractRowHandler<Map<String, Object>> {
   private final int headerRowIndex;
   List<String> headerList;

   public MapRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex) {
      super(startRowIndex, endRowIndex);
      this.headerRowIndex = headerRowIndex;
      this.convertFunc = (rowList) -> {
         return IterUtil.toMap((Iterable)this.headerList, (Iterable)rowList);
      };
   }

   public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
      if (rowIndex == (long)this.headerRowIndex) {
         this.headerList = ListUtil.unmodifiable(Convert.toList(String.class, rowCells));
      } else {
         super.handle(sheetIndex, rowIndex, rowCells);
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$new$533315fa$1":
            if (lambda.getImplMethodKind() == 7 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/poi/excel/sax/handler/MapRowHandler") && lambda.getImplMethodSignature().equals("(Ljava/util/List;)Ljava/util/Map;")) {
               return (rowList) -> {
                  return IterUtil.toMap((Iterable)this.headerList, (Iterable)rowList);
               };
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
