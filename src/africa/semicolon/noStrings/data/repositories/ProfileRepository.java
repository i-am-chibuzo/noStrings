package semicolon.noStrings.data.repositories;

import semicolon.noStrings.data.models.Profile;

public interface ProfileRepository {

    long count();

    Profile save(Profile profile);

    Profile findById(int id);
}
