package gql.dsl

import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation

/**
 * Builds an instance of {@link GraphQLError}
 *
 * @since 0.3.0
 */
class GraphQLErrorBuilder {
  /**
   * Simple implementation of a {@link GraphQLError}
   *
   * @since 0.3.0
   */
  GraphQLErrorImpl error = new GraphQLErrorImpl()

  /**
   * Sets custom error extensions. Extensions are a way of adding more
   * information to the current error.
   *
   * @param extensions custom properties of the current error
   * @return current builder instance
   * @since 0.3.0
   */
  GraphQLErrorBuilder extensions(Map<String, ?> extensions) {
    error = error.copyWith(extensions: extensions)
    return this
  }

  /**
   * Sets a human friendly message to explain the error
   *
   * @param message string message
   * @return current builder instance
   * @since 0.3.0
   */
  GraphQLErrorBuilder message(String message) {
    error = error.copyWith(message: message)
    return this
  }

  /**
   * Sets where the error comes from
   *
   * @param locations where to locate the error
   * @return current builder instance
   * @since 0.3.0
   */
  GraphQLErrorBuilder locations(List<SourceLocation> locations) {
    error = error.copyWith(locations: locations)
    return this
  }

  /**
   * Sets the type of the error
   *
   * @param errorType it should be an instance of {@link ErrorType}
   * @return current builder instance
   * @since 0.3.0
   */
  GraphQLErrorBuilder errorType(ErrorType errorType) {
    error = error.copyWith(errorType: errorType)
    return this
  }

  /**
   * Returns the resulting {@link GraphQLError}
   *
   * @return an instance of {@link GraphQLError}
   * @since 0.3.0
   */
  GraphQLError build() {
    return error
  }
}
