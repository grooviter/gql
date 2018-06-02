package gql.ratpack

import gql.DSL
import graphql.GraphQL
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.GraphQLSchema
import ratpack.jackson.JsonRender
import ratpack.registry.Registry
import ratpack.test.handling.RequestFixture
import spock.lang.Specification

class GraphQLHandlerSpec extends Specification {

  void 'execute a simple query'() {
    given: 'the handler and the schema'
    def handler = new GraphQLHandler()
    def schema = DSL.schema {
      queries('Queries') {
        field('hello') {
          type GraphQLString
          staticValue('world!')
        }
      }
    }

    and: 'setting the schema in the registry'
      def requestFixture = RequestFixture
      .requestFixture()
      .registry { r ->
        r.add(GraphQL, GraphQL.newGraphQL(schema).build())
      }

    when: 'executing the query against the handler'
    def result = requestFixture
      .body('{ "query": "{ hello }" }', 'application/json')
      .handle(handler)

    then: 'no errors should be found'
    !result.rendered(JsonRender).object.errors

    and: 'result data should be received'
    result.rendered(JsonRender).object.data == [hello: 'world!']
  }

  void 'execute a query with arguments'() {
    given: 'the handler, the schema, and the body'
    def handler = new GraphQLHandler()
    def schema = DSL.schema {
      queries('Queries') {
        field('echoName') {
          type GraphQLString
          argument 'name', GraphQLString
          fetcher { env -> "Hello ${env.arguments.name}" }
        }
      }
    }
    def body = '''{
      "query": "query EchoMyName($name: String) { \
         echoName(name: $name) \
      }",
      "variables": {
        "name": "John"
      }
    }
    '''

    and: 'setting the schema in the registry'
      def requestFixture = RequestFixture
      .requestFixture()
      .registry { r ->
        r.add(GraphQL, GraphQL.newGraphQL(schema).build())
    }

    when: 'executing the query against the handler'
    def result = requestFixture
      .body(body, 'application/json')
      .handle(handler)

    then: 'no errors should be found'
    !result.rendered(JsonRender).object.errors

    and: 'result data should be received'
    result.rendered(JsonRender).object.data == [echoName: 'Hello John']
  }

  void 'trying to execute a badly formed query'() {
    given: 'the handler and the schema'
    def handler = new GraphQLHandler()
    def schema = DSL.schema {
      queries('Queries') {
        field('hello') {
          type GraphQLString
          staticValue('world!')
        }
      }
    }
    // json is not properly formed
    def body = '{ "query: "{ hello }"}'

    and: 'setting the schema in the registry'
      def requestFixture = RequestFixture
      .requestFixture()
      .registry { r ->
        r.add(GraphQL, GraphQL.newGraphQL(schema).build())
    }

    when: 'executing the query against the handler'
    def result = requestFixture
      .body(body, 'application/json')
      .handle(handler)

    then: 'errors should be found'
    with(result.rendered(JsonRender).object.errors) {
      locations.find().line == 1
      locations.find().column == 13
      message.startsWith 'JsonParseException'
    }
  }

  void 'execute a query when security check passes'() {
    given: 'the handler, the schema, and the body'
    def handler = new GraphQLHandler()
    def schema = DSL.schema {
      queries('Queries') {
        field('echoName') {
          type GraphQLString
          argument 'name', GraphQLString
          fetcher { env -> "Hello ${env.arguments.name}" }
        }
      }
    }
    def body = '''{
      "query": "query EchoMyName($name: String) { \
         echoName(name: $name) \
      }",
      "variables": {
        "name": "John"
      }
    }
    '''

    and: 'setting the schema in the registry'
    def graphql = GraphQL
      .newGraphQL(schema)
      .instrumentation(new SecurityChecker())
      .build()

    def requestFixture = RequestFixture
      .requestFixture()
      .registry { r ->
          r.add(GraphQL, graphql)
      }

    when: 'executing the query against the handler'
    def result = requestFixture
      .body(body, 'application/json')
      .header('Authorization', 'JWT random')
      .handle(handler)

    then: 'no errors should be found'
    !result.rendered(JsonRender).object.errors
    result.rendered(JsonRender).object.data == [echoName: 'Hello John']
  }

  void 'throws exception when security check fails'() {
    given: 'the handler, the schema, and the body'
    def handler = new GraphQLHandler()
    def schema = DSL.schema {
      queries('Queries') {
        field('echoName') {
          type GraphQLString
          argument 'name', GraphQLString
          fetcher { env -> "Hello ${env.arguments.name}" }
        }
      }
    }
    def body = '''{
      "query": "query EchoMyName($name: String) { \
         echoName(name: $name) \
      }",
      "variables": {
        "name": "John"
      }
    }
    '''

    and: 'setting the schema in the registry'
    def graphql = GraphQL
      .newGraphQL(schema)
      .instrumentation(new SecurityChecker())
      .build()

    def requestFixture = RequestFixture
      .requestFixture()
      .registry { r ->
        r.add(GraphQL, graphql)
    }

    when: 'executing the query without credentials'
    def result = requestFixture
      .body(body, 'application/json')
      .handle(handler)

    then: 'errors should be found'
    result
      .rendered(JsonRender)
      .object
      .errors
      .first()
      .extensions.i18n == 'error.security.authorization'
  }
}
