package gql.dsl

import gql.DSL
import graphql.ExecutionResult
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLSchema
import spock.lang.Specification

/**
 * @since 0.1.4
 */
class InputTypeSpec extends Specification {

  void 'create an simple input type'() {
    given: 'the definition of an input and an output type'
    // tag::inputTypeDeclaration[]
    GraphQLInputObjectType MailFilterType = DSL.input('MailFilter') {
      field 'from', GraphQLString
      field 'to', GraphQLString
    }
    // end::inputTypeDeclaration[]

    GraphQLOutputType MailResult = DSL.type('Mail') {
      field 'subject', GraphQLString
    }

    and: 'defining the schema to query'
    GraphQLSchema schema = DSL.schema {
      query('Queries') {
        field('searchByFilter') {
          type list(MailResult)

          argument('filter') {
            type MailFilterType
          }

          fetcher { DataFetchingEnvironment env ->
            assert env.arguments.filter.from == 'me@somedomain.com'
            assert env.arguments.filter.to == 'you@somedomain.com'

            return [[subject: 'just this email here!']]
          }
        }
      }
    }

    and: 'the query'
    // tag::inputTypeQuery[]
    def query = '''
    query QueryMail($filter: MailFilter) {
        result: searchByFilter(filter: $filter) {
           subject
        }
    }
    '''
    // end::inputTypeQuery[]

    when: 'executing the query with the required parameters'
    // tag::inputTypeQueryExecution[]
    ExecutionResult result =
      DSL.execute(schema, query, [filter: [from: 'me@somedomain.com', to: 'you@somedomain.com']])
    // end::inputTypeQueryExecution[]

    then: 'we should get what we want'
    result.data.result.first().subject == 'just this email here!'
  }
}