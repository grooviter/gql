package gql.dsl

/**
 * It's used as a delegate object for a closure in order
 * to capture all properties called within that {@link Closure}. It
 * creates a list with all those fields.
 *
 * @since 0.1.0
 */
class DynamicFieldCapture {

  /**
   * If the captured fields should belong to a
   * specific class, you can specify that class
   *
   * @since 0.1.0
   */
  Class clazz

  /**
   * List of field names captured by this class
   *
   * @since 0.1.0
   */
  List<String> fields = []

  /**
   * Captures all properties called within a {@link Closure}
   * using an instance of this class as a delegate. <br/>
   * If a class has been provided, then it will be validated
   * that the property belongs to that type, if not, an
   * {@link IllegalStateException} will be thrown
   *
   * @param name name of the missing property
   * @return the current instance
   * @throw IllegalStateException if the property doesn't belong
   * to the class passed
   * @since 0.1.0
   */
  def propertyMissing(String name) {
    // This could be moved to a Type Checker
    if (clazz && !clazz.newInstance().hasProperty(name)) {
      throw new IllegalStateException("No field $name found for type ${clazz.name}")
    }

    fields << name

    return this
  }
}
