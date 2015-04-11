package uqbar.arena.persistence;

import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

public abstract class AbstractArenaPersistenceTest {

	protected GraphDatabaseService graphDb;
	protected Transaction transaction;

	@Before
	public void setUp() throws Exception {
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		SessionManager.testMode(graphDb);
		Configuration.rootPackageName_$eq("uqbar");
		Configuration.configure();
		transaction = graphDb.beginTx();
	}

	@After
	public void tearDown() throws Exception {
		transaction.finish();
		graphDb.shutdown();
		Configuration.clear();
	}

}
