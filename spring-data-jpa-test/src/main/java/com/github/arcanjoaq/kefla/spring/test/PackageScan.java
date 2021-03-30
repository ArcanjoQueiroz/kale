package com.github.arcanjoaq.kefla.spring.test;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

@Inherited
@Target(TYPE)
public @interface PackageScan {
  String[] value() default "";
}
