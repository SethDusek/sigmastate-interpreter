import org.ergoplatform.{ErgoBox, ErgoBoxCandidate, ErgoLikeContext}
import sigma.data.{RType, GeneralType}
import sigmastate.Values._
import sigmastate.lang.CheckingSigmaBuilder

import scala.annotation.nowarn
import scala.reflect.classTag

package object sigmastate {
  import CheckingSigmaBuilder._

  /** Shadow the implicit from sigma package so it doesn't interfere with the resolution
    * of ClassTags below.
    */
  @nowarn private def rtypeToClassTag = ???

  /** RType descriptors for predefined types used in AOTC-based interpreter. */

  implicit val SigmaBooleanRType    : RType[SigmaBoolean]     = RType.fromClassTag(classTag[SigmaBoolean])

  implicit val ErgoBoxRType         : RType[ErgoBox]          = RType.fromClassTag(classTag[ErgoBox])

  implicit val ErgoBoxCandidateRType: RType[ErgoBoxCandidate] = RType.fromClassTag(classTag[ErgoBoxCandidate])

  implicit val AvlTreeDataRType     : RType[AvlTreeData]      = GeneralType(classTag[AvlTreeData])

  implicit val ErgoLikeContextRType : RType[ErgoLikeContext]  = RType.fromClassTag(classTag[ErgoLikeContext])

  /** Helper method to create "+" operation node. */
  def Plus[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkPlus(left, right)

  /** Helper method to create "-" operation node. */
  def Minus[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkMinus(left, right)

  /** Helper method to create "*" operation node. */
  def Multiply[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkMultiply(left, right)

  /** Helper method to create "/" operation node. */
  def Divide[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkDivide(left, right)

  /** Helper method to create "%" operation node. */
  def Modulo[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkModulo(left, right)

  /** Helper method to create "min" operation node. */
  def Min[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkMin(left, right)

  /** Helper method to create "max" operation node. */
  def Max[T <: SNumericType](left: Value[T], right: Value[T]): Value[T] =
    mkMax(left, right)

}
