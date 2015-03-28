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

class SaveAndGetEntityTest extends AbstractArenaPersistenceTest {

  @Test
  def testCelular() {
    val nac = new Date();
    val per = new Persona("Juan Carlos", "Batman", nac, 125)
    val cel = new Celular(Modelo.MOTOROLA_1100, "12345", per, 1.25);
    val cel2 = new Celular(Modelo.IPHONE, "12345", per, 1.25);
    val cel3 = new Celular(Modelo.SANGSUNG_S2, "12345", per, 1.25);

    per.setPreferido(cel)
    per.getCelulares().add(cel)
    per.getCelulares().add(cel2)
    per.getCelulares().add(cel3)
    
    SessionManager.runInSession({
      SessionManager.currentSession.save(cel);
    });

    SessionManager.currentSession.evictAll();
    var actual:Celular = null;
    
    SessionManager.runInSession({
      actual = SessionManager.currentSession.get(classOf[Celular], 1)
    });
    
    
    Assert.assertEquals(cel, actual)
    Assert.assertEquals(3, actual.getDuenio().getCelulares().size())
    Assert.assertEquals(cel, actual.getDuenio().getCelulares().get(0))
    Assert.assertEquals(cel2, actual.getDuenio().getCelulares().get(1))
    Assert.assertEquals(cel3, actual.getDuenio().getCelulares().get(2))
    Assert.assertEquals(cel.getDuenio(), cel2.getDuenio())
  }
  
  @Test
  def testNullInDate(){
    val perWithNullInDate = new Persona("Juan Carlos", "Batman", null, 125)
    
    SessionManager.runInSession({
      SessionManager.currentSession.save(perWithNullInDate);
    });

    SessionManager.currentSession.evictAll();
    var actual:Persona = null;
    
    SessionManager.runInSession({
      actual = SessionManager.currentSession.get(classOf[Persona], 1)
    });

    Assert.assertEquals(perWithNullInDate, actual)
    Assert.assertNull(perWithNullInDate.getFechaNacimiento())
  }
}