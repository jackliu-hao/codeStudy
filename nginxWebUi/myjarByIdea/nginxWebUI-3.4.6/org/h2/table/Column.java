package org.h2.table;

import java.util.Objects;
import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.command.ddl.SequenceOptions;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.util.HasSQL;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueUuid;

public final class Column implements HasSQL, Typed, ColumnTemplate {
   public static final String ROWID = "_ROWID_";
   public static final int NOT_NULLABLE = 0;
   public static final int NULLABLE = 1;
   public static final int NULLABLE_UNKNOWN = 2;
   private TypeInfo type;
   private Table table;
   private String name;
   private int columnId;
   private boolean nullable = true;
   private Expression defaultExpression;
   private Expression onUpdateExpression;
   private SequenceOptions identityOptions;
   private boolean defaultOnNull;
   private Sequence sequence;
   private boolean isGeneratedAlways;
   private GeneratedColumnResolver generatedTableFilter;
   private int selectivity;
   private String comment;
   private boolean primaryKey;
   private boolean visible = true;
   private boolean rowId;
   private Domain domain;

   public static StringBuilder writeColumns(StringBuilder var0, Column[] var1, int var2) {
      int var3 = 0;

      for(int var4 = var1.length; var3 < var4; ++var3) {
         if (var3 > 0) {
            var0.append(", ");
         }

         var1[var3].getSQL(var0, var2);
      }

      return var0;
   }

   public static StringBuilder writeColumns(StringBuilder var0, Column[] var1, String var2, String var3, int var4) {
      int var5 = 0;

      for(int var6 = var1.length; var5 < var6; ++var5) {
         if (var5 > 0) {
            var0.append(var2);
         }

         var1[var5].getSQL(var0, var4).append(var3);
      }

      return var0;
   }

   public Column(String var1, TypeInfo var2) {
      this.name = var1;
      this.type = var2;
   }

   public Column(String var1, TypeInfo var2, Table var3, int var4) {
      this.name = var1;
      this.type = var2;
      this.table = var3;
      this.columnId = var4;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Column)) {
         return false;
      } else {
         Column var2 = (Column)var1;
         if (this.table != null && var2.table != null && this.name != null && var2.name != null) {
            return this.table != var2.table ? false : this.name.equals(var2.name);
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      return this.table != null && this.name != null ? this.table.getId() ^ this.name.hashCode() : 0;
   }

   public Column getClone() {
      Column var1 = new Column(this.name, this.type);
      var1.copy(this);
      return var1;
   }

   public Value convert(CastDataProvider var1, Value var2) {
      try {
         return var2.convertTo(this.type, var1, this);
      } catch (DbException var4) {
         DbException var3 = var4;
         if (var4.getErrorCode() == 22018) {
            var3 = this.getDataConversionError(var2, var4);
         }

         throw var3;
      }
   }

   public boolean isIdentity() {
      return this.sequence != null || this.identityOptions != null;
   }

   public boolean isGenerated() {
      return this.isGeneratedAlways && this.defaultExpression != null;
   }

   public boolean isGeneratedAlways() {
      return this.isGeneratedAlways;
   }

   public void setGeneratedExpression(Expression var1) {
      this.isGeneratedAlways = true;
      this.defaultExpression = var1;
   }

   public void setTable(Table var1, int var2) {
      this.table = var1;
      this.columnId = var2;
   }

   public Table getTable() {
      return this.table;
   }

   public void setDefaultExpression(SessionLocal var1, Expression var2) {
      if (var2 != null) {
         var2 = ((Expression)var2).optimize(var1);
         if (((Expression)var2).isConstant()) {
            var2 = ValueExpression.get(((Expression)var2).getValue(var1));
         }
      }

      this.defaultExpression = (Expression)var2;
      this.isGeneratedAlways = false;
   }

   public void setOnUpdateExpression(SessionLocal var1, Expression var2) {
      if (var2 != null) {
         var2 = ((Expression)var2).optimize(var1);
         if (((Expression)var2).isConstant()) {
            var2 = ValueExpression.get(((Expression)var2).getValue(var1));
         }
      }

      this.onUpdateExpression = (Expression)var2;
   }

   public int getColumnId() {
      return this.columnId;
   }

   public String getSQL(int var1) {
      return this.rowId ? this.name : Parser.quoteIdentifier(this.name, var1);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return this.rowId ? var1.append(this.name) : ParserUtil.quoteIdentifier(var1, this.name, var2);
   }

   public StringBuilder getSQLWithTable(StringBuilder var1, int var2) {
      return this.getSQL(this.table.getSQL(var1, var2).append('.'), var2);
   }

   public String getName() {
      return this.name;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void setNullable(boolean var1) {
      this.nullable = var1;
   }

   public boolean getVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public Domain getDomain() {
      return this.domain;
   }

   public void setDomain(Domain var1) {
      this.domain = var1;
   }

   public boolean isRowId() {
      return this.rowId;
   }

   public void setRowId(boolean var1) {
      this.rowId = var1;
   }

   Value validateConvertUpdateSequence(SessionLocal var1, Value var2, Row var3) {
      label41: {
         if (var2 == null) {
            if (this.sequence != null) {
               var2 = var1.getNextValueFor(this.sequence, (Prepared)null);
               break label41;
            }

            var2 = this.getDefaultOrGenerated(var1, var3);
         }

         if (var2 == ValueNull.INSTANCE && !this.nullable) {
            throw DbException.get(23502, (String)this.name);
         }
      }

      try {
         var2 = var2.convertForAssignTo(this.type, var1, this.name);
      } catch (DbException var5) {
         DbException var4 = var5;
         if (var5.getErrorCode() == 22018) {
            var4 = this.getDataConversionError(var2, var5);
         }

         throw var4;
      }

      if (this.domain != null) {
         this.domain.checkConstraints(var1, var2);
      }

      if (this.sequence != null && var1.getMode().updateSequenceOnManualIdentityInsertion) {
         this.updateSequenceIfRequired(var1, var2.getLong());
      }

      return var2;
   }

   private Value getDefaultOrGenerated(SessionLocal var1, Row var2) {
      Expression var4 = this.getEffectiveDefaultExpression();
      Object var3;
      if (var4 == null) {
         var3 = ValueNull.INSTANCE;
      } else if (this.isGeneratedAlways) {
         synchronized(this) {
            this.generatedTableFilter.set(var2);

            try {
               var3 = var4.getValue(var1);
            } finally {
               this.generatedTableFilter.set((Row)null);
            }
         }
      } else {
         var3 = var4.getValue(var1);
      }

      return (Value)var3;
   }

   private DbException getDataConversionError(Value var1, DbException var2) {
      StringBuilder var3 = (new StringBuilder()).append(var1.getTraceSQL()).append(" (");
      if (this.table != null) {
         var3.append(this.table.getName()).append(": ");
      }

      var3.append(this.getCreateSQL()).append(')');
      return DbException.get(22018, var2, var3.toString());
   }

   private void updateSequenceIfRequired(SessionLocal var1, long var2) {
      if (this.sequence.getCycle() != Sequence.Cycle.EXHAUSTED) {
         long var4 = this.sequence.getCurrentValue();
         long var6 = this.sequence.getIncrement();
         if (var6 > 0L) {
            if (var2 < var4) {
               return;
            }
         } else if (var2 > var4) {
            return;
         }

         try {
            this.sequence.modify(var2 + var6, (Long)null, (Long)null, (Long)null, (Long)null, (Sequence.Cycle)null, (Long)null);
         } catch (DbException var9) {
            if (var9.getErrorCode() == 90009) {
               return;
            }

            throw var9;
         }

         this.sequence.flush(var1);
      }
   }

   public void initializeSequence(SessionLocal var1, Schema var2, int var3, boolean var4) {
      if (this.identityOptions == null) {
         throw DbException.getInternalError();
      } else {
         String var5;
         do {
            var5 = "SYSTEM_SEQUENCE_" + StringUtils.toUpperEnglish(ValueUuid.getNewRandom().getString().replace('-', '_'));
         } while(var2.findSequence(var5) != null);

         this.identityOptions.setDataType(this.type);
         Sequence var6 = new Sequence(var1, var2, var3, var5, this.identityOptions, true);
         var6.setTemporary(var4);
         var1.getDatabase().addSchemaObject(var1, var6);
         this.setSequence(var6, this.isGeneratedAlways);
      }
   }

   public void prepareExpressions(SessionLocal var1) {
      if (this.defaultExpression != null) {
         if (this.isGeneratedAlways) {
            this.generatedTableFilter = new GeneratedColumnResolver(this.table);
            this.defaultExpression.mapColumns(this.generatedTableFilter, 0, 0);
         }

         this.defaultExpression = this.defaultExpression.optimize(var1);
      }

      if (this.onUpdateExpression != null) {
         this.onUpdateExpression = this.onUpdateExpression.optimize(var1);
      }

      if (this.domain != null) {
         this.domain.prepareExpressions(var1);
      }

   }

   public String getCreateSQLWithoutName() {
      return this.getCreateSQL(new StringBuilder(), false);
   }

   public String getCreateSQL() {
      return this.getCreateSQL(false);
   }

   public String getCreateSQL(boolean var1) {
      StringBuilder var2 = new StringBuilder();
      if (this.name != null) {
         ParserUtil.quoteIdentifier(var2, this.name, 0).append(' ');
      }

      return this.getCreateSQL(var2, var1);
   }

   private String getCreateSQL(StringBuilder var1, boolean var2) {
      if (this.domain != null) {
         this.domain.getSQL(var1, 0);
      } else {
         this.type.getSQL(var1, 0);
      }

      if (!this.visible) {
         var1.append(" INVISIBLE ");
      }

      if (this.sequence != null) {
         var1.append(" GENERATED ").append(this.isGeneratedAlways ? "ALWAYS" : "BY DEFAULT").append(" AS IDENTITY");
         if (!var2) {
            this.sequence.getSequenceOptionsSQL(var1.append('(')).append(')');
         }
      } else if (this.defaultExpression != null) {
         if (this.isGeneratedAlways) {
            this.defaultExpression.getEnclosedSQL(var1.append(" GENERATED ALWAYS AS "), 0);
         } else {
            this.defaultExpression.getUnenclosedSQL(var1.append(" DEFAULT "), 0);
         }
      }

      if (this.onUpdateExpression != null) {
         this.onUpdateExpression.getUnenclosedSQL(var1.append(" ON UPDATE "), 0);
      }

      if (this.defaultOnNull) {
         var1.append(" DEFAULT ON NULL");
      }

      if (var2 && this.sequence != null) {
         this.sequence.getSQL(var1.append(" SEQUENCE "), 0);
      }

      if (this.selectivity != 0) {
         var1.append(" SELECTIVITY ").append(this.selectivity);
      }

      if (this.comment != null) {
         StringUtils.quoteStringSQL(var1.append(" COMMENT "), this.comment);
      }

      if (!this.nullable) {
         var1.append(" NOT NULL");
      }

      return var1.toString();
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public Expression getDefaultExpression() {
      return this.defaultExpression;
   }

   public Expression getEffectiveDefaultExpression() {
      if (this.sequence != null) {
         return null;
      } else {
         return this.defaultExpression != null ? this.defaultExpression : (this.domain != null ? this.domain.getEffectiveDefaultExpression() : null);
      }
   }

   public Expression getOnUpdateExpression() {
      return this.onUpdateExpression;
   }

   public Expression getEffectiveOnUpdateExpression() {
      if (this.sequence == null && !this.isGeneratedAlways) {
         return this.onUpdateExpression != null ? this.onUpdateExpression : (this.domain != null ? this.domain.getEffectiveOnUpdateExpression() : null);
      } else {
         return null;
      }
   }

   public boolean hasIdentityOptions() {
      return this.identityOptions != null;
   }

   public void setIdentityOptions(SequenceOptions var1, boolean var2) {
      this.identityOptions = var1;
      this.isGeneratedAlways = var2;
      this.removeNonIdentityProperties();
   }

   private void removeNonIdentityProperties() {
      this.nullable = false;
      this.onUpdateExpression = this.defaultExpression = null;
   }

   public SequenceOptions getIdentityOptions() {
      return this.identityOptions;
   }

   public void setDefaultOnNull(boolean var1) {
      this.defaultOnNull = var1;
   }

   public boolean isDefaultOnNull() {
      return this.defaultOnNull;
   }

   public void rename(String var1) {
      this.name = var1;
   }

   public void setSequence(Sequence var1, boolean var2) {
      this.sequence = var1;
      this.isGeneratedAlways = var2;
      this.identityOptions = null;
      if (var1 != null) {
         this.removeNonIdentityProperties();
         if (var1.getDatabase().getMode().identityColumnsHaveDefaultOnNull) {
            this.defaultOnNull = true;
         }
      }

   }

   public Sequence getSequence() {
      return this.sequence;
   }

   public int getSelectivity() {
      return this.selectivity == 0 ? 50 : this.selectivity;
   }

   public void setSelectivity(int var1) {
      var1 = var1 < 0 ? 0 : (var1 > 100 ? 100 : var1);
      this.selectivity = var1;
   }

   public String getDefaultSQL() {
      return this.defaultExpression == null ? null : this.defaultExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
   }

   public String getOnUpdateSQL() {
      return this.onUpdateExpression == null ? null : this.onUpdateExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
   }

   public void setComment(String var1) {
      this.comment = var1 != null && !var1.isEmpty() ? var1 : null;
   }

   public String getComment() {
      return this.comment;
   }

   public void setPrimaryKey(boolean var1) {
      this.primaryKey = var1;
   }

   boolean isEverything(ExpressionVisitor var1) {
      if (var1.getType() == 7 && this.sequence != null) {
         var1.getDependencies().add(this.sequence);
      }

      Expression var2 = this.getEffectiveDefaultExpression();
      if (var2 != null && !var2.isEverything(var1)) {
         return false;
      } else {
         var2 = this.getEffectiveOnUpdateExpression();
         return var2 == null || var2.isEverything(var1);
      }
   }

   public boolean isPrimaryKey() {
      return this.primaryKey;
   }

   public String toString() {
      return this.name;
   }

   public boolean isWideningConversion(Column var1) {
      TypeInfo var2 = var1.type;
      int var3 = this.type.getValueType();
      if (var3 != var2.getValueType()) {
         return false;
      } else {
         long var4 = this.type.getPrecision();
         long var6 = var2.getPrecision();
         if (var4 <= var6 && (var4 >= var6 || var3 != 1 && var3 != 5)) {
            if (this.type.getScale() != var2.getScale()) {
               return false;
            } else if (!Objects.equals(this.type.getExtTypeInfo(), var2.getExtTypeInfo())) {
               return false;
            } else if (this.nullable && !var1.nullable) {
               return false;
            } else if (this.primaryKey != var1.primaryKey) {
               return false;
            } else if (this.identityOptions == null && var1.identityOptions == null) {
               if (this.domain != var1.domain) {
                  return false;
               } else if (this.defaultExpression == null && var1.defaultExpression == null) {
                  if (!this.isGeneratedAlways && !var1.isGeneratedAlways) {
                     return this.onUpdateExpression == null && var1.onUpdateExpression == null;
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public void copy(Column var1) {
      this.name = var1.name;
      this.type = var1.type;
      this.domain = var1.domain;
      this.nullable = var1.nullable;
      this.defaultExpression = var1.defaultExpression;
      this.onUpdateExpression = var1.onUpdateExpression;
      this.defaultOnNull = var1.defaultOnNull;
      this.sequence = var1.sequence;
      this.comment = var1.comment;
      this.generatedTableFilter = var1.generatedTableFilter;
      this.isGeneratedAlways = var1.isGeneratedAlways;
      this.selectivity = var1.selectivity;
      this.primaryKey = var1.primaryKey;
      this.visible = var1.visible;
   }
}
