/*
 * This file is generated by jOOQ.
 */
package edu.java.jooq.generated.tables.records;


import edu.java.jooq.generated.tables.Link;

import jakarta.validation.constraints.Size;

import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class LinkRecord extends UpdatableRecordImpl<LinkRecord> implements Record6<Long, String, String, OffsetDateTime, OffsetDateTime, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>LINK.LINK_ID</code>.
     */
    public void setLinkId(@Nullable Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINK.LINK_ID</code>.
     */
    @Nullable
    public Long getLinkId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>LINK.URL</code>.
     */
    public void setUrl(@NotNull String value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINK.URL</code>.
     */
    @jakarta.validation.constraints.NotNull
    @Size(max = 1000000000)
    @NotNull
    public String getUrl() {
        return (String) get(1);
    }

    /**
     * Setter for <code>LINK.DESCRIPTION</code>.
     */
    public void setDescription(@Nullable String value) {
        set(2, value);
    }

    /**
     * Getter for <code>LINK.DESCRIPTION</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getDescription() {
        return (String) get(2);
    }

    /**
     * Setter for <code>LINK.CREATED_AT</code>.
     */
    public void setCreatedAt(@NotNull OffsetDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>LINK.CREATED_AT</code>.
     */
    @jakarta.validation.constraints.NotNull
    @NotNull
    public OffsetDateTime getCreatedAt() {
        return (OffsetDateTime) get(3);
    }

    /**
     * Setter for <code>LINK.LAST_CHECK_TIME</code>.
     */
    public void setLastCheckTime(@Nullable OffsetDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>LINK.LAST_CHECK_TIME</code>.
     */
    @Nullable
    public OffsetDateTime getLastCheckTime() {
        return (OffsetDateTime) get(4);
    }

    /**
     * Setter for <code>LINK.LAST_UPDATE_TIME</code>.
     */
    public void setLastUpdateTime(@Nullable OffsetDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>LINK.LAST_UPDATE_TIME</code>.
     */
    @Nullable
    public OffsetDateTime getLastUpdateTime() {
        return (OffsetDateTime) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row6<Long, String, String, OffsetDateTime, OffsetDateTime, OffsetDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row6<Long, String, String, OffsetDateTime, OffsetDateTime, OffsetDateTime> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<Long> field1() {
        return Link.LINK.LINK_ID;
    }

    @Override
    @NotNull
    public Field<String> field2() {
        return Link.LINK.URL;
    }

    @Override
    @NotNull
    public Field<String> field3() {
        return Link.LINK.DESCRIPTION;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field4() {
        return Link.LINK.CREATED_AT;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field5() {
        return Link.LINK.LAST_CHECK_TIME;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field6() {
        return Link.LINK.LAST_UPDATE_TIME;
    }

    @Override
    @Nullable
    public Long component1() {
        return getLinkId();
    }

    @Override
    @NotNull
    public String component2() {
        return getUrl();
    }

    @Override
    @Nullable
    public String component3() {
        return getDescription();
    }

    @Override
    @NotNull
    public OffsetDateTime component4() {
        return getCreatedAt();
    }

    @Override
    @Nullable
    public OffsetDateTime component5() {
        return getLastCheckTime();
    }

    @Override
    @Nullable
    public OffsetDateTime component6() {
        return getLastUpdateTime();
    }

    @Override
    @Nullable
    public Long value1() {
        return getLinkId();
    }

    @Override
    @NotNull
    public String value2() {
        return getUrl();
    }

    @Override
    @Nullable
    public String value3() {
        return getDescription();
    }

    @Override
    @NotNull
    public OffsetDateTime value4() {
        return getCreatedAt();
    }

    @Override
    @Nullable
    public OffsetDateTime value5() {
        return getLastCheckTime();
    }

    @Override
    @Nullable
    public OffsetDateTime value6() {
        return getLastUpdateTime();
    }

    @Override
    @NotNull
    public LinkRecord value1(@Nullable Long value) {
        setLinkId(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value2(@NotNull String value) {
        setUrl(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value3(@Nullable String value) {
        setDescription(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value4(@NotNull OffsetDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value5(@Nullable OffsetDateTime value) {
        setLastCheckTime(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value6(@Nullable OffsetDateTime value) {
        setLastUpdateTime(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord values(@Nullable Long value1, @NotNull String value2, @Nullable String value3, @NotNull OffsetDateTime value4, @Nullable OffsetDateTime value5, @Nullable OffsetDateTime value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LinkRecord
     */
    public LinkRecord() {
        super(Link.LINK);
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    @ConstructorProperties({ "linkId", "url", "description", "createdAt", "lastCheckTime", "lastUpdateTime" })
    public LinkRecord(@Nullable Long linkId, @NotNull String url, @Nullable String description, @NotNull OffsetDateTime createdAt, @Nullable OffsetDateTime lastCheckTime, @Nullable OffsetDateTime lastUpdateTime) {
        super(Link.LINK);

        setLinkId(linkId);
        setUrl(url);
        setDescription(description);
        setCreatedAt(createdAt);
        setLastCheckTime(lastCheckTime);
        setLastUpdateTime(lastUpdateTime);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    public LinkRecord(edu.java.jooq.generated.tables.pojos.Link value) {
        super(Link.LINK);

        if (value != null) {
            setLinkId(value.getLinkId());
            setUrl(value.getUrl());
            setDescription(value.getDescription());
            setCreatedAt(value.getCreatedAt());
            setLastCheckTime(value.getLastCheckTime());
            setLastUpdateTime(value.getLastUpdateTime());
            resetChangedOnNotNull();
        }
    }
}