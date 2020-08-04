package com.alexanderberndt.appintegration.pipeline.valuemap;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Stream;

public class RankedAndTypedValue {

    private final EnumSet<Ranking> ranksWithDefinedTypeSet = EnumSet.noneOf(Ranking.class);

    private final EnumMap<Ranking, Object> valueMap = new EnumMap<>(Ranking.class);

    private Ranking effectiveRank;

    private Class<?> effectiveType;

    private boolean isNonNull;

    public static RankedAndTypedValue createByValue(@Nonnull Ranking rank, Object value) {
        assertRankNotNull(rank);
        RankedAndTypedValue valueObj = new RankedAndTypedValue();
        valueObj.effectiveRank = rank;
        valueObj.valueMap.put(rank, value);
        valueObj.setEffectiveType((value != null) ? value.getClass() : null);
        if (value != null) {
            valueObj.ranksWithDefinedTypeSet.add(rank);
        }
        return valueObj;
    }

    public static RankedAndTypedValue createByType(@Nonnull Ranking rank, @Nonnull Class<?> type) {
        assertRankNotNull(rank);
        RankedAndTypedValue valueObj = new RankedAndTypedValue();
        valueObj.effectiveRank = rank;
        valueObj.ranksWithDefinedTypeSet.add(rank);
        valueObj.setEffectiveType(type);
        return valueObj;
    }


    public Class<?> getType() {
        return effectiveType;
    }

    public String getTypeName() {
        return simpleName(getType());
    }

    public Object getValue() {
        return (effectiveRank != null) ? valueMap.get(effectiveRank) : null;
    }

    /**
     * <p>Sets a value for specific rank. If multiple ranks specify a value or an type, then the types must be the
     * same for all ranks.</p>
     *
     * <p>ToDo: Define when exceptions are thrown</p>
     *
     * @param rank  Rank - MUST NOT be null.
     * @param value Value object, may be null (except for Boolean or Number types)
     * @throws ValueException thrown, if value could not be modified
     */
    public void setValue(@Nonnull Ranking rank, Object value) throws ValueException {

        assertRankNotNull(rank);

        if (isNonNull && (value == null)) {
            throw new ValueException(String.format("Cannot overwrite type %s with null!", getTypeName()));
        }

        if ((this.effectiveType != null) && (value != null) && (value.getClass() != this.effectiveType)) {
            throw new ValueException(String.format("Cannot overwrite type %s with value %s of type %s!",
                    getTypeName(), value, simpleName(value.getClass())));
        }

        // set value anyway
        this.valueMap.put(rank, value);

        // handle new type (after value was set, and eventually a NullPointerException for missing rank was thrown.
        if (value != null) {
            this.ranksWithDefinedTypeSet.add(rank);
            if (this.effectiveType == null) {
                setEffectiveType(value.getClass());
            }
        }

        // calculate effective ranking
        if (this.effectiveRank == null) {
            this.effectiveRank = rank;
        } else {
            if (this.effectiveRank.ordinal() >= rank.ordinal()) {
                this.effectiveRank = rank;
            } else {
                // throw an Exception afterwards as a kind of warning
                // ToDo: Rethink handling of warnings
                throw new ValueException(String.format("Cannot set value by rank of %s, as it is already set by rank of %s!",
                        rank, this.effectiveRank));
            }
        }
    }

    public void setType(@Nonnull Ranking rank, @Nonnull Class<?> type) throws ValueException {

        assertRankNotNull(rank);

        if (this.effectiveType == null) {
            setEffectiveType(Objects.requireNonNull(type, "Type must not be null!"));
            this.ranksWithDefinedTypeSet.add(rank);
        } else {
            if (this.effectiveType == type) {
                this.ranksWithDefinedTypeSet.add(rank);
            } else {
                throw new ValueException(String.format("Type of %s cannot be re-defined as %s!", getTypeName(), simpleName(type)));
            }
        }
    }


    /**
     * Clears the value and the type for a specific rank. So the value of a lower rank might appear, or the ValueObject
     * may become clear again.
     *
     * @param rank Rank (must not be null)
     */
    public void clear(@Nonnull Ranking rank) {

        assertRankNotNull(rank);

        // clear type and value
        this.ranksWithDefinedTypeSet.remove(rank);
        this.valueMap.remove(rank);

        // search for the highest rank defined (either type of value)
        this.effectiveRank = Stream.concat(this.ranksWithDefinedTypeSet.stream(), this.valueMap.keySet().stream())
                .min(Comparator.comparing(Ranking::ordinal))
                .orElse(null);

        // clear effective type, if absolutely nothing is there anymore
        if (this.effectiveRank == null) {
            setEffectiveType(null);
        }
    }

    private void setEffectiveType(Class<?> type) {
        this.effectiveType = type;
        this.isNonNull = isNonNullType(type);
    }

    static boolean isNonNullType(Class<?> type) {
        return (type != null) && ((type == Boolean.class) || isNumberType(type));
    }

    static boolean isNumberType(Class<?> type) {
        return (type != null) && Number.class.isAssignableFrom(type);
    }


    @SuppressWarnings("java:S2583")
    private static void assertRankNotNull(@Nonnull Ranking rank) {
        //noinspection ConstantConditions
        if (rank == null) {
            throw new IllegalArgumentException("rank must not be null!");
        }
    }

    static String simpleName(Class<?> type) {
        return (type != null) ? type.getSimpleName() : null;
    }
}
