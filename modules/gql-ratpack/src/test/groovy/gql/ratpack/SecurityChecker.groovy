package gql.ratpack

import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimpleInstrumentation
import ratpack.handling.Context
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters

import gql.DSL

class SecurityChecker extends SimpleInstrumentation {
  @Override
  DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters, InstrumentationState state) {
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
