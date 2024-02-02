package org.h2.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.table.TableView;

public abstract class Prepared {
   protected SessionLocal session;
   protected String sqlStatement;
   protected ArrayList<Token> sqlTokens;
   protected boolean create = true;
   protected ArrayList<Parameter> parameters;
   protected boolean prepareAlways;
   private long modificationMetaId;
   private Command command;
   private int persistedObjectId;
   private long currentRowNumber;
   private int rowScanCount;
   private List<TableView> cteCleanups;

   public Prepared(SessionLocal var1) {
      this.session = var1;
      this.modificationMetaId = var1.getDatabase().getModificationMetaId();
   }

   public abstract boolean isTransactional();

   public abstract ResultInterface queryMeta();

   public abstract int getType();

   public boolean isReadOnly() {
      return false;
   }

   public boolean needRecompile() {
      Database var1 = this.session.getDatabase();
      if (var1 == null) {
         throw DbException.get(90067, "database closed");
      } else {
         return this.prepareAlways || this.modificationMetaId < var1.getModificationMetaId() || var1.getSettings().recompileAlways;
      }
   }

   long getModificationMetaId() {
      return this.modificationMetaId;
   }

   void setModificationMetaId(long var1) {
      this.modificationMetaId = var1;
   }

   public void setParameterList(ArrayList<Parameter> var1) {
      this.parameters = var1;
   }

   public ArrayList<Parameter> getParameters() {
      return this.parameters;
   }

   protected void checkParameters() {
      if (this.persistedObjectId < 0) {
         this.persistedObjectId = ~this.persistedObjectId;
      }

      if (this.parameters != null) {
         Iterator var1 = this.parameters.iterator();

         while(var1.hasNext()) {
            Parameter var2 = (Parameter)var1.next();
            var2.checkSet();
         }
      }

   }

   public void setCommand(Command var1) {
      this.command = var1;
   }

   public boolean isQuery() {
      return false;
   }

   public void prepare() {
   }

   public long update() {
      throw DbException.get(90001);
   }

   public ResultInterface query(long var1) {
      throw DbException.get(90002);
   }

   public final void setSQL(String var1, ArrayList<Token> var2) {
      this.sqlStatement = var1;
      this.sqlTokens = var2;
   }

   public final String getSQL() {
      return this.sqlStatement;
   }

   public final ArrayList<Token> getSQLTokens() {
      return this.sqlTokens;
   }

   public int getPersistedObjectId() {
      int var1 = this.persistedObjectId;
      return var1 >= 0 ? var1 : 0;
   }

   protected int getObjectId() {
      int var1 = this.persistedObjectId;
      if (var1 == 0) {
         var1 = this.session.getDatabase().allocateObjectId();
      } else if (var1 < 0) {
         throw DbException.getInternalError("Prepared.getObjectId() was called before");
      }

      this.persistedObjectId = ~this.persistedObjectId;
      return var1;
   }

   public String getPlanSQL(int var1) {
      return null;
   }

   public void checkCanceled() {
      this.session.checkCanceled();
      Command var1 = this.command != null ? this.command : this.session.getCurrentCommand();
      if (var1 != null) {
         var1.checkCanceled();
      }

   }

   public void setPersistedObjectId(int var1) {
      this.persistedObjectId = var1;
      this.create = false;
   }

   public void setSession(SessionLocal var1) {
      this.session = var1;
   }

   void trace(long var1, long var3) {
      long var5;
      if (this.session.getTrace().isInfoEnabled() && var1 > 0L) {
         var5 = System.nanoTime() - var1;
         String var7 = Trace.formatParams(this.parameters);
         this.session.getTrace().infoSQL(this.sqlStatement, var7, var3, var5 / 1000000L);
      }

      if (this.session.getDatabase().getQueryStatistics() && var1 != 0L) {
         var5 = System.nanoTime() - var1;
         this.session.getDatabase().getQueryStatisticsData().update(this.toString(), var5, var3);
      }

   }

   public void setPrepareAlways(boolean var1) {
      this.prepareAlways = var1;
   }

   public void setCurrentRowNumber(long var1) {
      if ((++this.rowScanCount & 127) == 0) {
         this.checkCanceled();
      }

      this.currentRowNumber = var1;
      this.setProgress();
   }

   public long getCurrentRowNumber() {
      return this.currentRowNumber;
   }

   private void setProgress() {
      if ((this.currentRowNumber & 127L) == 0L) {
         this.session.getDatabase().setProgress(7, this.sqlStatement, this.currentRowNumber, 0L);
      }

   }

   public String toString() {
      return this.sqlStatement;
   }

   public static String getSimpleSQL(Expression[] var0) {
      return Expression.writeExpressions(new StringBuilder(), (Expression[])var0, 3).toString();
   }

   protected DbException setRow(DbException var1, long var2, String var4) {
      StringBuilder var5 = new StringBuilder();
      if (this.sqlStatement != null) {
         var5.append(this.sqlStatement);
      }

      var5.append(" -- ");
      if (var2 > 0L) {
         var5.append("row #").append(var2 + 1L).append(' ');
      }

      var5.append('(').append(var4).append(')');
      return var1.addSQL(var5.toString());
   }

   public boolean isCacheable() {
      return false;
   }

   public List<TableView> getCteCleanups() {
      return this.cteCleanups;
   }

   public void setCteCleanups(List<TableView> var1) {
      this.cteCleanups = var1;
   }

   public final SessionLocal getSession() {
      return this.session;
   }

   public void collectDependencies(HashSet<DbObject> var1) {
   }
}
