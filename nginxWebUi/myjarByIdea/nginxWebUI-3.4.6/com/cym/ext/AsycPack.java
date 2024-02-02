package com.cym.ext;

import com.cym.model.Basic;
import com.cym.model.Cert;
import com.cym.model.CertCode;
import com.cym.model.Http;
import com.cym.model.Location;
import com.cym.model.Param;
import com.cym.model.Password;
import com.cym.model.Server;
import com.cym.model.Stream;
import com.cym.model.Template;
import com.cym.model.Upstream;
import com.cym.model.UpstreamServer;
import java.util.List;

public class AsycPack {
   List<Basic> basicList;
   List<Http> httpList;
   List<Server> serverList;
   List<Location> locationList;
   List<Upstream> upstreamList;
   List<UpstreamServer> upstreamServerList;
   List<Stream> streamList;
   List<Template> templateList;
   List<Param> paramList;
   List<Password> passwordList;
   List<Cert> certList;
   List<CertCode> certCodeList;
   String acmeZip;

   public String getAcmeZip() {
      return this.acmeZip;
   }

   public void setAcmeZip(String acmeZip) {
      this.acmeZip = acmeZip;
   }

   public List<Cert> getCertList() {
      return this.certList;
   }

   public void setCertList(List<Cert> certList) {
      this.certList = certList;
   }

   public List<CertCode> getCertCodeList() {
      return this.certCodeList;
   }

   public void setCertCodeList(List<CertCode> certCodeList) {
      this.certCodeList = certCodeList;
   }

   public List<Template> getTemplateList() {
      return this.templateList;
   }

   public void setTemplateList(List<Template> templateList) {
      this.templateList = templateList;
   }

   public List<Password> getPasswordList() {
      return this.passwordList;
   }

   public void setPasswordList(List<Password> passwordList) {
      this.passwordList = passwordList;
   }

   public List<Basic> getBasicList() {
      return this.basicList;
   }

   public void setBasicList(List<Basic> basicList) {
      this.basicList = basicList;
   }

   public List<Param> getParamList() {
      return this.paramList;
   }

   public void setParamList(List<Param> paramList) {
      this.paramList = paramList;
   }

   public List<Stream> getStreamList() {
      return this.streamList;
   }

   public void setStreamList(List<Stream> streamList) {
      this.streamList = streamList;
   }

   public List<Location> getLocationList() {
      return this.locationList;
   }

   public void setLocationList(List<Location> locationList) {
      this.locationList = locationList;
   }

   public List<Http> getHttpList() {
      return this.httpList;
   }

   public void setHttpList(List<Http> httpList) {
      this.httpList = httpList;
   }

   public List<Server> getServerList() {
      return this.serverList;
   }

   public void setServerList(List<Server> serverList) {
      this.serverList = serverList;
   }

   public List<Upstream> getUpstreamList() {
      return this.upstreamList;
   }

   public void setUpstreamList(List<Upstream> upstreamList) {
      this.upstreamList = upstreamList;
   }

   public List<UpstreamServer> getUpstreamServerList() {
      return this.upstreamServerList;
   }

   public void setUpstreamServerList(List<UpstreamServer> upstreamServerList) {
      this.upstreamServerList = upstreamServerList;
   }
}
