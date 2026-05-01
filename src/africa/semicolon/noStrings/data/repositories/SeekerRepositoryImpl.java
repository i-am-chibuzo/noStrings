package semicolon.noStrings.data.repositories;

import semicolon.noStrings.data.models.Seeker;

import java.util.ArrayList;
import java.util.List;

public class SeekerRepositoryImpl implements SeekerRepository {

    // In-memory list that acts as our data store
    private List<Seeker> seekers = new ArrayList<>();

    // Tracks the next id to assign; increments every time a brand-new Seeker is created
    private int idCounter = 1;

    /**
     * Saves a Seeker to the in-memory list.
     *
     * <p>This single method handles <strong>both creation and updating</strong>:
     *
     * <ul>
     *   <li><strong>Create (id == 0):</strong> When a Seeker arrives without an id
     *       (Java sets {@code int} fields to 0 by default), we know it is brand new.
     *       We assign the next available id and add it to the list.</li>
     *   <li><strong>Update (id &gt; 0):</strong> When the Seeker already carries an id,
     *       we search the list for the matching entry and swap it with the new data.
     *       The count stays the same — we are replacing, not adding.</li>
     * </ul>
     *
     * @param seeker the Seeker to create (id == 0) or update (id > 0)
     * @return the saved Seeker
     */
    @Override
    public Seeker save(Seeker seeker) {
        // --- CREATE path ---
        // An id of 0 means this Seeker has never been saved before
        if (seeker.getId() == 0) {
            // Give the Seeker a unique id, then add it to the list
            seeker.setId(idCounter++);
            seekers.add(seeker);
            return seeker;
        }

        // --- UPDATE path ---
        // The Seeker already has an id — find the existing entry and replace it
        for (int i = 0; i < seekers.size(); i++) {
            if (seekers.get(i).getId() == seeker.getId()) {
                // Swap the old Seeker at this position with the updated one
                seekers.set(i, seeker);
                return seeker;
            }
        }

        // Edge case: id was set manually but was never stored — add as a new entry
        seekers.add(seeker);
        return seeker;
    }

    /**
     * Searches the list for a Seeker whose id matches the given id.
     * A traditional for-each loop is used here to keep things easy to follow.
     *
     * @param id the unique id to search for
     * @return the found Seeker, or null if no Seeker with that id exists
     */
    @Override
    public Seeker findById(int id) {
        // Loop through every Seeker in the list
        for (Seeker seeker : seekers) {
            // Compare the current Seeker's id with the requested id
            if (seeker.getId() == id) {
                return seeker; // Found — return immediately
            }
        }
        // No matching Seeker was found
        return null;
    }

    /**
     * Returns a copy of the entire list of Seekers.
     * Returning a new ArrayList prevents external code from directly
     * modifying the internal list.
     *
     * @return a new List containing all stored Seekers
     */
    @Override
    public List<Seeker> findAll() {
        // Wrap our internal list in a new ArrayList to protect it
        return new ArrayList<>(seekers);
    }

    /**
     * Removes the Seeker with the given id from the list.
     * removeIf() walks the list and removes any element that satisfies
     * the condition — a concise alternative to a manual loop with Iterator.
     *
     * @param id the id of the Seeker to delete
     */
    @Override
    public void deleteById(int id) {
        // Remove every Seeker whose id equals the requested id
        // (in practice only one will match, since ids are unique)
        seekers.removeIf(seeker -> seeker.getId() == id);
    }

    /**
     * Removes all Seekers from the in-memory list in one operation.
     *
     * <p>This is useful in tests and reset flows where we need a "clean slate"
     * repository without iterating through ids one at a time.</p>
     */
    @Override
    public void deleteAll() {
        // clear() empties the list completely; afterwards size() becomes 0
        seekers.clear();
    }

    /**
     * Returns how many Seekers are currently stored.
     * List.size() gives us this in O(1) time.
     *
     * @return the number of Seekers in the data store
     */
    @Override
    public int count() {
        return seekers.size();
    }
}
