package gql.dsl

import gql.DSL
import gql.test.util.Queries
import graphql.ExecutionResult
import graphql.GraphQLException
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import spock.lang.Specification
import spock.lang.Unroll
import java.util.concurrent.CompletableFuture

/**
 * @since 0.1.9
 */
class AsyncExecutionSpec extends Specification {

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
    // tag::inputTypeArgument[]
    GraphQLSchema schema = DSL.schema {
      queries {
        field('searchByFilter') {
          type list(MailResult)

          argument 'filter', MailFilterType // --> input type

          fetcher { DataFetchingEnvironment env ->
            assert env.arguments.filter.from == 'me@somedomain.com'
            assert env.arguments.filter.to == 'you@somedomain.com'

            return [[subject: 'just this email here!']]
          }
        }
      }
    }
    // end::inputTypeArgument[]
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
    CompletableFuture<ExecutionResult> future =
      DSL.executeAsync(schema, query, [filter: [from: 'me@somedomain.com', to: 'you@somedomain.com']])
    // end::inputTypeQueryExecution[]

    then: 'we should get what we want'
    future.get().data.result.first().subject == 'just this email here!'
  }

  void 'execute static typed query'() {
    given: 'building the type'
    GraphQLObjectType filmType = DSL.type('film') {
      field 'title', GraphQLString
      field 'year', GraphQLString
      field 'bond', GraphQLString
    }

    and: 'building the schema'
    // tag::findByYearSchema[]
    GraphQLSchema schema = DSL.schema {
      queries {
        field('byYear') {
          type filmType
          fetcher Queries.&findByYear
          argument 'year', GraphQLString
        }
      }
    }
    // end::findByYearSchema[]

    when: 'executing a queryString against that schema'
    // tag::staticQueryChecked[]
    CompletableFuture<ExecutionResult> future = DSL.executeAsync(schema) {
      query('byYear', [year: '1962']) { // <1>
        returns(Film) { // <2>
          title
          year
        }

        alias 'first' // <3>
      }
    }
    ExecutionResult result = future.get()
    // end::staticQueryChecked[]

    then: 'we should get the expected values'
    result.data.first.title == 'DR. NO'
    result.data.first.year == '1962'


    when:
    // tag::staticQueryUnchecked[]
    CompletableFuture<ExecutionResult> future2 = DSL.executeAsync(schema) {
      query('byYear', [year: '2015']) { // <1>
        returns { // <2>
          title
          year
          bond
        }

        alias 'last' // <3>
      }
    }
    ExecutionResult result2 = future2.get()
    // end::staticQueryUnchecked[]

    then:
    result2.data.last.title == 'SPECTRE'
    result2.data.last.year == '2015'
    result2.data.last.bond == 'Daniel Craig'
  }

  class Film {
    String title
    String bond
    Integer year
  }
}
