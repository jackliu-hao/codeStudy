package com.cym.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cym.model.Param;
import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.bean.Update;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionOrWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class UpstreamService {
   @Inject
   SqlHelper sqlHelper;

   public Page search(Page page, String word) {
      ConditionAndWrapper conditionAndWrapper = new ConditionAndWrapper();
      if (StrUtil.isNotEmpty(word)) {
         conditionAndWrapper.and((new ConditionOrWrapper()).like("name", word));
      }

      page = this.sqlHelper.findPage(conditionAndWrapper, new Sort("seq", Sort.Direction.DESC), page, Upstream.class);
      return page;
   }

   public void deleteById(String id) {
      this.sqlHelper.deleteById(id, Upstream.class);
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"upstreamId", id), UpstreamServer.class);
   }

   public void addOver(Upstream upstream, List<UpstreamServer> upstreamServers, String upstreamParamJson) {
      if (upstream.getProxyType() == 1 || upstream.getTactics() == null) {
         upstream.setTactics("");
      }

      this.sqlHelper.insertOrUpdate(upstream);
      List<Param> paramList = new ArrayList();
      if (StrUtil.isNotEmpty(upstreamParamJson) && JSONUtil.isTypeJSON(upstreamParamJson)) {
         paramList = JSONUtil.toList(JSONUtil.parseArray(upstreamParamJson), Param.class);
      }

      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"upstreamId", upstream.getId()), Param.class);
      Collections.reverse((List)paramList);
      Iterator var5 = ((List)paramList).iterator();

      while(var5.hasNext()) {
         Param param = (Param)var5.next();
         param.setUpstreamId(upstream.getId());
         this.sqlHelper.insert(param);
      }

      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"upstreamId", upstream.getId()), UpstreamServer.class);
      if (upstreamServers != null) {
         Collections.reverse(upstreamServers);
         var5 = upstreamServers.iterator();

         while(var5.hasNext()) {
            UpstreamServer upstreamServer = (UpstreamServer)var5.next();
            upstreamServer.setUpstreamId(upstream.getId());
            this.sqlHelper.insert(upstreamServer);
         }
      }

   }

   public List<UpstreamServer> getUpstreamServers(String id) {
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"upstreamId", id), UpstreamServer.class);
   }

   public List<Upstream> getListByProxyType(Integer proxyType) {
      Sort sort = (new Sort()).add("seq", Sort.Direction.DESC);
      return this.sqlHelper.findListByQuery((new ConditionAndWrapper()).eq((String)"proxyType", proxyType), sort, Upstream.class);
   }

   public Long getCountByName(String name) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"name", name), Upstream.class);
   }

   public Long getCountByNameWithOutId(String name, String id) {
      return this.sqlHelper.findCountByQuery((new ConditionAndWrapper()).eq((String)"name", name).ne((String)"id", id), Upstream.class);
   }

   public List<UpstreamServer> getServerListByMonitor(int monitor) {
      List<String> upstreamIds = this.sqlHelper.findIdsByQuery((new ConditionAndWrapper()).eq((String)"monitor", monitor), Upstream.class);
      return this.sqlHelper.findListByQuery((ConditionWrapper)(new ConditionAndWrapper()).in((String)"upstreamId", (Collection)upstreamIds), UpstreamServer.class);
   }

   public List<UpstreamServer> getAllServer() {
      return this.sqlHelper.findAll(UpstreamServer.class);
   }

   public void resetMonitorStatus() {
      this.sqlHelper.updateMulti(new ConditionAndWrapper(), (new Update()).set("monitorStatus", -1), UpstreamServer.class);
   }

   public void setSeq(String upstreamId, Integer seqAdd) {
      Upstream upstream = (Upstream)this.sqlHelper.findById(upstreamId, Upstream.class);
      List<Upstream> upstreamList = this.sqlHelper.findAll(new Sort("seq", Sort.Direction.DESC), Upstream.class);
      if (upstreamList.size() > 0) {
         Upstream tagert = null;
         int i;
         if (seqAdd < 0) {
            for(i = 0; i < upstreamList.size(); ++i) {
               if (((Upstream)upstreamList.get(i)).getSeq() < upstream.getSeq()) {
                  tagert = (Upstream)upstreamList.get(i);
                  break;
               }
            }
         } else {
            for(i = upstreamList.size() - 1; i >= 0; --i) {
               if (((Upstream)upstreamList.get(i)).getSeq() > upstream.getSeq()) {
                  tagert = (Upstream)upstreamList.get(i);
                  break;
               }
            }
         }

         if (tagert != null) {
            Long seq = tagert.getSeq();
            tagert.setSeq(upstream.getSeq());
            upstream.setSeq(seq);
            this.sqlHelper.updateById(tagert);
            this.sqlHelper.updateById(upstream);
         }
      }

   }
}
