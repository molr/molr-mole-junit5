package io.molr.mole.junit5.mole;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;
import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionParameterDescription;
import io.molr.commons.domain.MissionRepresentation;
import io.molr.mole.core.tree.AbstractJavaMole;
import io.molr.mole.core.tree.MissionExecutor;
import io.molr.mole.junit5.util.Junit5Missions;

import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class JUnit5Mole extends AbstractJavaMole {

    private final Launcher launcher = LauncherFactory.create();

    private final Map<Mission, JUnit5Mission> missions;
    private final Map<Mission, MissionRepresentation> missionRepresentations;

    public JUnit5Mole(Set<JUnit5Mission> missions) {
        super(extractMissions(missions));
        this.missions = missions.stream().collect(toMap(m -> new Mission(m.name()), m -> m));
        this.missionRepresentations = this.missions.entrySet().stream().collect(toMap(e -> e.getKey(), e -> representationOf(e.getValue())));
    }

    private static Set<Mission> extractMissions(Set<JUnit5Mission> missions) {
        requireNonNull(missions, "missions cannot be null");
        return missions.stream().map(jum -> new Mission(jum.name())).collect(toSet());
    }

    private MissionRepresentation representationOf(JUnit5Mission mission) {
        TestPlan testPlan = launcher.discover(mission.request());
        return Junit5Missions.representationFrom(mission.name(), testPlan);
    }

    @Override
    public MissionRepresentation missionRepresentationOf(Mission mission) {
        return this.missionRepresentations.get(mission);
    }

    @Override
    public MissionParameterDescription missionParameterDescriptionOf(Mission mission) {
        return MissionParameterDescription.empty();
    }

    @Override
    protected MissionExecutor executorFor(Mission mission, Map<String, Object> params) {
        return new Junit5MissionExecutor(missions.get(mission), missionRepresentations.get(mission));
    }

}
