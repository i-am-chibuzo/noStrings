package semicolon.noStrings.data.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import semicolon.noStrings.data.models.Complexion;
import semicolon.noStrings.data.models.Gender;
import semicolon.noStrings.data.models.Seeker;
import semicolon.noStrings.data.repositories.SeekerRepository;
import semicolon.noStrings.data.repositories.SeekerRepositoryImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SeekerRepositoryImplTest {

    // The repository instance we will test — created fresh before each test
    private SeekerRepository seekerRepository;

    /**
     * @BeforeEach runs this method before EVERY single test method below.
     * It creates a brand-new, empty repository so that tests do not
     * share state with each other (this is called "test isolation").
     */
    @BeforeEach
    void setUp() {
        seekerRepository = new SeekerRepositoryImpl();
    }

    // ------------------------------------------------------------------ //
    //  Helper — builds a ready-to-use Seeker so tests stay concise        //
    // ------------------------------------------------------------------ //

    private Seeker buildSeeker(String name, Gender gender) {
        Seeker seeker = new Seeker();
        seeker.setName(name);
        seeker.setGender(gender);
        seeker.setDateOfBirth(LocalDate.of(1995, 6, 15));
        seeker.setCurrentLocation("Lagos");
        seeker.setHeightInCM(170);
        seeker.setComplexion(Complexion.DARK);
        return seeker;
    }

    // ============================== save() — CREATE path ================ //

    /**
     * After saving a brand-new Seeker (id == 0), the returned object must not
     * be null and must have been given a positive id by the repository.
     */
    @Test
    void testSave_assignsIdToNewSeeker() {
        Seeker seeker = buildSeeker("Ada", Gender.FEMALE);

        Seeker saved = seekerRepository.save(seeker);

        assertNotNull(saved, "Saved Seeker should not be null");
        assertTrue(saved.getId() > 0, "Saved Seeker should have an id greater than 0");
    }

    /**
     * Each brand-new Seeker must receive a different (unique) id.
     */
    @Test
    void testSave_assignsUniqueIdsToMultipleSeekers() {
        Seeker first  = seekerRepository.save(buildSeeker("Ada",   Gender.FEMALE));
        Seeker second = seekerRepository.save(buildSeeker("Emeka", Gender.MALE));

        assertNotEquals(first.getId(), second.getId(),
                "Two different Seekers must not share the same id");
    }

    /**
     * Saving a new Seeker should increase the repository count by one.
     */
    @Test
    void testSave_newSeeker_increasesCount() {
        assertEquals(0, seekerRepository.count(), "Repository should start empty");

        seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        assertEquals(1, seekerRepository.count(), "Count should be 1 after saving one Seeker");
    }

    // ============================== save() — UPDATE path ================ //

    /**
     * Calling save() with a Seeker that already has an id (was previously saved)
     * should update the stored fields — NOT add a duplicate entry.
     *
     * <p>This demonstrates that save() acts as an "upsert":
     * create when new, update when the id already exists.
     */
    @Test
    void testSave_existingSeeker_updatesStoredData() {
        // First, create the Seeker (id assigned by repository)
        Seeker saved = seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        // Change some fields on the returned object
        saved.setName("Ada Updated");
        saved.setCurrentLocation("Abuja");

        // Pass the modified Seeker back to save() — it carries the id now
        Seeker updated = seekerRepository.save(saved);

        assertNotNull(updated, "save() should return the updated Seeker");
        assertEquals("Ada Updated", seekerRepository.findById(saved.getId()).getName(),
                "Name should reflect the update");
        assertEquals("Abuja", seekerRepository.findById(saved.getId()).getCurrentLocation(),
                "Location should reflect the update");
    }

    /**
     * Updating via save() must NOT increase the count — the old entry is
     * replaced, not a new one added.
     */
    @Test
    void testSave_existingSeeker_doesNotIncreaseCount() {
        Seeker saved = seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        saved.setName("Ada v2");
        seekerRepository.save(saved); // update

        assertEquals(1, seekerRepository.count(),
                "Count should remain 1 after updating via save()");
    }

    /**
     * After updating via save(), findById() must return the new data,
     * not the original data.
     */
    @Test
    void testSave_existingSeeker_oldDataIsNoLongerStored() {
        Seeker saved = seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));
        int id = saved.getId();

        saved.setName("NewName");
        seekerRepository.save(saved);

        assertEquals("NewName", seekerRepository.findById(id).getName(),
                "findById() should return the updated name, not the old one");
    }

    // ============================ findById() ============================ //

    /**
     * A Seeker that was saved can be retrieved by the id that was assigned to it.
     */
    @Test
    void testFindById_returnsSavedSeeker() {
        Seeker saved = seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        Seeker found = seekerRepository.findById(saved.getId());

        assertNotNull(found, "Should find the Seeker that was saved");
        assertEquals(saved.getId(),   found.getId(),   "Id should match");
        assertEquals(saved.getName(), found.getName(), "Name should match");
    }

    /**
     * Searching for an id that does not exist should return null,
     * NOT throw an exception.
     */
    @Test
    void testFindById_returnsNullForNonExistentId() {
        Seeker found = seekerRepository.findById(999);

        assertNull(found, "Should return null when no Seeker has the given id");
    }

    // ============================= findAll() ============================ //

    /**
     * An empty repository should return an empty list, not null.
     */
    @Test
    void testFindAll_returnsEmptyListWhenNoSeekersExist() {
        List<Seeker> all = seekerRepository.findAll();

        assertNotNull(all,        "findAll() should never return null");
        assertTrue(all.isEmpty(), "List should be empty when no Seekers have been saved");
    }

    /**
     * All previously saved Seekers must appear in the list returned by findAll().
     */
    @Test
    void testFindAll_returnsAllSavedSeekers() {
        seekerRepository.save(buildSeeker("Ada",   Gender.FEMALE));
        seekerRepository.save(buildSeeker("Emeka", Gender.MALE));
        seekerRepository.save(buildSeeker("Zara",  Gender.FEMALE));

        List<Seeker> all = seekerRepository.findAll();

        assertEquals(3, all.size(), "findAll() should return every saved Seeker");
    }

    /**
     * The list returned by findAll() must be a separate copy, so changes
     * to it do not corrupt the internal data store.
     */
    @Test
    void testFindAll_returnsDefensiveCopy() {
        seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        List<Seeker> all = seekerRepository.findAll();
        all.clear(); // clear the returned list

        assertEquals(1, seekerRepository.count(),
                "Clearing the returned list should NOT affect the repository");
    }

    // =========================== deleteById() =========================== //

    /**
     * A Seeker that has been deleted should no longer be findable by id.
     */
    @Test
    void testDeleteById_removesSeeker() {
        Seeker saved = seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        seekerRepository.deleteById(saved.getId());

        assertNull(seekerRepository.findById(saved.getId()),
                "Deleted Seeker should no longer be retrievable");
    }

    /**
     * After deletion the count must decrease by one.
     */
    @Test
    void testDeleteById_decreasesCount() {
        Seeker saved = seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));
        seekerRepository.save(buildSeeker("Emeka", Gender.MALE));

        seekerRepository.deleteById(saved.getId());

        assertEquals(1, seekerRepository.count(),
                "Count should drop to 1 after deleting one of two Seekers");
    }

    /**
     * Deleting with an id that was never saved should not throw an exception
     * and must not alter the stored data.
     */
    @Test
    void testDeleteById_doesNothingForNonExistentId() {
        seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));

        assertDoesNotThrow(() -> seekerRepository.deleteById(999),
                "deleteById() should not throw when the id does not exist");

        assertEquals(1, seekerRepository.count(),
                "Existing Seekers should be unaffected by a delete of a non-existent id");
    }

    // ============================ deleteAll() =========================== //

    /**
     * deleteAll() should remove every stored Seeker in one call.
     */
    @Test
    void testDeleteAll_removesAllSeekers() {
        seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));
        seekerRepository.save(buildSeeker("Emeka", Gender.MALE));
        seekerRepository.save(buildSeeker("Zara", Gender.FEMALE));

        seekerRepository.deleteAll();

        assertEquals(0, seekerRepository.count(),
                "Count should be 0 after deleteAll()");
        assertTrue(seekerRepository.findAll().isEmpty(),
                "findAll() should return an empty list after deleteAll()");
    }

    /**
     * Calling deleteAll() on an already-empty repository should be safe.
     */
    @Test
    void testDeleteAll_onEmptyRepository_doesNotThrow() {
        assertDoesNotThrow(() -> seekerRepository.deleteAll(),
                "deleteAll() should not throw when repository is already empty");
        assertEquals(0, seekerRepository.count(),
                "Count should remain 0 after deleteAll() on empty repository");
    }

    /**
     * After deleteAll(), repository should still accept new saves normally.
     */
    @Test
    void testDeleteAll_thenSave_stillWorks() {
        seekerRepository.save(buildSeeker("Ada", Gender.FEMALE));
        seekerRepository.deleteAll();

        Seeker savedAgain = seekerRepository.save(buildSeeker("New Seeker", Gender.MALE));

        assertNotNull(savedAgain, "save() should still work after deleteAll()");
        assertTrue(savedAgain.getId() > 0, "Saved Seeker should still receive a valid id");
        assertEquals(1, seekerRepository.count(),
                "Count should be 1 after saving one Seeker post-deleteAll()");
    }

    // ============================== count() ============================= //

    /**
     * A fresh repository must report zero Seekers.
     */
    @Test
    void testCount_isZeroForEmptyRepository() {
        assertEquals(0, seekerRepository.count(), "A new repository must have a count of 0");
    }

    /**
     * The count must reflect the exact number of saves performed.
     */
    @Test
    void testCount_reflectsNumberOfSavedSeekers() {
        seekerRepository.save(buildSeeker("Ada",   Gender.FEMALE));
        seekerRepository.save(buildSeeker("Emeka", Gender.MALE));
        seekerRepository.save(buildSeeker("Zara",  Gender.FEMALE));

        assertEquals(3, seekerRepository.count(), "Count must equal the number of saved Seekers");
    }
}

