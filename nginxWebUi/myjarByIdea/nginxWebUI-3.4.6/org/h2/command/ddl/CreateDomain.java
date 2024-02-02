package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class CreateDomain extends SchemaOwnerCommand {
   private String typeName;
   private boolean ifNotExists;
   private TypeInfo dataType;
   private Domain parentDomain;
   private Expression defaultExpression;
   private Expression onUpdateExpression;
   private String comment;
   private ArrayList<AlterDomainAddConstraint> constraintCommands;

   public CreateDomain(SessionLocal var1, Schema var2) {
      super(var1, var2);
   }

   public void setTypeName(String var1) {
      this.typeName = var1;
   }

   public void setIfNotExists(boolean var1) {
      this.ifNotExists = var1;
   }

   public void setDataType(TypeInfo var1) {
      this.dataType = var1;
   }

   public void setParentDomain(Domain var1) {
      this.parentDomain = var1;
   }

   public void setDefaultExpression(Expression var1) {
      this.defaultExpression = var1;
   }

   public void setOnUpdateExpression(Expression var1) {
      this.onUpdateExpression = var1;
   }

   public void setComment(String var1) {
      this.comment = var1;
   }

   long update(Schema var1) {
      if (var1.findDomain(this.typeName) != null) {
         if (this.ifNotExists) {
            return 0L;
         } else {
            throw DbException.get(90119, this.typeName);
         }
      } else {
         if (this.typeName.indexOf(32) < 0) {
            DataType var2 = DataType.getTypeByName(this.typeName, this.session.getDatabase().getMode());
            if (var2 != null) {
               if (this.session.getDatabase().equalsIdentifiers(this.typeName, Value.getTypeName(var2.type))) {
                  throw DbException.get(90119, this.typeName);
               }

               Table var3 = this.session.getDatabase().getFirstUserTable();
               if (var3 != null) {
                  StringBuilder var8 = (new StringBuilder(this.typeName)).append(" (");
                  var3.getSQL(var8, 3).append(')');
                  throw DbException.get(90119, var8.toString());
               }
            }
         }

         int var6 = this.getObjectId();
         Domain var7 = new Domain(var1, var6, this.typeName);
         var7.setDataType(this.dataType != null ? this.dataType : this.parentDomain.getDataType());
         var7.setDomain(this.parentDomain);
         var7.setDefaultExpression(this.session, this.defaultExpression);
         var7.setOnUpdateExpression(this.session, this.onUpdateExpression);
         var7.setComment(this.comment);
         var1.getDatabase().addSchemaObject(this.session, var7);
         if (this.constraintCommands != null) {
            Iterator var4 = this.constraintCommands.iterator();

            while(var4.hasNext()) {
               AlterDomainAddConstraint var5 = (AlterDomainAddConstraint)var4.next();
               var5.update();
            }
         }

         return 0L;
      }
   }

   public int getType() {
      return 33;
   }

   public void addConstraintCommand(AlterDomainAddConstraint var1) {
      if (this.constraintCommands == null) {
         this.constraintCommands = Utils.newSmallArrayList();
      }

      this.constraintCommands.add(var1);
   }
}
