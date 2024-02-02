package org.noear.solon.data.tran;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;
import javax.sql.DataSource;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tranImp.DbTran;
import org.noear.solon.data.tranImp.TranDbImp;
import org.noear.solon.data.tranImp.TranDbNewImp;
import org.noear.solon.data.tranImp.TranMandatoryImp;
import org.noear.solon.data.tranImp.TranNeverImp;
import org.noear.solon.data.tranImp.TranNotImp;
import org.noear.solon.ext.RunnableEx;

public class TranExecutorImp implements TranExecutor {
   public static final TranExecutorImp global = new TranExecutorImp();
   protected ThreadLocal<Stack<TranEntity>> local = new ThreadLocal();
   protected TranNode tranNot = new TranNotImp();
   protected TranNode tranNever = new TranNeverImp();
   protected TranNode tranMandatory = new TranMandatoryImp();

   protected TranExecutorImp() {
   }

   public boolean inTrans() {
      return TranManager.current() != null;
   }

   public boolean inTransAndReadOnly() {
      DbTran tran = TranManager.current();
      return tran != null && tran.getMeta().readOnly();
   }

   public Connection getConnection(DataSource ds) throws SQLException {
      DbTran tran = TranManager.current();
      return tran == null ? ds.getConnection() : tran.getConnection(ds);
   }

   public void execute(Tran meta, RunnableEx runnable) throws Throwable {
      if (meta == null) {
         runnable.run();
      } else {
         switch (meta.policy()) {
            case supports:
               runnable.run();
               return;
            case not_supported:
               this.tranNot.apply(runnable);
               return;
            case never:
               this.tranNever.apply(runnable);
               return;
            case mandatory:
               this.tranMandatory.apply(runnable);
               return;
            default:
               Stack<TranEntity> stack = (Stack)this.local.get();
               if (stack == null) {
                  this.forRoot(stack, meta, runnable);
               } else {
                  this.forNotRoot(stack, meta, runnable);
               }

         }
      }
   }

   protected void forRoot(Stack<TranEntity> stack, Tran meta, RunnableEx runnable) throws Throwable {
      TranNode tran = this.create(meta);
      stack = new Stack();

      try {
         this.local.set(stack);
         this.applyDo(stack, tran, meta, runnable);
      } finally {
         this.local.remove();
      }

   }

   protected void forNotRoot(Stack<TranEntity> stack, Tran meta, RunnableEx runnable) throws Throwable {
      TranNode tran;
      switch (meta.policy()) {
         case required:
            runnable.run();
            return;
         case requires_new:
            tran = this.create(meta);
            this.applyDo(stack, tran, meta, runnable);
            return;
         case nested:
            tran = this.create(meta);
            ((TranEntity)stack.peek()).tran.add(tran);
            this.applyDo(stack, tran, meta, runnable);
            return;
         default:
      }
   }

   protected void applyDo(Stack<TranEntity> stack, TranNode tran, Tran meta, RunnableEx runnable) throws Throwable {
      if (meta.policy().code <= TranPolicy.nested.code) {
         try {
            stack.push(new TranEntity(tran, meta));
            tran.apply(runnable);
         } finally {
            stack.pop();
         }
      } else {
         tran.apply(runnable);
      }

   }

   protected TranNode create(Tran meta) {
      if (meta.policy() == TranPolicy.not_supported) {
         return this.tranNot;
      } else if (meta.policy() == TranPolicy.never) {
         return this.tranNever;
      } else if (meta.policy() == TranPolicy.mandatory) {
         return this.tranMandatory;
      } else {
         return (TranNode)(meta.policy() != TranPolicy.requires_new && meta.policy() != TranPolicy.nested ? new TranDbImp(meta) : new TranDbNewImp(meta));
      }
   }
}
