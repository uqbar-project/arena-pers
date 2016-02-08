package uqbar.arena.persistence

import org.uqbar.commons.model.Entity
import org.uqbar.commons.model.Repo
import scala.collection.JavaConversions._
import java.util.List
import java.lang.Exception
import java.lang.RuntimeException

class PersistentRepoImpl[T <: Entity] {
  
	def searchByExample(example:T):List[T] = {
	  SessionManager.runInSession( {s:Session => s.searchByExample(example)})
	}
	
	def checkEntityType(repo:Repo[T]) = {
	  val entityType = repo.getEntityType()
	  if(entityType == null)
	    throw new RuntimeException("El repo:" + repo.getClass().getCanonicalName() + " esta devolviendo un tipo de entidad nulo")
	  else
	    entityType
	}
	
	def allInstances(repo:Repo[T]):List[T] = {
	  SessionManager.runInSession( {s:Session => s.getAll(checkEntityType(repo))})
	}

	def searchById(id:Int,repo:Repo[T]):T={
  	  SessionManager.runInSession( {s:Session => s.get(checkEntityType(repo), id)})
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