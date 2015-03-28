package uqbar.arena.persistence

import org.uqbar.commons.model.Entity
import org.uqbar.commons.model.Home
import scala.collection.JavaConversions._
import java.util.List
import java.lang.Exception
import java.lang.RuntimeException

class PersistentHomeImpl[T <: Entity] {
  
	def searchByExample(example:T):List[T] = {
	  SessionManager.runInSession( {s:Session => s.searchByExample(example)})
	}
	
	def checkEntityType(home:Home[T]) = {
	  val entityType = home.getEntityType();
	  if(entityType == null)
	    throw new RuntimeException("El home:" + home.getClass().getCanonicalName() + " esta devolviendo un tipo de entidad nulo");
	  else
	    entityType;
	}
	
	def allInstances(home:Home[T]):List[T] = {
	  SessionManager.runInSession( {s:Session => s.getAll(checkEntityType(home))})
	}

	def searchById(id:Int,home:Home[T]):T={
  	  SessionManager.runInSession( {s:Session => s.get(checkEntityType(home), id)})
	}
	
	def create(entity:T){
	  SessionManager.runInSession( {s:Session => s.save(entity)})
	}

	def update(entity:T){
	  SessionManager.runInSession( {s:Session => s.save(entity)})
	}

	def delete(entity:T){
	  SessionManager.runInSession( {s:Session => s.delete(entity)})
	}
}