package com.tickaroo.tikxml.autovalue;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.JavaFile;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.processor.ProcessingException;
import java.io.IOException;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

/**
 * This is the auto-value extension for TikXml
 *
 * @author Hannes Dorfmann
 */
@AutoService(AutoValueExtension.class)
public class TikXmlAutoValueExtension extends AutoValueExtension {

  @Override public boolean applicable(Context context) {
    ProcessingEnvironment environment = context.processingEnvironment();

    try {
      Xml xmlAnnotation = context.autoValueClass().getAnnotation(Xml.class);
      if (xmlAnnotation == null) {
        return false; // Auto value class not annotated with @Xml annotation
      }

      if (!xmlAnnotation.inheritance()) {
        throw new ProcessingException(context.autoValueClass(),
            "Inheritance in TikXml can't be disabled via @"
                + Xml.class.getSimpleName()
                + "(inheritance = false) in class "
                + context.autoValueClass().getQualifiedName());
      }

      List<AnnotatedMethod<?>> annotatedMethods =
          AutoValueScannerKt.extractAutoValueProperties(context.autoValueClass(),
              context.properties(), context.processingEnvironment().getTypeUtils(), context.processingEnvironment().getElementUtils());

      // generate code
      AutoValueAnnotatedClass annotatedClass =
          new AutoValueAnnotatedClass(context.packageName(), context.autoValueClass(),
              xmlAnnotation, annotatedMethods);

      try {
        Filer filer = context.processingEnvironment().getFiler();

        JavaFile.builder(context.packageName(),
            AutoValueTypeAdapterCodeGeneratorKt.generateValueHolder(annotatedClass,
                context.processingEnvironment().getElementUtils()))
            .build()
            .writeTo(filer);

        JavaFile.builder(context.packageName(),
            AutoValueTypeAdapterCodeGeneratorKt.generateTypeAdapter(annotatedClass))
            .build()
            .writeTo(filer);
      } catch (IOException e) {
        throw new ProcessingException(annotatedClass.getAutoValueClass(),
            "Error while generating code for " + annotatedClass.getAutoValueClass()
                .getQualifiedName() + ": " + e.getMessage());
      }
    } catch (ProcessingException exception) {
      environment.getMessager()
          .printMessage(Diagnostic.Kind.ERROR, exception.getMessage(), exception.getElement());
    }

    return false; // We don't generate code as an autovalue extension
  }

  @Override public String generateClass(Context context, String className,
      String classToExtend, boolean isFinal) {
    // We don't generate an AutoValue class
    return null;
  }
}
