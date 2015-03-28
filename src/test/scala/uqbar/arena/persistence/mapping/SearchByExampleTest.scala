package uqbar.arena.persistence.mapping

import uqbar.arena.persistence.AbstractArenaPersistenceTest
import org.junit.Test
import uqbar.arena.persistence.SessionManager
import uqbar.arena.persistence.testDomain.Persona
import uqbar.arena.persistence.testDomain.Celular
import uqbar.arena.persistence.testDomain.Modelo
import java.util.Date
import org.junit.Assert
import org.neo4j.graphdb.Direction
import scala.collection.JavaConversions._

class SearchByExampleTest extends AbstractArenaPersistenceTest {

  @Test
  def testCelular() {
    val cel = new Celular(Modelo.MOTOROLA_1100, "12345", null, 1.25);
    val cel2 = new Celular(Modelo.MOTOROLA_1100, "44444", null, 1.25);
    val cel3 = new Celular(Modelo.MOTOROLA_1100, "55555", null, 2.25);
    val cel4 = new Celular(Modelo.MOTOROLA_1100, "66666", null, 2.25);
    val cel5 = new Celular(Modelo.IPHONE, "66666", null, 2.25);

    SessionManager.runInSession({
      SessionManager.currentSession.save(cel);
      SessionManager.currentSession.save(cel2);
      SessionManager.currentSession.save(cel3);
      SessionManager.currentSession.save(cel4);
      SessionManager.currentSession.save(cel5);
    });

    var result:List[Celular] = null
    
    SessionManager.runInSession({
      val example = new Celular(Modelo.MOTOROLA_1100, null, null, null);
      result = SessionManager.currentSession.searchByExample(example);
    });
    
    Assert.assertEquals(4, result.size)
    Assert.assertTrue(result.contains(cel))
    Assert.assertTrue(result.contains(cel2))
    Assert.assertTrue(result.contains(cel3))
    Assert.assertTrue(result.contains(cel4))
    
    SessionManager.runInSession({
      val example = new Celular(Modelo.MOTOROLA_1100, null, null, 1.25);
      result = SessionManager.currentSession.searchByExample(example);
    });
    
    Assert.assertEquals(2, result.size)
    Assert.assertTrue(result.contains(cel))
    Assert.assertTrue(result.contains(cel2))
    
    SessionManager.runInSession({
      val example = new Celular(null, null, null, 2.25);
      result = SessionManager.currentSession.searchByExample(example);
    });
    
    Assert.assertEquals(3, result.size)
    Assert.assertTrue(result.contains(cel3))
    Assert.assertTrue(result.contains(cel4))
    Assert.assertTrue(result.contains(cel5))
  }
}