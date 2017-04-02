package gql.test.util

import groovy.json.JsonSlurper
import graphql.schema.DataFetchingEnvironment

class Queries {

  @SuppressWarnings('UnusedMethodParameter')
  static Map<String,Object> findLastFilm(DataFetchingEnvironment env) {
    def datasetPath = SystemResources.classpath('data/bond.json')
    def datasetData = new JsonSlurper().parse(datasetPath.toFile()) as List<Map>
    def lastFilm = datasetData.last()

    return lastFilm
  }

  static Map<String,Object> findByYear(DataFetchingEnvironment env) {
    def year = "${env.arguments.year}"
    def datasetURL = SystemResources.classpathAsURL('/data/bond.json')
    def inputStream = datasetURL.openStream()
    def datasetData = new JsonSlurper().parse(inputStream) as List<Map>
    def filmByYear = datasetData.find(byYear(year))

    println filmByYear
    return filmByYear
  }

  static Closure<Boolean> byYear(String year) {
    return { Map<String,Object> m -> m.year == year } as Closure<Boolean>
  }
}
