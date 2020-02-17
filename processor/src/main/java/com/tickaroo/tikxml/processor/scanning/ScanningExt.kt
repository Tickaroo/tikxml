package com.tickaroo.tikxml.processor.scanning

import com.tickaroo.tikxml.annotation.Xml
import javax.lang.model.element.TypeElement

fun TypeElement.getXmlElementName(): String =
  getAnnotation(Xml::class.java)?.name?.takeIf { xmlElementName -> xmlElementName.isNotBlank() }
    ?: simpleName.toString().decapitalize()