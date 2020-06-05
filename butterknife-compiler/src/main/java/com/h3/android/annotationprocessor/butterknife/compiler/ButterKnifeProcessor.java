package com.h3.android.annotationprocessor.butterknife.compiler;

import com.google.auto.service.AutoService;
import com.h3.android.annotationprocessor.butterknife.annotations.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elements;
    private Messager messager;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<FieldViewBinding>> targetMap = getTargetClassMap(roundEnvironment);
        createJavaFile(targetMap.entrySet());
        return true;
    }

    /**
     * 获取所有注解信息
     *
     * @param roundEnvironment
     * @return key=Activity, value=Activity中所有被注解修饰过的字段
     */
    private Map<TypeElement, List<FieldViewBinding>> getTargetClassMap(RoundEnvironment roundEnvironment) {
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();

        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : annotatedElements) {
            String fieldName = element.getSimpleName().toString(); // 字段名
            TypeMirror typeMirror = element.asType(); // 字段类型
            int value = element.getAnnotation(BindView.class).value(); // 注解的值
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list = targetMap.get(typeElement);
            if (list == null) {
                list = new ArrayList<>();
                targetMap.put(typeElement, list);
            }
            list.add(new FieldViewBinding(fieldName, typeMirror, value));
        }

        return targetMap;

    }

    private void createJavaFile(Set<Map.Entry<TypeElement, List<FieldViewBinding>>> entries) {
        for (Map.Entry<TypeElement, List<FieldViewBinding>> entry : entries) {
            TypeElement typeElement = entry.getKey();
            List<FieldViewBinding> list = entry.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }
            // 获取类的包名
            String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
            // 创建Java文件
            String className = typeElement.getQualifiedName().toString().substring(packageName.length() + 1);
            // 新类名，后面加上一个_ViewBinding用以区分
            String newClassName = className + "_ViewBinding";

            // javapoet创建Java类
            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.bestGuess(className), "target");
            for (FieldViewBinding fieldViewBinding : list) {
                // 获取类的全名
                String packageNameString = fieldViewBinding.getTypeMirror().toString();
                ClassName viewClass = ClassName.bestGuess(packageNameString);
                methodBuilder.addStatement("target.$L=($L)target.findViewById($L)",
                        fieldViewBinding.getFieldName(), viewClass, fieldViewBinding.getViewId());
            }
            TypeSpec typeSpec = TypeSpec.classBuilder(newClassName).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .addFileComment("Generated code from Butter Knife. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}