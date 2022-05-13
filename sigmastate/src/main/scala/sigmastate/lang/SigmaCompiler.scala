package sigmastate.lang

import fastparse.core.Parsed
import fastparse.core.Parsed.Success
import org.ergoplatform.Context
import org.ergoplatform.ErgoAddressEncoder.NetworkPrefix
import sigmastate.SType
import sigmastate.Values.{SValue, Value}
import sigmastate.eval.IRContext
import sigmastate.interpreter.Interpreter.ScriptEnv
import sigmastate.lang.SigmaPredef.PredefinedFuncRegistry
import sigmastate.lang.syntax.ParserException

/**
  * @param networkPrefix    network prefix to decode an ergo address from string (PK op)
  * @param builder          used to create ErgoTree nodes
  * @param lowerMethodCalls if true, then MethodCall nodes are lowered to ErgoTree nodes
  *                         when [[sigmastate.SMethod.irInfo.irBuilder]] is defined. For
  *                         example, in the `coll.map(x => x+1)` code, the `map` method
  *                         call can be lowered to MapCollection node.
  *                         The lowering if preferable, because it is more compact (1 byte
  *                         for MapCollection instead of 3 bytes for MethodCall).
  */
case class CompilerSettings(
    networkPrefix: NetworkPrefix,
    builder: SigmaBuilder,
    lowerMethodCalls: Boolean
)

class SigmaCompiler(settings: CompilerSettings) {
  def this(networkPrefix: Byte) = this(
    CompilerSettings(networkPrefix, TransformingSigmaBuilder, lowerMethodCalls = true)
  )

  @inline final def builder = settings.builder
  @inline final def networkPrefix = settings.networkPrefix

  def parse(x: String): SValue = {
    SigmaParser(x, builder) match {
      case Success(v, _) => v
      case f: Parsed.Failure[_,String] =>
        throw new ParserException(s"Syntax error: $f", Some(SourceContext.fromParserFailure(f)))
    }
  }

  def typecheck(env: ScriptEnv, parsed: SValue): Value[SType] = {
    val predefinedFuncRegistry = new PredefinedFuncRegistry(builder)
    val binder = new SigmaBinder(env, builder, networkPrefix, predefinedFuncRegistry)
    val bound = binder.bind(parsed)
    val typer = new SigmaTyper(builder, predefinedFuncRegistry, settings.lowerMethodCalls)
    val typed = typer.typecheck(bound)
    typed
  }

  def typecheck(env: ScriptEnv, code: String): Value[SType] = {
    val parsed = parse(code)
    typecheck(env, parsed)
  }

  private[sigmastate] def compileWithoutCosting(env: ScriptEnv, code: String): Value[SType] = {
    val typed = typecheck(env, code)
    val spec = new SigmaSpecializer(builder)
    val ir = spec.specialize(typed)
    ir
  }

  def compile(env: ScriptEnv, code: String)(implicit IR: IRContext): Value[SType] = {
    val interProp = typecheck(env, code)
    val IR.Pair(calcF, _) = IR.doCosting(env, interProp, true)
    IR.buildTree(calcF)
  }

  case class CompilerResult[Ctx <: IRContext](
    calcF: Ctx#Ref[Ctx#Context => Any],
    compiledGraph: Ctx#Ref[Ctx#Context => Any],
    /** Tree obtained from calcF graph. */
    calcTree: SValue,
    /** Tree obtained from graph created by GraphBuilding */
    buildTree: SValue
  )

  /** TODO v5.x: remove after AOT costing is removed */
  def compile2(env: ScriptEnv, code: String)(implicit IR: IRContext): CompilerResult[IR.type] = {
    val interProp = typecheck(env, code)
    val IR.Pair(calcF, _) = IR.doCosting(env, interProp, true)
    val compiledGraph = IR.doBuild(env, interProp, true)
    val calcTree = IR.buildTree(calcF)
    val compiledTree = IR.buildTree(compiledGraph)
    CompilerResult(calcF, compiledGraph, calcTree, compiledTree)
  }
}

object SigmaCompiler {
  def apply(settings: CompilerSettings): SigmaCompiler =
    new SigmaCompiler(settings)
}
