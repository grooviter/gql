package gql.dsl

import graphql.schema.DataFetchingEnvironment
import spock.lang.Specification
import gql.DSL

class ExecutionSpec extends Specification {

  void 'execute query providing a custom context'() {
    given: 'a simple schema'
    def schema = DSL.schema {
      queries('helloQuery') {
        description'simple droid'
        field('hello') {
          description'name of the droid'
          type GraphQLString
          argument 'name', GraphQLString
          fetcher { DataFetchingEnvironment env ->
            def name = env.arguments.name
            def user = env?.context?.user

            user ? "You can pass $name" : "unauthorized"
          }
        }
      }
    }

    and: 'query'
    def query = '''
    query Hello($name: String) {
      hello(name: $name)
    }
    '''

    when: 'executing a query without a given context'
    def resultWithContext = DSL.execute(schema, query) {
      withContext(user: 'john')
      withVariables(name: 'Peter')
    }

    then: 'we should get unauthorized'
    resultWithContext.data.hello == 'You can pass Peter'

    when: 'executing a query without any context'
    def resultWithoutContext = DSL.execute(schema, query) {
      withVariables(name: 'Peter')
    }

    then: 'we should be allowed to pass'
    resultWithoutContext.data.hello == 'unauthorized'
  }

  void 'execute query providing a simple instrumentation'() {
    given: 'a simple schema'
    def schema = DSL.schema {
      queries('helloQuery') {
        description'simple droid'
        field('hello') {
          description'name of the droid'
          type GraphQLString
          argument 'name', GraphQLString
          fetcher { DataFetchingEnvironment env ->
            def name = env.arguments.name

            return "Hello $name"
          }
        }
      }
    }

    and: 'query'
    def query = '''
    query Hello($name: String) {
      hello(name: $name)
    }
    '''

    when: 'executing a query without a given context'
    def resultWithContext = DSL.execute(schema, query) {
      withContext(user: 'john')
      withVariables(name: 'Peter')
      withInstrumentation(new SecurityInstrumentation())
    }

    then: 'we should get unauthorized'
    resultWithContext.data.hello == 'Hello Peter'

    when: 'executing a query without any context'
    def resultWithoutContext = DSL.execute(schema, query) {
      withVariables(name: 'Peter')
      withInstrumentation(new SecurityInstrumentation())
    }
    def sampleError = resultWithoutContext.errors.first()

    then: 'we should be allowed to pass'
    sampleError.message == 'No user present'
    sampleError.extensions == [i18n: 'error.not.present']
  }
}
