package org.molr.mole.ext.junit.util;

import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.molr.commons.domain.Block;
import org.molr.commons.domain.ImmutableMissionRepresentation;
import org.molr.commons.domain.MissionRepresentation;

import java.util.Set;
import java.util.stream.Collectors;

public class Junit5Missions {

    public static final MissionRepresentation representationFrom(Block rootBlock, TestPlan testPlan) {
        return new TestPlanConverter(rootBlock, testPlan).toMissionRepresentation();
    }


    public static final Block blockFrom(TestIdentifier ti) {
        return Block.idAndText(ti.getUniqueId(), ti.getDisplayName());
    }


    private static class TestPlanConverter {

        private final ImmutableMissionRepresentation.Builder builder;
        private final TestPlan testPlan;


        private TestPlanConverter(Block rootBlock, TestPlan testPlan) {
            this.builder = ImmutableMissionRepresentation.builder(rootBlock);
            this.testPlan = testPlan;
            addChildren();
        }

        private MissionRepresentation toMissionRepresentation() {
            return builder.build();
        }

        private void addChildren() {
            Set<TestIdentifier> flattenedRoots = testPlan.getRoots().stream().flatMap(root ->
                    testPlan.getChildren(root).stream()).collect(Collectors.toSet());
            addAsChildren(builder.root(), flattenedRoots);
        }

        private void addAsChildren(Block parent, Set<TestIdentifier> children) {
            for (TestIdentifier ti : children) {
                Block childBlock = blockFrom(ti);
                builder.parentToChild(parent, childBlock);
                addAsChildren(childBlock, testPlan.getChildren(ti));
            }
        }


    }


}
