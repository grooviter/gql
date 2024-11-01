package gql.dsl.executor

import gql.dsl.MutationBuilder
import gql.dsl.QueryBuilder
import graphql.ExecutionResult
import graphql.GraphQL

import java.util.concurrent.CompletableFuture

/**
 * Instances implementing this interface MUST reuse a {@link graphql.GraphQL} instance
 * in order to improve performance when executing GraphQL operations.
 * @since 0.4.0
 */
interface GraphQLExecutor {

  /**
   * Executes a GraphQL query passing some arguments
   *
   * @param query the GraphQL query as a {@link String}
   * @param a simple {@link Map} containing query argument values
   * @return an {@link ExecutionResult} containing the result of the query
   * @since 0.4.0
   */
  ExecutionResult execute(String query, Map<String,Object> arguments)

/**
 * Executes a GraphQL query with no arguments
 *
 * @param query the GraphQL query as a {@link String}
 * @return an {@link ExecutionResult} containing the result of the query
 * @since 0.4.0
 */
  ExecutionResult execute(String query)

  /**
   * Executes a query built using a DSL following the {@link gql.dsl.QueryBuilder}
   * conventions
   *
   * @param queries a closure following the {@link gql.dsl.QueryBuilder} dsl
   * @return an {@link ExecutionResult} containing the result of the query
   * @since 0.4.0
   */
  ExecutionResult execute(@DelegatesTo(QueryBuilder) Closure queries)

  /**
   * Executes a mutation built using a DSL following the {@link gql.dsl.MutationBuilder}
   * conventions
   *
   * @param mutation a closure following the {@link gql.dsl.MutationBuilder} dsl
   * @return an {@link ExecutionResult} containing the result of the query
   * @since 1.1.0
   */
  ExecutionResult executeMutation(@DelegatesTo(MutationBuilder) Closure queries)

  /**
   * Executes a GraphQL query asynchronously passing some arguments
   *
   * @param query the GraphQL query as a {@link String}
   * @param a simple {@link Map} containing query argument values
   * @return an {@link CompletableFuture} containing the result of the query
   * @since 0.4.0
   */
  CompletableFuture<ExecutionResult> executeAsync(String query, Map<String,Object> arguments)

  /**
   * Executes a GraphQL query asynchronously with no arguments
   *
   * @param query the GraphQL query as a {@link String}
   * @return an {@link CompletableFuture} containing the result of the query
   * @since 0.4.0
   */
  CompletableFuture<ExecutionResult> executeAsync(String query)

  /**
   * Executes a query asynchronously using a DSL following the {@link QueryBuilder}
   * conventions
   *
   * @param queries a closure following the {@link QueryBuilder} dsl
   * @return an {@link CompletableFuture} containing the result of the query
   * @since 0.4.0
   */
  CompletableFuture<ExecutionResult> executeAsync(@DelegatesTo(QueryBuilder) Closure queries)

/**
 * Executes a query asynchronously using a DSL following the {@link MutationBuilder}
 * conventions
 *
 * @param mutation a closure following the {@link MutationBuilder} dsl
 * @return an {@link CompletableFuture} containing the result of the query
 * @since 0.4.0
 */
  CompletableFuture<ExecutionResult> executeAsyncMutation(@DelegatesTo(MutationBuilder) Closure mutation)

  /**
   * Returns the underlying {@link GraphQL} instance used to execute queries
   *
   * @return an instance of type {@link GraphQL}
   * @since 0.4.0
   */
  GraphQL getGraphQL()
}
