package semicolon.noStrings.data.repositories;

import semicolon.noStrings.data.models.Profile;

import java.util.ArrayList;
import java.util.List;

public class ProfileRepositoryImpl implements ProfileRepository{
    private int count;
    private List<Profile> profiles = new ArrayList<>();
    @Override
    public long count() {
        return count;
    }

    @Override
    public Profile save(Profile profile) {
        if(isNew(profile)) saveNew(profile);else update(profile);

        return profile;
    }

    private void update(Profile profile) {
        profiles.remove(findById(profile.getId()));
        profiles.add(profile);
    }

    private void saveNew(Profile profile) {
        profile.setId(generateId());
        profiles.add(profile);
    }

    private boolean isNew(Profile profile) {
        return profile.getId() == 0;

    }

    private int generateId() {
        return ++count;
    }

    @Override
    public Profile findById(int id) {
        return profiles.get(id-1);
    }
}
