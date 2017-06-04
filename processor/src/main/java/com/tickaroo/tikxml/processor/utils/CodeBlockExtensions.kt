package com.tickaroo.tikxml.processor.utils

import com.squareup.javapoet.CodeBlock
import com.tickaroo.tikxml.processor.field.Field
import com.tickaroo.tikxml.processor.generator.CodeGeneratorHelper

/**
 * this generates the code that adds a if check if (value != null)
 * @author Hannes Dorfmann
 */
inline fun CodeBlock.Builder.ifValueNotNullCheck(field: Field, block: CodeBlock.Builder.() -> Unit) =
        if (field.element.asType().isPrimitive())
            apply(block)
        else
            beginControlFlow("if (${field.accessResolver.resolveGetterForWritingXml()} != null)")
                    .apply(block)
                    .endControlFlow()

fun CodeBlock.Builder.endXmlElement() = addStatement("${CodeGeneratorHelper.writerParam}.endElement()")