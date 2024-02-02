package cn.hutool.poi.excel.sax;

public enum CellDataType {
   BOOL("b"),
   ERROR("e"),
   FORMULA("formula"),
   INLINESTR("inlineStr"),
   SSTINDEX("s"),
   NUMBER(""),
   DATE("m/d/yy"),
   NULL("");

   private final String name;

   private CellDataType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static CellDataType of(String name) {
      if (null == name) {
         return NUMBER;
      } else if (BOOL.name.equals(name)) {
         return BOOL;
      } else if (ERROR.name.equals(name)) {
         return ERROR;
      } else if (INLINESTR.name.equals(name)) {
         return INLINESTR;
      } else if (SSTINDEX.name.equals(name)) {
         return SSTINDEX;
      } else {
         return FORMULA.name.equals(name) ? FORMULA : NULL;
      }
   }
}
