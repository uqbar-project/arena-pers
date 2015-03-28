package uqbar.arena.persistence.reflection

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

import uqbar.arena.persistence.annotations.PersistentClass

class TypeWrapper(t: Type) {
  def isNative(): Boolean = {
    runIfClass({ c: Class[_] => c.isPrimitive() }, { _ => false })
  }

  def runIfClass[T](a: Function[Class[_], T], b: Function[Type, T]): T = {
    return t match {
      case c: Class[_] => a.apply(c);
      case c => b.apply(c);
    }
  }

  def isBuiltinType(): Boolean = {
    runIfClass({ c: Class[_] =>
      c.getName() match {
        case "java.lang.String" => true
        case "java.util.Date" => true
        case "java.lang.Boolean" => true
        case "java.lang.Char" => true
        case "java.lang.Integer" => true
        case "java.lang.Float" => true
        case "java.lang.Double" => true
        case "java.math.BigDecimal" => true
        case _ => false
      }
    }, { _ => false })
  }

  def isCollectionOfPersistent(): Boolean = {
    return t match {
      case c: ParameterizedType => {
        val rawType = c.getRawType()
        val y = rawType match {
          case z: Class[_] => classOf[java.util.Collection[_]].isAssignableFrom(z)
        }

        if (c.getActualTypeArguments().length == 0)
          return false

        val x = c.getActualTypeArguments()(0) match {
          case z: Class[_] => new TypeWrapper(z).isPersistent
        }

        return y && x
      }
      case c => false;
    }
  }

  def isPersistent(): Boolean = {
    runIfClass({ c: Class[_] => c.getAnnotation(classOf[PersistentClass]) != null }, { _ => false })
  }

  def isDate(): Boolean = {
    runIfClass({ c: Class[_] => c.getName() == "java.util.Date" }, { _ => false })
  }

  def isBigDecimal(): Boolean = {
    runIfClass({ c: Class[_] => c.getName() == "java.math.BigDecimal" }, { _ => false })
  }

  def isEnum(): Boolean = {
    runIfClass({ c: Class[_] => c.isEnum() }, { _ => false })
  }

  def enumValue[T <: Enum[T]](v: Any): T = {
    runIfClass({
      c: Class[_] => Enum.valueOf(c.asInstanceOf[Class[T]], v.toString)
    },
      {
        _ => throw new Exception("Invalid value for enum:" + t + ":" + v)
      })
  }

  def newInstance[T](): T = {
    return t match {
      case c: ParameterizedType => {
        val rawType = c.getRawType()
        rawType match {
          case z: Class[_] => z.newInstance().asInstanceOf[T]
        }
      }
      case c: Class[_] => c.newInstance().asInstanceOf[T]
    }
  }

  def name(): String = {
    return t match {
      case c: Class[_] => c.getName()
      case x => "" + x
    }
  }
}