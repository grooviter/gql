package gql.exception

import groovy.transform.InheritConstructors
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.DataFetcher
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphQLException
import graphql.language.SourceLocation

/**
 * Exception adding i18n key to error extensions
 *
 * @since 0.3.0
 */
class I18nException extends GraphQLException implements GraphQLError {

  /**
   * This key could be used by front-end developers to
   * internationalize an error message
   *
   * @since 0.3.0
   */
  final String i18n

  /**
   * Builds a new {@link I18nException} with a general message and a
   * internationalization key
   *
   * @param message the plain error message
   * @param i18n the key to be used to internationalize the error
   * @since 0.3.0
   */
  I18nException(String message, String i18n) {
    super(message)

    this.i18n = i18n
  }

  @Override
  Map<String, Object> getExtensions() {
    return [i18n: i18n] as Map<String, Object>
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
