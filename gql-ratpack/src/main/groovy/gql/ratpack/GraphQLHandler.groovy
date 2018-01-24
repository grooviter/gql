package bond.handler

import static gql.DSL.executeAsync
import static ratpack.jackson.Jackson.json

import javax.inject.Inject
import graphql.ExecutionResult
import graphql.schema.GraphQLSchema
import ratpack.exec.Blocking
import ratpack.exec.Promise
import ratpack.exec.Downstream
import ratpack.handling.Context

/**
 * GraphQL endpoint. This handler will be exposing a given GraphQL
 * schema engine.
 *
 * It expects both query and variables to be sent as JSON. Then it
 * will parse it and handle it as a map
 *
 * @since 0.2.0
 */
class Handler implements ratpack.handling.Handler {

  /**
   * Instance of the schema this handler is exposing
   *
   * @since 0.2.0
   */
  @Inject
  GraphQLSchema schema

  @Override
  void handle(Context ctx) throws Exception {
    def payload = ctx.get(Map)
    def query = payload.query
    def params = payload.variables as Map<String,Object>

    Promise.async { Downstream downstream ->
      executeAsync(schema, "$query", params).thenApply { value ->
        downstream.success(value)
      }
    }.then { ExecutionResult result ->
      ctx.render(json(errors: result.errors, data: result.data))
    }
  }
}
