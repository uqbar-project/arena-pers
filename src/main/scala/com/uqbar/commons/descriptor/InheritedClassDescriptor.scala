package com.uqbar.commons.descriptor

import java.lang.reflect.Field
import scala.collection.mutable.HashSet

class InheritedClassDescriptor extends ClassDescriptor {

  override def getFields(clazz: java.lang.Class[_]): Array[Field] = {
    var result : Array[Field] = Array()
    var internalClass : Class[_] = clazz
    while (internalClass != null && internalClass != classOf[Object]) {
   	  result = result ++ newFields(internalClass.getDeclaredFields(), result)
      internalClass = internalClass.getSuperclass()
    }
    result
  }

  private def newFields(fields1 : Array[Field], fields2 : Array[Field] ) : Array[Field] = {
      val existingFieldNames = fields2.map { it => it.getName }
      var newFields : Array[Field] = Array()
      for (possibleField <- fields1) {
        if (!existingFieldNames.contains(possibleField.getName)) {
          newFields = newFields.+:(possibleField)
        }
      }
      newFields
  }
  
}