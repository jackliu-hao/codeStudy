/*      */ package org.apache.maven.model;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Model
/*      */   extends ModelBase
/*      */   implements Serializable, Cloneable
/*      */ {
/*      */   private String modelVersion;
/*      */   private Parent parent;
/*      */   private String groupId;
/*      */   private String artifactId;
/*      */   private String version;
/*   92 */   private String packaging = "jar";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String name;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String description;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String url;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String inceptionYear;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Organization organization;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<License> licenses;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<Developer> developers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<Contributor> contributors;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<MailingList> mailingLists;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Prerequisites prerequisites;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Scm scm;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IssueManagement issueManagement;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CiManagement ciManagement;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Build build;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private List<Profile> profiles;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  194 */   private String modelEncoding = "UTF-8";
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private File pomFile;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addContributor(Contributor contributor) {
/*  208 */     getContributors().add(contributor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addDeveloper(Developer developer) {
/*  218 */     getDevelopers().add(developer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addLicense(License license) {
/*  228 */     getLicenses().add(license);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addMailingList(MailingList mailingList) {
/*  238 */     getMailingLists().add(mailingList);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addProfile(Profile profile) {
/*  248 */     getProfiles().add(profile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Model clone() {
/*      */     try {
/*  260 */       Model copy = (Model)super.clone();
/*      */       
/*  262 */       if (this.parent != null)
/*      */       {
/*  264 */         copy.parent = this.parent.clone();
/*      */       }
/*      */       
/*  267 */       if (this.organization != null)
/*      */       {
/*  269 */         copy.organization = this.organization.clone();
/*      */       }
/*      */       
/*  272 */       if (this.licenses != null) {
/*      */         
/*  274 */         copy.licenses = new ArrayList<License>();
/*  275 */         for (License item : this.licenses)
/*      */         {
/*  277 */           copy.licenses.add(item.clone());
/*      */         }
/*      */       } 
/*      */       
/*  281 */       if (this.developers != null) {
/*      */         
/*  283 */         copy.developers = new ArrayList<Developer>();
/*  284 */         for (Developer item : this.developers)
/*      */         {
/*  286 */           copy.developers.add(item.clone());
/*      */         }
/*      */       } 
/*      */       
/*  290 */       if (this.contributors != null) {
/*      */         
/*  292 */         copy.contributors = new ArrayList<Contributor>();
/*  293 */         for (Contributor item : this.contributors)
/*      */         {
/*  295 */           copy.contributors.add(item.clone());
/*      */         }
/*      */       } 
/*      */       
/*  299 */       if (this.mailingLists != null) {
/*      */         
/*  301 */         copy.mailingLists = new ArrayList<MailingList>();
/*  302 */         for (MailingList item : this.mailingLists)
/*      */         {
/*  304 */           copy.mailingLists.add(item.clone());
/*      */         }
/*      */       } 
/*      */       
/*  308 */       if (this.prerequisites != null)
/*      */       {
/*  310 */         copy.prerequisites = this.prerequisites.clone();
/*      */       }
/*      */       
/*  313 */       if (this.scm != null)
/*      */       {
/*  315 */         copy.scm = this.scm.clone();
/*      */       }
/*      */       
/*  318 */       if (this.issueManagement != null)
/*      */       {
/*  320 */         copy.issueManagement = this.issueManagement.clone();
/*      */       }
/*      */       
/*  323 */       if (this.ciManagement != null)
/*      */       {
/*  325 */         copy.ciManagement = this.ciManagement.clone();
/*      */       }
/*      */       
/*  328 */       if (this.build != null)
/*      */       {
/*  330 */         copy.build = this.build.clone();
/*      */       }
/*      */       
/*  333 */       if (this.profiles != null) {
/*      */         
/*  335 */         copy.profiles = new ArrayList<Profile>();
/*  336 */         for (Profile item : this.profiles)
/*      */         {
/*  338 */           copy.profiles.add(item.clone());
/*      */         }
/*      */       } 
/*      */       
/*  342 */       cloneHook(copy);
/*      */       
/*  344 */       return copy;
/*      */     }
/*  346 */     catch (Exception ex) {
/*      */       
/*  348 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getArtifactId() {
/*  366 */     return this.artifactId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Build getBuild() {
/*  376 */     return this.build;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CiManagement getCiManagement() {
/*  386 */     return this.ciManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Contributor> getContributors() {
/*  396 */     if (this.contributors == null)
/*      */     {
/*  398 */       this.contributors = new ArrayList<Contributor>();
/*      */     }
/*      */     
/*  401 */     return this.contributors;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDescription() {
/*  420 */     return this.description;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Developer> getDevelopers() {
/*  430 */     if (this.developers == null)
/*      */     {
/*  432 */       this.developers = new ArrayList<Developer>();
/*      */     }
/*      */     
/*  435 */     return this.developers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getGroupId() {
/*  450 */     return this.groupId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getInceptionYear() {
/*  463 */     return this.inceptionYear;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public IssueManagement getIssueManagement() {
/*  473 */     return this.issueManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<License> getLicenses() {
/*  483 */     if (this.licenses == null)
/*      */     {
/*  485 */       this.licenses = new ArrayList<License>();
/*      */     }
/*      */     
/*  488 */     return this.licenses;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MailingList> getMailingLists() {
/*  498 */     if (this.mailingLists == null)
/*      */     {
/*  500 */       this.mailingLists = new ArrayList<MailingList>();
/*      */     }
/*      */     
/*  503 */     return this.mailingLists;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getModelEncoding() {
/*  513 */     return this.modelEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getModelVersion() {
/*  524 */     return this.modelVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  534 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Organization getOrganization() {
/*  548 */     return this.organization;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPackaging() {
/*  566 */     return this.packaging;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Parent getParent() {
/*  580 */     return this.parent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Prerequisites getPrerequisites() {
/*  591 */     return this.prerequisites;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Profile> getProfiles() {
/*  601 */     if (this.profiles == null)
/*      */     {
/*  603 */       this.profiles = new ArrayList<Profile>();
/*      */     }
/*      */     
/*  606 */     return this.profiles;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Scm getScm() {
/*  617 */     return this.scm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUrl() {
/*  627 */     return this.url;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getVersion() {
/*  638 */     return this.version;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeContributor(Contributor contributor) {
/*  648 */     getContributors().remove(contributor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeDeveloper(Developer developer) {
/*  658 */     getDevelopers().remove(developer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeLicense(License license) {
/*  668 */     getLicenses().remove(license);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeMailingList(MailingList mailingList) {
/*  678 */     getMailingLists().remove(mailingList);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeProfile(Profile profile) {
/*  688 */     getProfiles().remove(profile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setArtifactId(String artifactId) {
/*  704 */     this.artifactId = artifactId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBuild(Build build) {
/*  714 */     this.build = build;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCiManagement(CiManagement ciManagement) {
/*  724 */     this.ciManagement = ciManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setContributors(List<Contributor> contributors) {
/*  735 */     this.contributors = contributors;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDescription(String description) {
/*  754 */     this.description = description;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDevelopers(List<Developer> developers) {
/*  764 */     this.developers = developers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGroupId(String groupId) {
/*  779 */     this.groupId = groupId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setInceptionYear(String inceptionYear) {
/*  792 */     this.inceptionYear = inceptionYear;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIssueManagement(IssueManagement issueManagement) {
/*  802 */     this.issueManagement = issueManagement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLicenses(List<License> licenses) {
/*  822 */     this.licenses = licenses;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMailingLists(List<MailingList> mailingLists) {
/*  832 */     this.mailingLists = mailingLists;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModelEncoding(String modelEncoding) {
/*  842 */     this.modelEncoding = modelEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModelVersion(String modelVersion) {
/*  853 */     this.modelVersion = modelVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setName(String name) {
/*  863 */     this.name = name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOrganization(Organization organization) {
/*  877 */     this.organization = organization;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPackaging(String packaging) {
/*  895 */     this.packaging = packaging;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setParent(Parent parent) {
/*  909 */     this.parent = parent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPrerequisites(Prerequisites prerequisites) {
/*  920 */     this.prerequisites = prerequisites;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setProfiles(List<Profile> profiles) {
/*  932 */     this.profiles = profiles;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setScm(Scm scm) {
/*  943 */     this.scm = scm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setUrl(String url) {
/*  953 */     this.url = url;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setVersion(String version) {
/*  964 */     this.version = version;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void cloneHook(Model copy) {
/*  971 */     copy.pomFile = this.pomFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public File getPomFile() {
/*  987 */     return this.pomFile;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setPomFile(File pomFile) {
/*  992 */     this.pomFile = (pomFile != null) ? pomFile.getAbsoluteFile() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public File getProjectDirectory() {
/* 1003 */     return (this.pomFile != null) ? this.pomFile.getParentFile() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getId() {
/* 1011 */     StringBuilder id = new StringBuilder(64);
/*      */     
/* 1013 */     id.append((getGroupId() == null) ? "[inherited]" : getGroupId());
/* 1014 */     id.append(":");
/* 1015 */     id.append(getArtifactId());
/* 1016 */     id.append(":");
/* 1017 */     id.append(getPackaging());
/* 1018 */     id.append(":");
/* 1019 */     id.append((getVersion() == null) ? "[inherited]" : getVersion());
/*      */     
/* 1021 */     return id.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1027 */     return getId();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Model.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */