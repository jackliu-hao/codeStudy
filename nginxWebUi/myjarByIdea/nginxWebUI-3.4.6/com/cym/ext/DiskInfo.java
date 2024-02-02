package com.cym.ext;

public class DiskInfo {
   String path;
   String useSpace;
   String totalSpace;
   String percent;

   public String getPercent() {
      return this.percent;
   }

   public void setPercent(String percent) {
      this.percent = percent;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getUseSpace() {
      return this.useSpace;
   }

   public void setUseSpace(String useSpace) {
      this.useSpace = useSpace;
   }

   public String getTotalSpace() {
      return this.totalSpace;
   }

   public void setTotalSpace(String totalSpace) {
      this.totalSpace = totalSpace;
   }
}
