package org.h2.table;

import org.h2.command.ddl.CreateSynonymData;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.util.ParserUtil;

public class TableSynonym extends SchemaObject {
   private CreateSynonymData data;
   private Table synonymFor;

   public TableSynonym(CreateSynonymData var1) {
      super(var1.schema, var1.id, var1.synonymName, 11);
      this.data = var1;
   }

   public Table getSynonymFor() {
      return this.synonymFor;
   }

   public void updateData(CreateSynonymData var1) {
      this.data = var1;
   }

   public int getType() {
      return 15;
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      return this.synonymFor.getCreateSQLForCopy(var1, var2);
   }

   public void rename(String var1) {
      throw DbException.getUnsupportedException("SYNONYM");
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.synonymFor.removeSynonym(this);
      this.database.removeMeta(var1, this.getId());
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("CREATE SYNONYM ");
      this.getSQL(var1, 0).append(" FOR ");
      ParserUtil.quoteIdentifier(var1, this.data.synonymForSchema.getName(), 0).append('.');
      ParserUtil.quoteIdentifier(var1, this.data.synonymFor, 0);
      return var1.toString();
   }

   public String getDropSQL() {
      return this.getSQL(new StringBuilder("DROP SYNONYM "), 0).toString();
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("SYNONYM");
   }

   public String getSynonymForName() {
      return this.data.synonymFor;
   }

   public Schema getSynonymForSchema() {
      return this.data.synonymForSchema;
   }

   public boolean isInvalid() {
      return this.synonymFor.isValid();
   }

   public void updateSynonymFor() {
      if (this.synonymFor != null) {
         this.synonymFor.removeSynonym(this);
      }

      this.synonymFor = this.data.synonymForSchema.getTableOrView(this.data.session, this.data.synonymFor);
      this.synonymFor.addSynonym(this);
   }
}
