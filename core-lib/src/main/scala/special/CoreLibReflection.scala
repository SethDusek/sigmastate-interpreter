package special

import scalan.RType
import scalan.reflection.CommonReflection.registerClassEntry
import scalan.reflection._
import special.collection.{Coll, CollBuilder}
import special.sigma.{SigmaProp, BigInt}

object CoreLibReflection {
  {
    val clazz = classOf[SigmaProp]
    registerClassEntry(clazz,
      methods = Map(
        mkMethod(clazz, "$bar$bar", Array[Class[_]](classOf[SigmaProp])) { (obj, args) =>
          obj.asInstanceOf[SigmaProp].$bar$bar(args(0).asInstanceOf[SigmaProp])
        },
        mkMethod(clazz, "isValid", Array[Class[_]]()) { (obj, args) =>
          obj.asInstanceOf[SigmaProp].isValid
        },
        mkMethod(clazz, "propBytes", Array[Class[_]]()) { (obj, args) =>
          obj.asInstanceOf[SigmaProp].propBytes
        },
        mkMethod(clazz, "$amp$amp", Array[Class[_]](classOf[SigmaProp])) { (obj, args) =>
          obj.asInstanceOf[SigmaProp].$amp$amp(args(0).asInstanceOf[SigmaProp])
        }
      )
    )
  }

  { val clazz = classOf[special.sigma.BigInt]
    val paramTypes = Array[Class[_]](clazz)
    registerClassEntry(clazz,
      methods = Map(
        mkMethod(clazz, "add", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].add(args(0).asInstanceOf[BigInt])
        },
        mkMethod(clazz, "max", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].max(args(0).asInstanceOf[BigInt])
        },
        mkMethod(clazz, "min", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].min(args(0).asInstanceOf[BigInt])
        },
        mkMethod(clazz, "subtract", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].subtract(args(0).asInstanceOf[BigInt])
        },
        mkMethod(clazz, "multiply", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].multiply(args(0).asInstanceOf[BigInt])
        },
        mkMethod(clazz, "mod", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].mod(args(0).asInstanceOf[BigInt])
        },
        mkMethod(clazz, "divide", paramTypes) { (obj, args) =>
          obj.asInstanceOf[BigInt].divide(args(0).asInstanceOf[BigInt])
        }
      )
    )
  }

  { val clazz = classOf[CollBuilder]
    registerClassEntry(clazz,
      methods = Map(
        mkMethod(clazz, "xor", Array[Class[_]](classOf[Coll[_]], classOf[Coll[_]])) { (obj, args) =>
          obj.asInstanceOf[CollBuilder].xor(
            args(0).asInstanceOf[Coll[Byte]],
            args(1).asInstanceOf[Coll[Byte]])
        },
        mkMethod(clazz, "fromItems", Array[Class[_]](classOf[Seq[_]], classOf[RType[_]])) { (obj, args) =>
          obj.asInstanceOf[CollBuilder].fromItems[Any](
            args(0).asInstanceOf[Seq[Any]]: _*)(args(1).asInstanceOf[RType[Any]])
        },
        mkMethod(clazz, "replicate", Array[Class[_]](classOf[Int], classOf[Object], classOf[RType[_]])) { (obj, args) =>
          obj.asInstanceOf[CollBuilder].replicate(args(0).asInstanceOf[Int],
            args(1).asInstanceOf[Any])(args(2).asInstanceOf[RType[Any]])
        }
      )
    )
  }


  //  { val clazz = classOf[special.collection.Coll[_]]
//    registerClassEntry(clazz,
//      methods = Map(
//      {
//        val paramTypes: Seq[Class[_]] = Array(classOf[Int], classOf[java.lang.Object])
//        ("updated", paramTypes) ->
//          new SRMethod(clazz, "updated", paramTypes) {
//            override def invoke(obj: Any, args: AnyRef*): AnyRef = obj match {
//              case obj: Coll[a] =>
//                obj.updated(args(0).asInstanceOf[Int], args(1).asInstanceOf[a])
//            }
//          }
//      },
//      {
//        val paramTypes: Seq[Class[_]] = Array(classOf[java.lang.Object], classOf[Int])
//        ("indexOf", paramTypes) ->
//          new SRMethod(clazz, "indexOf", paramTypes) {
//            override def invoke(obj: Any, args: AnyRef*): AnyRef = obj match {
//              case obj: Coll[a] =>
//                obj.indexOf(args(0).asInstanceOf[a], args(1).asInstanceOf[Int]).asInstanceOf[AnyRef]
//            }
//          }
//      },
//      {
//        val paramTypes: Seq[Class[_]] = Array(classOf[Function1[_, _]], classOf[RType[_]])
//        ("flatMap", paramTypes) ->
//          new SRMethod(clazz, "flatMap", paramTypes) {
//            override def invoke(obj: Any, args: AnyRef*): AnyRef = obj match {
//              case obj: Coll[a] =>
//                obj.flatMap(args(0).asInstanceOf[a => Coll[Any]])(args(1).asInstanceOf[RType[Any]])
//            }
//          }
//      }
//      )
//    )
//  }
}
