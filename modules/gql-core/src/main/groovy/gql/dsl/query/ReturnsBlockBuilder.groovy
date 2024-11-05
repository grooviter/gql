package gql.dsl.query

import gql.dsl.Leveled
import gql.dsl.StringUtils
import gql.dsl.DynamicFieldCapture
import groovy.transform.CompileDynamic

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * Builds the body and return parts of a given GraphQL query/mutation
 *
 * @since 0.1.0
 */
class ReturnsBlockBuilder extends Leveled {
  String name = ""
  String variables = ""
  String aliasFragment = ""
  String fieldFragment = ""
  String nestedQueries = ""

  /**
   * Returns the fields that the query/mutation is going to return
   *
   * @param fields a {@link Closure} declaring which fields to return
   * @return the current {@link ReturnsBlockBuilder} instance
   * @since 0.1.0
   */
  @CompileDynamic
  ReturnsBlockBuilder returns(@DelegatesTo(strategy = DELEGATE_FIRST, value = DynamicFieldCapture) Closure fields) {
    return this.returns(null, fields)
  }

  /**
   * Sets the type and the fields that are going to be returned by the
   * query/mutation
   *
   * @param clazz type of the returned result
   * @param fields which fields of the current query/mutation are you interested n
   * @return the current builder instance
   * @since 0.1.0
   */
  @CompileDynamic
  <T> ReturnsBlockBuilder returns(
    @DelegatesTo.Target Class<T> clazz,
    @DelegatesTo(strategy = DELEGATE_FIRST, genericTypeIndex = 0) Closure fields
  ){
    DynamicFieldCapture fieldCapture = new DynamicFieldCapture(clazz: clazz, level: this.level)
    Closure<DynamicFieldCapture> clos = fields.clone() as Closure<DynamicFieldCapture>

    clos.delegate = fieldCapture
    clos.resolveStrategy = DELEGATE_FIRST
    clos()

    def capFields = fieldCapture.fields
    def fieldFragmentTabs = levelTabs + "\t"
    fieldFragment = capFields ? fieldFragmentTabs + capFields.join("\n$fieldFragmentTabs") : StringUtils.EMPTY
    nestedQueries = fieldCapture.buildNested()
    return this
  }

  /**
   * Adds an alias to the current query/mutation
   *
   * @param alias name of the alias
   * @return the current builder instance
   * @since 0.1.0
   */
  ReturnsBlockBuilder alias(String alias) {
    this.aliasFragment = "$alias:"
    return this
  }

  /**
   * Returns the configured query/mutation body
   *
   * @return the body of the query/mutation
   * @since 0.1.0
   */
  String build() {
    String content = [fieldFragment, nestedQueries].grep().join(StringUtils.NEW_LINE)
    String body    = "{\n$content\n$levelTabs}"
    String query   = [aliasFragment, name, variables, body].grep().join(StringUtils.SPACE)
    return "$levelTabs$query"
  }
}
