package sigmastate.serialization

import org.scalacheck.Gen
import sigmastate.{SType, CrossVersionProps}
import sigmastate.Values.Constant
import sigmastate.lang.{SigmaBuilder, DeserializationSigmaBuilder}

class BlockSerializerSpecification extends SerializationSpecification with CrossVersionProps {

  property("ValDef: serializer round trip") {
    forAll(valOrFunDefGen) { v =>
      roundTripTest(v)
    }
  }

  property("BlockValue and ValUse: serializer round trip") {
    forAll(blockValueGen) { v =>
      roundTripTest(v)
    }
  }

  property("ConstantPlaceholder: serializer round trip") {
    forAll(Gen.oneOf(byteConstGen, intConstGen, groupElementConstGen, booleanConstGen)) { v =>
      implicit val builder: SigmaBuilder = DeserializationSigmaBuilder
      val store = new ConstantStore(IndexedSeq())
      val placeholder = store.put(v.asInstanceOf[Constant[SType]])
      val s = ConstantPlaceholderSerializer(DeserializationSigmaBuilder.mkConstantPlaceholder)
      val w = SigmaSerializer.startWriter()
      s.serialize(placeholder, w)
      val r = SigmaSerializer.startReader(w.toBytes, store, resolvePlaceholdersToConstants = false, activatedVersion)
      s.parse(r) shouldEqual placeholder
    }
  }

  property("FuncValue: serializer round trip") {
    forAll(funcValueGen) { v =>
      roundTripTest(v)
    }
  }
}
