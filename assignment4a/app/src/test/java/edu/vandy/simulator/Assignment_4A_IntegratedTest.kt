package edu.vandy.simulator

import admin.AssignmentIntegratedTest
import edu.vandy.simulator.managers.beings.BeingManager
import edu.vandy.simulator.managers.palantiri.PalantiriManager
import org.junit.Test

/**
 * Precision of 0 means round percents up to 0 decimal places.
 * No point values are used for each rubric since the default
 * point value of 1 is acceptable for this assignment. No weight
 * has been specified instrumented and unit tests are worth the
 * same value towards the final mark (50% for each class each).
 */
class Assignment_4A_IntegratedTest : AssignmentIntegratedTest() {
    override val beingManager = BeingManager.Factory.Type.COMMON_FORK_JOIN_POOL
    override val palantirManager = PalantiriManager.Factory.Type.CONCURRENT_MAP_FAIR_SEMAPHORE

    @Test
    override fun normalTest() {
        super.normalTest()
    }

    @Test
    override fun stressTest() {
        super.stressTest()
    }
}
