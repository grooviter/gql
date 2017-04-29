package gql.dsl

import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType

/**
 * Any builder implementing this trait will be capable of
 * converting any {@link GraphQLType} to a {@link GraphQLNonNull},
 * meaning that that type is mandatory
 *
 * @since 0.1.5
 */
trait NonNullAware {

  /**
   * Converts any given {@link GraphQLType} to a
   * required type ({@link GraphQLNonNull})
   *
   * @param type Type that wants to be defined as a {@link GraphQLNonNull}
   * @return an instance of {@link GraphQLNonNull}
   * @since 0.1.5
   */
  static GraphQLNonNull nonNull(GraphQLType type) {
    return GraphQLNonNull.nonNull(type)
  }
}
