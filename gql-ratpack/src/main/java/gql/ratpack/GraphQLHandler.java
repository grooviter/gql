package gql.ratpack;

import static java.util.Arrays.asList;
import static gql.DSL.executeAsync;
import static ratpack.jackson.Jackson.json;
import static com.google.common.collect.ImmutableMap.of;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;
import ratpack.func.Action;
import ratpack.func.Function;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.exec.Upstream;
import ratpack.exec.Downstream;
import ratpack.handling.Handler;
import ratpack.handling.Context;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;

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

  private Action<JsonParseException> renderGraphQLError(Context ctx) {
    return (JsonParseException error) -> {
      JsonLocation location = error.getLocation();
      Collection<Map> locations =
        asList(of("line",
                  location.getLineNr(),
                  "column",
                  location.getColumnNr()));

        ctx.render(json(of("errors", of("message",
                                        "JsonParseException: " + error.getOriginalMessage(),
                                        "locations",
                                        locations))));
    };
  }

  @SuppressWarnings("unchecked")
  private Function<Map, Promise<ExecutionResult>> executeGraphQL(Context ctx) {
    return (Map payload) -> {
      String query = payload.get("query").toString();
      Map<String,Object> params = (Map<String,Object>) payload.get("variables");

      Upstream<ExecutionResult> upstream = (downstream) -> {
        executeAsync(ctx.get(GraphQLSchema.class), query, params)
        .thenAccept(value -> downstream.success(value));
      };

      return Promise.async(upstream);
    };
  }

  private Action<ExecutionResult> renderGraphQL(Context ctx) {
    return (ExecutionResult result) -> {
      Map<String,Object> resultMap = new HashMap<>();
      resultMap.put("errors", result.getErrors());
      resultMap.put("data", result.getData());

      ctx.render(json(resultMap));
    };
  }
}
