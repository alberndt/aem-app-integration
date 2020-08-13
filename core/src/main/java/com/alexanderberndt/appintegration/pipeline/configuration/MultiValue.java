package com.alexanderberndt.appintegration.pipeline.configuration;

import com.alexanderberndt.appintegration.engine.resources.ExternalResourceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MultiValue {

    private static final Set<Ranking> OVERRULING_RANKINGS = Ranking.PIPELINE_EXECUTION.getOverrulingRanks();

    private final List<SingleValue> values = new ArrayList<>();

    private Class<?> valueType;

    public MultiValue() {
    }

    public MultiValue(@Nullable Class<?> valueType) {
        this.valueType = valueType;
    }

    private MultiValue(SingleValue singleValue, Class<?> valueType) {
        this(valueType);
        this.values.add(singleValue);
    }

    public static MultiValue createByValue(@Nonnull Ranking rank, @Nonnull ExternalResourceType resourceType, @Nullable Object value) {
        if (value == null) {
            return new MultiValue();
        } else {
            return new MultiValue(new SingleValue(rank, resourceType, value), value.getClass());
        }
    }

    public static MultiValue createByType(@Nullable Class<?> type) {
        return new MultiValue(type);
    }


    public Class<?> getType() {
        return valueType;
    }

    public String getTypeName() {
        return simpleName(getType());
    }

    public Object getValue(@Nonnull final ExternalResourceType contextResourceType) {
        // go through all values to find best match
        return this.values.stream()
                .filter(singleValue -> contextResourceType.isSameOrSpecializationOf(singleValue.getResourceType()))
                .reduce((single1, single2) -> single1.isOverruling(single2) ? single1 : single2)
                .map(SingleValue::getValue)
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(@Nonnull final ExternalResourceType contextResourceType, @Nonnull final T executionValue) throws ConfigurationException {

        Objects.requireNonNull(executionValue, "Parameter executionValue MUST NOT be null!");
        assertValueType(executionValue);

        // go through all values (which would overrule) to find best match
        return (T) this.values.stream()
                .filter(singleValue -> OVERRULING_RANKINGS.contains(singleValue.getRank()))
                .filter(singleValue -> contextResourceType.isSameOrSpecializationOf(singleValue.getResourceType()))
                .reduce((single1, single2) -> single1.isOverruling(single2) ? single1 : single2)
                .map(SingleValue::getValue)
                .orElse(executionValue);
    }


    /**
     * <p>Sets a value for specific rank. If multiple ranks specify a value or an type, then the types must be the
     * same for all ranks.</p>
     *
     * <p>ToDo: Define when exceptions are thrown</p>
     *
     * @param rank  Rank - MUST NOT be null.
     * @param value Value object, may be null (except for Boolean or Number types)
     * @throws ConfigurationException thrown, if value could not be modified
     */
    public void setValue(@Nonnull Ranking rank, @Nonnull ExternalResourceType resourceType, Object value) throws ConfigurationException {

        Objects.requireNonNull(rank, "Parameter rank MUST NOT be null!");
        Objects.requireNonNull(resourceType, "Parameter resourceType MUST NOT be null!");
        assertValueType(value);

        // handle new type (after value was set, and eventually a NullPointerException for missing rank was thrown.
        if ((value != null) && (this.valueType == null)) {
            this.valueType = (value.getClass());
        }

        // remove old value
        this.values.removeIf(singleValue -> (singleValue.getRank() == rank) && (singleValue.getResourceType() == resourceType));
        if (value != null) {
            this.values.add(new SingleValue(rank, resourceType, value));
        }
    }

    public void setType(@Nonnull Class<?> type) throws ConfigurationException {
        if (this.valueType == null) {
            this.valueType = Objects.requireNonNull(type, "Type must not be null!");
        } else {
            if (this.valueType != type) {
                throw new ConfigurationException(String.format("Type of %s cannot be re-defined as %s!", getTypeName(), simpleName(type)));
            }
        }
    }

    private void assertValueType(Object value) throws ConfigurationException {
        if ((this.valueType != null) && (value != null) && (value.getClass() != this.valueType)) {
            throw new ConfigurationException(String.format("Cannot overwrite type %s with value %s of type %s!",
                    getTypeName(), value, simpleName(value.getClass())));
        }
    }

    private static String simpleName(Class<?> type) {
        return (type != null) ? type.getSimpleName() : null;
    }


    public static class SingleValue {

        @Nonnull
        private final Ranking rank;

        @Nonnull
        private final ExternalResourceType resourceType;

        @Nonnull
        private final Object value;

        public SingleValue(@Nonnull Ranking rank, @Nonnull ExternalResourceType resourceType, @Nonnull Object value) {
            this.rank = rank;
            this.resourceType = resourceType;
            this.value = value;
        }

        public boolean isOverruling(@Nullable SingleValue other) {
            // always better than nothing
            if (other == null) return true;

            // the rank is most decisive
            if (this.getRank().ordinal() < other.getRank().ordinal()) return true;
            if (this.getRank().ordinal() > other.getRank().ordinal()) return false;

            // best most specific resource-type wins
            return this.getResourceType().isMoreQualifiedThan(other.getResourceType());
        }

        @Nonnull
        public Ranking getRank() {
            return rank;
        }

        @Nonnull
        public ExternalResourceType getResourceType() {
            return resourceType;
        }

        @Nonnull
        public Object getValue() {
            return value;
        }
    }
}
