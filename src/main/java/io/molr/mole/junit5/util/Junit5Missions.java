package io.molr.mole.junit5.util;

import com.google.common.collect.Iterables;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import io.molr.commons.domain.Block;
import io.molr.commons.domain.ImmutableMissionRepresentation;
import io.molr.commons.domain.MissionRepresentation;

import java.util.Set;

public class Junit5Missions {

    public static final MissionRepresentation representationFrom(String missionName, TestPlan testPlan) {
        return new TestPlanConverter(missionName, testPlan).toMissionRepresentation();
    }


    public static final Block blockFrom(TestIdentifier ti) {
        return Block.idAndText(ti.getUniqueId(), ti.getDisplayName());
    }

    public static final Block blockFrom(String name, TestIdentifier ti) {
        return Block.idAndText(ti.getUniqueId(), name);
    }

    private static class TestPlanConverter {

        private final ImmutableMissionRepresentation.Builder builder;
        private final TestPlan testPlan;

        private TestPlanConverter(String missionName, TestPlan testPlan) {
            this.testPlan = testPlan;
            this.builder = ImmutableMissionRepresentation.builder(blockFrom(missionName, rootId()));
            addAsChildren(builder.root(), this.testPlan.getChildren(rootId()));
        }

        private MissionRepresentation toMissionRepresentation() {
            return builder.build();
        }

        private void addAsChildren(Block parent, Set<TestIdentifier> children) {
            for (TestIdentifier ti : children) {
                Block childBlock = blockFrom(ti);
                builder.parentToChild(parent, childBlock);
                addAsChildren(childBlock, testPlan.getChildren(ti));
            }
        }

        private TestIdentifier rootId() {
            int numberOfRoots = testPlan.getRoots().size();
            if (numberOfRoots != 1) {
                throw new IllegalArgumentException("Currently only test plans with one root are supported! However the given test plan has " + numberOfRoots + " roots: " + testPlan.getRoots());
            }
            return Iterables.getOnlyElement(testPlan.getRoots());
        }


    }


}
