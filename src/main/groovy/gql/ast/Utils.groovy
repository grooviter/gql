package gql.ast

import asteroid.A

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLFieldDefinition

import gql.Field

@SuppressWarnings('DuplicateStringLiteral')
class Utils {

  static final String METHOD_NAME_NAME = 'name'
  static final String METHOD_DESC_NAME = 'description'
  static final String METHOD_BUILD_NAME = 'build'

  /**
   * A limited and typed version of Groovy's inject function. It
   * should return an object of the same type as the seed provided.
   *
   * @param seed the initial value
   * @param list the list we would like to
   * @param process {@link Closure} receiving two params <A> and <B> types
   * @return a result of type <A>
   * @since 0.1.0
   */
  static <A,B> A inject(A seed, List<B> list, Closure<A> process) {
    return (A) list.inject(seed, process)
  }

  static MethodCallExpression buildFieldDefinition(final FieldNode fieldInfo) {
    // Checking if the field is annotated with @Field
    AnnotationNode fieldAnnotation = getAnnotationFrom(fieldInfo, Field)

    // Getting name and description
    String fieldName = fieldInfo.name
    String fieldDescription = extractDescFromAnnotation(fieldAnnotation) ?: fieldName

    // newFieldDefinition().name(fieldName).description(fieldDescription)
    MethodCallExpression newFieldDefinitionX =
      A.EXPR.callX(A.EXPR.callX(A.EXPR.staticCallX(GraphQLFieldDefinition, 'newFieldDefinition'),
                                METHOD_NAME_NAME,
                                A.EXPR.constX(fieldName)),
                   METHOD_DESC_NAME,
                   A.EXPR.constX(fieldDescription))

      // ...description(...).type(GraphQLString)
      String type = matchFieldType(fieldInfo.type)

      PropertyExpression matchedTypeX = A.EXPR.propX(A.EXPR.classX(Scalars), A.EXPR.constX(type))
      MethodCallExpression fieldTypeX = A.EXPR.callX(newFieldDefinitionX, 'type', matchedTypeX)

      // ...type(GraphQLString).build()
      return A.EXPR.callX(fieldTypeX, METHOD_BUILD_NAME)
  }

  static String matchFieldType(ClassNode fieldClass) {
    switch (fieldClass.name) {
      case String.name:  return "GraphQLString"
      case Integer.name: return "GraphQLInt"
      case Boolean.name: return "GraphQLBoolean"
      case Float.name:   return "GraphQLFloat"

      default:
      return "GraphQLString"
    }
  }

  static AnnotationNode getAnnotationFrom(final AnnotatedNode annotatedNode, final Class annotationType) {
    ClassNode annotationClassNode = A.NODES.clazz(annotationType).build()

    return annotatedNode
    .getAnnotations(annotationClassNode)
    .find()
  }

  static String extractNameFromAnnotation(final AnnotationNode annotation, String defaultValue) {
    return A.UTIL.ANNOTATION.get(annotation, 'name', String) ?: defaultValue
  }

  static String extractDescFromAnnotation(final AnnotationNode annotation) {
    return annotation ? A.UTIL.ANNOTATION.get(annotation, 'desc', String) : ""
  }

  static MethodCallExpression newObjectTypeWithNameAndDesc(String name, String desc) {
    return A.EXPR.callX(
      A.EXPR.callX(A.EXPR.staticCallX(GraphQLObjectType, 'newObject'),
                   METHOD_NAME_NAME,
                   A.EXPR.constX(name)),
      METHOD_DESC_NAME,
      A.EXPR.constX(desc))
  }
}
