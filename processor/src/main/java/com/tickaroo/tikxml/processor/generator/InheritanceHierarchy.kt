package com.tickaroo.tikxml.processor.generator

import com.google.common.graph.GraphBuilder
import com.tickaroo.tikxml.processor.field.PolymorphicTypeElementNameMatcher
import com.tickaroo.tikxml.processor.utils.hasSuperClass
import java.util.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 *
 * @author Hannes Dorfmann
 */
fun orderByInheritanceHierarchy(typeElementNameMatcher: List<PolymorphicTypeElementNameMatcher>, elementUtils: Elements, typeUtils: Types): List<PolymorphicTypeElementNameMatcher> {


    val matcherMap = HashMap<String, PolymorphicTypeElementNameMatcher>()
    typeElementNameMatcher.forEach { matcherMap.put(it.type.toString(), it) }

    val graph = GraphBuilder.directed().build<TypeElement>()

    fun addSuperClassToGraph(current: TypeElement) {
        if (current.hasSuperClass()) {
            val superClass = typeUtils.asElement(current.superclass) as TypeElement
            if (superClass.qualifiedName.toString() != "java.lang.Object" && superClass.qualifiedName.toString() != Any::class.qualifiedName.toString()) {
                graph.addNode(superClass)
                graph.putEdge(superClass, current)
                addSuperClassToGraph(superClass)
            }
        }
    }

    fun addInterfacesToGraph(element: TypeElement) {
        if (element.interfaces != null && !element.interfaces.isEmpty()) {
            element.interfaces.forEach {
                val interfaceElement = typeUtils.asElement(it) as TypeElement
                graph.addNode(interfaceElement)
                graph.putEdge(interfaceElement, element)
                addInterfacesToGraph(interfaceElement)
                addSuperClassToGraph(interfaceElement)
            }
        }
    }

    typeElementNameMatcher.forEach { matcher ->
        val current = elementUtils.getTypeElement(matcher.type.toString())
        graph.addNode(current)
        addSuperClassToGraph(current)
        addInterfacesToGraph(current)
    }


    val hierarchy = LinkedHashSet<PolymorphicTypeElementNameMatcher>()
    fun addMatcherToHierarchyResult(top: TypeElement) {
        val matcher = matcherMap[top.qualifiedName.toString()]
        if (matcher != null) {
            hierarchy.add(matcher)
        }

        graph.successors(top).forEach { addMatcherToHierarchyResult(it) }
    }

    graph.nodes().filter {
        val predecessors = graph.predecessors(it)
        predecessors == null || predecessors.isEmpty()
    }.forEach {
        addMatcherToHierarchyResult(it)
    }

    return hierarchy.toList().asReversed()
}
