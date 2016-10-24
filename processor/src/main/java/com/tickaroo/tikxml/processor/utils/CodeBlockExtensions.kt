package com.tickaroo.tikxml.processor.utils

import com.squareup.javapoet.CodeBlock
import com.tickaroo.tikxml.processor.field.access.FieldAccessResolver
import com.tickaroo.tikxml.processor.generator.CodeGeneratorHelper

/**
 * this generates the code that adds a if check if (value != null)
 * @author Hannes Dorfmann
 */
inline fun CodeBlock.Builder.ifValueNotNullCheck(accessResolver: FieldAccessResolver, block: CodeBlock.Builder.() -> Unit) =
        beginControlFlow("if (${accessResolver.resolveGetterForWritingXml()} != null)")
                .apply(block)
                .endControlFlow()

inline fun CodeBlock.Builder.endXmlElement() = addStatement("${CodeGeneratorHelper.writerParam}.endElement()")