package gql.dsl

import groovy.transform.InheritConstructors
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphQLException
import graphql.language.SourceLocation
import gql.exception.CustomException

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
    DataFetcher<?> errorFn = { env -> throw new CustomException('type','No user present', 'error.not.present') } as DataFetcher

    return user ? dataFetcher : errorFn
  }
}
