package gql.dsl

import gql.DSL
import graphql.execution.ResultPath
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.GraphQLError

/**
 * Example of instrumentation, this time used to check whether the user can invoke
 * a certain data fetcher or not
 *
 * @since 0.3.0
 */
// tag::instrumentation[]
class SecurityInstrumentation extends SimpleInstrumentation {
  @Override
  DataFetcher<?> instrumentDataFetcher(
    DataFetcher<?> dataFetcher,
    InstrumentationFieldFetchParameters parameters,
    InstrumentationState state
  ) {
    String user = parameters.environment.contextAsMap.user?.toString()

    if (user) {
      return dataFetcher
    }

    ResultPath path = parameters
      .getEnvironment()
      .executionStepInfo
      .getPath()

    GraphQLError error = DSL.error {
      message 'No user present'
      extensions(i18n:'error.not.present')
    }

    parameters
      .getExecutionContext()
      .addError(error, path)

    return { env -> } as DataFetcher
  }
}
// end::instrumentation[]
