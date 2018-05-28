package gql.ratpack;

import static com.google.common.collect.ImmutableMap.of;
import static gql.DSL.executeAsync;
import static java.util.Arrays.asList;
import static ratpack.jackson.Jackson.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import gql.dsl.ExecutionBuilder;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.NoOpInstrumentation;
import graphql.schema.GraphQLSchema;
import groovy.lang.Closure;
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
   * Takes a {@link JsonParseException} and renders it as JSON to the output
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
      Map<String,Object> variables = (Map<String,Object>) payload.get("variables");

      Upstream<ExecutionResult> upstream = (downstream) -> {
        GraphQLSchema schema = ctx.get(GraphQLSchema.class);
        Instrumentation instrumentation = ctx
          .maybeGet(Instrumentation.class)
          .orElse(new NoOpInstrumentation());

        CompletableFuture<ExecutionResult> result =
          executeAsync(schema, query, createExecutionBuilder(ctx, variables, instrumentation));

        result.thenAccept(downstream::success);
      };

      return Promise.async(upstream);
    };
  }

  /**
   * Prepares the GraphQL execution context
   *
   * @param ctx Ratpack's context
   * @param variables query variables
   * @param instrumentation GraphQL {@link Instrumentation} instance
   * @return a {@link Closure} which once execute will return an {@link ExecutionBuilder}
   * @since 0.3.1
   */
  public static Closure<ExecutionBuilder> createExecutionBuilder(Context ctx, Map<String,Object> variables, Instrumentation instrumentation) {
    return new Closure<ExecutionBuilder>(null) {
      public ExecutionBuilder doCall(Object o) {
        return new ExecutionBuilder()
          .withContext(ctx)
          .withVariables(variables)
          .withInstrumentation(instrumentation);
      }
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
