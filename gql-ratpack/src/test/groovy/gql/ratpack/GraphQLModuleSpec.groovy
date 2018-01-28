package gql.ratpack

import spock.lang.AutoCleanup
import spock.lang.Specification
import ratpack.guice.BindingsImposition
import ratpack.impose.ImpositionsSpec
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest

class GraphQLModuleSpec extends Specification {

  @AutoCleanup
  def aut = new GroovyRatpackMainApplicationUnderTest() {
    @Override
    protected void addImpositions(ImpositionsSpec impositions) {
      impositions.add(BindingsImposition.of { r ->
        r.module(GraphQLModule)
      })
    }
  }

  void 'loading both handlers'() {
    when: ''
    def result = aut
      .httpClient
      .requestSpec {
        it.get('/graphql/browser')
    }

    then: ''
    result

  }
}
