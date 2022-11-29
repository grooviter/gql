package gql.dsl.executor

import gql.DSL
import graphql.ExecutionResult
import graphql.schema.GraphQLSchema
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class GraphQLExecutorSpec extends Specification {

  void 'execute asynchronous simple query'() {
    given: 'a simple schema'
    GraphQLSchema schema = DSL.schema {
      queries {
        field("getName") {
          type(GraphQLString)
          staticValue("John")
        }
      }
    }
    when: 'executing'
    // tag::newExecutorExecuteAsync[]
    CompletableFuture<ExecutionResult> result = DSL
      .newExecutor(schema)
      .executeAsync(" { getName } ")
    // end::newExecutorExecuteAsync[]
    Map<String,?> data = result.get().data

    then: 'we should get the expected result'
    data.getName == 'John'
  }

  void 'get underlying GraphQL instance'() {
    given: 'a simple schema'
    GraphQLSchema schema = DSL.schema {
      queries {
        field("getName") {
          type(GraphQLString)
          staticValue("John")
        }
      }
    }
    when: 'executing'
    // tag::newExecutorGraphQL[]
    GraphQLExecutor executor = DSL.newExecutor(schema)
    ExecutionResult result = executor.graphQL.execute("{ getName }")
    // end::newExecutorGraphQL[]

    then: 'we should get the expected result'
    result.data.getName == 'John'
  }
}
