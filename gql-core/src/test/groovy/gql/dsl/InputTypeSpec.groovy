package gql.dsl

import gql.DSL
import graphql.ExecutionResult
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLSchema
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @since 0.1.4
 */
@SuppressWarnings('VariableName')
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
    ExecutionResult result =
      DSL.execute(schema, query, [filter: [from: 'me@somedomain.com', to: 'you@somedomain.com']])
    // end::inputTypeQueryExecution[]

    then: 'we should get what we want'
    result.data.result.first().subject == 'just this email here!'
  }

  @Unroll
  void 'create an invalid input with mandatory fields'() {
    given: 'the definition of an input and an output type'
    GraphQLInputObjectType MailFilterType = DSL.input('MailFilter') {
      field 'from', nonNull(GraphQLString)
      field 'to', nonNull(GraphQLString)
    }

    GraphQLOutputType MailResult = DSL.type('Mail') {
      field 'subject', GraphQLString
    }

    and: 'defining the schema to query'
    GraphQLSchema schema = DSL.schema {
      queries {
        field('searchByFilter') {
          type list(MailResult)

          argument('filter', MailFilterType) {
            description 'filters mails'
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
    def query = '''
    query QueryMail($filter: MailFilter) {
        result: searchByFilter(filter: $filter) {
           subject
        }
    }
    '''

    when: 'executing the query with the required parameters'
    ExecutionResult result =
      DSL.execute(schema, query, [filter: input])

    then: 'we should get execution errors'
    result.errors

    where: 'possible invalid inputs are'
    input << [
      [to: 'you@somedomain.com'],
      [from: 'me@somedomain.com'],
      [:],
    ]
  }

  void 'use default value in arguments'() {
    given: 'defining the schema to query'
    GraphQLSchema schema = DSL.schema {
      queries {
        field('sayHello') {
          type(GraphQLString)
          argument ('name',  GraphQLString) {
            defaultValue("UNKNOWN")
          }

          fetcher { DataFetchingEnvironment env ->
            String name = env.arguments.name

            return "Hello $name"
          }
        }
      }
    }

    and: 'the query'
    def query = '''
    {
        sayHello
    }
    '''

    when: 'executing the query with the required parameters'
    ExecutionResult result = DSL.execute(schema, query)

    then: 'we should get what we want'
    result.data.sayHello == 'Hello UNKNOWN'
  }

  @Ignore("pending issue in graphql-java")
  void 'use default value in arguments using variable reference'() {
    given: 'defining the schema to query'
    GraphQLSchema schema = DSL.schema {
      queries {
        field('sayHello') {
          type(GraphQLString)
          argument ('name',  GraphQLString) {
            defaultValue("UNKNOWN")
          }

          fetcher { DataFetchingEnvironment env ->
            String name = env.arguments.name

            return "Hello $name"
          }
        }
      }
    }

    and: 'the query'
    def query = '''
    query SayHelloQuery($name: String) {
        sayHello(name: $name)
    }
    '''

    when: 'executing the query with the required parameters'
    ExecutionResult result = DSL.execute(schema, query)

    then: 'we should get what we want'
    result.data.sayHello == 'Hello UNKNOWN'
  }
}
