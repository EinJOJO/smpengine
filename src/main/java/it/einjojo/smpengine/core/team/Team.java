package it.einjojo.smpengine.core.team;

import java.time.Instant;

public interface Team {

    String getName();
    String getDisplayName();
    String getOwner_uuid();
    Instant getCreated_at();
}
