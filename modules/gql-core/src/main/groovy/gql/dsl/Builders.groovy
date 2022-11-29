package gql.dsl

/**
 * This utility class helps to avoid repeating boilerplate code when using
 * the different builders in GQL internals
 *
 * @since 0.4.0
 */
class Builders {

  /**
   * Builds a query {@link String} from the closure following the {@link QueryBuilder} dsl
   *
   * @param builder the closure following the {@link QueryBuilder} dsl
   * @since 0.4.0
   */
  static String buildQuery(@DelegatesTo(QueryBuilder) Closure builder) {
    Closure<QueryBuilder> clos = builder.clone() as Closure<QueryBuilder>
    QueryBuilder builderSource = new QueryBuilder()
    QueryBuilder builderResult = builderSource.with(clos) ?: builderSource

    return builderResult.build()
  }

  /**
   * Creates
   *
   * @param options the closure following the {@link ExecutionBuilder} dsl
   * @return an {@link ExecutionBuilder.Result} instance
   * @since 0.4.0
   */
  static ExecutionBuilder.Result buildExecutionBuilderResult(@DelegatesTo(ExecutionBuilder) Closure options) {
    Closure<ExecutionBuilder> clos = options.clone() as Closure<ExecutionBuilder>
    ExecutionBuilder builderSource = new ExecutionBuilder()
    ExecutionBuilder builderResult = builderSource.with(clos) ?: builderSource
    ExecutionBuilder.Result result = builderResult.build()

    return result
  }
}
