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
import com.tickaroo.tikxml.annotation.Xml;
import com.tickaroo.tikxml.processor.field.AnnotatedClass;
import com.tickaroo.tikxml.processor.field.AnnotatedClassImpl;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Annotation processor for @Xml annotated images
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
@AutoService(Processor.class)
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
    annotationDetector = new DefaultAnnotationDetector(elementUtils, typeUtils);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new HashSet<>();
    types.add(Xml.class.getCanonicalName());
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

      String primitiveTypeConverterOptions =
          processingEnv.getOptions().get(OPTION_TYPE_CONVERTER_FOR_PRIMITIVES);
      Set<String> primitiveTypeConverters =
          readPrimitiveTypeConverterOptions(primitiveTypeConverterOptions);

      AnnotationScanner scanner =
          new AnnotationScanner(elementUtils, typeUtils, annotationDetector);
      Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Xml.class);

      for (Element element : elementsAnnotatedWith) {

        // Skip abstract classes
        if (element.getKind() == ElementKind.CLASS && element.getModifiers()
            .contains(Modifier.ABSTRACT)) {
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
    Set<String> primitiveTypeConverters = new HashSet<String>();

    if (optionsAsString != null && optionsAsString.length() > 0) {
      String[] options = optionsAsString.split(",");
      for (String o : options) {
        primitiveTypeConverters.add(o.trim());
      }
    }

    return primitiveTypeConverters;
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
