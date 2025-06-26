package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);
//    added
    Profile getProfileByUserId(int userId);
    void updateProfile(Profile profile);
}
