package it.einjojo.smpengine.core.team;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TeamImpl implements Team {

    private int id;
    private String name;
    private String displayName;
    private String owner_uuid;
    private Instant created_at;

    public TeamImpl(int id, String name, String displayName, String owner_uuid, Instant created_at) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.owner_uuid = owner_uuid;
        this.created_at = created_at;
    }

    public void getMembers() {

    }


}
