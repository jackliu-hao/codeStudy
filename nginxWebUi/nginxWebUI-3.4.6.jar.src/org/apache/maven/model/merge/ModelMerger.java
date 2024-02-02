/*      */ package org.apache.maven.model.merge;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import org.apache.maven.model.Activation;
/*      */ import org.apache.maven.model.Build;
/*      */ import org.apache.maven.model.BuildBase;
/*      */ import org.apache.maven.model.CiManagement;
/*      */ import org.apache.maven.model.ConfigurationContainer;
/*      */ import org.apache.maven.model.Contributor;
/*      */ import org.apache.maven.model.Dependency;
/*      */ import org.apache.maven.model.DependencyManagement;
/*      */ import org.apache.maven.model.DeploymentRepository;
/*      */ import org.apache.maven.model.Developer;
/*      */ import org.apache.maven.model.DistributionManagement;
/*      */ import org.apache.maven.model.Exclusion;
/*      */ import org.apache.maven.model.Extension;
/*      */ import org.apache.maven.model.FileSet;
/*      */ import org.apache.maven.model.InputLocation;
/*      */ import org.apache.maven.model.IssueManagement;
/*      */ import org.apache.maven.model.License;
/*      */ import org.apache.maven.model.MailingList;
/*      */ import org.apache.maven.model.Model;
/*      */ import org.apache.maven.model.ModelBase;
/*      */ import org.apache.maven.model.Notifier;
/*      */ import org.apache.maven.model.Organization;
/*      */ import org.apache.maven.model.Parent;
/*      */ import org.apache.maven.model.PatternSet;
/*      */ import org.apache.maven.model.Plugin;
/*      */ import org.apache.maven.model.PluginConfiguration;
/*      */ import org.apache.maven.model.PluginContainer;
/*      */ import org.apache.maven.model.PluginExecution;
/*      */ import org.apache.maven.model.PluginManagement;
/*      */ import org.apache.maven.model.Prerequisites;
/*      */ import org.apache.maven.model.Profile;
/*      */ import org.apache.maven.model.Relocation;
/*      */ import org.apache.maven.model.ReportPlugin;
/*      */ import org.apache.maven.model.ReportSet;
/*      */ import org.apache.maven.model.Reporting;
/*      */ import org.apache.maven.model.Repository;
/*      */ import org.apache.maven.model.RepositoryBase;
/*      */ import org.apache.maven.model.RepositoryPolicy;
/*      */ import org.apache.maven.model.Resource;
/*      */ import org.apache.maven.model.Scm;
/*      */ import org.apache.maven.model.Site;
/*      */ import org.codehaus.plexus.util.xml.Xpp3Dom;
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
/*      */ public class ModelMerger
/*      */ {
/*      */   public void merge(Model target, Model source, boolean sourceDominant, Map<?, ?> hints) {
/*   95 */     if (target == null)
/*      */     {
/*   97 */       throw new IllegalArgumentException("target missing");
/*      */     }
/*      */     
/*  100 */     if (source == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  105 */     Map<Object, Object> context = new HashMap<Object, Object>();
/*  106 */     if (hints != null)
/*      */     {
/*  108 */       context.putAll(hints);
/*      */     }
/*      */     
/*  111 */     mergeModel(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeModel(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  116 */     mergeModelBase((ModelBase)target, (ModelBase)source, sourceDominant, context);
/*      */     
/*  118 */     mergeModel_ModelVersion(target, source, sourceDominant, context);
/*  119 */     mergeModel_Parent(target, source, sourceDominant, context);
/*  120 */     mergeModel_GroupId(target, source, sourceDominant, context);
/*  121 */     mergeModel_ArtifactId(target, source, sourceDominant, context);
/*  122 */     mergeModel_Version(target, source, sourceDominant, context);
/*  123 */     mergeModel_Packaging(target, source, sourceDominant, context);
/*  124 */     mergeModel_Name(target, source, sourceDominant, context);
/*  125 */     mergeModel_Description(target, source, sourceDominant, context);
/*  126 */     mergeModel_Url(target, source, sourceDominant, context);
/*  127 */     mergeModel_InceptionYear(target, source, sourceDominant, context);
/*  128 */     mergeModel_Organization(target, source, sourceDominant, context);
/*  129 */     mergeModel_Licenses(target, source, sourceDominant, context);
/*  130 */     mergeModel_MailingLists(target, source, sourceDominant, context);
/*  131 */     mergeModel_Developers(target, source, sourceDominant, context);
/*  132 */     mergeModel_Contributors(target, source, sourceDominant, context);
/*  133 */     mergeModel_IssueManagement(target, source, sourceDominant, context);
/*  134 */     mergeModel_Scm(target, source, sourceDominant, context);
/*  135 */     mergeModel_CiManagement(target, source, sourceDominant, context);
/*  136 */     mergeModel_Prerequisites(target, source, sourceDominant, context);
/*  137 */     mergeModel_Build(target, source, sourceDominant, context);
/*  138 */     mergeModel_Profiles(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_ModelVersion(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  144 */     String src = source.getModelVersion();
/*  145 */     if (src != null)
/*      */     {
/*  147 */       if (sourceDominant || target.getModelVersion() == null) {
/*      */         
/*  149 */         target.setModelVersion(src);
/*  150 */         target.setLocation("modelVersion", source.getLocation("modelVersion"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeModel_Parent(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  157 */     Parent src = source.getParent();
/*  158 */     if (source.getParent() != null) {
/*      */       
/*  160 */       Parent tgt = target.getParent();
/*  161 */       if (tgt == null) {
/*      */         
/*  163 */         tgt = new Parent();
/*  164 */         target.setParent(tgt);
/*      */       } 
/*  166 */       mergeParent(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_GroupId(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  173 */     String src = source.getGroupId();
/*  174 */     if (src != null)
/*      */     {
/*  176 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/*  178 */         target.setGroupId(src);
/*  179 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_ArtifactId(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  187 */     String src = source.getArtifactId();
/*  188 */     if (src != null)
/*      */     {
/*  190 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/*  192 */         target.setArtifactId(src);
/*  193 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Version(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  201 */     String src = source.getVersion();
/*  202 */     if (src != null)
/*      */     {
/*  204 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/*  206 */         target.setVersion(src);
/*  207 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Packaging(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  215 */     String src = source.getPackaging();
/*  216 */     if (src != null)
/*      */     {
/*  218 */       if (sourceDominant || target.getPackaging() == null) {
/*      */         
/*  220 */         target.setPackaging(src);
/*  221 */         target.setLocation("packaging", source.getLocation("packaging"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeModel_Name(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  228 */     String src = source.getName();
/*  229 */     if (src != null)
/*      */     {
/*  231 */       if (sourceDominant || target.getName() == null) {
/*      */         
/*  233 */         target.setName(src);
/*  234 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Description(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  242 */     String src = source.getDescription();
/*  243 */     if (src != null)
/*      */     {
/*  245 */       if (sourceDominant || target.getDescription() == null) {
/*      */         
/*  247 */         target.setDescription(src);
/*  248 */         target.setLocation("description", source.getLocation("description"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeModel_Url(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  255 */     String src = source.getUrl();
/*  256 */     if (src != null)
/*      */     {
/*  258 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/*  260 */         target.setUrl(src);
/*  261 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_InceptionYear(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  269 */     String src = source.getInceptionYear();
/*  270 */     if (src != null)
/*      */     {
/*  272 */       if (sourceDominant || target.getInceptionYear() == null) {
/*      */         
/*  274 */         target.setInceptionYear(src);
/*  275 */         target.setLocation("inceptionYear", source.getLocation("inceptionYear"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Organization(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  283 */     Organization src = source.getOrganization();
/*  284 */     if (source.getOrganization() != null) {
/*      */       
/*  286 */       Organization tgt = target.getOrganization();
/*  287 */       if (tgt == null) {
/*      */         
/*  289 */         tgt = new Organization();
/*  290 */         target.setOrganization(tgt);
/*      */       } 
/*  292 */       mergeOrganization(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Licenses(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  299 */     List<License> src = source.getLicenses();
/*  300 */     if (!src.isEmpty()) {
/*      */       
/*  302 */       List<License> tgt = target.getLicenses();
/*  303 */       Map<Object, License> merged = new LinkedHashMap<Object, License>((src.size() + tgt.size()) * 2);
/*      */       
/*  305 */       for (License element : tgt) {
/*      */         
/*  307 */         Object key = getLicenseKey(element);
/*  308 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  311 */       for (License element : src) {
/*      */         
/*  313 */         Object key = getLicenseKey(element);
/*  314 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  316 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  320 */       target.setLicenses(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_MailingLists(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  327 */     List<MailingList> src = source.getMailingLists();
/*  328 */     if (!src.isEmpty()) {
/*      */       
/*  330 */       List<MailingList> tgt = target.getMailingLists();
/*  331 */       Map<Object, MailingList> merged = new LinkedHashMap<Object, MailingList>((src.size() + tgt.size()) * 2);
/*      */       
/*  333 */       for (MailingList element : tgt) {
/*      */         
/*  335 */         Object key = getMailingListKey(element);
/*  336 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  339 */       for (MailingList element : src) {
/*      */         
/*  341 */         Object key = getMailingListKey(element);
/*  342 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  344 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  348 */       target.setMailingLists(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Developers(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  355 */     List<Developer> src = source.getDevelopers();
/*  356 */     if (!src.isEmpty()) {
/*      */       
/*  358 */       List<Developer> tgt = target.getDevelopers();
/*  359 */       Map<Object, Developer> merged = new LinkedHashMap<Object, Developer>((src.size() + tgt.size()) * 2);
/*      */       
/*  361 */       for (Developer element : tgt) {
/*      */         
/*  363 */         Object key = getDeveloperKey(element);
/*  364 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  367 */       for (Developer element : src) {
/*      */         
/*  369 */         Object key = getDeveloperKey(element);
/*  370 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  372 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  376 */       target.setDevelopers(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Contributors(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  383 */     List<Contributor> src = source.getContributors();
/*  384 */     if (!src.isEmpty()) {
/*      */       
/*  386 */       List<Contributor> tgt = target.getContributors();
/*  387 */       Map<Object, Contributor> merged = new LinkedHashMap<Object, Contributor>((src.size() + tgt.size()) * 2);
/*      */       
/*  389 */       for (Contributor element : tgt) {
/*      */         
/*  391 */         Object key = getContributorKey(element);
/*  392 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  395 */       for (Contributor element : src) {
/*      */         
/*  397 */         Object key = getContributorKey(element);
/*  398 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  400 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  404 */       target.setContributors(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_IssueManagement(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  411 */     IssueManagement src = source.getIssueManagement();
/*  412 */     if (source.getIssueManagement() != null) {
/*      */       
/*  414 */       IssueManagement tgt = target.getIssueManagement();
/*  415 */       if (tgt == null) {
/*      */         
/*  417 */         tgt = new IssueManagement();
/*  418 */         target.setIssueManagement(tgt);
/*      */       } 
/*  420 */       mergeIssueManagement(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeModel_Scm(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  426 */     Scm src = source.getScm();
/*  427 */     if (source.getScm() != null) {
/*      */       
/*  429 */       Scm tgt = target.getScm();
/*  430 */       if (tgt == null) {
/*      */         
/*  432 */         tgt = new Scm();
/*  433 */         target.setScm(tgt);
/*      */       } 
/*  435 */       mergeScm(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_CiManagement(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  442 */     CiManagement src = source.getCiManagement();
/*  443 */     if (source.getCiManagement() != null) {
/*      */       
/*  445 */       CiManagement tgt = target.getCiManagement();
/*  446 */       if (tgt == null) {
/*      */         
/*  448 */         tgt = new CiManagement();
/*  449 */         target.setCiManagement(tgt);
/*      */       } 
/*  451 */       mergeCiManagement(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Prerequisites(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  458 */     Prerequisites src = source.getPrerequisites();
/*  459 */     if (source.getPrerequisites() != null) {
/*      */       
/*  461 */       Prerequisites tgt = target.getPrerequisites();
/*  462 */       if (tgt == null) {
/*      */         
/*  464 */         tgt = new Prerequisites();
/*  465 */         target.setPrerequisites(tgt);
/*      */       } 
/*  467 */       mergePrerequisites(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeModel_Build(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  473 */     Build src = source.getBuild();
/*  474 */     if (source.getBuild() != null) {
/*      */       
/*  476 */       Build tgt = target.getBuild();
/*  477 */       if (tgt == null) {
/*      */         
/*  479 */         tgt = new Build();
/*  480 */         target.setBuild(tgt);
/*      */       } 
/*  482 */       mergeBuild(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModel_Profiles(Model target, Model source, boolean sourceDominant, Map<Object, Object> context) {
/*  489 */     List<Profile> src = source.getProfiles();
/*  490 */     if (!src.isEmpty()) {
/*      */       
/*  492 */       List<Profile> tgt = target.getProfiles();
/*  493 */       Map<Object, Profile> merged = new LinkedHashMap<Object, Profile>((src.size() + tgt.size()) * 2);
/*      */       
/*  495 */       for (Profile element : tgt) {
/*      */         
/*  497 */         Object key = getProfileKey(element);
/*  498 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  501 */       for (Profile element : src) {
/*      */         
/*  503 */         Object key = getProfileKey(element);
/*  504 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  506 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  510 */       target.setProfiles(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  517 */     mergeModelBase_DistributionManagement(target, source, sourceDominant, context);
/*  518 */     mergeModelBase_Modules(target, source, sourceDominant, context);
/*  519 */     mergeModelBase_Repositories(target, source, sourceDominant, context);
/*  520 */     mergeModelBase_PluginRepositories(target, source, sourceDominant, context);
/*  521 */     mergeModelBase_Dependencies(target, source, sourceDominant, context);
/*  522 */     mergeModelBase_Reporting(target, source, sourceDominant, context);
/*  523 */     mergeModelBase_DependencyManagement(target, source, sourceDominant, context);
/*  524 */     mergeModelBase_Properties(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_Modules(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  530 */     List<String> src = source.getModules();
/*  531 */     if (!src.isEmpty()) {
/*      */       
/*  533 */       List<String> tgt = target.getModules();
/*  534 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/*  535 */       merged.addAll(tgt);
/*  536 */       merged.addAll(src);
/*  537 */       target.setModules(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_Dependencies(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  544 */     List<Dependency> src = source.getDependencies();
/*  545 */     if (!src.isEmpty()) {
/*      */       
/*  547 */       List<Dependency> tgt = target.getDependencies();
/*  548 */       Map<Object, Dependency> merged = new LinkedHashMap<Object, Dependency>((src.size() + tgt.size()) * 2);
/*      */       
/*  550 */       for (Dependency element : tgt) {
/*      */         
/*  552 */         Object key = getDependencyKey(element);
/*  553 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  556 */       for (Dependency element : src) {
/*      */         
/*  558 */         Object key = getDependencyKey(element);
/*  559 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  561 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  565 */       target.setDependencies(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_Repositories(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  572 */     List<Repository> src = source.getRepositories();
/*  573 */     if (!src.isEmpty()) {
/*      */       
/*  575 */       List<Repository> tgt = target.getRepositories();
/*  576 */       Map<Object, Repository> merged = new LinkedHashMap<Object, Repository>((src.size() + tgt.size()) * 2);
/*      */       
/*  578 */       for (Repository element : tgt) {
/*      */         
/*  580 */         Object key = getRepositoryKey(element);
/*  581 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  584 */       for (Repository element : src) {
/*      */         
/*  586 */         Object key = getRepositoryKey(element);
/*  587 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  589 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  593 */       target.setRepositories(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_PluginRepositories(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  600 */     List<Repository> src = source.getPluginRepositories();
/*  601 */     if (!src.isEmpty()) {
/*      */       
/*  603 */       List<Repository> tgt = target.getPluginRepositories();
/*  604 */       Map<Object, Repository> merged = new LinkedHashMap<Object, Repository>((src.size() + tgt.size()) * 2);
/*      */       
/*  606 */       for (Repository element : tgt) {
/*      */         
/*  608 */         Object key = getRepositoryKey(element);
/*  609 */         merged.put(key, element);
/*      */       } 
/*      */       
/*  612 */       for (Repository element : src) {
/*      */         
/*  614 */         Object key = getRepositoryKey(element);
/*  615 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/*  617 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/*  621 */       target.setPluginRepositories(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_DistributionManagement(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  628 */     DistributionManagement src = source.getDistributionManagement();
/*  629 */     if (source.getDistributionManagement() != null) {
/*      */       
/*  631 */       DistributionManagement tgt = target.getDistributionManagement();
/*  632 */       if (tgt == null) {
/*      */         
/*  634 */         tgt = new DistributionManagement();
/*  635 */         target.setDistributionManagement(tgt);
/*      */       } 
/*  637 */       mergeDistributionManagement(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_Reporting(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  644 */     Reporting src = source.getReporting();
/*  645 */     if (source.getReporting() != null) {
/*      */       
/*  647 */       Reporting tgt = target.getReporting();
/*  648 */       if (tgt == null) {
/*      */         
/*  650 */         tgt = new Reporting();
/*  651 */         target.setReporting(tgt);
/*      */       } 
/*  653 */       mergeReporting(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_DependencyManagement(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  660 */     DependencyManagement src = source.getDependencyManagement();
/*  661 */     if (source.getDependencyManagement() != null) {
/*      */       
/*  663 */       DependencyManagement tgt = target.getDependencyManagement();
/*  664 */       if (tgt == null) {
/*      */         
/*  666 */         tgt = new DependencyManagement();
/*  667 */         target.setDependencyManagement(tgt);
/*      */       } 
/*  669 */       mergeDependencyManagement(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeModelBase_Properties(ModelBase target, ModelBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  676 */     Properties merged = new Properties();
/*  677 */     if (sourceDominant) {
/*      */       
/*  679 */       merged.putAll(target.getProperties());
/*  680 */       merged.putAll(source.getProperties());
/*      */     }
/*      */     else {
/*      */       
/*  684 */       merged.putAll(source.getProperties());
/*  685 */       merged.putAll(target.getProperties());
/*      */     } 
/*  687 */     target.setProperties(merged);
/*  688 */     target.setLocation("properties", InputLocation.merge(target.getLocation("properties"), source.getLocation("properties"), sourceDominant));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDistributionManagement(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
/*  695 */     mergeDistributionManagement_Repository(target, source, sourceDominant, context);
/*  696 */     mergeDistributionManagement_SnapshotRepository(target, source, sourceDominant, context);
/*  697 */     mergeDistributionManagement_Site(target, source, sourceDominant, context);
/*  698 */     mergeDistributionManagement_Status(target, source, sourceDominant, context);
/*  699 */     mergeDistributionManagement_DownloadUrl(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDistributionManagement_Repository(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
/*  706 */     DeploymentRepository src = source.getRepository();
/*  707 */     if (src != null) {
/*      */       
/*  709 */       DeploymentRepository tgt = target.getRepository();
/*  710 */       if (tgt == null) {
/*      */         
/*  712 */         tgt = new DeploymentRepository();
/*  713 */         target.setRepository(tgt);
/*      */       } 
/*  715 */       mergeDeploymentRepository(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDistributionManagement_SnapshotRepository(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
/*  724 */     DeploymentRepository src = source.getSnapshotRepository();
/*  725 */     if (src != null) {
/*      */       
/*  727 */       DeploymentRepository tgt = target.getSnapshotRepository();
/*  728 */       if (tgt == null) {
/*      */         
/*  730 */         tgt = new DeploymentRepository();
/*  731 */         target.setSnapshotRepository(tgt);
/*      */       } 
/*  733 */       mergeDeploymentRepository(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDistributionManagement_Site(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
/*  740 */     Site src = source.getSite();
/*  741 */     if (src != null) {
/*      */       
/*  743 */       Site tgt = target.getSite();
/*  744 */       if (tgt == null) {
/*      */         
/*  746 */         tgt = new Site();
/*  747 */         target.setSite(tgt);
/*      */       } 
/*  749 */       mergeSite(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDistributionManagement_Status(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
/*  756 */     String src = source.getStatus();
/*  757 */     if (src != null)
/*      */     {
/*  759 */       if (sourceDominant || target.getStatus() == null) {
/*      */         
/*  761 */         target.setStatus(src);
/*  762 */         target.setLocation("status", source.getLocation("status"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDistributionManagement_DownloadUrl(DistributionManagement target, DistributionManagement source, boolean sourceDominant, Map<Object, Object> context) {
/*  771 */     String src = source.getDownloadUrl();
/*  772 */     if (src != null)
/*      */     {
/*  774 */       if (sourceDominant || target.getDownloadUrl() == null) {
/*      */         
/*  776 */         target.setDownloadUrl(src);
/*  777 */         target.setLocation("downloadUrl", source.getLocation("downloadUrl"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRelocation(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
/*  785 */     mergeRelocation_GroupId(target, source, sourceDominant, context);
/*  786 */     mergeRelocation_ArtifactId(target, source, sourceDominant, context);
/*  787 */     mergeRelocation_Version(target, source, sourceDominant, context);
/*  788 */     mergeRelocation_Message(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRelocation_GroupId(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
/*  794 */     String src = source.getGroupId();
/*  795 */     if (src != null)
/*      */     {
/*  797 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/*  799 */         target.setGroupId(src);
/*  800 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRelocation_ArtifactId(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
/*  808 */     String src = source.getArtifactId();
/*  809 */     if (src != null)
/*      */     {
/*  811 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/*  813 */         target.setArtifactId(src);
/*  814 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRelocation_Version(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
/*  822 */     String src = source.getVersion();
/*  823 */     if (src != null)
/*      */     {
/*  825 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/*  827 */         target.setVersion(src);
/*  828 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRelocation_Message(Relocation target, Relocation source, boolean sourceDominant, Map<Object, Object> context) {
/*  836 */     String src = source.getMessage();
/*  837 */     if (src != null)
/*      */     {
/*  839 */       if (sourceDominant || target.getMessage() == null) {
/*      */         
/*  841 */         target.setMessage(src);
/*  842 */         target.setLocation("message", source.getLocation("message"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDeploymentRepository(DeploymentRepository target, DeploymentRepository source, boolean sourceDominant, Map<Object, Object> context) {
/*  850 */     mergeRepository((Repository)target, (Repository)source, sourceDominant, context);
/*  851 */     mergeDeploymentRepository_UniqueVersion(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDeploymentRepository_UniqueVersion(DeploymentRepository target, DeploymentRepository source, boolean sourceDominant, Map<Object, Object> context) {
/*  857 */     if (sourceDominant) {
/*      */       
/*  859 */       target.setUniqueVersion(source.isUniqueVersion());
/*  860 */       target.setLocation("uniqueVersion", source.getLocation("uniqueVersion"));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeSite(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
/*  866 */     mergeSite_Id(target, source, sourceDominant, context);
/*  867 */     mergeSite_Name(target, source, sourceDominant, context);
/*  868 */     mergeSite_Url(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeSite_Id(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
/*  873 */     String src = source.getId();
/*  874 */     if (src != null)
/*      */     {
/*  876 */       if (sourceDominant || target.getId() == null) {
/*      */         
/*  878 */         target.setId(src);
/*  879 */         target.setLocation("id", source.getLocation("id"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeSite_Name(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
/*  886 */     String src = source.getName();
/*  887 */     if (src != null)
/*      */     {
/*  889 */       if (sourceDominant || target.getName() == null) {
/*      */         
/*  891 */         target.setName(src);
/*  892 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeSite_Url(Site target, Site source, boolean sourceDominant, Map<Object, Object> context) {
/*  899 */     String src = source.getUrl();
/*  900 */     if (src != null)
/*      */     {
/*  902 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/*  904 */         target.setUrl(src);
/*  905 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepository(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
/*  913 */     mergeRepositoryBase((RepositoryBase)target, (RepositoryBase)source, sourceDominant, context);
/*  914 */     mergeRepository_Releases(target, source, sourceDominant, context);
/*  915 */     mergeRepository_Snapshots(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepository_Releases(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
/*  921 */     RepositoryPolicy src = source.getReleases();
/*  922 */     if (src != null) {
/*      */       
/*  924 */       RepositoryPolicy tgt = target.getReleases();
/*  925 */       if (tgt == null) {
/*      */         
/*  927 */         tgt = new RepositoryPolicy();
/*  928 */         target.setReleases(tgt);
/*      */       } 
/*  930 */       mergeRepositoryPolicy(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepository_Snapshots(Repository target, Repository source, boolean sourceDominant, Map<Object, Object> context) {
/*  937 */     RepositoryPolicy src = source.getSnapshots();
/*  938 */     if (src != null) {
/*      */       
/*  940 */       RepositoryPolicy tgt = target.getSnapshots();
/*  941 */       if (tgt == null) {
/*      */         
/*  943 */         tgt = new RepositoryPolicy();
/*  944 */         target.setSnapshots(tgt);
/*      */       } 
/*  946 */       mergeRepositoryPolicy(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryBase(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  953 */     mergeRepositoryBase_Id(target, source, sourceDominant, context);
/*  954 */     mergeRepositoryBase_Name(target, source, sourceDominant, context);
/*  955 */     mergeRepositoryBase_Url(target, source, sourceDominant, context);
/*  956 */     mergeRepositoryBase_Layout(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryBase_Id(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  962 */     String src = source.getId();
/*  963 */     if (src != null)
/*      */     {
/*  965 */       if (sourceDominant || target.getId() == null) {
/*      */         
/*  967 */         target.setId(src);
/*  968 */         target.setLocation("id", source.getLocation("id"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryBase_Url(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  976 */     String src = source.getUrl();
/*  977 */     if (src != null)
/*      */     {
/*  979 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/*  981 */         target.setUrl(src);
/*  982 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryBase_Name(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
/*  990 */     String src = source.getName();
/*  991 */     if (src != null)
/*      */     {
/*  993 */       if (sourceDominant || target.getName() == null) {
/*      */         
/*  995 */         target.setName(src);
/*  996 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryBase_Layout(RepositoryBase target, RepositoryBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 1004 */     String src = source.getLayout();
/* 1005 */     if (src != null)
/*      */     {
/* 1007 */       if (sourceDominant || target.getLayout() == null) {
/*      */         
/* 1009 */         target.setLayout(src);
/* 1010 */         target.setLocation("layout", source.getLocation("layout"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryPolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
/* 1018 */     mergeRepositoryPolicy_Enabled(target, source, sourceDominant, context);
/* 1019 */     mergeRepositoryPolicy_UpdatePolicy(target, source, sourceDominant, context);
/* 1020 */     mergeRepositoryPolicy_ChecksumPolicy(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryPolicy_Enabled(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
/* 1026 */     String src = source.getEnabled();
/* 1027 */     if (src != null)
/*      */     {
/* 1029 */       if (sourceDominant || target.getEnabled() == null) {
/*      */         
/* 1031 */         target.setEnabled(src);
/* 1032 */         target.setLocation("enabled", source.getLocation("enabled"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryPolicy_UpdatePolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
/* 1040 */     String src = source.getUpdatePolicy();
/* 1041 */     if (src != null)
/*      */     {
/* 1043 */       if (sourceDominant || target.getUpdatePolicy() == null) {
/*      */         
/* 1045 */         target.setUpdatePolicy(src);
/* 1046 */         target.setLocation("updatePolicy", source.getLocation("updatePolicy"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeRepositoryPolicy_ChecksumPolicy(RepositoryPolicy target, RepositoryPolicy source, boolean sourceDominant, Map<Object, Object> context) {
/* 1054 */     String src = source.getChecksumPolicy();
/* 1055 */     if (src != null)
/*      */     {
/* 1057 */       if (sourceDominant || target.getChecksumPolicy() == null) {
/*      */         
/* 1059 */         target.setChecksumPolicy(src);
/* 1060 */         target.setLocation("checksumPolicy", source.getLocation("checksumPolicy"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1068 */     mergeDependency_GroupId(target, source, sourceDominant, context);
/* 1069 */     mergeDependency_ArtifactId(target, source, sourceDominant, context);
/* 1070 */     mergeDependency_Version(target, source, sourceDominant, context);
/* 1071 */     mergeDependency_Type(target, source, sourceDominant, context);
/* 1072 */     mergeDependency_Classifier(target, source, sourceDominant, context);
/* 1073 */     mergeDependency_Scope(target, source, sourceDominant, context);
/* 1074 */     mergeDependency_SystemPath(target, source, sourceDominant, context);
/* 1075 */     mergeDependency_Optional(target, source, sourceDominant, context);
/* 1076 */     mergeDependency_Exclusions(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_GroupId(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1082 */     String src = source.getGroupId();
/* 1083 */     if (src != null)
/*      */     {
/* 1085 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/* 1087 */         target.setGroupId(src);
/* 1088 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_ArtifactId(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1096 */     String src = source.getArtifactId();
/* 1097 */     if (src != null)
/*      */     {
/* 1099 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/* 1101 */         target.setArtifactId(src);
/* 1102 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_Version(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1110 */     String src = source.getVersion();
/* 1111 */     if (src != null)
/*      */     {
/* 1113 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/* 1115 */         target.setVersion(src);
/* 1116 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_Type(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1124 */     String src = source.getType();
/* 1125 */     if (src != null)
/*      */     {
/* 1127 */       if (sourceDominant || target.getType() == null) {
/*      */         
/* 1129 */         target.setType(src);
/* 1130 */         target.setLocation("type", source.getLocation("type"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_Classifier(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1138 */     String src = source.getClassifier();
/* 1139 */     if (src != null)
/*      */     {
/* 1141 */       if (sourceDominant || target.getClassifier() == null) {
/*      */         
/* 1143 */         target.setClassifier(src);
/* 1144 */         target.setLocation("classifier", source.getLocation("classifier"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_Scope(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1152 */     String src = source.getScope();
/* 1153 */     if (src != null)
/*      */     {
/* 1155 */       if (sourceDominant || target.getScope() == null) {
/*      */         
/* 1157 */         target.setScope(src);
/* 1158 */         target.setLocation("scope", source.getLocation("scope"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_SystemPath(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1166 */     String src = source.getSystemPath();
/* 1167 */     if (src != null)
/*      */     {
/* 1169 */       if (sourceDominant || target.getSystemPath() == null) {
/*      */         
/* 1171 */         target.setSystemPath(src);
/* 1172 */         target.setLocation("systemPath", source.getLocation("systemPath"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_Optional(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1180 */     String src = source.getOptional();
/* 1181 */     if (src != null)
/*      */     {
/* 1183 */       if (sourceDominant || target.getOptional() == null) {
/*      */         
/* 1185 */         target.setOptional(src);
/* 1186 */         target.setLocation("optional", source.getLocation("optional"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependency_Exclusions(Dependency target, Dependency source, boolean sourceDominant, Map<Object, Object> context) {
/* 1194 */     List<Exclusion> src = source.getExclusions();
/* 1195 */     if (!src.isEmpty()) {
/*      */       
/* 1197 */       List<Exclusion> tgt = target.getExclusions();
/*      */       
/* 1199 */       Map<Object, Exclusion> merged = new LinkedHashMap<Object, Exclusion>((src.size() + tgt.size()) * 2);
/*      */       
/* 1201 */       for (Exclusion element : tgt) {
/*      */         
/* 1203 */         Object key = getExclusionKey(element);
/* 1204 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 1207 */       for (Exclusion element : src) {
/*      */         
/* 1209 */         Object key = getExclusionKey(element);
/* 1210 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 1212 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 1216 */       target.setExclusions(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExclusion(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
/* 1223 */     mergeExclusion_GroupId(target, source, sourceDominant, context);
/* 1224 */     mergeExclusion_ArtifactId(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExclusion_GroupId(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
/* 1230 */     String src = source.getGroupId();
/* 1231 */     if (src != null)
/*      */     {
/* 1233 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/* 1235 */         target.setGroupId(src);
/* 1236 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExclusion_ArtifactId(Exclusion target, Exclusion source, boolean sourceDominant, Map<Object, Object> context) {
/* 1244 */     String src = source.getArtifactId();
/* 1245 */     if (src != null)
/*      */     {
/* 1247 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/* 1249 */         target.setArtifactId(src);
/* 1250 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReporting(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
/* 1258 */     mergeReporting_OutputDirectory(target, source, sourceDominant, context);
/* 1259 */     mergeReporting_ExcludeDefaults(target, source, sourceDominant, context);
/* 1260 */     mergeReporting_Plugins(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReporting_OutputDirectory(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
/* 1266 */     String src = source.getOutputDirectory();
/* 1267 */     if (src != null)
/*      */     {
/* 1269 */       if (sourceDominant || target.getOutputDirectory() == null) {
/*      */         
/* 1271 */         target.setOutputDirectory(src);
/* 1272 */         target.setLocation("outputDirectory", source.getLocation("outputDirectory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReporting_ExcludeDefaults(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
/* 1280 */     String src = source.getExcludeDefaults();
/* 1281 */     if (src != null)
/*      */     {
/* 1283 */       if (sourceDominant || target.getExcludeDefaults() == null) {
/*      */         
/* 1285 */         target.setExcludeDefaults(src);
/* 1286 */         target.setLocation("excludeDefaults", source.getLocation("excludeDefaults"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReporting_Plugins(Reporting target, Reporting source, boolean sourceDominant, Map<Object, Object> context) {
/* 1294 */     List<ReportPlugin> src = source.getPlugins();
/* 1295 */     if (!src.isEmpty()) {
/*      */       
/* 1297 */       List<ReportPlugin> tgt = target.getPlugins();
/* 1298 */       Map<Object, ReportPlugin> merged = new LinkedHashMap<Object, ReportPlugin>((src.size() + tgt.size()) * 2);
/*      */ 
/*      */       
/* 1301 */       for (ReportPlugin element : tgt) {
/*      */         
/* 1303 */         Object key = getReportPluginKey(element);
/* 1304 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 1307 */       for (ReportPlugin element : src) {
/*      */         
/* 1309 */         Object key = getReportPluginKey(element);
/* 1310 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 1312 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 1316 */       target.setPlugins(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1323 */     mergeReportPlugin_Inherited(target, source, sourceDominant, context);
/* 1324 */     mergeReportPlugin_Configuration(target, source, sourceDominant, context);
/* 1325 */     mergeReportPlugin_GroupId(target, source, sourceDominant, context);
/* 1326 */     mergeReportPlugin_ArtifactId(target, source, sourceDominant, context);
/* 1327 */     mergeReportPlugin_Version(target, source, sourceDominant, context);
/* 1328 */     mergeReportPlugin_ReportSets(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin_GroupId(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1334 */     String src = source.getGroupId();
/* 1335 */     if (src != null)
/*      */     {
/* 1337 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/* 1339 */         target.setGroupId(src);
/* 1340 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin_ArtifactId(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1348 */     String src = source.getArtifactId();
/* 1349 */     if (src != null)
/*      */     {
/* 1351 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/* 1353 */         target.setArtifactId(src);
/* 1354 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin_Version(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1362 */     String src = source.getVersion();
/* 1363 */     if (src != null)
/*      */     {
/* 1365 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/* 1367 */         target.setVersion(src);
/* 1368 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin_Inherited(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1376 */     String src = source.getInherited();
/* 1377 */     if (src != null)
/*      */     {
/* 1379 */       if (sourceDominant || target.getInherited() == null) {
/*      */         
/* 1381 */         target.setInherited(src);
/* 1382 */         target.setLocation("inherited", source.getLocation("inherited"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin_Configuration(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1390 */     Xpp3Dom src = (Xpp3Dom)source.getConfiguration();
/* 1391 */     if (src != null) {
/*      */       
/* 1393 */       Xpp3Dom tgt = (Xpp3Dom)target.getConfiguration();
/* 1394 */       if (sourceDominant || tgt == null) {
/*      */         
/* 1396 */         tgt = Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(src), tgt);
/*      */       }
/*      */       else {
/*      */         
/* 1400 */         tgt = Xpp3Dom.mergeXpp3Dom(tgt, src);
/*      */       } 
/* 1402 */       target.setConfiguration(tgt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeReportPlugin_ReportSets(ReportPlugin target, ReportPlugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 1409 */     List<ReportSet> src = source.getReportSets();
/* 1410 */     if (!src.isEmpty()) {
/*      */       
/* 1412 */       List<ReportSet> tgt = target.getReportSets();
/* 1413 */       Map<Object, ReportSet> merged = new LinkedHashMap<Object, ReportSet>((src.size() + tgt.size()) * 2);
/*      */       
/* 1415 */       for (ReportSet element : tgt) {
/*      */         
/* 1417 */         Object key = getReportSetKey(element);
/* 1418 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 1421 */       for (ReportSet element : src) {
/*      */         
/* 1423 */         Object key = getReportSetKey(element);
/* 1424 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 1426 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 1430 */       target.setReportSets(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependencyManagement(DependencyManagement target, DependencyManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1437 */     mergeDependencyManagement_Dependencies(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDependencyManagement_Dependencies(DependencyManagement target, DependencyManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1443 */     List<Dependency> src = source.getDependencies();
/* 1444 */     if (!src.isEmpty()) {
/*      */       
/* 1446 */       List<Dependency> tgt = target.getDependencies();
/* 1447 */       Map<Object, Dependency> merged = new LinkedHashMap<Object, Dependency>((src.size() + tgt.size()) * 2);
/*      */       
/* 1449 */       for (Dependency element : tgt) {
/*      */         
/* 1451 */         Object key = getDependencyKey(element);
/* 1452 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 1455 */       for (Dependency element : src) {
/*      */         
/* 1457 */         Object key = getDependencyKey(element);
/* 1458 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 1460 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 1464 */       target.setDependencies(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeParent(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
/* 1470 */     mergeParent_GroupId(target, source, sourceDominant, context);
/* 1471 */     mergeParent_ArtifactId(target, source, sourceDominant, context);
/* 1472 */     mergeParent_Version(target, source, sourceDominant, context);
/* 1473 */     mergeParent_RelativePath(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeParent_GroupId(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
/* 1479 */     String src = source.getGroupId();
/* 1480 */     if (src != null)
/*      */     {
/* 1482 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/* 1484 */         target.setGroupId(src);
/* 1485 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeParent_ArtifactId(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
/* 1493 */     String src = source.getArtifactId();
/* 1494 */     if (src != null)
/*      */     {
/* 1496 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/* 1498 */         target.setArtifactId(src);
/* 1499 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeParent_Version(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
/* 1507 */     String src = source.getVersion();
/* 1508 */     if (src != null)
/*      */     {
/* 1510 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/* 1512 */         target.setVersion(src);
/* 1513 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeParent_RelativePath(Parent target, Parent source, boolean sourceDominant, Map<Object, Object> context) {
/* 1521 */     String src = source.getRelativePath();
/* 1522 */     if (src != null)
/*      */     {
/* 1524 */       if (sourceDominant || target.getRelativePath() == null) {
/*      */         
/* 1526 */         target.setRelativePath(src);
/* 1527 */         target.setLocation("relativePath", source.getLocation("relativePath"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeOrganization(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
/* 1535 */     mergeOrganization_Name(target, source, sourceDominant, context);
/* 1536 */     mergeOrganization_Url(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeOrganization_Name(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
/* 1542 */     String src = source.getName();
/* 1543 */     if (src != null)
/*      */     {
/* 1545 */       if (sourceDominant || target.getName() == null) {
/*      */         
/* 1547 */         target.setName(src);
/* 1548 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeOrganization_Url(Organization target, Organization source, boolean sourceDominant, Map<Object, Object> context) {
/* 1556 */     String src = source.getUrl();
/* 1557 */     if (src != null)
/*      */     {
/* 1559 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/* 1561 */         target.setUrl(src);
/* 1562 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeLicense(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
/* 1569 */     mergeLicense_Name(target, source, sourceDominant, context);
/* 1570 */     mergeLicense_Url(target, source, sourceDominant, context);
/* 1571 */     mergeLicense_Distribution(target, source, sourceDominant, context);
/* 1572 */     mergeLicense_Comments(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeLicense_Name(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
/* 1578 */     String src = source.getName();
/* 1579 */     if (src != null)
/*      */     {
/* 1581 */       if (sourceDominant || target.getName() == null) {
/*      */         
/* 1583 */         target.setName(src);
/* 1584 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeLicense_Url(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
/* 1592 */     String src = source.getUrl();
/* 1593 */     if (src != null)
/*      */     {
/* 1595 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/* 1597 */         target.setUrl(src);
/* 1598 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeLicense_Distribution(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
/* 1606 */     String src = source.getDistribution();
/* 1607 */     if (src != null)
/*      */     {
/* 1609 */       if (sourceDominant || target.getDistribution() == null) {
/*      */         
/* 1611 */         target.setDistribution(src);
/* 1612 */         target.setLocation("distribution", source.getLocation("distribution"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeLicense_Comments(License target, License source, boolean sourceDominant, Map<Object, Object> context) {
/* 1620 */     String src = source.getComments();
/* 1621 */     if (src != null)
/*      */     {
/* 1623 */       if (sourceDominant || target.getComments() == null) {
/*      */         
/* 1625 */         target.setComments(src);
/* 1626 */         target.setLocation("comments", source.getLocation("comments"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1634 */     mergeMailingList_Name(target, source, sourceDominant, context);
/* 1635 */     mergeMailingList_Subscribe(target, source, sourceDominant, context);
/* 1636 */     mergeMailingList_Unsubscribe(target, source, sourceDominant, context);
/* 1637 */     mergeMailingList_Post(target, source, sourceDominant, context);
/* 1638 */     mergeMailingList_OtherArchives(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList_Name(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1644 */     String src = source.getName();
/* 1645 */     if (src != null)
/*      */     {
/* 1647 */       if (sourceDominant || target.getName() == null) {
/*      */         
/* 1649 */         target.setName(src);
/* 1650 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList_Subscribe(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1658 */     String src = source.getSubscribe();
/* 1659 */     if (src != null)
/*      */     {
/* 1661 */       if (sourceDominant || target.getSubscribe() == null) {
/*      */         
/* 1663 */         target.setSubscribe(src);
/* 1664 */         target.setLocation("subscribe", source.getLocation("subscribe"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList_Unsubscribe(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1672 */     String src = source.getUnsubscribe();
/* 1673 */     if (src != null)
/*      */     {
/* 1675 */       if (sourceDominant || target.getUnsubscribe() == null) {
/*      */         
/* 1677 */         target.setUnsubscribe(src);
/* 1678 */         target.setLocation("unsubscribe", source.getLocation("unsubscribe"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList_Post(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1686 */     String src = source.getPost();
/* 1687 */     if (src != null)
/*      */     {
/* 1689 */       if (sourceDominant || target.getPost() == null) {
/*      */         
/* 1691 */         target.setPost(src);
/* 1692 */         target.setLocation("post", source.getLocation("post"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList_Archive(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1700 */     String src = source.getArchive();
/* 1701 */     if (src != null)
/*      */     {
/* 1703 */       if (sourceDominant || target.getArchive() == null) {
/*      */         
/* 1705 */         target.setArchive(src);
/* 1706 */         target.setLocation("archive", source.getLocation("archive"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeMailingList_OtherArchives(MailingList target, MailingList source, boolean sourceDominant, Map<Object, Object> context) {
/* 1714 */     List<String> src = source.getOtherArchives();
/* 1715 */     if (!src.isEmpty()) {
/*      */       
/* 1717 */       List<String> tgt = target.getOtherArchives();
/* 1718 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/* 1719 */       merged.addAll(tgt);
/* 1720 */       merged.addAll(src);
/* 1721 */       target.setOtherArchives(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDeveloper(Developer target, Developer source, boolean sourceDominant, Map<Object, Object> context) {
/* 1728 */     mergeContributor((Contributor)target, (Contributor)source, sourceDominant, context);
/* 1729 */     mergeDeveloper_Id(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeDeveloper_Id(Developer target, Developer source, boolean sourceDominant, Map<Object, Object> context) {
/* 1735 */     String src = source.getId();
/* 1736 */     if (src != null)
/*      */     {
/* 1738 */       if (sourceDominant || target.getId() == null) {
/*      */         
/* 1740 */         target.setId(src);
/* 1741 */         target.setLocation("id", source.getLocation("id"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1749 */     mergeContributor_Name(target, source, sourceDominant, context);
/* 1750 */     mergeContributor_Email(target, source, sourceDominant, context);
/* 1751 */     mergeContributor_Url(target, source, sourceDominant, context);
/* 1752 */     mergeContributor_Organization(target, source, sourceDominant, context);
/* 1753 */     mergeContributor_OrganizationUrl(target, source, sourceDominant, context);
/* 1754 */     mergeContributor_Timezone(target, source, sourceDominant, context);
/* 1755 */     mergeContributor_Roles(target, source, sourceDominant, context);
/* 1756 */     mergeContributor_Properties(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Name(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1762 */     String src = source.getName();
/* 1763 */     if (src != null)
/*      */     {
/* 1765 */       if (sourceDominant || target.getName() == null) {
/*      */         
/* 1767 */         target.setName(src);
/* 1768 */         target.setLocation("name", source.getLocation("name"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Email(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1776 */     String src = source.getEmail();
/* 1777 */     if (src != null)
/*      */     {
/* 1779 */       if (sourceDominant || target.getEmail() == null) {
/*      */         
/* 1781 */         target.setEmail(src);
/* 1782 */         target.setLocation("email", source.getLocation("email"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Url(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1790 */     String src = source.getUrl();
/* 1791 */     if (src != null)
/*      */     {
/* 1793 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/* 1795 */         target.setUrl(src);
/* 1796 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Organization(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1804 */     String src = source.getOrganization();
/* 1805 */     if (src != null)
/*      */     {
/* 1807 */       if (sourceDominant || target.getOrganization() == null) {
/*      */         
/* 1809 */         target.setOrganization(src);
/* 1810 */         target.setLocation("organization", source.getLocation("organization"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_OrganizationUrl(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1818 */     String src = source.getOrganizationUrl();
/* 1819 */     if (src != null)
/*      */     {
/* 1821 */       if (sourceDominant || target.getOrganizationUrl() == null) {
/*      */         
/* 1823 */         target.setOrganizationUrl(src);
/* 1824 */         target.setLocation("organizationUrl", source.getLocation("organizationUrl"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Timezone(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1832 */     String src = source.getTimezone();
/* 1833 */     if (src != null)
/*      */     {
/* 1835 */       if (sourceDominant || target.getTimezone() == null) {
/*      */         
/* 1837 */         target.setTimezone(src);
/* 1838 */         target.setLocation("timezone", source.getLocation("timezone"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Roles(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1846 */     List<String> src = source.getRoles();
/* 1847 */     if (!src.isEmpty()) {
/*      */       
/* 1849 */       List<String> tgt = target.getRoles();
/* 1850 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/* 1851 */       merged.addAll(tgt);
/* 1852 */       merged.addAll(src);
/* 1853 */       target.setRoles(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeContributor_Properties(Contributor target, Contributor source, boolean sourceDominant, Map<Object, Object> context) {
/* 1860 */     Properties merged = new Properties();
/* 1861 */     if (sourceDominant) {
/*      */       
/* 1863 */       merged.putAll(target.getProperties());
/* 1864 */       merged.putAll(source.getProperties());
/*      */     }
/*      */     else {
/*      */       
/* 1868 */       merged.putAll(source.getProperties());
/* 1869 */       merged.putAll(target.getProperties());
/*      */     } 
/* 1871 */     target.setProperties(merged);
/* 1872 */     target.setLocation("properties", InputLocation.merge(target.getLocation("properties"), source.getLocation("properties"), sourceDominant));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeIssueManagement(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1879 */     mergeIssueManagement_Url(target, source, sourceDominant, context);
/* 1880 */     mergeIssueManagement_System(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeIssueManagement_System(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1886 */     String src = source.getSystem();
/* 1887 */     if (src != null)
/*      */     {
/* 1889 */       if (sourceDominant || target.getSystem() == null) {
/*      */         
/* 1891 */         target.setSystem(src);
/* 1892 */         target.setLocation("system", source.getLocation("system"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeIssueManagement_Url(IssueManagement target, IssueManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1900 */     String src = source.getUrl();
/* 1901 */     if (src != null)
/*      */     {
/* 1903 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/* 1905 */         target.setUrl(src);
/* 1906 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeScm(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
/* 1913 */     mergeScm_Url(target, source, sourceDominant, context);
/* 1914 */     mergeScm_Connection(target, source, sourceDominant, context);
/* 1915 */     mergeScm_DeveloperConnection(target, source, sourceDominant, context);
/* 1916 */     mergeScm_Tag(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeScm_Url(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
/* 1921 */     String src = source.getUrl();
/* 1922 */     if (src != null)
/*      */     {
/* 1924 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/* 1926 */         target.setUrl(src);
/* 1927 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeScm_Connection(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
/* 1934 */     String src = source.getConnection();
/* 1935 */     if (src != null)
/*      */     {
/* 1937 */       if (sourceDominant || target.getConnection() == null) {
/*      */         
/* 1939 */         target.setConnection(src);
/* 1940 */         target.setLocation("connection", source.getLocation("connection"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeScm_DeveloperConnection(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
/* 1948 */     String src = source.getDeveloperConnection();
/* 1949 */     if (src != null)
/*      */     {
/* 1951 */       if (sourceDominant || target.getDeveloperConnection() == null) {
/*      */         
/* 1953 */         target.setDeveloperConnection(src);
/* 1954 */         target.setLocation("developerConnection", source.getLocation("developerConnection"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeScm_Tag(Scm target, Scm source, boolean sourceDominant, Map<Object, Object> context) {
/* 1961 */     String src = source.getTag();
/* 1962 */     if (src != null)
/*      */     {
/* 1964 */       if (sourceDominant || target.getTag() == null) {
/*      */         
/* 1966 */         target.setTag(src);
/* 1967 */         target.setLocation("tag", source.getLocation("tag"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeCiManagement(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1975 */     mergeCiManagement_System(target, source, sourceDominant, context);
/* 1976 */     mergeCiManagement_Url(target, source, sourceDominant, context);
/* 1977 */     mergeCiManagement_Notifiers(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeCiManagement_System(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1983 */     String src = source.getSystem();
/* 1984 */     if (src != null)
/*      */     {
/* 1986 */       if (sourceDominant || target.getSystem() == null) {
/*      */         
/* 1988 */         target.setSystem(src);
/* 1989 */         target.setLocation("system", source.getLocation("system"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeCiManagement_Url(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 1997 */     String src = source.getUrl();
/* 1998 */     if (src != null)
/*      */     {
/* 2000 */       if (sourceDominant || target.getUrl() == null) {
/*      */         
/* 2002 */         target.setUrl(src);
/* 2003 */         target.setLocation("url", source.getLocation("url"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeCiManagement_Notifiers(CiManagement target, CiManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 2011 */     List<Notifier> src = source.getNotifiers();
/* 2012 */     if (!src.isEmpty()) {
/*      */       
/* 2014 */       List<Notifier> tgt = target.getNotifiers();
/* 2015 */       Map<Object, Notifier> merged = new LinkedHashMap<Object, Notifier>((src.size() + tgt.size()) * 2);
/*      */       
/* 2017 */       for (Notifier element : tgt) {
/*      */         
/* 2019 */         Object key = getNotifierKey(element);
/* 2020 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2023 */       for (Notifier element : src) {
/*      */         
/* 2025 */         Object key = getNotifierKey(element);
/* 2026 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2028 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2032 */       target.setNotifiers(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2039 */     mergeNotifier_Type(target, source, sourceDominant, context);
/* 2040 */     mergeNotifier_Address(target, source, sourceDominant, context);
/* 2041 */     mergeNotifier_Configuration(target, source, sourceDominant, context);
/* 2042 */     mergeNotifier_SendOnError(target, source, sourceDominant, context);
/* 2043 */     mergeNotifier_SendOnFailure(target, source, sourceDominant, context);
/* 2044 */     mergeNotifier_SendOnSuccess(target, source, sourceDominant, context);
/* 2045 */     mergeNotifier_SendOnWarning(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_Type(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2051 */     String src = source.getType();
/* 2052 */     if (src != null)
/*      */     {
/* 2054 */       if (sourceDominant || target.getType() == null)
/*      */       {
/* 2056 */         target.setType(src);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_Address(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2064 */     String src = source.getAddress();
/* 2065 */     if (src != null)
/*      */     {
/* 2067 */       if (sourceDominant || target.getAddress() == null)
/*      */       {
/* 2069 */         target.setAddress(src);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_Configuration(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2077 */     Properties merged = new Properties();
/* 2078 */     if (sourceDominant) {
/*      */       
/* 2080 */       merged.putAll(target.getConfiguration());
/* 2081 */       merged.putAll(source.getConfiguration());
/*      */     }
/*      */     else {
/*      */       
/* 2085 */       merged.putAll(source.getConfiguration());
/* 2086 */       merged.putAll(target.getConfiguration());
/*      */     } 
/* 2088 */     target.setConfiguration(merged);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_SendOnError(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2094 */     if (sourceDominant)
/*      */     {
/* 2096 */       target.setSendOnError(source.isSendOnError());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_SendOnFailure(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2103 */     if (sourceDominant)
/*      */     {
/* 2105 */       target.setSendOnFailure(source.isSendOnFailure());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_SendOnSuccess(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2112 */     if (sourceDominant)
/*      */     {
/* 2114 */       target.setSendOnSuccess(source.isSendOnSuccess());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeNotifier_SendOnWarning(Notifier target, Notifier source, boolean sourceDominant, Map<Object, Object> context) {
/* 2121 */     if (sourceDominant)
/*      */     {
/* 2123 */       target.setSendOnWarning(source.isSendOnWarning());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePrerequisites(Prerequisites target, Prerequisites source, boolean sourceDominant, Map<Object, Object> context) {
/* 2130 */     mergePrerequisites_Maven(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePrerequisites_Maven(Prerequisites target, Prerequisites source, boolean sourceDominant, Map<Object, Object> context) {
/* 2136 */     String src = source.getMaven();
/* 2137 */     if (src != null)
/*      */     {
/* 2139 */       if (sourceDominant || target.getMaven() == null) {
/*      */         
/* 2141 */         target.setMaven(src);
/* 2142 */         target.setLocation("maven", source.getLocation("maven"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeBuild(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2149 */     mergeBuildBase((BuildBase)target, (BuildBase)source, sourceDominant, context);
/* 2150 */     mergeBuild_SourceDirectory(target, source, sourceDominant, context);
/* 2151 */     mergeBuild_ScriptSourceDirectory(target, source, sourceDominant, context);
/* 2152 */     mergeBuild_TestSourceDirectory(target, source, sourceDominant, context);
/* 2153 */     mergeBuild_OutputDirectory(target, source, sourceDominant, context);
/* 2154 */     mergeBuild_TestOutputDirectory(target, source, sourceDominant, context);
/* 2155 */     mergeBuild_Extensions(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuild_SourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2161 */     String src = source.getSourceDirectory();
/* 2162 */     if (src != null)
/*      */     {
/* 2164 */       if (sourceDominant || target.getSourceDirectory() == null) {
/*      */         
/* 2166 */         target.setSourceDirectory(src);
/* 2167 */         target.setLocation("sourceDirectory", source.getLocation("sourceDirectory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuild_ScriptSourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2175 */     String src = source.getScriptSourceDirectory();
/* 2176 */     if (src != null)
/*      */     {
/* 2178 */       if (sourceDominant || target.getScriptSourceDirectory() == null) {
/*      */         
/* 2180 */         target.setScriptSourceDirectory(src);
/* 2181 */         target.setLocation("scriptSourceDirectory", source.getLocation("scriptSourceDirectory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuild_TestSourceDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2189 */     String src = source.getTestSourceDirectory();
/* 2190 */     if (src != null)
/*      */     {
/* 2192 */       if (sourceDominant || target.getTestSourceDirectory() == null) {
/*      */         
/* 2194 */         target.setTestSourceDirectory(src);
/* 2195 */         target.setLocation("testSourceDirectory", source.getLocation("testSourceDirectory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuild_OutputDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2203 */     String src = source.getOutputDirectory();
/* 2204 */     if (src != null)
/*      */     {
/* 2206 */       if (sourceDominant || target.getOutputDirectory() == null) {
/*      */         
/* 2208 */         target.setOutputDirectory(src);
/* 2209 */         target.setLocation("outputDirectory", source.getLocation("outputDirectory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuild_TestOutputDirectory(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2217 */     String src = source.getTestOutputDirectory();
/* 2218 */     if (src != null)
/*      */     {
/* 2220 */       if (sourceDominant || target.getTestOutputDirectory() == null) {
/*      */         
/* 2222 */         target.setTestOutputDirectory(src);
/* 2223 */         target.setLocation("testOutputDirectory", source.getLocation("testOutputDirectory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuild_Extensions(Build target, Build source, boolean sourceDominant, Map<Object, Object> context) {
/* 2231 */     List<Extension> src = source.getExtensions();
/* 2232 */     if (!src.isEmpty()) {
/*      */       
/* 2234 */       List<Extension> tgt = target.getExtensions();
/* 2235 */       Map<Object, Extension> merged = new LinkedHashMap<Object, Extension>((src.size() + tgt.size()) * 2);
/*      */       
/* 2237 */       for (Extension element : tgt) {
/*      */         
/* 2239 */         Object key = getExtensionKey(element);
/* 2240 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2243 */       for (Extension element : src) {
/*      */         
/* 2245 */         Object key = getExtensionKey(element);
/* 2246 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2248 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2252 */       target.setExtensions(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExtension(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
/* 2259 */     mergeExtension_GroupId(target, source, sourceDominant, context);
/* 2260 */     mergeExtension_ArtifactId(target, source, sourceDominant, context);
/* 2261 */     mergeExtension_Version(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExtension_GroupId(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
/* 2267 */     String src = source.getGroupId();
/* 2268 */     if (src != null)
/*      */     {
/* 2270 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/* 2272 */         target.setGroupId(src);
/* 2273 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExtension_ArtifactId(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
/* 2281 */     String src = source.getArtifactId();
/* 2282 */     if (src != null)
/*      */     {
/* 2284 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/* 2286 */         target.setArtifactId(src);
/* 2287 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeExtension_Version(Extension target, Extension source, boolean sourceDominant, Map<Object, Object> context) {
/* 2295 */     String src = source.getVersion();
/* 2296 */     if (src != null)
/*      */     {
/* 2298 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/* 2300 */         target.setVersion(src);
/* 2301 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2309 */     mergePluginConfiguration((PluginConfiguration)target, (PluginConfiguration)source, sourceDominant, context);
/* 2310 */     mergeBuildBase_DefaultGoal(target, source, sourceDominant, context);
/* 2311 */     mergeBuildBase_FinalName(target, source, sourceDominant, context);
/* 2312 */     mergeBuildBase_Directory(target, source, sourceDominant, context);
/* 2313 */     mergeBuildBase_Resources(target, source, sourceDominant, context);
/* 2314 */     mergeBuildBase_TestResources(target, source, sourceDominant, context);
/* 2315 */     mergeBuildBase_Filters(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase_DefaultGoal(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2321 */     String src = source.getDefaultGoal();
/* 2322 */     if (src != null)
/*      */     {
/* 2324 */       if (sourceDominant || target.getDefaultGoal() == null) {
/*      */         
/* 2326 */         target.setDefaultGoal(src);
/* 2327 */         target.setLocation("defaultGoal", source.getLocation("defaultGoal"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase_Directory(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2335 */     String src = source.getDirectory();
/* 2336 */     if (src != null)
/*      */     {
/* 2338 */       if (sourceDominant || target.getDirectory() == null) {
/*      */         
/* 2340 */         target.setDirectory(src);
/* 2341 */         target.setLocation("directory", source.getLocation("directory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase_FinalName(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2349 */     String src = source.getFinalName();
/* 2350 */     if (src != null)
/*      */     {
/* 2352 */       if (sourceDominant || target.getFinalName() == null) {
/*      */         
/* 2354 */         target.setFinalName(src);
/* 2355 */         target.setLocation("finalName", source.getLocation("finalName"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase_Filters(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2363 */     List<String> src = source.getFilters();
/* 2364 */     if (!src.isEmpty()) {
/*      */       
/* 2366 */       List<String> tgt = target.getFilters();
/* 2367 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/* 2368 */       merged.addAll(tgt);
/* 2369 */       merged.addAll(src);
/* 2370 */       target.setFilters(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase_Resources(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2377 */     List<Resource> src = source.getResources();
/* 2378 */     if (!src.isEmpty()) {
/*      */       
/* 2380 */       List<Resource> tgt = target.getResources();
/* 2381 */       Map<Object, Resource> merged = new LinkedHashMap<Object, Resource>((src.size() + tgt.size()) * 2);
/*      */       
/* 2383 */       for (Resource element : tgt) {
/*      */         
/* 2385 */         Object key = getResourceKey(element);
/* 2386 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2389 */       for (Resource element : src) {
/*      */         
/* 2391 */         Object key = getResourceKey(element);
/* 2392 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2394 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2398 */       target.setResources(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeBuildBase_TestResources(BuildBase target, BuildBase source, boolean sourceDominant, Map<Object, Object> context) {
/* 2405 */     List<Resource> src = source.getTestResources();
/* 2406 */     if (!src.isEmpty()) {
/*      */       
/* 2408 */       List<Resource> tgt = target.getTestResources();
/* 2409 */       Map<Object, Resource> merged = new LinkedHashMap<Object, Resource>((src.size() + tgt.size()) * 2);
/*      */       
/* 2411 */       for (Resource element : tgt) {
/*      */         
/* 2413 */         Object key = getResourceKey(element);
/* 2414 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2417 */       for (Resource element : src) {
/*      */         
/* 2419 */         Object key = getResourceKey(element);
/* 2420 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2422 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2426 */       target.setTestResources(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginConfiguration(PluginConfiguration target, PluginConfiguration source, boolean sourceDominant, Map<Object, Object> context) {
/* 2433 */     mergePluginContainer((PluginContainer)target, (PluginContainer)source, sourceDominant, context);
/* 2434 */     mergePluginConfiguration_PluginManagement(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginConfiguration_PluginManagement(PluginConfiguration target, PluginConfiguration source, boolean sourceDominant, Map<Object, Object> context) {
/* 2440 */     PluginManagement src = source.getPluginManagement();
/* 2441 */     if (source.getPluginManagement() != null) {
/*      */       
/* 2443 */       PluginManagement tgt = target.getPluginManagement();
/* 2444 */       if (tgt == null) {
/*      */         
/* 2446 */         tgt = new PluginManagement();
/* 2447 */         target.setPluginManagement(tgt);
/*      */       } 
/* 2449 */       mergePluginManagement(tgt, src, sourceDominant, context);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginContainer(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
/* 2456 */     mergePluginContainer_Plugins(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginContainer_Plugins(PluginContainer target, PluginContainer source, boolean sourceDominant, Map<Object, Object> context) {
/* 2462 */     List<Plugin> src = source.getPlugins();
/* 2463 */     if (!src.isEmpty()) {
/*      */       
/* 2465 */       List<Plugin> tgt = target.getPlugins();
/* 2466 */       Map<Object, Plugin> merged = new LinkedHashMap<Object, Plugin>((src.size() + tgt.size()) * 2);
/*      */       
/* 2468 */       for (Plugin element : tgt) {
/*      */         
/* 2470 */         Object key = getPluginKey(element);
/* 2471 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2474 */       for (Plugin element : src) {
/*      */         
/* 2476 */         Object key = getPluginKey(element);
/* 2477 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2479 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2483 */       target.setPlugins(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginManagement(PluginManagement target, PluginManagement source, boolean sourceDominant, Map<Object, Object> context) {
/* 2490 */     mergePluginContainer((PluginContainer)target, (PluginContainer)source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergePlugin(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2495 */     mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context);
/* 2496 */     mergePlugin_GroupId(target, source, sourceDominant, context);
/* 2497 */     mergePlugin_ArtifactId(target, source, sourceDominant, context);
/* 2498 */     mergePlugin_Version(target, source, sourceDominant, context);
/* 2499 */     mergePlugin_Extensions(target, source, sourceDominant, context);
/* 2500 */     mergePlugin_Dependencies(target, source, sourceDominant, context);
/* 2501 */     mergePlugin_Executions(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePlugin_GroupId(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2507 */     String src = source.getGroupId();
/* 2508 */     if (src != null)
/*      */     {
/* 2510 */       if (sourceDominant || target.getGroupId() == null) {
/*      */         
/* 2512 */         target.setGroupId(src);
/* 2513 */         target.setLocation("groupId", source.getLocation("groupId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePlugin_ArtifactId(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2521 */     String src = source.getArtifactId();
/* 2522 */     if (src != null)
/*      */     {
/* 2524 */       if (sourceDominant || target.getArtifactId() == null) {
/*      */         
/* 2526 */         target.setArtifactId(src);
/* 2527 */         target.setLocation("artifactId", source.getLocation("artifactId"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePlugin_Version(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2535 */     String src = source.getVersion();
/* 2536 */     if (src != null)
/*      */     {
/* 2538 */       if (sourceDominant || target.getVersion() == null) {
/*      */         
/* 2540 */         target.setVersion(src);
/* 2541 */         target.setLocation("version", source.getLocation("version"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePlugin_Extensions(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2549 */     String src = source.getExtensions();
/* 2550 */     if (src != null)
/*      */     {
/* 2552 */       if (sourceDominant || target.getExtensions() == null) {
/*      */         
/* 2554 */         target.setExtensions(src);
/* 2555 */         target.setLocation("extensions", source.getLocation("extensions"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePlugin_Dependencies(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2563 */     List<Dependency> src = source.getDependencies();
/* 2564 */     if (!src.isEmpty()) {
/*      */       
/* 2566 */       List<Dependency> tgt = target.getDependencies();
/* 2567 */       Map<Object, Dependency> merged = new LinkedHashMap<Object, Dependency>((src.size() + tgt.size()) * 2);
/*      */       
/* 2569 */       for (Dependency element : tgt) {
/*      */         
/* 2571 */         Object key = getDependencyKey(element);
/* 2572 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2575 */       for (Dependency element : src) {
/*      */         
/* 2577 */         Object key = getDependencyKey(element);
/* 2578 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2580 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2584 */       target.setDependencies(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePlugin_Executions(Plugin target, Plugin source, boolean sourceDominant, Map<Object, Object> context) {
/* 2591 */     List<PluginExecution> src = source.getExecutions();
/* 2592 */     if (!src.isEmpty()) {
/*      */       
/* 2594 */       List<PluginExecution> tgt = target.getExecutions();
/*      */       
/* 2596 */       Map<Object, PluginExecution> merged = new LinkedHashMap<Object, PluginExecution>((src.size() + tgt.size()) * 2);
/*      */ 
/*      */       
/* 2599 */       for (PluginExecution element : tgt) {
/*      */         
/* 2601 */         Object key = getPluginExecutionKey(element);
/* 2602 */         merged.put(key, element);
/*      */       } 
/*      */       
/* 2605 */       for (PluginExecution element : src) {
/*      */         
/* 2607 */         Object key = getPluginExecutionKey(element);
/* 2608 */         if (sourceDominant || !merged.containsKey(key))
/*      */         {
/* 2610 */           merged.put(key, element);
/*      */         }
/*      */       } 
/*      */       
/* 2614 */       target.setExecutions(new ArrayList(merged.values()));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeConfigurationContainer(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
/* 2621 */     mergeConfigurationContainer_Inherited(target, source, sourceDominant, context);
/* 2622 */     mergeConfigurationContainer_Configuration(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeConfigurationContainer_Inherited(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
/* 2628 */     String src = source.getInherited();
/* 2629 */     if (src != null)
/*      */     {
/* 2631 */       if (sourceDominant || target.getInherited() == null) {
/*      */         
/* 2633 */         target.setInherited(src);
/* 2634 */         target.setLocation("inherited", source.getLocation("inherited"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeConfigurationContainer_Configuration(ConfigurationContainer target, ConfigurationContainer source, boolean sourceDominant, Map<Object, Object> context) {
/* 2643 */     Xpp3Dom src = (Xpp3Dom)source.getConfiguration();
/* 2644 */     if (src != null) {
/*      */       
/* 2646 */       Xpp3Dom tgt = (Xpp3Dom)target.getConfiguration();
/* 2647 */       if (sourceDominant || tgt == null) {
/*      */         
/* 2649 */         tgt = Xpp3Dom.mergeXpp3Dom(new Xpp3Dom(src), tgt);
/*      */       }
/*      */       else {
/*      */         
/* 2653 */         tgt = Xpp3Dom.mergeXpp3Dom(tgt, src);
/*      */       } 
/* 2655 */       target.setConfiguration(tgt);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginExecution(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
/* 2662 */     mergeConfigurationContainer((ConfigurationContainer)target, (ConfigurationContainer)source, sourceDominant, context);
/* 2663 */     mergePluginExecution_Id(target, source, sourceDominant, context);
/* 2664 */     mergePluginExecution_Phase(target, source, sourceDominant, context);
/* 2665 */     mergePluginExecution_Goals(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginExecution_Id(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
/* 2671 */     String src = source.getId();
/* 2672 */     if (src != null)
/*      */     {
/* 2674 */       if (sourceDominant || target.getId() == null) {
/*      */         
/* 2676 */         target.setId(src);
/* 2677 */         target.setLocation("id", source.getLocation("id"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginExecution_Phase(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
/* 2685 */     String src = source.getPhase();
/* 2686 */     if (src != null)
/*      */     {
/* 2688 */       if (sourceDominant || target.getPhase() == null) {
/*      */         
/* 2690 */         target.setPhase(src);
/* 2691 */         target.setLocation("phase", source.getLocation("phase"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePluginExecution_Goals(PluginExecution target, PluginExecution source, boolean sourceDominant, Map<Object, Object> context) {
/* 2699 */     List<String> src = source.getGoals();
/* 2700 */     if (!src.isEmpty()) {
/*      */       
/* 2702 */       List<String> tgt = target.getGoals();
/* 2703 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/* 2704 */       merged.addAll(tgt);
/* 2705 */       merged.addAll(src);
/* 2706 */       target.setGoals(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeResource(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
/* 2713 */     mergeFileSet((FileSet)target, (FileSet)source, sourceDominant, context);
/* 2714 */     mergeResource_TargetPath(target, source, sourceDominant, context);
/* 2715 */     mergeResource_Filtering(target, source, sourceDominant, context);
/* 2716 */     mergeResource_MergeId(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeResource_TargetPath(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
/* 2722 */     String src = source.getTargetPath();
/* 2723 */     if (src != null)
/*      */     {
/* 2725 */       if (sourceDominant || target.getTargetPath() == null) {
/*      */         
/* 2727 */         target.setTargetPath(src);
/* 2728 */         target.setLocation("targetPath", source.getLocation("targetPath"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeResource_Filtering(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
/* 2736 */     String src = source.getFiltering();
/* 2737 */     if (src != null)
/*      */     {
/* 2739 */       if (sourceDominant || target.getFiltering() == null) {
/*      */         
/* 2741 */         target.setFiltering(src);
/* 2742 */         target.setLocation("filtering", source.getLocation("filtering"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeResource_MergeId(Resource target, Resource source, boolean sourceDominant, Map<Object, Object> context) {
/* 2750 */     String src = source.getMergeId();
/* 2751 */     if (src != null)
/*      */     {
/* 2753 */       if (sourceDominant || target.getMergeId() == null)
/*      */       {
/* 2755 */         target.setMergeId(src);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeFileSet(FileSet target, FileSet source, boolean sourceDominant, Map<Object, Object> context) {
/* 2762 */     mergePatternSet((PatternSet)target, (PatternSet)source, sourceDominant, context);
/* 2763 */     mergeFileSet_Directory(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeFileSet_Directory(FileSet target, FileSet source, boolean sourceDominant, Map<Object, Object> context) {
/* 2769 */     String src = source.getDirectory();
/* 2770 */     if (src != null)
/*      */     {
/* 2772 */       if (sourceDominant || target.getDirectory() == null) {
/*      */         
/* 2774 */         target.setDirectory(src);
/* 2775 */         target.setLocation("directory", source.getLocation("directory"));
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePatternSet(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
/* 2783 */     mergePatternSet_Includes(target, source, sourceDominant, context);
/* 2784 */     mergePatternSet_Excludes(target, source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePatternSet_Includes(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
/* 2790 */     List<String> src = source.getIncludes();
/* 2791 */     if (!src.isEmpty()) {
/*      */       
/* 2793 */       List<String> tgt = target.getIncludes();
/* 2794 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/* 2795 */       merged.addAll(tgt);
/* 2796 */       merged.addAll(src);
/* 2797 */       target.setIncludes(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergePatternSet_Excludes(PatternSet target, PatternSet source, boolean sourceDominant, Map<Object, Object> context) {
/* 2804 */     List<String> src = source.getExcludes();
/* 2805 */     if (!src.isEmpty()) {
/*      */       
/* 2807 */       List<String> tgt = target.getExcludes();
/* 2808 */       List<String> merged = new ArrayList<String>(tgt.size() + src.size());
/* 2809 */       merged.addAll(tgt);
/* 2810 */       merged.addAll(src);
/* 2811 */       target.setExcludes(merged);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void mergeProfile(Profile target, Profile source, boolean sourceDominant, Map<Object, Object> context) {
/* 2817 */     mergeModelBase((ModelBase)target, (ModelBase)source, sourceDominant, context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void mergeActivation(Activation target, Activation source, boolean sourceDominant, Map<Object, Object> context) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Object getDependencyKey(Dependency dependency) {
/* 2829 */     return dependency;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getPluginKey(Plugin object) {
/* 2834 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getPluginExecutionKey(PluginExecution object) {
/* 2839 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getReportPluginKey(ReportPlugin object) {
/* 2844 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getReportSetKey(ReportSet object) {
/* 2849 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getLicenseKey(License object) {
/* 2854 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getMailingListKey(MailingList object) {
/* 2859 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getDeveloperKey(Developer object) {
/* 2864 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getContributorKey(Contributor object) {
/* 2869 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getProfileKey(Profile object) {
/* 2874 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getRepositoryKey(Repository object) {
/* 2879 */     return getRepositoryBaseKey((RepositoryBase)object);
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getRepositoryBaseKey(RepositoryBase object) {
/* 2884 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getNotifierKey(Notifier object) {
/* 2889 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getResourceKey(Resource object) {
/* 2894 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getExtensionKey(Extension object) {
/* 2899 */     return object;
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object getExclusionKey(Exclusion object) {
/* 2904 */     return object;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\merge\ModelMerger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */