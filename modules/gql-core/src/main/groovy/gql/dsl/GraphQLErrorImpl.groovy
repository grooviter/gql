package gql.dsl

import groovy.transform.Immutable
import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

/**
 * Simple implementation of a {@link GraphQLError}
 *
 * @since 0.3.0
 */
@Immutable(copyWith = true)
class GraphQLErrorImpl implements GraphQLError {

  /**
   * Extra information of the current error
   *
   * @since 0.3.0
   */
  Map<String,Object> extensions

  /**
   * Message explaining the error
   *
   * @since 0.3.0
   */
  String message

  /**
   * Information about where to find the source of the error
   *
   * @since 0.3.0
   */
  List<SourceLocation> locations

  /**
   * An instance of type {@link ErrorType} to establish the type of
   * the error
   *
   * @since 0.3.0
   */
  ErrorType errorType
}
