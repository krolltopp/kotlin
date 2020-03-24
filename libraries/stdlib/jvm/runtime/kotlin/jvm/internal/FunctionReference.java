/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.jvm.internal;

import kotlin.SinceKotlin;
import kotlin.reflect.KCallable;
import kotlin.reflect.KFunction;

@SuppressWarnings({"rawtypes", "unused"})
public class FunctionReference extends CallableReference implements FunctionBase, KFunction {
    private final int arity;

    /**
     * Bitmask where bits represent the following flags:<br/>
     * <li>
     *     <ul>0..7 - the number of parameters in the functional type where this reference was originally passed to.
     *                Might differ from the number of parameters of the referenced function, in case it has default
     *                and/or vararg parameters.</ul>
     *     <ul>8 - whether the vararg->Array parameter type conversion happened.</ul>
     *     <ul>9 - whether coercion of return type to Unit happened.</ul>
     * </li>
     */
    @SinceKotlin(version = "1.4")
    private final int flags;

    public FunctionReference(int arity) {
        this(arity, NO_RECEIVER, null, null, null, 0);
    }

    @SinceKotlin(version = "1.1")
    public FunctionReference(int arity, Object receiver) {
        this(arity, receiver, null, null, null, 0);
    }

    @SinceKotlin(version = "1.4")
    public FunctionReference(int arity, Object receiver, Class owner, String name, String signature, int flags) {
        super(receiver, owner, name, signature, (flags & 1) == 1);
        this.arity = arity;
        this.flags = flags >> 1;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    @SinceKotlin(version = "1.1")
    protected KFunction getReflected() {
        return (KFunction) super.getReflected();
    }

    @Override
    @SinceKotlin(version = "1.1")
    protected KCallable computeReflected() {
        return Reflection.function(this);
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isInline() {
        return getReflected().isInline();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isExternal() {
        return getReflected().isExternal();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isOperator() {
        return getReflected().isOperator();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isInfix() {
        return getReflected().isInfix();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isSuspend() {
        return getReflected().isSuspend();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof FunctionReference) {
            FunctionReference other = (FunctionReference) obj;

            return Intrinsics.areEqual(getOwner(), other.getOwner()) &&
                   getName().equals(other.getName()) &&
                   getSignature().equals(other.getSignature()) &&
                   flags == other.flags &&
                   Intrinsics.areEqual(getBoundReceiver(), other.getBoundReceiver());
        }
        if (obj instanceof KFunction) {
            return obj.equals(compute());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (((getOwner() == null ? 0 : getOwner().hashCode() * 31) + getName().hashCode()) * 31 + getSignature().hashCode()) * 31 +
               Integer.valueOf(flags).hashCode();
    }

    @Override
    public String toString() {
        KCallable reflected = compute();
        if (reflected != this) {
            return reflected.toString();
        }

        // TODO: consider adding the class name to toString() for constructors
        return "<init>".equals(getName())
               ? "constructor" + Reflection.REFLECTION_NOT_AVAILABLE
               : "function " + getName() + Reflection.REFLECTION_NOT_AVAILABLE;
    }
}
