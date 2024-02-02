/*     */ package org.noear.solon.core;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.Bean;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Import;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.noear.solon.annotation.Mapping;
/*     */ import org.noear.solon.annotation.Remoting;
/*     */ import org.noear.solon.annotation.ServerEndpoint;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.event.EventListener;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.core.message.Listener;
/*     */ import org.noear.solon.core.util.RankEntity;
/*     */ import org.noear.solon.core.wrap.ClassWrap;
/*     */ import org.noear.solon.core.wrap.FieldWrap;
/*     */ import org.noear.solon.core.wrap.MethodWrap;
/*     */ import org.noear.solon.core.wrap.ParamWrap;
/*     */ import org.noear.solon.core.wrap.VarGather;
/*     */ 
/*     */ public class AopContext extends BeanContainer {
/*     */   private final Map<Method, MethodWrap> methodCached;
/*     */   private final Set<Class<?>> tryCreateCached;
/*     */   private boolean loadDone;
/*     */   private final Set<RankEntity<Consumer<AopContext>>> loadEvents;
/*     */   
/*     */   public AopContext() {
/*  37 */     this((ClassLoader)null, (Props)null);
/*     */   }
/*     */   
/*     */   public AopContext(ClassLoader classLoader, Props props) {
/*  41 */     super(classLoader, props);
/*     */ 
/*     */ 
/*     */     
/*  45 */     this.methodCached = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 409 */     this.tryCreateCached = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 543 */     this.loadEvents = new LinkedHashSet<>(); initialize();
/*     */   }
/*     */   public MethodWrap methodGet(Method method) { MethodWrap mw = this.methodCached.get(method); if (mw == null) synchronized (method) { mw = this.methodCached.get(method); if (mw == null) { mw = new MethodWrap(this, method); this.methodCached.put(method, mw); }  }   return mw; }
/*     */   public void clear() { super.clear(); this.methodCached.clear(); this.tryCreateCached.clear(); this.loadDone = false; this.loadEvents.clear(); }
/*     */   protected BeanWrap wrapCreate(Class<?> type, Object bean) { return new BeanWrap(this, type, bean); }
/*     */   public AopContext copy() { return copy((ClassLoader)null, (Props)null); }
/*     */   public AopContext copy(ClassLoader classLoader, Props props) { AopContext tmp = new AopContext(classLoader, props); copyTo(tmp); return tmp; }
/*     */   protected void initialize() { beanBuilderAdd(Configuration.class, (clz, bw, anno) -> { beanInjectProperties(clz, bw.raw()); for (Method m : ClassWrap.get(bw.clz()).getMethods()) { Bean m_an = m.<Bean>getAnnotation(Bean.class); if (m_an != null) { m.setAccessible(true); MethodWrap mWrap = methodGet(m); tryBuildBean(m_an, mWrap, bw); }  }  addBeanShape(clz, bw, 0); for (Annotation a1 : clz.getAnnotations()) { if (a1 instanceof Import) { beanImport((Import)a1); } else { beanImport(a1.annotationType().<Import>getAnnotation(Import.class)); }  }  beanRegisterSup0(bw); }); beanBuilderAdd(Component.class, (clz, bw, anno) -> { String beanName = Utils.annoAlias(anno.value(), anno.name()); bw.nameSet(beanName); bw.tagSet(anno.tag()); bw.attrsSet(anno.attrs()); bw.typedSet(anno.typed()); addBeanShape(clz, bw, anno.index()); beanRegister(bw, beanName, anno.typed()); beanExtract(bw); if (bw.singleton()) EventBus.push(bw.raw());  }); beanBuilderAdd(Remoting.class, (clz, bw, anno) -> { bw.remotingSet(true); beanRegister(bw, "", false); }); beanBuilderAdd(Controller.class, (clz, bw, anno) -> (new HandlerLoader(bw)).load((HandlerSlots)Solon.app())); beanBuilderAdd(ServerEndpoint.class, (clz, wrap, anno) -> { if (Listener.class.isAssignableFrom(clz)) { Listener l = wrap.<Listener>raw(); Solon.app().router().add(Utils.annoAlias(anno.value(), anno.path()), anno.method(), l); }  }); beanInjectorAdd(Inject.class, (fwT, anno) -> beanInject(fwT, anno.value(), anno.autoRefreshed())); }
/*     */   private void addBeanShape(Class<?> clz, BeanWrap bw, int index) { if (Plugin.class.isAssignableFrom(clz)) { Solon.app().plug(bw.<Plugin>raw()); return; }  if (EventListener.class.isAssignableFrom(clz)) { addEventListener(clz, bw); return; }  if (LoadBalance.Factory.class.isAssignableFrom(clz)) Bridge.upstreamFactorySet(bw.<LoadBalance.Factory>raw());  if (Handler.class.isAssignableFrom(clz)) { Mapping mapping = clz.<Mapping>getAnnotation(Mapping.class); if (mapping != null) { Handler handler = bw.<Handler>raw(); Set<MethodType> v0 = MethodTypeUtil.findAndFill(new HashSet(), t -> (bw.annotationGet(t) != null)); if (v0.size() == 0) v0 = new HashSet<>(Arrays.asList(mapping.method()));  Solon.app().add(mapping, v0, handler); }  }  if (Filter.class.isAssignableFrom(clz)) Solon.app().filter(index, bw.<Filter>raw());  }
/* 552 */   private void addEventListener(Class<?> clz, BeanWrap bw) { Class<?>[] ets = GenericUtil.resolveTypeArguments(clz, EventListener.class); if (ets != null && ets.length > 0) EventBus.subscribe(ets[0], bw.<EventListener>raw());  } public void beanExtract(BeanWrap bw) { if (bw == null) return;  if (this.beanExtractors.size() == 0) return;  ClassWrap clzWrap = ClassWrap.get(bw.clz()); for (Method m : clzWrap.getMethods()) { for (Annotation a : m.getAnnotations()) { BeanExtractor<Annotation> be = (BeanExtractor)this.beanExtractors.get(a.annotationType()); if (be != null) try { be.doExtract(bw, m, a); } catch (Throwable e) { e = Utils.throwableUnwrap(e); if (e instanceof RuntimeException) throw (RuntimeException)e;  throw new RuntimeException(e); }   }  }  } public void beanInject(Object obj) { if (obj == null) return;  ClassWrap clzWrap = ClassWrap.get(obj.getClass()); for (Map.Entry<String, FieldWrap> kv : (Iterable<Map.Entry<String, FieldWrap>>)clzWrap.getFieldAllWraps().entrySet()) { Annotation[] annS = ((FieldWrap)kv.getValue()).annoS; if (annS.length > 0) { VarHolder varH = ((FieldWrap)kv.getValue()).holder(this, obj); tryInject(varH, annS); }  }  } public void beanImport(Import anno) { if (anno != null) { for (Class<?> clz : anno.value()) beanMake(clz);  for (String pkg : anno.scanPackages()) beanScan(pkg);  for (Class<?> src : anno.scanPackageClasses()) beanScan(src);  }  } @Note("添加bean加载完成事件") public void beanOnloaded(Consumer<AopContext> fun) { beanOnloaded(0, fun); }
/*     */   public void beanScan(Class<?> source) { if (source.getPackage() != null) beanScan(source.getClassLoader(), source.getPackage().getName());  }
/*     */   public void beanScan(String basePackage) { beanScan(getClassLoader(), basePackage); }
/*     */   public void beanScan(ClassLoader classLoader, String basePackage) { if (Utils.isEmpty(basePackage)) return;  if (classLoader == null) return;  String dir = basePackage.replace('.', '/'); ScanUtil.scan(classLoader, dir, n -> n.endsWith(".class")).stream().sorted(Comparator.comparing(s -> Integer.valueOf(s.length()))).forEach(name -> { String className = name.substring(0, name.length() - 6); Class<?> clz = Utils.loadClass(classLoader, className.replace("/", ".")); if (clz != null) tryCreateBean(clz);  }); }
/*     */   public BeanWrap beanMake(Class<?> clz) { BeanWrap bw = wrap(clz, null); tryCreateBean(bw); putWrap(clz, bw); return bw; }
/* 557 */   protected void tryInject(VarHolder varH, Annotation[] annS) { for (Annotation a : annS) { BeanInjector<Annotation> bi = (BeanInjector)this.beanInjectors.get(a.annotationType()); if (bi != null) bi.doInject(varH, a);  }  } protected void tryCreateBean(Class<?> clz) { tryCreateBean0(clz, (c, a) -> { BeanWrap bw = wrap(clz, null); c.doBuild(clz, bw, a); putWrap(clz, bw); }); } protected void tryCreateBean(BeanWrap bw) { tryCreateBean0(bw.clz(), (c, a) -> c.doBuild(bw.clz(), bw, a)); } protected void tryCreateBean0(Class<?> clz, BiConsumerEx<BeanBuilder, Annotation> consumer) { Annotation[] annS = clz.getDeclaredAnnotations(); if (annS.length > 0) { if (this.tryCreateCached.contains(clz)) return;  this.tryCreateCached.add(clz); for (Annotation a : annS) { BeanBuilder builder = this.beanBuilders.get(a.annotationType()); if (builder != null) try { consumer.accept(builder, a); } catch (Throwable e) { e = Utils.throwableUnwrap(e); if (e instanceof RuntimeException) throw (RuntimeException)e;  throw new RuntimeException(e); }   }  }  } protected void tryBuildBean(Bean anno, MethodWrap mWrap, BeanWrap bw) throws Exception { int size2 = (mWrap.getParamWraps()).length; if (size2 == 0) { Object raw = mWrap.invoke(bw.raw(), new Object[0]); tryBuildBean0(mWrap, anno, raw); } else { VarGather gather = new VarGather(bw, size2, args2 -> { try { Object raw = mWrap.invoke(bw.raw(), args2); tryBuildBean0(mWrap, anno, raw); } catch (Throwable e) { e = Utils.throwableUnwrap(e); if (e instanceof RuntimeException) throw (RuntimeException)e;  throw new RuntimeException(e); }  }); for (ParamWrap p1 : mWrap.getParamWraps()) { VarHolder p2 = gather.add(p1.getParameter()); tryParameterInject(p2, p1.getParameter()); }  }  } protected void tryParameterInject(VarHolder varH, Parameter p) { Annotation[] annoS = p.getDeclaredAnnotations(); if (annoS.length == 0) { beanInject(varH, null); } else { for (Annotation anno : annoS) { BeanInjector<Annotation> injector = (BeanInjector)this.beanInjectors.get(anno.annotationType()); if (injector != null) { injector.doInject(varH, anno); break; }  }  }  } protected void tryBuildBean0(MethodWrap mWrap, Bean anno, Object raw) { if (raw != null) { Class<?> beanClz = mWrap.getReturnType(); Type beanGtp = mWrap.getGenericReturnType(); BeanWrap m_bw = null; if (raw instanceof BeanWrap) { m_bw = (BeanWrap)raw; } else { EventBus.push(raw); m_bw = wrapCreate(beanClz, raw); m_bw.attrsSet(anno.attrs()); }  String beanName = Utils.annoAlias(anno.value(), anno.name()); m_bw.nameSet(beanName); m_bw.tagSet(anno.tag()); m_bw.typedSet(anno.typed()); addBeanShape(m_bw.clz(), m_bw, anno.index()); beanRegister(m_bw, beanName, anno.typed()); if (beanGtp instanceof java.lang.reflect.ParameterizedType) putWrap(beanGtp.getTypeName(), m_bw);  EventBus.push(m_bw); }  } @Note("添加bean加载完成事件") public void beanOnloaded(int index, Consumer<AopContext> fun) { this.loadEvents.add(new RankEntity(fun, index));
/*     */ 
/*     */     
/* 560 */     if (this.loadDone) {
/* 561 */       fun.accept(this);
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beanLoaded() {
/* 569 */     this.loadDone = true;
/*     */ 
/*     */     
/* 572 */     this.loadEvents.stream()
/* 573 */       .sorted(Comparator.comparingInt(m -> m.index))
/* 574 */       .forEach(m -> ((Consumer<AopContext>)m.target).accept(this));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\AopContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */