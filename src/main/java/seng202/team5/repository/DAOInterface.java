package seng202.team5.repository;

import seng202.team5.exceptions.DuplicateEntryException;

import java.util.List;

/**
 * Data Access Objects interface for common database operations. Inspired by
 * (and indeed a copy of) the version given in the
 * <a href="https://eng-git.canterbury.ac.nz/men63/seng202-advanced-fx-public">advanced JavaFX lab</a>.
 *
 * @author Sean Reitsma
 */
public interface DAOInterface<T> {
    /**
     * Gets all of T from the database
     * @return List of all objects type T from the database
     */
    List<T> getAll();

    /**
     * Gets a single object of type T from the database by id
     * @param id id of object to get
     * @return Object of type T that has id given
     */
    T getOne(int id);

    /**
     * Adds a single object of type T to database
     * @param toAdd object of type T to add
     * @throws DuplicateEntryException if the object already exists
     * @return object insert id if inserted correctly
     */
    int add(T toAdd) throws DuplicateEntryException;

    /**
     * Deletes and object from database that matches id given
     * @param id id of object to delete
     */
    void delete(int id);

    /**
     * Updates an object in the database
     * @param toUpdate Object that needs to be updated (this object must be able to identify itself and its previous self)
     */
    void update(T toUpdate);
}
