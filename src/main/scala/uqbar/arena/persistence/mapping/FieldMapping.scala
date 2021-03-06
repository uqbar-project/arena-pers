package uqbar.arena.persistence.mapping

import java.lang.Long
import java.lang.reflect.Type
import java.math.BigDecimal
import java.util.Date

import org.apache.log4j.Logger
import org.neo4j.graphdb.Node
import org.uqbar.commons.utils.ReflectionUtils

import uqbar.arena.persistence.ConfigurationException
import uqbar.arena.persistence.Session
import uqbar.arena.persistence.reflection.TypeWrapper

object FieldMapping {

  def create(name: String, fieldType: Type): FieldMapping = {
    val wrappedType = new TypeWrapper(fieldType)

    //    if (wrappedType.isEnum) {
    //      return new EnumFieldMapping(name, wrappedType)
    //    }
    //
    //    if (wrappedType.isDate) {
    //      return new DateFieldMapping(name, wrappedType)
    //    }
    //
    //    if (wrappedType.isBigDecimal) {
    //      return new BigDecimalFieldMapping(name, wrappedType)
    //    }

    return new FieldMapping(name, wrappedType)
  }
}

class FieldMapping(name: String, wrappedType: TypeWrapper) extends Mapping {
  checkNativeOrEnum()

  def checkNativeOrEnum() {
    if (!wrappedType.isNative && !wrappedType.isEnum && !wrappedType.isBuiltinType) {
      throw new ConfigurationException("La annotation PersistentField es solo aplicable a tipos nativos, Enum, String, java.util.Date o java.math.BigDecimal:" + wrappedType.name)
    }
  }

  def persist(session: Session, node: Node, target: Object) = {
    val v = getValue(target)

    if (v == null) {
      node.removeProperty(this.name)
      session.graphDB.index().forNodes("Attr").remove(node, this.name)
    } else {
      session.graphDB.index().forNodes("Attr-" + target.getClass().toString()).add(node, this.name, v)
      node.setProperty(this.name, v)
    }
  }

  protected def getValue(target: Object): Object = {
    try {
    	convertValueAfterGet(ReflectionUtils.invokeGetter(target, this.name))
    } catch {
      case e : RuntimeException => throw new Exception("Debe revisar el getter y setter de la propiedad " + this.name + " para la entidad " + target.getClass)
    }
  }

  protected def convertValueAfterGet(originalValue: Object): Object = {
    if (wrappedType.isEnum) {
      return if (originalValue == null) null else originalValue.toString()
    }

    if (wrappedType.isDate) {
      return if (originalValue == null) null else originalValue.asInstanceOf[Date].getTime().asInstanceOf[java.lang.Long]
    }

    if (wrappedType.isBigDecimal) {
      return if (originalValue == null) null else originalValue.toString()
    }

    if (wrappedType.isInt) {
      return if (originalValue == null || originalValue == 0) null else originalValue.toString()
    }

    originalValue
  }

  def convertValueBeforeSet(originalValue: Object): Object = {
    if (originalValue == null)
      return null

    if (wrappedType.isEnum) {
      return wrappedType.enumValue(originalValue)
    }

    if (wrappedType.isDate) {
      return new Date(originalValue.asInstanceOf[java.lang.Long])
    }

    if (wrappedType.isBigDecimal) {
      return new BigDecimal(originalValue.asInstanceOf[String])
    }

    if (wrappedType.isInt) {
      return new Integer(originalValue.toString)
    }

    originalValue
  }

  def hidrate(session: Session, node: Node, target: Object) = {
    try {
    	ReflectionUtils.invokeSetter(target, this.name, convertValueBeforeSet(node.getProperty(this.name, null)))
    } catch {
      case e : RuntimeException => throw new Exception("Debe revisar el getter y setter de la propiedad " + this.name + " para la entidad " + target.getClass)
    }
  }

  def query(queryBuilder: QueryBuilder, target: Object) {
    val v = this.getValue(target)
    if (v != null) {
      val stringValue = v.toString()
      if (!stringValue.isEmpty())
        queryBuilder.add(this.name, stringValue)
    }
  }

  def log(): Logger = Logger.getLogger(this.getClass())

}
