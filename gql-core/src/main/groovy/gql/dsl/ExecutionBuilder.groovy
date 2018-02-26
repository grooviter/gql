package gql.dsl

import groovy.util.logging.Slf4j
import graphql.ExecutionInput

/**
 * Builds a new {@link ExecutionInput} instance
 *
 * @since 0.3.0
 */
@Slf4j
class ExecutionBuilder {

  /**
   * @since 0.3.0
   */
  ExecutionInput.Builder executionBuilder = ExecutionInput.newExecutionInput()

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
   * @return
   * @since 0.3.0
   */
  ExecutionInput build() {
    return executionBuilder.build()
  }
}
