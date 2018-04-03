package gql.ratpack

import ratpack.handling.Context
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters

import gql.DSL

class SecurityChecker extends NoOpInstrumentation {
  @Override
  DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters) {
    Context context = parameters.environment.context as Context

    return context
      .header('Authorization')
      .map { dataFetcher }
      .orElse(DSL.errorFetcher(parameters) {
        message 'security'
        extensions(i18n: 'error.security.authorization')
      })
  }
}
