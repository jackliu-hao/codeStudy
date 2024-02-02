package org.h2.command.ddl;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.TableSynonym;

public class CreateSynonym extends SchemaOwnerCommand {
   private final CreateSynonymData data = new CreateSynonymData();
   private boolean ifNotExists;
   private boolean orReplace;
   private String comment;

   public CreateSynonym(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setName(String var1) {
      this.data.synonymName = var1;
   }

   public void setSynonymFor(String var1) {
      this.data.synonymFor = var1;
   }

   public void setSynonymForSchema(Schema var1) {
      this.data.synonymForSchema = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setOrReplace(boolean var1) {
      this.orReplace = var1;
   }

   long update(Schema var1) {
      Database var2 = this.session.getDatabase();
      this.data.session = this.session;
      var2.lockMeta(this.session);
      if (var1.findTableOrView(this.session, this.data.synonymName) != null) {
         throw DbException.get(42101, (String)this.data.synonymName);
      } else if (this.data.synonymForSchema.findTableOrView(this.session, this.data.synonymFor) != null) {
         return (long)this.createTableSynonym(var2);
      } else {
         throw DbException.get(42102, (String)(this.data.synonymForSchema.getName() + "." + this.data.synonymFor));
      }
   }

   private int createTableSynonym(Database var1) {
      TableSynonym var2 = this.getSchema().getSynonym(this.data.synonymName);
      if (var2 != null && !this.orReplace) {
         if (this.ifNotExists) {
            return 0;
         } else {
            throw DbException.get(42101, (String)this.data.synonymName);
         }
      } else {
         TableSynonym var3;
         if (var2 != null) {
            var3 = var2;
            this.data.schema = var2.getSchema();
            var2.updateData(this.data);
            var2.setComment(this.comment);
            var2.setModified();
            var1.updateMeta(this.session, var2);
         } else {
            this.data.id = this.getObjectId();
            var3 = this.getSchema().createSynonym(this.data);
            var3.setComment(this.comment);
            var1.addSchemaObject(this.session, var3);
         }

         var3.updateSynonymFor();
         return 0;
      }
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   public int getType() {
      return 88;
   }
}
