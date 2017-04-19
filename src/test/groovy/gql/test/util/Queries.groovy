package gql.test.util

import groovy.json.JsonSlurper
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j

@Slf4j
class Queries {

  @SuppressWarnings('UnusedMethodParameter')
  // tag::findLastFilm[]
  static Map<String,Object> findLastFilm(DataFetchingEnvironment env) {
    List<Map> dataSet = loadBondFilms()
    Map<String,?> lastFilm = dataSet.last()

    return lastFilm
  }
  // end::findLastFilm[]

  // tag::findByYear[]
  static Map<String,?> findByYear(DataFetchingEnvironment env) {
    String year = "${env.arguments.year}"

    List<Map> dataSet = loadBondFilms()
    Map<String,?> filmByYear = dataSet.find(byYear(year))

    return filmByYear
  }
  // end::findByYear[]

  private static List<Map> loadBondFilms() {
    def datasetPath = SystemResources.classpath('data/bond.json')
    def datasetData = new JsonSlurper().parse(datasetPath.toFile()) as List<Map>
    datasetData
  }

  static Closure<Boolean> byYear(String year) {
    return { Map<String,Object> m -> m.year == year } as Closure<Boolean>
  }
}
