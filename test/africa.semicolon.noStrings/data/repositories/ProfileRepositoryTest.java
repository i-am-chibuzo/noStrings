package data.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import semicolon.noStrings.data.models.Profile;
import semicolon.noStrings.data.repositories.ProfileRepository;
import semicolon.noStrings.data.repositories.ProfileRepositoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfileRepositoryTest {
    private ProfileRepository profileRepository;

    @BeforeEach
    public void setUp(){
        profileRepository = new ProfileRepositoryImpl();
    }

    @Test
    public void saveOneProfile_countIsOneTest(){
        assertEquals(0, profileRepository.count());
        profileRepository.save(new Profile());
        assertEquals(1, profileRepository.count());
    }

    @Test
    public void saveX_findXReturnsXTest(){
        Profile profile = new Profile();
        profile.setBio("Good guy");
        Profile savedProfile = profileRepository.save(profile);
        Profile foundProfile = profileRepository.findById(savedProfile.getId());

        assertEquals(savedProfile, foundProfile);
        assertEquals("Good guy", foundProfile.getBio());
    }

    @Test
    public void saveXYZ_findXZ_returnsXZTest(){
        Profile profileOne = new Profile();
        profileOne.setBio("Good guy");
        Profile savedProfileOne = profileRepository.save(profileOne);
        Profile profileTwo= new Profile();
        profileTwo.setBio("Another Good guy");
        Profile savedProfileTwo = profileRepository.save(profileTwo);
        Profile profileThree= new Profile();
        profileThree.setBio("Another Good Badt guy");
        Profile savedProfileThree = profileRepository.save(profileThree);

        assertEquals(savedProfileOne, profileRepository.findById(savedProfileOne.getId()));
        assertEquals(savedProfileThree, profileRepository.findById(savedProfileThree.getId()));
    }

    @Test
    public void saveXTwice_countIsOneTest(){
        Profile profile = new Profile();
        profile.setBio("Good guy");
        Profile savedProfile = profileRepository.save(profile);
        savedProfile.setBio("Good guy 2");
        profileRepository.save(savedProfile);
        assertEquals(1, profileRepository.count());

    }
}
