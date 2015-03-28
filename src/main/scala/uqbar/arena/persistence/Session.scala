package uqbar.arena.persistence

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import org.neo4j.graphdb.GraphDatabaseService
import org.uqbar.commons.model.Entity
import org.neo4j.graphdb.Transaction
import scala.collection.JavaConversions._
import org.neo4j.graphdb.Node
import scala.collection.immutable.Nil

class Session(graphDB: GraphDatabaseService) {
  protected var entities: Map[String, Object] = new HashMap[String, Object]()
  protected var transaction: Transaction = null

  def graphDB(): GraphDatabaseService = graphDB

  def beginTransaction() = { transaction = graphDB.beginTx() }
  
  def commit() = if(transaction!=null)transaction.success()
  def closeTransaction() = if(transaction!=null)transaction.finish()
  def rollback() = if(transaction!=null)transaction.failure()

  def registerEntity(e: Entity) {
    entities.put(e.getClass().getCanonicalName() + "#" + e.getId(), e)
  }
  
  def evictAll(){
    entities.clear()
  }

  def findEntity(clazzName: String, id: Int): Option[Object] = {
    entities.get(clazzName + "#" + id)
  }

  def findOrCreate(clazzName: String, id: Int): Object = {
    findEntity(clazzName, id) match {
      case Some(x) => return x
      case None => createEntity(clazzName, id)
    }
  }

  def get[T](clazzName: String, id: Int): T = {
    return findOrCreate(clazzName, id).asInstanceOf[T]
  }
  
  def get[T](clazz: Class[T], id: Int): T = {
    return findOrCreate(clazz.getName, id).asInstanceOf[T]
  }

  def delete(obj:Entity){
    assert(obj != null, "El objeto a borrar no puede ser null")

    val node = graphDB.getNodeById(obj.getId().longValue());
    for(r <- node.getRelationships())
      r.delete();
    node.delete();
  }
  
  def save(obj:Entity){
    assert(obj != null, "El objeto a salvar no puede ser null")
    val mapping = Configuration.mappingFor(obj)
    
    assert(mapping != null, "No se encontro el mapping para la clase:" + obj.getClass().getName() + " revisar los annotations")
    mapping.persist(this, obj);
  }
  
  def createEntity(clazzName: String, id: Int): Object = {
    val clazz = Class.forName(clazzName);
    val obj = clazz.newInstance().asInstanceOf[Entity]
    obj.setId(id)

    this.registerEntity(obj)

    Configuration.mappingFor(obj).hidrate(this, obj)
    
    return obj
  }
  
  def getAll[T](clazz:Class[T]):List[T] = {
    assert(clazz != null, "La clase a buscar no puede ser null")
    
    val index = graphDB.index().forNodes("CLASS")
    
    assert(index != null, "El indice para CLASS es null !!!!")
    val indexHits = index.get("clazzName", clazz.getName())
    
    assert(indexHits != null, "El indexHits para " + clazz.getName() + " es null!!!!")
    
    val nodes = indexHits.iterator()
    convertTo(clazz,nodes)
  }
  
  def convertTo[T](clazz:Class[T], nodes: Iterator[Node]):List[T] = {
    var r:List[T] = List[T]();
    for(n <- nodes){
    	val c:Any = this.get(n.getProperty("clazzName").toString(), n.getId().intValue) 
    	r = c.asInstanceOf[T] :: r;
    }
    r
  }
  
  def searchByExample[T <: Entity](example:T):List[T] = {
    assert(example != null, "El objeto ejemplo no puede ser null")
    Configuration.mappingFor(example).searchByExample(example,this) 
  }
}