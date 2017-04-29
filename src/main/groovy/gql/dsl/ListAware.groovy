package gql.dsl

import graphql.schema.GraphQLList
import graphql.schema.GraphQLType

/**
 * Any builder implementing this trait will be capable of
 * converting any {@link GraphQLType} to a {@link GraphQLList}
 *
 * @since 0.1.5
 */
trait ListAware {

  /**
   * Converts any given {@link GraphQLType} to a
   * {@link GraphQLList}
   *
   * @param type Type that wants to be defined as a {@link GraphQLList}
   * @return an instance of {@link GraphQLList}
   * @since 0.1.5
   */
  static GraphQLList list(GraphQLType type) {
    return GraphQLList.list(type)
  }
}
