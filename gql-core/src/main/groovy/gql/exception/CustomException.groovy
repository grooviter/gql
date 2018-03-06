package gql.exception

import groovy.transform.InheritConstructors
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.NoOpInstrumentation
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphQLException
import graphql.language.SourceLocation

class CustomException extends GraphQLException implements GraphQLError {

  final String i18n
  final String type

  CustomException(String type, String message, String i18n) {
    super(message)

    this.type = type
    this.i18n = i18n
  }


  @Override
  Map<String, Object> getExtensions() {
    return [type: type, i18n: i18n] as Map<String, Object>
  }

  @Override
  List<SourceLocation> getLocations() {
    return null
  }

  @Override
  ErrorType getErrorType() {
    return null
  }
}
