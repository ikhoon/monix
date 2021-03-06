/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://monix.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monix.types

/** A type-class providing the `coflatMap` operation, the dual of
  * `flatMap`.
  *
  * The purpose of this type-class is to support the data-types in the
  * Monix library and it is considered a shim for a lawful type-class
  * to be supplied by libraries such as Cats or Scalaz or equivalent.
  *
  * To implement it in instances, inherit from [[CoflatMapClass]].
  *
  * Credit should be given where it is due.The type-class encoding has
  * been copied from the Scado project and
  * [[https://github.com/scalaz/scalaz/ Scalaz 8]] and the type has
  * been extracted from [[http://typelevel.org/cats/ Cats]].
  */
trait CoflatMap[F[_]] extends Serializable {
  def functor: Functor[F]

  def coflatMap[A, B](fa: F[A])(f: F[A] => B): F[B]
  def coflatten[A](fa: F[A]): F[F[A]] =
    coflatMap(fa)(fa => fa)
}

object CoflatMap extends CoflatMapSyntax {
  @inline def apply[F[_]](implicit F: CoflatMap[F]): CoflatMap[F] = F
}

/** The `CoflatMapClass` provides the means to combine
  * [[CoflatMap]] instances with other type-classes.
  *
  * To be inherited by `CoflatMap` instances.
  */
trait CoflatMapClass[F[_]] extends CoflatMap[F] with FunctorClass[F] {
  final def coflatMap: CoflatMap[F] = this
}

/** Provides syntax for [[CoflatMap]]. */
trait CoflatMapSyntax extends Serializable {
  implicit final def coflatMapOps[F[_], A](fa: F[A])
    (implicit F: CoflatMap[F]): CoflatMapSyntax.Ops[F, A] =
    new CoflatMapSyntax.Ops(fa)
}

object CoflatMapSyntax {
  final class Ops[F[_], A](self: F[A])(implicit F: CoflatMap[F])
    extends Serializable {

    /** Extension method for [[CoflatMap.coflatMap]]. */
    def coflatMap[B](f: F[A] => B): F[B] = F.coflatMap(self)(f)
    /** Extension method for [[CoflatMap.coflatten]]. */
    def coflatten: F[F[A]] = F.coflatten(self)
  }
}
