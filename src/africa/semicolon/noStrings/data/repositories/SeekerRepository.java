package semicolon.noStrings.data.repositories;

import semicolon.noStrings.data.models.Seeker;

import java.util.List;

public interface SeekerRepository {


    /**
     * Persists a Seeker to the data store.
     *
     * <p>This method handles both <strong>creating</strong> and <strong>updating</strong>
     * a Seeker in a single call — a pattern sometimes called an "upsert":
     * <ul>
     *   <li>If the Seeker's id is <code>0</code> (the default), a new unique id is
     *       assigned and the Seeker is added to the store.</li>
     *   <li>If the Seeker already has an id that exists in the store, the existing
     *       record is replaced with the new data — effectively an update.</li>
     * </ul>
     *
     * @param seeker the Seeker object to create or update
     * @return the saved Seeker (with its assigned id when newly created)
     */
    Seeker save(Seeker seeker);

    /**
     * Retrieves a Seeker by their unique id.
     *
     * @param id the unique identifier of the Seeker
     * @return the matching Seeker, or null if not found
     */
    Seeker findById(int id);

    /**
     * Retrieves every Seeker currently in the data store.
     *
     * @return a List containing all Seekers
     */
    List<Seeker> findAll();

    /**
     * Removes the Seeker with the specified id from the data store.
     *
     * @param id the unique identifier of the Seeker to delete
     */
    void deleteById(int id);

    /**
     * Removes every Seeker currently stored in the data store.
     *
     * <p>Use this when you want to reset the repository to an empty state
     * without deleting records one-by-one.</p>
     */
    void deleteAll();

    /**
     * Returns the total number of Seekers currently in the data store.
     *
     * @return the count of Seekers
     */
    int count();
}
