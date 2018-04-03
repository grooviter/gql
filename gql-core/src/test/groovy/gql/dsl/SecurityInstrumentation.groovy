package gql.dsl

import gql.DSL
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.language.SourceLocation
import graphql.execution.ExecutionPath
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.GraphQLError

/**
 * Example of instrumentation, this time used to check whether the user can invoke
 * a certain data fetcher or not
 *
 * @since 0.3.0
 */
class SecurityInstrumentation extends NoOpInstrumentation {
  @Override
  DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters) {
    String user = parameters.environment?.context?.user?.toString()

    if (user) {
      return dataFetcher
    }

    SourceLocation sourceLocation = parameters
      .getEnvironment()
      .getFields()
      .find()
      .getSourceLocation()

    ExecutionPath path = parameters
      .getEnvironment()
      .getFieldTypeInfo()
      .getPath()

    GraphQLError error = DSL.error {
      message 'No user present'
      extensions(i18n:'error.not.present')
      locations([sourceLocation])
    }

    parameters
      .getExecutionContext()
      .addError(error as graphql.GraphQLError, path)

    return { env -> } as DataFetcher
  }
}
