package uqbar.arena.persistence

import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.kernel.EmbeddedGraphDatabase
import java.lang.Runtime
import scala.sys.ShutdownHookThread
import scala.util.DynamicVariable

object SessionManager {
  protected var graphDB: GraphDatabaseService = null;
  protected var testMode = false
//  protected var _session = new DynamicVariable[Session](null);
  protected var session:Session = null
  
  def startDB() {
    if (!testMode) {
      graphDB = new EmbeddedGraphDatabase("./target/graphDB/")
      registerShutDownHook()
    }
    session = new Session(graphDB)
  }

  def runInSession[T](body: Session => T): T = {
    runInSession({body.apply(currentSession)})
  }

  
  def runInSession[T](body: => T): T = {
    Configuration.checkStarted()
    var value: T = null.asInstanceOf[T]
    
    try {
      session.beginTransaction()
      value = body
      session.commit();
    } catch {
      case e: Exception => session.rollback(); throw e;
    } finally{
      session.closeTransaction();
    }

    return value;
  }

  def currentSession(): Session = {
    session
  }

  def registerShutDownHook() {
    ShutdownHookThread({ graphDB.shutdown() })
  }

  def testMode(db: GraphDatabaseService) {
    graphDB = db;
    testMode = true;
  }
}