/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.settings.updater;

public enum MergeRule {
    SECTION_AT_MAPPING,
    MAPPING_AT_SECTION,
    MAPPINGS;


    public static MergeRule getFor(boolean documentBlockIsSection, boolean defaultBlockIsSection) {
        return documentBlockIsSection ? (defaultBlockIsSection ? null : SECTION_AT_MAPPING) : (defaultBlockIsSection ? MAPPING_AT_SECTION : MAPPINGS);
    }
}

