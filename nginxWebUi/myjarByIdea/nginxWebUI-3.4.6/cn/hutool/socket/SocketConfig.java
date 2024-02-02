package cn.hutool.socket;

import cn.hutool.core.util.RuntimeUtil;
import java.io.Serializable;

public class SocketConfig implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int CPU_COUNT = RuntimeUtil.getProcessorCount();
   private int threadPoolSize;
   private long readTimeout;
   private long writeTimeout;
   private int readBufferSize;
   private int writeBufferSize;

   public SocketConfig() {
      this.threadPoolSize = CPU_COUNT;
      this.readBufferSize = 8192;
      this.writeBufferSize = 8192;
   }

   public int getThreadPoolSize() {
      return this.threadPoolSize;
   }

   public void setThreadPoolSize(int threadPoolSize) {
      this.threadPoolSize = threadPoolSize;
   }

   public long getReadTimeout() {
      return this.readTimeout;
   }

   public void setReadTimeout(long readTimeout) {
      this.readTimeout = readTimeout;
   }

   public long getWriteTimeout() {
      return this.writeTimeout;
   }

   public void setWriteTimeout(long writeTimeout) {
      this.writeTimeout = writeTimeout;
   }

   public int getReadBufferSize() {
      return this.readBufferSize;
   }

   public void setReadBufferSize(int readBufferSize) {
      this.readBufferSize = readBufferSize;
   }

   public int getWriteBufferSize() {
      return this.writeBufferSize;
   }

   public void setWriteBufferSize(int writeBufferSize) {
      this.writeBufferSize = writeBufferSize;
   }
}
