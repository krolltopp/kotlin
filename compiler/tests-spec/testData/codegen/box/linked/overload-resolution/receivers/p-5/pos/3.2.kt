// WITH_RUNTIME

/*
 * KOTLIN CODEGEN BOX SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-268
 * PLACE:overload-resolution, receivers -> paragraph 5 -> sentence 3
 * NUMBER: 2
 * DESCRIPTION: Current class companion object receiver has higher priority than any of the superclass companion objects;
 */

class Case() : CaseBase() {

    companion object foo{
        var isCaseCompanionCalled = false
        fun foo(){isCaseCompanionCalled = true}
    }

    fun test(): String{
        foo.foo()
        if (!isCaseBaseReceiverCalled && isCaseCompanionCalled)
            return "OK"
        return "NOK"
    }
}

open class CaseBase {
    companion object foo {
        var isCaseBaseReceiverCalled = false
        fun foo() { this.isCaseBaseReceiverCalled = true }
    }
}

fun box(): String {
    return Case().test()
}
