package gql.dsl

import groovy.util.logging.Slf4j
import groovy.transform.Immutable
import graphql.ExecutionInput
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.execution.instrumentation.ChainedInstrumentation

/**
 * Builds a new {@link ExecutionInput} instance
 *
 * @since 0.3.0
 */
@Slf4j
class ExecutionBuilder {

  @Immutable(knownImmutableClasses = [ExecutionInput, Instrumentation])
  static class Result {
    ExecutionInput input
    Instrumentation instrumentation
  }

  /**
   * @since 0.3.0
   */
  ExecutionInput.Builder executionBuilder = ExecutionInput.newExecutionInput()
  Instrumentation instrumentation = new NoOpInstrumentation()

  /**
   * @param variables
   * @return
   * @since 0.3.0
   */
  ExecutionBuilder withVariables(Map<String, Object> variables) {
    executionBuilder.variables(variables ?: [:] as Map<String, Object>)
    return this
  }

  /**
   * @param query
   * @return
   * @since 0.3.0
   */
  ExecutionBuilder withQuery(String query) {
    executionBuilder.query(query)
    return this
  }

  /**
   * @param object
   * @return
   * @since 0.3.0
   */
  ExecutionBuilder withContext(Object context) {
    executionBuilder.context(context ?: [:])
    return this
  }

  /**
   * @param instrumentation
   * @return
   * @since 0.3.0
   */
  ExecutionBuilder withInstrumentation(Instrumentation... instrumentations) {
    instrumentation = new ChainedInstrumentation(instrumentations as List<Instrumentation>)
    return this
  }

  /**
   * @return
   * @since 0.3.0
   */
  ExecutionBuilder.Result build() {
    return new ExecutionBuilder.Result(
      input: executionBuilder.build(),
      instrumentation: instrumentation
    )
  }
}
