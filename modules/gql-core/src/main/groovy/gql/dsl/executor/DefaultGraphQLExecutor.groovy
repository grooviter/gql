package gql.dsl.executor

import gql.dsl.Builders
import gql.dsl.QueryBuilder
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import groovy.transform.TupleConstructor

import java.util.concurrent.CompletableFuture

/**
 * Default implementation of the {@link GraphQLExecutor}
 *
 * @see GraphQLExecutor
 * @since 0.4.0
 */
@TupleConstructor
class DefaultGraphQLExecutor implements GraphQLExecutor {

  GraphQL graphQL;

  @Override
  ExecutionResult execute(String query, Map<String, Object> arguments = [:]) {
    ExecutionInput.Builder builder = ExecutionInput.newExecutionInput().query(query);
    ExecutionInput executionInput = arguments ? builder.variables(arguments).build() : builder.build()

    return graphQL.execute(executionInput)
  }

  @Override
  ExecutionResult execute(@DelegatesTo(QueryBuilder) Closure queries) {
    return execute(Builders.buildQuery(queries), [:])
  }

  @Override
  CompletableFuture<ExecutionResult> executeAsync(String query, Map<String, Object> arguments = [:]) {
    ExecutionInput executionInput = ExecutionInput
      .newExecutionInput()
      .query(query)
      .variables(arguments)
      .build()

    return graphQL.executeAsync(executionInput)
  }

  @Override
  CompletableFuture<ExecutionResult> executeAsync(@DelegatesTo(QueryBuilder) Closure queries) {
    return executeAsync(Builders.buildQuery(queries), [:])
  }
}
