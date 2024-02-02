package ch.qos.logback.core.recovery;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import java.io.IOException;
import java.io.OutputStream;

public abstract class ResilientOutputStreamBase extends OutputStream {
   static final int STATUS_COUNT_LIMIT = 8;
   private int noContextWarning = 0;
   private int statusCount = 0;
   private Context context;
   private RecoveryCoordinator recoveryCoordinator;
   protected OutputStream os;
   protected boolean presumedClean = true;

   private boolean isPresumedInError() {
      return this.recoveryCoordinator != null && !this.presumedClean;
   }

   public void write(byte[] b, int off, int len) {
      if (this.isPresumedInError()) {
         if (!this.recoveryCoordinator.isTooSoon()) {
            this.attemptRecovery();
         }

      } else {
         try {
            this.os.write(b, off, len);
            this.postSuccessfulWrite();
         } catch (IOException var5) {
            this.postIOFailure(var5);
         }

      }
   }

   public void write(int b) {
      if (this.isPresumedInError()) {
         if (!this.recoveryCoordinator.isTooSoon()) {
            this.attemptRecovery();
         }

      } else {
         try {
            this.os.write(b);
            this.postSuccessfulWrite();
         } catch (IOException var3) {
            this.postIOFailure(var3);
         }

      }
   }

   public void flush() {
      if (this.os != null) {
         try {
            this.os.flush();
            this.postSuccessfulWrite();
         } catch (IOException var2) {
            this.postIOFailure(var2);
         }
      }

   }

   abstract String getDescription();

   abstract OutputStream openNewOutputStream() throws IOException;

   private void postSuccessfulWrite() {
      if (this.recoveryCoordinator != null) {
         this.recoveryCoordinator = null;
         this.statusCount = 0;
         this.addStatus(new InfoStatus("Recovered from IO failure on " + this.getDescription(), this));
      }

   }

   public void postIOFailure(IOException e) {
      this.addStatusIfCountNotOverLimit(new ErrorStatus("IO failure while writing to " + this.getDescription(), this, e));
      this.presumedClean = false;
      if (this.recoveryCoordinator == null) {
         this.recoveryCoordinator = new RecoveryCoordinator();
      }

   }

   public void close() throws IOException {
      if (this.os != null) {
         this.os.close();
      }

   }

   void attemptRecovery() {
      try {
         this.close();
      } catch (IOException var3) {
      }

      this.addStatusIfCountNotOverLimit(new InfoStatus("Attempting to recover from IO failure on " + this.getDescription(), this));

      try {
         this.os = this.openNewOutputStream();
         this.presumedClean = true;
      } catch (IOException var2) {
         this.addStatusIfCountNotOverLimit(new ErrorStatus("Failed to open " + this.getDescription(), this, var2));
      }

   }

   void addStatusIfCountNotOverLimit(Status s) {
      ++this.statusCount;
      if (this.statusCount < 8) {
         this.addStatus(s);
      }

      if (this.statusCount == 8) {
         this.addStatus(s);
         this.addStatus(new InfoStatus("Will supress future messages regarding " + this.getDescription(), this));
      }

   }

   public void addStatus(Status status) {
      if (this.context == null) {
         if (this.noContextWarning++ == 0) {
            System.out.println("LOGBACK: No context given for " + this);
         }

      } else {
         StatusManager sm = this.context.getStatusManager();
         if (sm != null) {
            sm.add(status);
         }

      }
   }

   public Context getContext() {
      return this.context;
   }

   public void setContext(Context context) {
      this.context = context;
   }
}
