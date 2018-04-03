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

  /**
   * Gathers data required to execute queries
   *
   * @since 0.3.0
   */
  @Immutable(knownImmutableClasses = [ExecutionInput, Instrumentation])
  static class Result {
    ExecutionInput input
    Instrumentation instrumentation
  }

  /*
   * Instance of type {@link ExecutionInput.Builder}
   *
   * @since 0.3.0
   */
  ExecutionInput.Builder executionBuilder = ExecutionInput.newExecutionInput()

  /*
   * Instance of type {@link Instrumentation}
   *
   * @since 0.3.0
   */
  Instrumentation instrumentation = new NoOpInstrumentation()

  /**
   * Sets variables to be used when executing the underlying query
   *
   * @param variables query variables
   * @return the current builder instance
   * @since 0.3.0
   */
  ExecutionBuilder withVariables(Map<String, Object> variables) {
    executionBuilder.variables(variables ?: [:] as Map<String, Object>)
    return this
  }

  /**
   * Sets the query to be executed
   *
   * @param query query to be executed
   * @return the current builder instance
   * @since 0.3.0
   */
  ExecutionBuilder withQuery(String query) {
    executionBuilder.query(query)
    return this
  }

  /**
   * Sets the context to be used when executing the underlying query
   *
   * @param object context used when executing the query
   * @return the current builder instance
   * @since 0.3.0
   */
  ExecutionBuilder withContext(Object context) {
    executionBuilder.context(context ?: [:])
    return this
  }

  /**
   * Adds instrumentation implementations to current execution
   *
   * @param instrumentation array of instrumentation instances
   * @return the current builder instance
   * @since 0.3.0
   */
  ExecutionBuilder withInstrumentation(Instrumentation... instrumentations) {
    instrumentation = new ChainedInstrumentation(instrumentations as List<Instrumentation>)
    return this
  }

  /**
   * Adds instrumentation implementations to current execution
   *
   * @param instrumentation a list of instrumentation instances
   * @return the current builder instance
   * @since 0.3.0
   */
  ExecutionBuilder withInstrumentation(List<Instrumentation> instrumentations) {
    instrumentation = new ChainedInstrumentation(instrumentations)
    return this
  }

  /**
   * Builds and returns the configured instance of {@link ExecutionBuilder}
   *
   * @return an instance of {@link ExecutionBuilder}
   * @since 0.3.0
   */
  ExecutionBuilder.Result build() {
    return new ExecutionBuilder.Result(
      input: executionBuilder.build(),
      instrumentation: instrumentation,
    )
  }
}
