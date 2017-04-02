import groovy.transform.CompileStatic

/**
 * This compiler configuration adds static compilation to ALL Groovy
 * classes but the classes ending with Spec (which are supposed to be
 * the Spock specs)
 *
 * @since 0.1.0
 */
withConfig(configuration) {
  source(unitValidator: { unit -> !unit.AST.classes.any { it.name.endsWith('Spec') } }) {
    ast(CompileStatic)
  }
}
