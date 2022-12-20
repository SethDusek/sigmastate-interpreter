package org.ergoplatform.sdk.js

import typings.fleetSdkCommon
import typings.fleetSdkCommon.boxesMod.Box
import typings.fleetSdkCommon.{commonMod, inputsMod, tokenMod, transactionsMod}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Prover")
class Prover {
  def reduceTransaction(
      unsignedTx: transactionsMod.UnsignedTransaction,
      boxesToSpend: js.Array[inputsMod.EIP12UnsignedInput],
      dataBoxes: js.Array[Box[commonMod.Amount]],
      stateDigest: String,
      baseCost: Int,
      tokensToBurn: js.Array[tokenMod.TokenAmount[commonMod.Amount]]
  ): (ReducedTransaction, Int) = {
    val tx = Isos.isoUnsignedTransaction.to(unsignedTx)
    (new ReducedTransaction, 0)
  }
}

@JSExportTopLevel("ReducedTransaction")
class ReducedTransaction

@JSExportTopLevel("ProverFactory")
object Prover {
}
