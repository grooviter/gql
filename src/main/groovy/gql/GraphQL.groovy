package gql

import asteroid.Local
import gql.ast.GraphQLImpl

/**
 * @since 0.1.0
 */
@Local(GraphQLImpl)
@interface GraphQL {

  /**
   * @since 0.1.0
   */
  String name() default ""

  /**
   * @since 0.1.0
   */
  String desc()
}
