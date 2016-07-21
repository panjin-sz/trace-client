/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

import javassist.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceClassFileTransformer.java 2016年7月21日 下午6:15:46 $
 */
public class TraceClassFileTransformer implements ClassFileTransformer {

    private static final Logger LOG        = LoggerFactory.getLogger(TraceClassFileTransformer.class);
    private static final String daoPackage = System.getProperty("dao.package", null);
    public static final byte[]  empty      = new byte[0];

    public static void premain(String options, Instrumentation ins) {
        // 注册字节码转换器
        ins.addTransformer(new TraceClassFileTransformer());
        LOG.info("TraceClassFileTransformer added, daoPackage={}", daoPackage == null ? "*.dao" : daoPackage);
    }

    /**
     * 字节码加载到虚拟机前会进入这个方法
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                                                                                      throws IllegalClassFormatException {

        if (daoPackage == null) {
            // 对包名以dao结尾的类做字节码转换
            int end = className.lastIndexOf('/');
            if (end < 0) {
                return empty;
            }
            int begin = className.lastIndexOf('/', end - 1);
            if (begin < 0) {
                return empty;
            }
            String leafPackage = className.substring(begin + 1, end);
            if (!"dao".equals(leafPackage)) {
                return empty;
            }
        } else {
            // 对指定包下的类做字节码转换
            int index = className.lastIndexOf('/');
            if (index < 0) {
                return empty;
            }
            String packageName = className.substring(0, index).replaceAll("/", ".");
            if (!daoPackage.equals(packageName)) {
                return empty;
            }
        }

        // 转换包名
        if (className.contains("/")) {
            className = className.replaceAll("/", ".");
        }
        try {
            CtClass cc = ClassPool.getDefault().get(className);
            int classMod = cc.getModifiers();
            // 不转换接口类
            if (Modifier.isInterface(classMod)) {
                return empty;
            }

            // import
            ClassPool pool = ClassPool.getDefault();
            pool.importPackage("com.netease.backend.trace.filters.Trace");
            pool.importPackage("com.netease.backend.trace.filters.support.IdFactory");
            pool.importPackage("com.netease.backend.trace.filters.support.Spans");
            pool.importPackage("com.netease.backend.trace.filters.enums.IdTypeEnums");
            pool.importPackage("com.netease.backend.trace.meta.model.Span");
            pool.importPackage("com.netease.backend.trace.meta.model.AppType");
            pool.importPackage("com.netease.backend.trace.meta.model.TraceType");
            pool.importPackage("com.netease.backend.trace.meta.model.Endpoint");
            pool.importPackage("com.netease.backend.trace.meta.model.Annotation");

            CtMethod[] allMethods = cc.getDeclaredMethods();
            for (CtMethod iMethod : allMethods) {
                int mod = iMethod.getModifiers();
                // 只对非静态公有方法做转换
                if (!Modifier.isStatic(mod) && Modifier.isPublic(mod)) {
                    addTrace(cc, iMethod);
                    LOG.info("Transformed method {}", iMethod.getLongName());
                }
            }
            return cc.toBytecode();
        } catch (Exception e) {
            LOG.info("Failed to transform class {}", className, e);
        }
        return empty;
    }

    private void addTrace(CtClass cc, CtMethod method) throws CannotCompileException, NotFoundException {
        String methodName = method.getName();
        CtMethod implMethod = CtNewMethod.copy(method, methodName, cc, null);
        String implName = methodName + "$impl";
        implMethod.setName(implName);
        cc.addMethod(implMethod);

        String spanName = method.getLongName();
        int idx = spanName.indexOf('(');
        if (idx > 0) {
            spanName = spanName.substring(0, idx);
        }

        String type = method.getReturnType().getName();
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        buf.append("String spanName = \"").append(spanName).append("\";\n");
        if (!"void".equals(type)) {
            buf.append(type).append(" result;");
        }
        buf.append("Trace trace = Trace.getInstance();\n"
                   + "        if (trace.isOn()) {\n"
                   + "            Span parentSpan = null;\n"
                   + "            boolean isWeb = trace.getWebContext() != null;\n"
                   + "            boolean isService = trace.getServiceContext() != null;\n"
                   + "            if (isWeb) {\n"
                   + "                parentSpan = trace.getWebContext().getSpan();\n"
                   + "            } else if (isService) {\n"
                   + "                parentSpan = trace.getServiceContext().getSpan();\n"
                   + "            }\n"
                   + "\n"
                   + "            Span span = null;\n"
                   + "            if (parentSpan == null) {\n"
                   + "                span = trace.newSpan(spanName);\n"
                   + "            } else {\n"
                   + "                span = trace.genSpan(parentSpan.getTraceId(),\n"
                   + "                                     IdFactory.getInstance().getNextId(IdTypeEnums.SPAN_ID.getType(), spanName),\n"
                   + "                                     parentSpan.getId(), spanName, parentSpan.isSample());\n"
                   + "            }\n"
                   + "\n"
                   + "            String appType = isWeb ? AppType.WEB.getType() : AppType.SERVICE.getType();\n"
                   + "            span.setAppType(appType);\n"
                   + "            span.setItemType(TraceType.SQL.getType());\n"
                   + "            span.setAppName(trace.getAppName());\n"
                   + "            span.setHost(trace.loadEndPort());\n"
                   + "            Annotation startAnno = Spans.genAnnotation(Annotation.AnnType.CS, System.currentTimeMillis());\n"
                   + "            span.addAnnotation(startAnno);");

        // 调用原方法
        buf.append("try {");
        if (!"void".equals(type)) {
            buf.append("result=");
        }
        buf.append(implName).append("($$);\n");
        buf.append("} catch (Throwable t){ " + "trace.logException(span, t);" + "trace.logSpan(span);" + " throw t;"
                   + "}");

        buf.append("Annotation endAnno = Spans.genAnnotation(Annotation.AnnType.CR, System.currentTimeMillis());\n"
                   + "            span.addAnnotation(endAnno);\n"
                   + "\n"
                   + "            trace.logSpan(span);\n"
                   + "        } else {");
        if (!"void".equals(type)) {
            buf.append("result=");
        }
        buf.append(implName).append("($$);\n");
        buf.append("}");
        if (!"void".equals(type)) {
            buf.append("return result;\n");
        }
        buf.append("}");

        String body = buf.toString();
        LOG.debug(body);
        method.setBody(body);
    }
}
