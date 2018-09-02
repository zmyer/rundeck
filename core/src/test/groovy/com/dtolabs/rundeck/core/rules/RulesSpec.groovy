package com.dtolabs.rundeck.core.rules

import spock.lang.Specification

/**
 * Created by greg on 5/17/16.
 */
class RulesSpec extends Specification {
    def "equals condition key value"() {
        given:
        def cond = Rules.equalsCondition("a", "b")
        def cond2 = Rules.equalsCondition("a", null)

        expect:
        cond.test(States.state("a", "b"))
        !cond.test(States.state("a", "c"))
        !cond.test(States.state("c", "b"))

        !cond2.test(States.state("a", "b"))
        cond2.test(States.state("b", "c"))
        cond2.test(States.state("a", null))
    }

    def "equals condition state"() {
        given:
        def cond = Rules.equalsCondition(States.state(a: 'b', c: 'd'))

        expect:
        !cond.test(States.state('a', 'b'))
        cond.test(States.state("a": "b", c: 'd'))
        cond.test(States.state("a": "b", c: 'd', e: 'f'))
        !cond.test(States.state("a": "d", c: 'd', e: 'f'))
    }

    def "matches condition"() {
        given:
        def cond = Rules.matchesCondition("abc", false, "[def]+", true)


        expect:
        cond.test(States.state('abc', 'd'))
        cond.test(States.state('abc', 'e'))
        cond.test(States.state('abc', 'f'))
        cond.test(States.state('abc', 'def'))
        !cond.test(States.state('abc', 'a'))
    }

    def "not predicate"() {
        given:
        def base = Rules.equalsCondition("a", "b")
        def cond = Condition.not(base)

        expect:
        !cond.test(States.state("a", "b"))
        cond.test(States.state("a", "c"))
        cond.test(States.state("c", "b"))
    }

    def "and predicate"() {
        given:
        def cond = Condition.and(Rules.equalsCondition("a", "b"), Rules.equalsCondition("c", "d"))

        expect:
        !cond.test(States.state("a", "b"))
        !cond.test(States.state("c", "d"))
        cond.test(States.state(a: 'b', c: 'd'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f'))
    }

    def "and predicate iterable"() {
        given:
        def cond = Condition.and([Rules.equalsCondition("a", "b"), Rules.equalsCondition("c", "d")])

        expect:
        !cond.test(States.state("a", "b"))
        !cond.test(States.state("c", "d"))
        cond.test(States.state(a: 'b', c: 'd'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f'))
    }

    def "and predicate varargs"() {
        given:
        def cond = Condition.and(
                Rules.equalsCondition("a", "b"),
                Rules.equalsCondition("c", "d"),
                Rules.equalsCondition("e", "f")
        )

        expect:
        !cond.test(States.state("a", "b"))
        !cond.test(States.state("c", "d"))
        !cond.test(States.state(a: 'b', c: 'd'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f', g: 'h'))
    }

    def "or predicate"() {
        given:
        def cond = Condition.or(Rules.equalsCondition("a", "b"), Rules.equalsCondition("c", "d"))

        expect:
        !cond.test(States.state("a", "z"))
        cond.test(States.state("a", "b"))
        !cond.test(States.state("c", "z"))
        cond.test(States.state("c", "d"))

        cond.test(States.state(a: 'b', c: 'z'))
        cond.test(States.state(a: 'z', c: 'd'))

        cond.test(States.state(a: 'b', c: 'd'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f'))

    }

    def "or predicate iterable"() {
        given:
        def cond = Condition.or([Rules.equalsCondition("a", "b"), Rules.equalsCondition("c", "d")])

        expect:
        !cond.test(States.state("a", "z"))
        cond.test(States.state("a", "b"))
        !cond.test(States.state("c", "z"))
        cond.test(States.state("c", "d"))

        cond.test(States.state(a: 'b', c: 'z'))
        cond.test(States.state(a: 'z', c: 'd'))

        cond.test(States.state(a: 'b', c: 'd'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f'))
    }

    def "or predicate varargs"() {
        given:
        def cond = Condition.or(
                Rules.equalsCondition("a", "b"),
                Rules.equalsCondition("c", "d"),
                Rules.equalsCondition('e', 'f')
        )

        expect:
        !cond.test(States.state("a", "z"))
        cond.test(States.state("a", "b"))
        !cond.test(States.state("c", "z"))
        cond.test(States.state("c", "d"))
        !cond.test(States.state("e", "z"))
        cond.test(States.state("e", "f"))

        cond.test(States.state(a: 'b', c: 'z'))
        cond.test(States.state(a: 'z', c: 'd'))

        cond.test(States.state(a: 'b', c: 'd'))
        cond.test(States.state(a: 'b', c: 'd', e: 'f'))
    }
}
