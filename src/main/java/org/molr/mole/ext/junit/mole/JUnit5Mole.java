package org.molr.mole.ext.junit.mole;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;
import org.molr.commons.domain.*;
import org.molr.mole.core.tree.AbstractJavaMole;
import org.molr.mole.core.tree.MissionExecutor;
import org.molr.mole.ext.junit.util.Junit5Missions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

public class JUnit5Mole extends AbstractJavaMole {

    private final Launcher launcher = LauncherFactory.create();

    private final Map<Mission, JUnit5Mission> missions;
    private final Map<Mission, MissionRepresentation> missionRepresentations;
    private final Map<MissionHandle, Junit5MissionExecutor> missionLaunchers = new ConcurrentHashMap<>();


    public JUnit5Mole(Set<JUnit5Mission> missions) {
        this.missions = missions.stream().collect(toImmutableMap(m -> new Mission(m.name()), m -> m));
        this.missionRepresentations = this.missions.entrySet().stream().collect(toImmutableMap(e -> e.getKey(), e -> representationOf(e.getValue())));
    }

    private MissionRepresentation representationOf(JUnit5Mission mission) {
        TestPlan testPlan = launcher.discover(mission.request());
        return Junit5Missions.representationFrom(mission.name(), testPlan);
    }


    @Override
    public Set<Mission> availableMissions() {
        return this.missions.keySet();
    }

    @Override
    public MissionRepresentation representationOf(Mission mission) {
        return this.missionRepresentations.get(mission);
    }

    @Override
    public MissionParameterDescription parameterDescriptionOf(Mission mission) {
        return MissionParameterDescription.empty();
    }

    @Override
    protected MissionExecutor instantiate(Mission mission, Map<String, Object> params) {
        return new Junit5MissionExecutor(missions.get(mission), missionRepresentations.get(mission));
    }


}
