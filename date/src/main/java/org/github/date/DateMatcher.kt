package org.github.date

import java.util.regex.Pattern

class DateMatcher : DateSelectorET.Matcher {

    override fun matches(date: String, PATTERN: String): Boolean {
        val pattern = Pattern.compile(PATTERN)
        return pattern.matcher(date).matches()
    }

}