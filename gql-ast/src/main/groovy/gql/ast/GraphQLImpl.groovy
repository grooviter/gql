package gql.ast

import asteroid.A
import asteroid.Phase
import asteroid.AbstractLocalTransformation

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.expr.MethodCallExpression

import graphql.schema.GraphQLObjectType
import gql.GraphQL

/**
 * Creates a method <b>getGraphQLDefinition</b> in the annotated type
 * which contains the type's translation to GraphQL schema.
 *
 * @since 0.1.0
 */
@Phase(Phase.LOCAL.SEMANTIC_ANALYSIS)
class GraphQLImpl extends AbstractLocalTransformation<GraphQL, ClassNode> {

  static final String METHOD_GENERATED_NAME = 'getGraphQLDefinition'
  static final String METHOD_BUILD_NAME = 'build'

  @Override
  void doVisit(final AnnotationNode annotation, final ClassNode node) {
    // Building type
    ReturnStatement codeStmt = GraphQLImpl.buildGraphQLCode(annotation, node)
    MethodNode graphQLMethod = GraphQLImpl.buildGraphQLMethod(codeStmt)

    // Adding GraphQL info method to class
    A.UTIL.CLASS.addMethodIfNotPresent(node, graphQLMethod)
  }

  static ReturnStatement buildGraphQLCode(final AnnotationNode annotation, final ClassNode annotatedNode) {
    final List<FieldNode> fields = annotatedNode.fields
    // Getting type's name and description
    String name = Utils.extractNameFromAnnotation(annotation, annotatedNode.nameWithoutPackage)
    String desc = Utils.extractDescFromAnnotation(annotation)

    // GraphQLObjectType.newObject().name(name).desc(desc)
    MethodCallExpression newObjectX = Utils.newObjectTypeWithNameAndDesc(name, desc)

    // builder.field(...).field(...)
    MethodCallExpression fullTypeX =
      Utils.inject(newObjectX, fields) { MethodCallExpression expr, FieldNode field ->
        return A.EXPR.callX(expr, 'field', Utils.buildFieldDefinition(field))
      }

      // ...field(...).build()
      MethodCallExpression buildX = A.EXPR.callX(fullTypeX, METHOD_BUILD_NAME)

      // return builder.build()
      return A.STMT.returnS(buildX)
  }

  /**
   * @param codeStmt
   * @return
   * @since 0.1.0
   */
  static MethodNode buildGraphQLMethod(final ReturnStatement codeStmt) {
    return A.NODES.method(METHOD_GENERATED_NAME)
      .modifiers(A.ACC.ACC_PUBLIC | A.ACC.ACC_STATIC)
      .returnType(GraphQLObjectType)
      .code(codeStmt)
      .build()
  }
}
