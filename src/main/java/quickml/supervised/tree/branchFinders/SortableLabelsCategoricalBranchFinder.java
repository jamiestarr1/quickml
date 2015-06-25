package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Node;
import quickml.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class SortableLabelsCategoricalBranchFinder<VC extends ValueCounter<VC>> extends BranchFinder<VC> {

    public SortableLabelsCategoricalBranchFinder(Set<String> candidateAttributes, BranchingConditions<VC> branchingConditions,
                                                 Scorer<VC> scorer, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy,
                                                 AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }



    @Override
    public Optional<? extends Branch<VC>> getBranch(Branch<VC> parent, AttributeStats<VC> attributeStats) {
        if (attributeStats.getStatsOnEachValue().size()<=1) {
            return Optional.absent();
        }

        Optional<SplittingUtils.SplitScore> splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, branchingConditions, attributeValueIgnoringStrategy);
        if (!splitScoreOptional.isPresent()) {
            return Optional.absent();
        }
        SplittingUtils.SplitScore splitScore = splitScoreOptional.get();
        return createBranch(parent, attributeStats, splitScore);

    }
    protected abstract Optional<? extends Branch<VC>> createBranch(Branch<VC> parent, AttributeStats<VC> attributeStats, SplittingUtils.SplitScore splitScore);
}
