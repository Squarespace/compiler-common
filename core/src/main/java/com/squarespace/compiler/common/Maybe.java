/**
 * Copyright, 2017, Squarespace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squarespace.compiler.common;

import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Maybe monad.
 */
public interface Maybe<T> {

  T get();

  boolean isJust();

  default boolean isNothing() {
    return !isJust();
  }

  @SuppressWarnings("unchecked")
  static <T> Maybe<T> nothing() {
    return (Maybe<T>) Nothing.NOTHING;
  }

  static <T> Maybe<T> just(T value) {
    return new Just<>(value);
  }

  default <R> Maybe<R> map(Function<T, R> f) {
    return isJust() ? just(f.apply(get())) : nothing();
  }

  default <R> Maybe<R> flatMap(Function<T, Maybe<R>> f) {
    return isJust() ? f.apply(get()) : nothing();
  }

  default Maybe<T> orElse(Supplier<Maybe<T>> f) {
    return isJust() ? this : f.get();
  }

}
