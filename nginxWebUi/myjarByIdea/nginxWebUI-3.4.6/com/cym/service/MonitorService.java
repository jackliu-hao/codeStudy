package com.cym.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.cym.ext.DiskInfo;
import com.cym.ext.MonitorInfo;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Init;
import org.noear.solon.aspect.annotation.Service;
import oshi.software.os.OSFileStore;
import oshi.util.FormatUtil;

@Service
public class MonitorService {
   OperatingSystemMXBean osmxb;

   @Init
   private void init() {
      this.osmxb = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
   }

   public MonitorInfo getMonitorInfoOshi() {
      MonitorInfo infoBean = new MonitorInfo();
      infoBean.setCpuCount(OshiUtil.getProcessor().getPhysicalProcessorCount());
      infoBean.setThreadCount(OshiUtil.getProcessor().getLogicalProcessorCount());
      infoBean.setUsedMemory(FormatUtil.formatBytes(OshiUtil.getMemory().getTotal() - OshiUtil.getMemory().getAvailable()));
      infoBean.setTotalMemorySize(FormatUtil.formatBytes(OshiUtil.getMemory().getTotal()));
      infoBean.setCpuRatio(NumberUtil.decimalFormat("#.##%", this.osmxb.getSystemCpuLoad()));
      infoBean.setMemRatio(NumberUtil.decimalFormat("#.##%", NumberUtil.div((float)(OshiUtil.getMemory().getTotal() - OshiUtil.getMemory().getAvailable()), (float)OshiUtil.getMemory().getTotal())));
      return infoBean;
   }

   public List<DiskInfo> getDiskInfo() {
      List<DiskInfo> list = new ArrayList();

      DiskInfo diskInfo;
      for(Iterator var2 = OshiUtil.getOs().getFileSystem().getFileStores().iterator(); var2.hasNext(); list.add(diskInfo)) {
         OSFileStore fs = (OSFileStore)var2.next();
         diskInfo = new DiskInfo();
         diskInfo.setPath(fs.getMount());
         diskInfo.setUseSpace(FormatUtil.formatBytes(fs.getTotalSpace() - fs.getUsableSpace()));
         diskInfo.setTotalSpace(FormatUtil.formatBytes(fs.getTotalSpace()));
         if (fs.getTotalSpace() != 0L) {
            diskInfo.setPercent(NumberUtil.decimalFormat("#.##%", NumberUtil.div((float)(fs.getTotalSpace() - fs.getUsableSpace()), (float)fs.getTotalSpace())));
         } else {
            diskInfo.setPercent(NumberUtil.decimalFormat("#.##%", 0L));
         }
      }

      return list;
   }
}
