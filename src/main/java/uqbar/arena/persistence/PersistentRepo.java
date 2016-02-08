package uqbar.arena.persistence;

import java.util.List;

import org.uqbar.commons.model.Entity;
import org.uqbar.commons.model.Repo;

public abstract class PersistentRepo<T extends Entity> implements Repo<T> {

	private PersistentRepoImpl<T> homeImpl = new PersistentRepoImpl<T>();

	public T searchById(int id) {
		return homeImpl.searchById(id, this);
	}

	public List<T> searchByExample(T example) {
		return homeImpl.searchByExample(example);
	}

	public List<T> allInstances() {
		return homeImpl.allInstances(this);
	}

	public void create(T object) {
		homeImpl.create(object);
	}

	public void update(T object) {
		homeImpl.update(object);
	}

	public void delete(T object) {
		homeImpl.delete(object);
	}
}
