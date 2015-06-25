package quickml.supervised.tree.dataExploration;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.constants.AttributeType;
import quickml.supervised.tree.constants.BranchType;

import java.util.*;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public class DTreeTrainingDataSurveyor<T extends ClassifierInstance> {

    private boolean considerBooleanAttributes = false;

    public DTreeTrainingDataSurveyor(boolean considerBooleanAttributes) {
        this.considerBooleanAttributes = considerBooleanAttributes;
    }

    public Map<AttributeType, Set<String>> groupAttributesByType(final List<T> trainingData) {
        Map<String, AttributeCharacteristics> attributeCharacteristics = getMapOfAttributesToAttributeCharacteristics(trainingData);
        Map<AttributeType, Set<String>> attributesByType = groupByType(attributeCharacteristics);
        return attributesByType;
    }

    private Map<String, AttributeCharacteristics> getMapOfAttributesToAttributeCharacteristics(List<T> trainingData) {
        Map<String, AttributeCharacteristics> attributeCharacteristics = Maps.newHashMap();

        for (T instance : trainingData) {
            for (Map.Entry<String, Object> e : instance.getAttributes().entrySet()) {
                AttributeCharacteristics attributeCharacteristic = attributeCharacteristics.get(e.getKey());
                if (attributeCharacteristic == null) {
                    attributeCharacteristic = new AttributeCharacteristics();
                    attributeCharacteristics.put(e.getKey(), attributeCharacteristic);
                }
                if (!(e.getValue() instanceof Number)) {
                    attributeCharacteristic.isNumber = false;
                }
                attributeCharacteristic.updateBooleanStatus(e.getValue());
            }
        }
        return attributeCharacteristics;
    }


    private Map<AttributeType, Set<String>> groupByType(Map<String, AttributeCharacteristics> attributeCharacteristics) {
        Map<AttributeType, Set<String>> attributesByType = Maps.newHashMap();
        attributesByType.put(AttributeType.CATEGORICAL, new HashSet<String>());
        attributesByType.put(AttributeType.NUMERIC, new HashSet<String>());
        if (considerBooleanAttributes)
            attributesByType.put(AttributeType.BOOLEAN, new HashSet<String>());

        for(String attribute : attributeCharacteristics.keySet()) {
            if (attributeCharacteristics.get(attribute).isNumber) {
                attributesByType.get(AttributeType.NUMERIC).add(attribute);
            }   else if (considerBooleanAttributes && attributeCharacteristics.get(attribute).isBoolean) {
                attributesByType.get(AttributeType.BOOLEAN).add(attribute);
            }   else {
                attributesByType.get(AttributeType.CATEGORICAL).add(attribute);
            }
        }
        return attributesByType;
    }


    public static class AttributeCharacteristics {

        public boolean isNumber = true;
        public boolean isBoolean = true;
        private HashSet<Object> observedVals = new HashSet();

        public void updateBooleanStatus(Object val) {
            if (!isBoolean || val == null) {
                return;
            }
            if (observedVals.size()>2 || (observedVals.size() == 2 && !observedVals.contains(val))) {
                isBoolean = false;
            } else {
                observedVals.add(val);
            }
            if (bothValsAreNumbers()) {
                isBoolean = false;
            }
        }

        private boolean bothValsAreNumbers() {
            boolean bothValsAreNum = true;

            for (Object key: observedVals) {
                if (!(key instanceof Number))
                    return false;
            }
            return true;
        }
    }
}
