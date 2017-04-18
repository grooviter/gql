package gql.dsl

import static groovy.lang.Closure.DELEGATE_FIRST

import groovy.transform.CompileDynamic

/**
 * Builder aimed to create a compliant GraphQL queryString using
 * a static typed DSL
 *
 * @since 0.1.0
 */
class QueryBuilder {

  String queryString = ""

  /**
   * Builds a GraphQL queryString with a given name. The class passed
   * as parameter helps the compiler to know which fields are
   * available for the returning type.
   *
   * @param name name of the queryString as it is defined in the schema
   * @param clazz return type to help the compiler to know which fields are available
   * @param fields fields to return in the response
   * @return an instance of {@link QueryBuilder}
   * @since 0.1.0
   */
  public <T> QueryBuilder query(String name,
                                @DelegatesTo(strategy = DELEGATE_FIRST, value = ReturnsBlockBuilder) Closure fields) {
    Closure<ReturnsBlockBuilder> clos = fields.clone() as Closure<ReturnsBlockBuilder>
    ReturnsBlockBuilder builderSource = new ReturnsBlockBuilder(name: name)
    ReturnsBlockBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.queryString += builderResult.build()

    return this
  }

  /**
   * Builds a GraphQL queryString with a given name. The class passed
   * as parameter helps the compiler to know which fields are
   * available for the returning type.
   *
   * @param name name of the queryString as it is defined in the schema
   * @param clazz return type to help the compiler to know which fields are available
   * @param fields fields to return in the response
   * @return an instance of {@link QueryBuilder}
   * @since 0.1.0
   */
  public <T> QueryBuilder query(String name,
                                Map<String,?> variables,
                                @DelegatesTo(strategy = DELEGATE_FIRST, value = ReturnsBlockBuilder) Closure fields) {
    Closure<ReturnsBlockBuilder> clos = fields.clone() as Closure<ReturnsBlockBuilder>
    String variablesString = processVariables(variables)
    ReturnsBlockBuilder builderSource = new ReturnsBlockBuilder(name: name, variables: variablesString)
    ReturnsBlockBuilder builderResult = builderSource.with(clos) ?: builderSource

    this.queryString += builderResult.build()

    return this
  }

  /**
   * Returns the {@link String} representation of the GraphQL queryString
   *
   * @return the queryString resulting of parsing the DSL
   * @since 0.1.0
   */
  String build() {
    return "{ $queryString }"
  }

  String processVariables(Map<String, ?> variables) {
    List<String> processed = variables.collect(this.&processVariableEntry)

    return processed ? "(${processed.join(",")})" : ""
  }

  String processVariableEntry(String key, Object value) {
    String fValue = value instanceof String ? /"$value"/ : "$value"

    return "$key: $fValue"
  }

  /**
   * @since 0.1.0
   */
  static class ReturnsBlockBuilder {

    String name = ""
    String variables = ""
    String aliasFragment = ""
    String fieldFragment = ""

    /**
     * @param clazz
     * @param fields
     * @return
     * @since 0.1.0
     */
    /*
    @CompileDynamic
    public <T> ReturnsBlockBuilder returns(@DelegatesTo.Target Class<T> clazz,
                                           @DelegatesTo(
                                             strategy = DELEGATE_FIRST,
                                             genericTypeIndex = 0) Closure fields){
      fields
        .getThisObject()
        .getMetaClass()
        .getProperty = { String name ->
          fieldFragment += "\t$name\n"
        }
      fields()

      return this
    }*/

    @CompileDynamic
    public <T> ReturnsBlockBuilder returns(@DelegatesTo.Target Class<T> clazz,
                                           @DelegatesTo(
                                             strategy = DELEGATE_FIRST,
                                             genericTypeIndex = 0) Closure fields){
      DynamicFieldCapture fieldCapture = new DynamicFieldCapture(clazz: clazz)
      Closure<DynamicFieldCapture> clos = fields.clone() as Closure<DynamicFieldCapture>

      clos.delegate = fieldCapture
      clos.resolveStrategy = DELEGATE_FIRST
      clos()

      fieldCapture
        .fields
        .each { String name ->
        fieldFragment += "\t$name\n"
      }

      return this
    }

    /**
     * @param fields
     * @return
     * @since 0.1.0
     */
    @CompileDynamic
    public ReturnsBlockBuilder returns(
      @DelegatesTo(strategy = DELEGATE_FIRST, value = DynamicFieldCapture) Closure fields) {
      DynamicFieldCapture fieldCapture = new DynamicFieldCapture()
      Closure<DynamicFieldCapture> clos = fields.clone() as Closure<DynamicFieldCapture>

      clos.delegate = fieldCapture
      clos.resolveStrategy = DELEGATE_FIRST
      clos()

      fieldCapture
        .fields
        .each { String name ->
          fieldFragment += "\t$name\n"
        }

      return this
    }

    /**
     * @param alias
     * @return
     * @since 0.1.0
     */
    ReturnsBlockBuilder alias(String alias) {
      this.aliasFragment = "$alias:"
      return this
    }

    /**
     * @return
     * @since 0.1.0
     */
    String build() {
      return "$aliasFragment $name $variables { \n $fieldFragment } \n"
    }
  }
}
