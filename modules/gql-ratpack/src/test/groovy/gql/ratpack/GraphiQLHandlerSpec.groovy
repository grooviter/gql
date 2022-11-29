package gql.ratpack

import spock.lang.Specification
import ratpack.test.handling.RequestFixture

class GraphiQLHandlerSpec extends Specification {

  void 'request for GraphiQL client'() {
    given: 'an instance of the GraphiQLHandler'
    def handler = new GraphiQLHandler()
    def config = new GraphQLModuleConfig()

    when: 'executing the query against the handler'
    def result = RequestFixture
      .requestFixture()
      .registry { r -> r.add(GraphQLModuleConfig, config) }
      .handle(handler)

    then: 'we should get the html page'
    result.status.code == 200
  }

  void 'disabling GraphiQL client'() {
    given: 'an instance of the GraphiQLHandler'
    def handler = new GraphiQLHandler()
    def config = new GraphQLModuleConfig()

    and: 'disabling GraphiQL'
    config.activateGraphiQL = false

    when: 'executing the query against the handler'
    def result = RequestFixture
      .requestFixture()
      .registry { r ->
          r.add(GraphQLModuleConfig, config)
      }.handle(handler)

    then: 'we should get the html page'
    result.status.code == 404
  }
}
