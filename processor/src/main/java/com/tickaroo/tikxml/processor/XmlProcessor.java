/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.processor;

import com.google.auto.service.AutoService;
import com.tickaroo.tikxml.annotation.GenericAdapter;
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.processor.field.AnnotatedClass;
import com.tickaroo.tikxml.processor.field.AnnotatedClassImpl;
import com.tickaroo.tikxml.processor.generator.GenericAdapterCodeGenerator;
import com.tickaroo.tikxml.processor.generator.TypeAdapterCodeGenerator;
import com.tickaroo.tikxml.processor.scanning.AnnotationDetector;
import com.tickaroo.tikxml.processor.scanning.AnnotationScanner;
import com.tickaroo.tikxml.processor.scanning.DefaultAnnotationDetector;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;

import static com.tickaroo.tikxml.processor.ProcessorConstants.AUTO_VALUE_POST_FIX;
import static net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING;

/**
 * Annotation processor for @Xml annotated images
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@AutoService(Processor.class)
@IncrementalAnnotationProcessor(ISOLATING)
public class XmlProcessor extends AbstractProcessor {

  /**
   * The default scan mode
   */
  private static final String OPTION_TYPE_CONVERTER_FOR_PRIMITIVES = "primitiveTypeConverters";
  private static final String MAP_IMPL = "tikxml.mapImpl";

  private Messager messager;
  private Filer filer;
  private Elements elementUtils;
  private Types typeUtils;
  private AnnotationDetector annotationDetector;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    messager = processingEnv.getMessager();
    filer = processingEnv.getFiler();
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    annotationDetector = new DefaultAnnotationDetector(elementUtils, typeUtils, messager);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new HashSet<>();
    types.add(Xml.class.getCanonicalName());
    types.add(GenericAdapter.class.getCanonicalName());
    return types;
  }

  @Override
  public Set<String> getSupportedOptions() {
    Set<String> options = new HashSet<>();
    options.add(OPTION_TYPE_CONVERTER_FOR_PRIMITIVES);
    options.add(MAP_IMPL);
    return options;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      String primitiveTypeConverterOptions = processingEnv.getOptions().get(OPTION_TYPE_CONVERTER_FOR_PRIMITIVES);
      Set<String> primitiveTypeConverters = readPrimitiveTypeConverterOptions(primitiveTypeConverterOptions);
      AnnotationScanner scanner = new AnnotationScanner(elementUtils, typeUtils, annotationDetector, messager);

      Set<? extends Element> genericAdapterElements = roundEnv.getElementsAnnotatedWith(GenericAdapter.class);

      // put all interfaces and abstract classes annotated with @GenericAdapter in the genericTypes map
      for (Element element : genericAdapterElements) {
        if (element.getKind() == ElementKind.INTERFACE || element.getKind().isClass() && element.getModifiers()
            .contains(Modifier.ABSTRACT)) {
          annotationDetector.addGenericType(element.toString(), null);
        } else {
          throw new ProcessingException(element,
              "Only interfaces and abstract classes can be annotated with @" + GenericAdapter.class.getSimpleName() +
                  "! Please remove @" + GenericAdapter.class.getSimpleName() + " from " + element + "!");
        }
      }

      Set<? extends Element> xmlElements = roundEnv.getElementsAnnotatedWith(Xml.class);



      // find all interfaces and abstract superclasses implemented by classes annotated with @Xml
      for (Element element : xmlElements) {
        addGenericAdapterInterfaces((TypeElement) element, (TypeElement) element);
        addGenericAdapterSuperclasses((TypeElement) element, (TypeElement) element);
      }

      GenericAdapterCodeGenerator genericAdapterCodeGenerator = new GenericAdapterCodeGenerator(filer, typeUtils, elementUtils);
      genericAdapterCodeGenerator.generateCode(annotationDetector.getGenericTypes());

      for (Element element : xmlElements) {
        if (element.getKind() == ElementKind.CLASS && element.getModifiers().contains(Modifier.ABSTRACT)) {
          continue;
        }
        AnnotatedClass clazz = new AnnotatedClassImpl(element);
        // Scan class
        scanner.scan(clazz);

        String mapImpl = processingEnv.getOptions().get(MAP_IMPL);
        TypeAdapterCodeGenerator generator =
            new TypeAdapterCodeGenerator(filer, elementUtils, typeUtils, primitiveTypeConverters, mapImpl);
        generator.generateCode(clazz);
      }
    } catch (ProcessingException e) {
      printError(e);
    }

    return false;
  }

  Set<String> readPrimitiveTypeConverterOptions(String optionsAsString) {
    Set<String> primitiveTypeConverters = new HashSet<>();

    if (optionsAsString != null && optionsAsString.length() > 0) {
      String[] options = optionsAsString.split(",");
      for (String o : options) {
        primitiveTypeConverters.add(o.trim());
      }
    }

    return primitiveTypeConverters;
  }

  private void addGenericAdapterSuperclasses(TypeElement originalElement, TypeElement element) {
    if (originalElement.getKind() == ElementKind.INTERFACE) {
      return;
    }

    TypeMirror typeMirror = element.getSuperclass();

    if (!typeMirror.toString().equals("java.lang.Object")) {
      if (annotationDetector.containsGenericType(typeMirror.toString())) {
        annotationDetector.addGenericType(typeMirror.toString(), originalElement.toString()); // add all abstract classes or interfaces types
      } else if (!typeMirror.toString().equals(originalElement.toString())
          && element.getKind() == ElementKind.CLASS
          && element.asType().getKind() == TypeKind.DECLARED) {
        if (!annotationDetector.containsGenericType(typeMirror.toString())) {
          annotationDetector.addGenericType(typeMirror.toString(), typeMirror.toString());
        }
        annotationDetector.addGenericType(typeMirror.toString(), element.toString()); // add all other polymorphism types
      }

      addGenericAdapterSuperclasses(originalElement, (TypeElement) typeUtils.asElement(typeMirror));
    } else if (originalElement.toString().endsWith(AUTO_VALUE_POST_FIX)) { // valueholder for AutoValue classes
      String realQualifiedName = originalElement.toString().replace(AUTO_VALUE_POST_FIX, "");
      if (annotationDetector.containsGenericType(realQualifiedName)) {
        annotationDetector.addGenericType(realQualifiedName, realQualifiedName);
      }
    }
  }

  private void addGenericAdapterInterfaces(TypeElement originalElement, TypeElement element) {
    for (TypeMirror typeMirror : ((TypeElement) element).getInterfaces()) {
      if (annotationDetector.containsGenericType(typeMirror.toString())) {
        annotationDetector.addGenericType(typeMirror.toString(), originalElement.toString());
      }

      addGenericAdapterInterfaces(originalElement, (TypeElement) typeUtils.asElement(typeMirror));
    }
  }

  /**
   * Prints the error message
   *
   * @param exception The exception that has caused an error
   */
  private void printError(ProcessingException exception) {
    messager.printMessage(Diagnostic.Kind.ERROR, exception.getMessage(), exception.getElement());
  }
}
