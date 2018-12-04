package org.molr.mole.ext.junit.mole;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherFactory;
import org.molr.commons.domain.Block;
import org.molr.commons.domain.ImmutableMissionRepresentation;
import org.molr.commons.domain.MissionOutput;
import org.molr.commons.domain.MissionRepresentation;
import org.molr.commons.domain.MissionState;
import org.molr.commons.domain.Result;
import org.molr.commons.domain.RunState;
import org.molr.commons.domain.Strand;
import org.molr.commons.domain.StrandCommand;
import org.molr.commons.util.Exceptions;
import org.molr.mole.core.tree.ConcurrentMissionOutputCollector;
import org.molr.mole.core.tree.MissionExecutor;
import org.molr.mole.core.tree.MissionOutputCollector;
import org.molr.mole.core.tree.tracking.TreeTracker;
import org.molr.mole.ext.junit.util.Junit5Missions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import javax.annotation.concurrent.GuardedBy;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.molr.commons.domain.Placeholders.THROWN;

public class Junit5MissionExecutor implements MissionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Junit5MissionExecutor.class);

    private final Strand singleStrand = Strand.ofId("0");

    private final JUnit5Mission mission;

    private final Object representationLock = new Object();

    @GuardedBy("representationLock")
    private MissionRepresentation missionRepresentation;

    @GuardedBy("representationLock")
    private TreeTracker<Result> resultTracker;


    private final Launcher launcher;
    private final Map<Block, RunState> runStateTracker = new ConcurrentHashMap<>();

    private final ReplayProcessor<MissionState> stateSink = ReplayProcessor.cacheLast();
    private final Flux<MissionState> stateStream = stateSink.publishOn(Schedulers.newSingle("MissionState publisher"));

    private final ReplayProcessor<MissionRepresentation> representationSink = ReplayProcessor.cacheLast();
    private final Flux<MissionRepresentation> representationStream = representationSink.publishOn(Schedulers.newSingle("MissionState publisher"));


    private final MissionOutputCollector outputCollector = new ConcurrentMissionOutputCollector();


    private final AtomicReference<Block> cursor;
    private final AtomicReference<RunState> strandRunState = new AtomicReference<>(RunState.PAUSED);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final AtomicBoolean started = new AtomicBoolean(false);

    public Junit5MissionExecutor(JUnit5Mission mission, MissionRepresentation representation) {
        this.mission = mission;
        this.missionRepresentation = representation;
        this.resultTracker = TreeTracker.create(representation, Result.UNDEFINED, Result::summaryOf);
        cursor = new AtomicReference<>(Iterables.getFirst(missionRepresentation.childrenOf(missionRepresentation.rootBlock()), null));
        this.launcher = LauncherFactory.create();

        TestExecutionListener listener = new OurTestExecutionListener();
        launcher.registerTestExecutionListeners(listener);
        publishState();
    }

    @Override
    public Flux<MissionState> states() {
        return this.stateStream;
    }

    @Override
    public Flux<MissionOutput> outputs() {
        return this.outputCollector.asStream();
    }

    @Override
    public Flux<MissionRepresentation> representations() {
        return this.representationStream;
    }

    private void resume() {
        if (started.getAndSet(true)) {
            LOGGER.warn("Already Running. Doing nothing.");
            return;
        }
        executorService.submit(() -> launcher.execute(mission.request()));
        publishState();
    }

    @Override
    public void instruct(Strand strand, StrandCommand command) {
        if (!singleStrand.equals(strand)) {
            LOGGER.warn("given strand {} is not equal to strand {}. Doing nothing.", strand, singleStrand);
            return;
        }
        if (StrandCommand.RESUME.equals(command)) {
            resume();
        } else {
            LOGGER.warn("given command {} is not supported. Doing nothing.", command);
        }
    }

    @Override
    public void instructRoot(StrandCommand command) {
        instruct(singleStrand, command);
    }

    private void publishState() {
        stateSink.onNext(actualState());
    }

    private MissionState actualState() {
        synchronized (representationLock) {
            MissionState.Builder builder = MissionState.builder(resultTracker.resultFor(missionRepresentation.rootBlock()));
            builder.add(singleStrand, strandRunState.get(), cursor.get(), allowedCommands());
            resultTracker.blockResults().entrySet().forEach(e -> builder.blockResult(e.getKey(), e.getValue()));
            runStateTracker.entrySet().forEach(e -> builder.blockRunState(e.getKey(), e.getValue()));
            return builder.build();
        }
    }

    private Set<StrandCommand> allowedCommands() {
        if (!started.get()) {
            return ImmutableSet.of(StrandCommand.RESUME);
        }
        return ImmutableSet.of();
    }


    private void updateRepresentation(TestIdentifier testIdentifier) {
        synchronized (representationLock) {
            Optional<Block> parentBlock = testIdentifier.getParentId().flatMap(missionRepresentation::blockOfId);
            if (!parentBlock.isPresent()) {
                return;
            }

            missionRepresentation = ImmutableMissionRepresentation.builder(missionRepresentation)
                    .parentToChild(parentBlock.get(), Junit5Missions.blockFrom(testIdentifier))
                    .build();

            resultTracker = TreeTracker.create(missionRepresentation, this.resultTracker);
        }

        publishRepresentation();

    }

    private void publishRepresentation() {
        synchronized (representationLock) {
            representationSink.onNext(this.missionRepresentation);
        }
    }

    private class OurTestExecutionListener implements TestExecutionListener {

        @Override
        public void testPlanExecutionStarted(TestPlan testPlan) {
            strandRunState.set(RunState.RUNNING);
            publishState();
        }

        @Override
        public void testPlanExecutionFinished(TestPlan testPlan) {
            strandRunState.set(RunState.FINISHED);
            cursor.set(null);
            publishState();
        }

        @Override
        public void dynamicTestRegistered(TestIdentifier testIdentifier) {
            updateRepresentation(testIdentifier);
        }

        @Override
        public void executionStarted(TestIdentifier testIdentifier) {
            Block block = Junit5Missions.blockFrom(testIdentifier);
            runStateTracker.put(block, RunState.RUNNING);
            cursor.set(block);
            publishState();
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            Block block = Junit5Missions.blockFrom(testIdentifier);
            runStateTracker.put(block, RunState.FINISHED);
            if (testIdentifier.isTest()) {
                synchronized (representationLock) {
                    resultTracker.push(block, resultFrom(testExecutionResult));
                }
            }
            testExecutionResult.getThrowable().ifPresent(t -> outputCollector.put(block, THROWN, Exceptions.stackTraceFrom(t)));
            publishState();
        }
    }

    private static Result resultFrom(TestExecutionResult junitResult) {
        switch (junitResult.getStatus()) {
            case FAILED:
                return Result.FAILED;
            case ABORTED:
                return Result.FAILED;
            case SUCCESSFUL:
                return Result.SUCCESS;
            default:
                return Result.UNDEFINED;
        }
    }


}
