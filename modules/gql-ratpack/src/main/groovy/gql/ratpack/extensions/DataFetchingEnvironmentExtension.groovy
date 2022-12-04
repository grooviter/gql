package gql.ratpack.extensions

import gql.ratpack.GraphQLHandlerUtil
import graphql.schema.DataFetchingEnvironment
import ratpack.handling.Context

class DataFetchingEnvironmentExtension {
  static Context getRatpackContext(DataFetchingEnvironment environment) {
    return environment.graphQlContext.get(GraphQLHandlerUtil.RATPACK_CONTEXT_KEY) as Context
  }
}
