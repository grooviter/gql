package gql.ratpack;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static ratpack.jackson.Jackson.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import ratpack.exec.Promise;
import ratpack.exec.Upstream;
import ratpack.func.Action;
import ratpack.func.Function;
import ratpack.handling.Context;

/**
 * Common functions to be used in handlers
 *
 * @since 0.3.1
 */
public class GraphQLHandlerUtil {

  /**
   * Takes `a {@link JsonParseException} and renders it as JSON to the output
   *
   * @param ctx Ratpack's context
   * @return an [@link Action} rendering the error
   * @since 0.3.1
   */
  public static Action<JsonParseException> renderGraphQLError(Context ctx) {
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

  /**
   * Wraps the execution of the GraphQL fetcher and returns a function that
   * given a map it will return a {@link Promise} which will return
   * an {@link ExecutionResult}
   *
   * @param ctx Ratpack's context
   * @return a {@link Function}
   * @since 0.3.1
   */
  public static Function<Map, Promise<ExecutionResult>> executeGraphQL(Context ctx) {
    return (Map payload) -> {
      String query = payload.get("query").toString();

      @SuppressWarnings("unchecked")
      Map<String,Object> variables = (Map<String,Object>) payload
        .getOrDefault("variables", new HashMap<String, Object>());

      Upstream<ExecutionInput> upstreamInput = (downstream) -> {
        ExecutionInput input = ExecutionInput
          .newExecutionInput()
          .query(query)
          .context(ctx)
          .variables(variables)
          .build();

        CompletableFuture.supplyAsync(() -> input)
          .thenAccept(downstream::success);
      };

      Function<ExecutionInput, Promise<ExecutionResult>> fn = (executionInput -> {
        return Promise.async((downstream) -> {
          GraphQL graphQL = ctx.get(GraphQL.class);

          graphQL
            .executeAsync(executionInput)
            .thenAccept(downstream::success);
        });
      });

      return Promise
        .async(upstreamInput)
        .cache()
        .flatMap(fn);
    };
  }

  /**
   * Renders the fetcher result
   *
   * @param ctx Ratpack's ctx
   * @return an {@link Action} which returns an {@link ExecutionResult}
   * @since 0.3.1
   */
  public static Action<ExecutionResult> renderGraphQL(Context ctx) {
    return (ExecutionResult result) -> {
      Map<String,Object> resultMap = new HashMap<>();
      resultMap.put("errors", result.getErrors());
      resultMap.put("data", result.getData());

      ctx.render(json(resultMap));
    };
  }
}
