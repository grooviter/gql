package gql.ratpack

import java.util.concurrent.CompletableFuture
import ratpack.handling.Context
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.execution.instrumentation.NoOpInstrumentation

import gql.exception.I18nException

class SecurityChecker extends NoOpInstrumentation {
  @Override
  DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher, InstrumentationFieldFetchParameters parameters) {
    Context context = parameters.environment.context as Context

    return context
      .header('Authorization')
      .map { dataFetcher }
      .orElse { env ->
        throw new I18nException('security', 'error.security.authorization')
      }
  }
}
