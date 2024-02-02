package cn.hutool.poi.excel.sax.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import java.lang.invoke.SerializedLambda;
import java.util.List;

public abstract class BeanRowHandler<T> extends AbstractRowHandler<T> {
   private final int headerRowIndex;
   List<String> headerList;

   public BeanRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> clazz) {
      super(startRowIndex, endRowIndex);
      Assert.isTrue(headerRowIndex <= startRowIndex, "Header row must before the start row!");
      this.headerRowIndex = headerRowIndex;
      this.convertFunc = (rowList) -> {
         return BeanUtil.toBean(IterUtil.toMap((Iterable)this.headerList, (Iterable)rowList), clazz);
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
         case "lambda$new$4d50292$1":
            if (lambda.getImplMethodKind() == 7 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/poi/excel/sax/handler/BeanRowHandler") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;Ljava/util/List;)Ljava/lang/Object;")) {
               BeanRowHandler var10000 = (BeanRowHandler)lambda.getCapturedArg(0);
               return (rowList) -> {
                  return BeanUtil.toBean(IterUtil.toMap((Iterable)this.headerList, (Iterable)rowList), clazz);
               };
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
