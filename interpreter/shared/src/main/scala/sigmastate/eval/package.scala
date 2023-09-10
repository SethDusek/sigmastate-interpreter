package sigmastate

import java.math.BigInteger
import org.ergoplatform.ErgoBox
import sigma.data.{AvlTreeData, RType}
import scorex.crypto.hash.Digest32
import sigmastate.Values.SigmaBoolean
import sigma.{Coll, CollBuilder}
import sigma._
import sigma.crypto.EcPointType
import sigmastate.exceptions.CostLimitException
import supertagged.TaggedType

import scala.language.implicitConversions

package object eval {
  /** The primary reference to Global instance of SigmaDsl.
    * Besides operations of SigmaDslBuilder class, this instance also contains methods,
    * which are not available in Dsl code, and which are not in SigmaDslBuilder interface.
    * For example methods like `Box`, `toErgoBox` are available here, but not available in Dsl.
    * @see SigmaDslBuilder
    */
  val SigmaDsl = CostingSigmaDslBuilder

  trait BaseDigestColl extends TaggedType[Coll[Byte]]

  object Digest32Coll extends BaseDigestColl

  type Digest32Coll = Digest32Coll.Type
  implicit val Digest32CollRType: RType[Digest32Coll] = RType[Coll[Byte]].asInstanceOf[RType[Digest32Coll] ]

  /** Implicit conversions between Dsl type and the type wrapped by the corresponding type Dsl type.
    * Here BigInt is Dsl type and BigInteger is wrapped type.
    * @see `sigma.CBigInt`
    */
  implicit def bigIntegerToBigInt(bi: BigInteger): BigInt = SigmaDsl.BigInt(bi)
  implicit def bigIntToBigInteger(bi: BigInt): BigInteger = SigmaDsl.toBigInteger(bi)

  implicit def ecPointToGroupElement(p: EcPointType): GroupElement = SigmaDsl.GroupElement(p)
  implicit def groupElementToECPoint(p: GroupElement): EcPointType = SigmaDsl.toECPoint(p).asInstanceOf[EcPointType]

  implicit def sigmaBooleanToSigmaProp(p: SigmaBoolean): SigmaProp = SigmaDsl.SigmaProp(p)
  implicit def sigmaPropToSigmaBoolean(p: SigmaProp): SigmaBoolean = SigmaDsl.toSigmaBoolean(p)

  implicit def avlTreeDataToAvlTree(p: AvlTreeData): AvlTree = SigmaDsl.avlTree(p)
  implicit def avlTreeToAvlTreeData(p: AvlTree): AvlTreeData = SigmaDsl.toAvlTreeData(p)

  implicit def ergoBoxToBox(p: ErgoBox): Box = SigmaDsl.Box(p)
  implicit def boxToErgoBox(p: Box): ErgoBox = SigmaDsl.toErgoBox(p)

  def msgCostLimitError(
      cost: Long,
      limit: Long) = s"Estimated execution cost $cost exceeds the limit $limit"

  /** Helper method to accumulate cost while checking limit.
    *
    * @param current current cost value
    * @param delta additional cost to add to the current value
    * @param limit total cost limit
    * @param msgSuffix use case-specific error message suffix
    * @return new increased cost when it doesn't exceed the limit
    * @throws CostLimitException
    */
  def addCostChecked(
      current: Long,
      delta: Long,
      limit: Long,
      msgSuffix: => String = ""): Long = {
    val newCost = java7.compat.Math.addExact(current, delta)
    if (newCost > limit) {
      throw new CostLimitException(
        estimatedCost = newCost,
        message = {
          val suffix = if (msgSuffix.isEmpty) "" else s": $msgSuffix"
          msgCostLimitError(newCost, limit) + suffix
        },
        cause = None)
    }
    newCost
  }
}
