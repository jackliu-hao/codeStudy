package com.cym.ext;

import java.util.List;

public class ConfExt {
   String conf;
   List<ConfFile> fileList;

   public String getConf() {
      return this.conf;
   }

   public void setConf(String conf) {
      this.conf = conf;
   }

   public List<ConfFile> getFileList() {
      return this.fileList;
   }

   public void setFileList(List<ConfFile> fileList) {
      this.fileList = fileList;
   }
}
