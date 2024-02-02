/*    */ package com.cym.service;
/*    */ 
/*    */ import cn.hutool.core.util.NumberUtil;
/*    */ import cn.hutool.system.oshi.OshiUtil;
/*    */ import com.cym.ext.DiskInfo;
/*    */ import com.cym.ext.MonitorInfo;
/*    */ import com.sun.management.OperatingSystemMXBean;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Init;
/*    */ import org.noear.solon.aspect.annotation.Service;
/*    */ import oshi.software.os.OSFileStore;
/*    */ import oshi.util.FormatUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Service
/*    */ public class MonitorService
/*    */ {
/*    */   OperatingSystemMXBean osmxb;
/*    */   
/*    */   @Init
/*    */   private void init() {
/* 31 */     this.osmxb = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/*    */   }
/*    */ 
/*    */   
/*    */   public MonitorInfo getMonitorInfoOshi() {
/* 36 */     MonitorInfo infoBean = new MonitorInfo();
/* 37 */     infoBean.setCpuCount(Integer.valueOf(OshiUtil.getProcessor().getPhysicalProcessorCount()));
/* 38 */     infoBean.setThreadCount(Integer.valueOf(OshiUtil.getProcessor().getLogicalProcessorCount()));
/*    */     
/* 40 */     infoBean.setUsedMemory(FormatUtil.formatBytes(OshiUtil.getMemory().getTotal() - OshiUtil.getMemory().getAvailable()));
/* 41 */     infoBean.setTotalMemorySize(FormatUtil.formatBytes(OshiUtil.getMemory().getTotal()));
/*    */     
/* 43 */     infoBean.setCpuRatio(NumberUtil.decimalFormat("#.##%", this.osmxb.getSystemCpuLoad()));
/* 44 */     infoBean.setMemRatio(NumberUtil.decimalFormat("#.##%", NumberUtil.div((float)(OshiUtil.getMemory().getTotal() - OshiUtil.getMemory().getAvailable()), (float)OshiUtil.getMemory().getTotal())));
/*    */     
/* 46 */     return infoBean;
/*    */   }
/*    */   
/*    */   public List<DiskInfo> getDiskInfo() {
/* 50 */     List<DiskInfo> list = new ArrayList<>();
/* 51 */     for (OSFileStore fs : OshiUtil.getOs().getFileSystem().getFileStores()) {
/* 52 */       DiskInfo diskInfo = new DiskInfo();
/*    */       
/* 54 */       diskInfo.setPath(fs.getMount());
/* 55 */       diskInfo.setUseSpace(FormatUtil.formatBytes(fs.getTotalSpace() - fs.getUsableSpace()));
/* 56 */       diskInfo.setTotalSpace(FormatUtil.formatBytes(fs.getTotalSpace()));
/* 57 */       if (fs.getTotalSpace() != 0L) {
/* 58 */         diskInfo.setPercent(NumberUtil.decimalFormat("#.##%", NumberUtil.div((float)(fs.getTotalSpace() - fs.getUsableSpace()), (float)fs.getTotalSpace())));
/*    */       } else {
/* 60 */         diskInfo.setPercent(NumberUtil.decimalFormat("#.##%", 0L));
/*    */       } 
/*    */       
/* 63 */       list.add(diskInfo);
/*    */     } 
/* 65 */     return list;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\service\MonitorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */