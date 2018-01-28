package gql.ratpack

import java.nio.file.Path
import spock.lang.Specification

class PathUtilSpec extends Specification {

  void 'get a resource from jar'() {
    given: 'a resource located in a jar'
    def resource = '/dsld/spk.dsld'

    when: 'trying to retrieve the resource'
    URL url = PathUtilSpec.class.getResource(resource)
    Path path = PathUtil.toPath(url)

    then: 'we should be able to resolve its path'
    path
  }

  void 'cannot handle other than jar or file types'() {
    when: 'trying to retrieve a resource'
    PathUtil.toPath(new URL('ftp://localhost/something.txt'))

    then: 'an illegal state exception should be thrown'
    thrown(IllegalStateException)
  }
}
