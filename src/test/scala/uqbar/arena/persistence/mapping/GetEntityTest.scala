package uqbar.arena.persistence.mapping

import org.junit.Test
import uqbar.arena.persistence.AbstractArenaPersistenceTest
import uqbar.arena.persistence.SessionManager
import uqbar.arena.persistence.testDomain.Celular
import uqbar.arena.persistence.testDomain.Modelo
import org.junit.Assert
import uqbar.arena.persistence.testDomain.Persona
import java.util.Date

class GetEntityTest extends AbstractArenaPersistenceTest {
	
  @Test
	def testGetCelular(){
	  val node = graphDb.createNode();
	  val nac = new Date();
	  val expected = new Celular(Modelo.MOTOROLA_1100, "12345", null, 1.25);
	  
	  node.setProperty("modelo", "MOTOROLA_1100")
	  node.setProperty("numero", "12345")
	  node.setProperty("precioPorMinuto", 1.25D)
	  
	  
	  
	  val actual = SessionManager.runInSession({
	    val session = SessionManager.currentSession;
	    session.get(classOf[Celular], node.getId().intValue);
	  })
	  
	  Assert.assertEquals(expected, actual)
	}
}