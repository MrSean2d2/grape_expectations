package seng202.team5.repository;

import java.util.List;

/**
 * Data Access Objects interface for common database operations. Inspired by
 * (and indeed a copy of) the version given in the
 * <a href="https://eng-git.canterbury.ac.nz/men63/seng202-advanced-fx-public">advanced JavaFX lab</a>.
 *
 * @author Sean Reitsma
 */
public interface DaoInterface<T> {
    /**
     * Gets all objects from the database.
     *
     * @return a list of all T objects in the database
     */
    List<T> getAll();

    /**
     * Get one object by id.
     *
     * @param id the id of the object to retrieve
     * @return the object referenced by id
     */
    T getById(int id);

    /**
     * Add an object to the database.
     *
     * @param object the object to add
     * @return the id of the object added if successful
     */
    int add(T object);

    /**
     * Delete an object from the database by id.
     *
     * @param id the id of the object to delete
     */
    void delete(int id);

    /**
     * Update an object in the database.
     *
     * @param object the object to update (must be able to identify itself)
     */
    void update(T object);
}
