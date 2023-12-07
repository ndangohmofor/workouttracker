package com.meufty.workoutplanner.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;

import java.io.IOException;
import java.util.Base64;

public class UserProfileDeserializer extends StdDeserializer<UserProfile> {

    public UserProfileDeserializer() {
        this(null);
    }

    public UserProfileDeserializer(Class<?> vc) {
        super(vc);
    }
    @Override
    public UserProfile deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        UserProfile profile = new UserProfile();

        if (node.has("id") && !node.get("id").isNull()) {
            profile.setId(node.get("id").asLong());
        }
        if (node.has("userId") && !node.get("userId").isNull()) {
            profile.setUserId(node.get("userId").asLong());
        }
        if (node.has("firstName") && !node.get("firstName").isNull()) {
            profile.setFirstName(node.get("firstName").asText());
        }
        if (node.has("lastName") && !node.get("lastName").isNull()) {
            profile.setLastName(node.get("lastName").asText());
        }
        if (node.has("middleName") && !node.get("middleName").isNull()) {
            profile.setMiddleName(node.get("middleName").asText());
        }
        if (node.has("preferredName") && !node.get("preferredName").isNull()) {
            profile.setPreferredName(node.get("preferredName").asText());
        }
        if (node.has("username") && !node.get("username").isNull()) {
            profile.setUsername(node.get("username").asText());
        }
        if (node.has("goal") && !node.get("goal").isNull()) {
            profile.setGoal(node.get("goal").asText());
        }
        if (node.has("role") && !node.get("role").isNull()) {
            profile.setRole(UserRole.valueOf(node.get("role").asText()));
        }
        if (node.has("profilePhoto") && !node.get("profilePhoto").isNull()) {
            String profilePhotoBase64 = node.get("profilePhoto").asText();
            System.out.println("photo" + profilePhotoBase64);
            byte[] profilePhoto = Base64.getDecoder().decode(profilePhotoBase64);
            profile.setProfilePhoto(profilePhoto);
        }
        return profile;
    }
}
