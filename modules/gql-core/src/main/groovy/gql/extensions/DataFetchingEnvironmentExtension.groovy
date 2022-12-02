package gql.extensions

import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment

/**
 * Extension functions attached to {@link DataFetchingEnvironment} instances
 *
 * @since 1.0.0
 */
class DataFetchingEnvironmentExtension {
  /**
   * Makes {@link GraphQLContext} stored values accessible as if you are using a map
   *
   * @environment instance of type {@link DataFetchingEnvironment}
   * @return an instance of type {@link java.util.Map}
   * @since 1.0.0
   */
  static Map<String,?> getContextAsMap(DataFetchingEnvironment environment) {
    return new GraphQLContextMap(environment.graphQlContext)
  }
}
