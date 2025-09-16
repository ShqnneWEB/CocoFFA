/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.google.errorprone.annotations.concurrent;

import com.google.errorprone.annotations.IncompatibleModifiers;
import com.google.errorprone.annotations.Modifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
@IncompatibleModifiers(modifier={Modifier.FINAL})
public @interface LazyInit {
}

