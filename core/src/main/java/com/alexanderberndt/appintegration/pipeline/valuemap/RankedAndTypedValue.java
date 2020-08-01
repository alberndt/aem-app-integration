package com.alexanderberndt.appintegration.pipeline.valuemap;

import com.alexanderberndt.appintegration.pipeline.context.Context;

import javax.annotation.Nonnull;

public class RankedAndTypedValue {

    private Context.Ranking rank;

    private Class<?> type;

    private Object value;

    private boolean isNullAllowed;

    public RankedAndTypedValue(@Nonnull Context.Ranking rank, Object value) {
        this.rank = rank;
        this.value = value;
        if (value != null) {
            this.type = value.getClass();
        }
        this.isNullAllowed = isNullableType(this.type);
    }

    public RankedAndTypedValue(@Nonnull Context.Ranking rank, Class<?> type) {
        this.rank = rank;
        this.type = type;
        this.isNullAllowed = isNullableType(type);
    }

    public Class<?> getType() {
        return type;
    }

    public String getTypeName() {
        return simpleName(getType());
    }

    public void setType(@Nonnull Context.Ranking rank, Class<?> type) throws ValueException {
        verifyRank(rank, "set type");
        if ((this.type != null) && (this.type != type)) {
            throw new ValueException(String.format("Type of %s cannot be re-defined as %s!", getTypeName(), simpleName(type)));
        }
        this.rank = rank;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(@Nonnull Context.Ranking rank, Object value) throws ValueException {
        verifyRank(rank, "set value");
        if (value == null) {
            if (isNullAllowed) {
                this.rank = rank;
                this.value = null;
            } else {
                throw new ValueException(String.format("Cannot overwrite type %s with null!", getTypeName()));
            }
        } else {
            if (this.type == null) {
                this.rank = rank;
                this.value = value;
                this.type = value.getClass();
                this.isNullAllowed = isNullableType(this.type);
            } else {
                if (this.type == value.getClass()) {
                    this.rank = rank;
                    this.value = value;
                } else {
                    throw new ValueException(String.format("Cannot overwrite type %s with value %s of type %s!",
                            getTypeName(), value, simpleName(value.getClass())));
                }
            }
        }
    }

    private void verifyRank(Context.Ranking rank, String action) throws ValueException {
        if (rank == null) {
            throw new ValueException(String.format("Rank must be specified, but was null! Cannot %s.", action));
        }
        if ((this.rank != null) && (this.rank.ordinal() < rank.ordinal())) {
            throw new ValueException(String.format("Cannot %s by rank of %s, as it is already set by rank of %s!",
                    action, rank, this.rank));
        }
    }

    static boolean isNullableType(Class<?> type) {
        return !((type == Boolean.class) || isNumberType(type));

    }

    static boolean isNumberType(Class<?> type) {
        return (type != null) && Number.class.isAssignableFrom(type);
    }

    static String simpleName(Class<?> type) {
        return (type != null) ? type.getSimpleName() : null;
    }
}
