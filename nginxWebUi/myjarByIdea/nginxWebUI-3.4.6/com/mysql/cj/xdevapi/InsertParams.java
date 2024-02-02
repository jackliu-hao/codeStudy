package com.mysql.cj.xdevapi;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InsertParams {
   private List<MysqlxCrud.Column> projection;
   private List<MysqlxCrud.Insert.TypedRow> rows = new LinkedList();

   public void setProjection(String[] projection) {
      this.projection = (List)Arrays.stream(projection).map((p) -> {
         return (new ExprParser(p, true)).parseTableInsertField();
      }).collect(Collectors.toList());
   }

   public Object getProjection() {
      return this.projection;
   }

   public void addRow(List<Object> row) {
      this.rows.add(MysqlxCrud.Insert.TypedRow.newBuilder().addAllField((Iterable)row.stream().map((f) -> {
         return ExprUtil.argObjectToExpr(f, true);
      }).collect(Collectors.toList())).build());
   }

   public Object getRows() {
      return this.rows;
   }

   public void setFieldsAndValues(Map<String, Object> fieldsAndValues) {
      this.projection = new ArrayList();
      MysqlxCrud.Insert.TypedRow.Builder rowBuilder = MysqlxCrud.Insert.TypedRow.newBuilder();
      fieldsAndValues.entrySet().stream().forEach((e) -> {
         this.projection.add((new ExprParser((String)e.getKey(), true)).parseTableInsertField());
         rowBuilder.addField(ExprUtil.argObjectToExpr(e.getValue(), true));
      });
      this.rows.add(rowBuilder.build());
   }
}
