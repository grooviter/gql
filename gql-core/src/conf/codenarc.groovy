ruleset {
  description 'GQL ruleset'

  ruleset('rulesets/basic.xml')
  ruleset('rulesets/exceptions.xml')
  ruleset('rulesets/imports.xml')
  ruleset('rulesets/unused.xml')
  ruleset('rulesets/dry.xml') {
    'DuplicateStringLiteral' {
      doNotApplyToClassNames = '*Spec'
    }
    'DuplicateMapLiteral' {
      doNotApplyToClassNames = '*Spec'
    }
    'DuplicateNumberLiteral' {
      doNotApplyToClassNames = '*Spec'
    }
    'DuplicateListLiteral' {
      doNotApplyToClassNames = '*Spec'
    }
  }
  ruleset('rulesets/formatting.xml') {
    'SpaceAroundMapEntryColon' {
      enabled = false
    }
    'ClassJavadoc' {
      enabled = false
    }
    'FileEndsWithoutNewline' {
      enabled = false
    }
    'LineLength' {
      doNotApplyToFileNames = '*Spec.*'
    }
    'MissingBlankLineAfterPackage' {
      enabled = false
    }
  }
  ruleset('rulesets/naming.xml') {
    'MethodName' {
      doNotApplyToClassNames = '*Spec'
    }
    'FactoryMethodName' {
      enabled = false
    }
    'VariableName' {
      finalRegex = 	/[a-z][a-zA-Z0-9]*/
    }
  }
  ruleset('rulesets/convention.xml') {
    'NoDef' {
      enabled = false
    }
  }
}
