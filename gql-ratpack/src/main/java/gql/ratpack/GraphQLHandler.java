package gql.ratpack;

import static gql.ratpack.GraphQLHandlerUtil.executeGraphQL;
import static gql.ratpack.GraphQLHandlerUtil.renderGraphQL;
import static gql.ratpack.GraphQLHandlerUtil.renderGraphQLError;

import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import ratpack.handling.Context;
import ratpack.handling.Handler;

/**
 * GraphQL endpoint. This handler will be exposing a given GraphQL
 * schema engine.
 *
 * It expects both query and variables to be sent as JSON. Then it
 * will parse it and handle it as a map
 *
 * @since 0.2.0
 */
public class GraphQLHandler implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
      ctx
        .parse(Map.class)
        .onError(JsonParseException.class, renderGraphQLError(ctx))
        .flatMap(executeGraphQL(ctx))
        .then(renderGraphQL(ctx));
  }
}
