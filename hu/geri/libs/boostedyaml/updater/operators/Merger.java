/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.updater.operators;

import hu.geri.libs.boostedyaml.YamlDocument;
import hu.geri.libs.boostedyaml.block.Block;
import hu.geri.libs.boostedyaml.block.implementation.Section;
import hu.geri.libs.boostedyaml.block.implementation.TerminatedBlock;
import hu.geri.libs.boostedyaml.engine.ExtendedConstructor;
import hu.geri.libs.boostedyaml.engine.ExtendedRepresenter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.route.Route;
import hu.geri.libs.boostedyaml.settings.general.GeneralSettings;
import hu.geri.libs.boostedyaml.settings.updater.MergeRule;
import hu.geri.libs.boostedyaml.settings.updater.UpdaterSettings;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class Merger {
    private static final Merger INSTANCE = new Merger();

    public static void merge(@NotNull Section document, @NotNull Section defaults, @NotNull UpdaterSettings settings) {
        INSTANCE.iterate(document, defaults, settings);
    }

    private void iterate(Section document, Section defaults, UpdaterSettings settings) {
        HashSet documentKeys = new HashSet(((Map)document.getStoredValue()).keySet());
        boolean sort = settings.getOptionSorting() == UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS;
        Map<Object, Block<?>> sorted = sort ? document.getRoot().getGeneralSettings().getDefaultMap() : null;
        for (Map.Entry entry : ((Map)defaults.getStoredValue()).entrySet()) {
            Object key2 = entry.getKey();
            Route route = Route.from(key2);
            documentKeys.remove(key2);
            Block documentBlock = document.getOptionalBlock(route).orElse(null);
            Block defaultBlock = (Block)entry.getValue();
            if (documentBlock != null) {
                if (documentBlock.isIgnored()) {
                    documentBlock.setIgnored(false);
                    if (documentBlock instanceof Section) {
                        this.resetIgnored((Section)documentBlock);
                    }
                    if (!sort) continue;
                    sorted.put(key2, documentBlock);
                    continue;
                }
                boolean isDocumentBlockSection = documentBlock instanceof Section;
                boolean isDefaultBlockSection = defaultBlock instanceof Section;
                if (isDefaultBlockSection && isDocumentBlockSection) {
                    this.iterate((Section)documentBlock, (Section)defaultBlock, settings);
                    if (!sort) continue;
                    sorted.put(key2, documentBlock);
                    continue;
                }
                if (sort) {
                    sorted.put(key2, this.getPreservedValue(settings.getMergeRules(), documentBlock, () -> this.cloneBlock(defaultBlock, document), isDocumentBlockSection, isDefaultBlockSection));
                    continue;
                }
                document.set(route, this.getPreservedValue(settings.getMergeRules(), documentBlock, () -> this.cloneBlock(defaultBlock, document), isDocumentBlockSection, isDefaultBlockSection));
                continue;
            }
            if (sort) {
                sorted.put(key2, this.cloneBlock(defaultBlock, document));
                continue;
            }
            document.set(route, this.cloneBlock(defaultBlock, document));
        }
        if (settings.isKeepAll()) {
            if (sort) {
                documentKeys.forEach(key -> {
                    Block cfr_ignored_0 = (Block)sorted.put(key, (Block<?>)((Map)document.getStoredValue()).get(key));
                });
                document.repopulate(sorted);
            }
            return;
        }
        for (Map.Entry key3 : documentKeys) {
            Route route = Route.fromSingleKey(key3);
            Block block = document.getOptionalBlock(route).orElse(null);
            if (block != null && block.isIgnored()) {
                block.setIgnored(false);
                if (block instanceof Section) {
                    this.resetIgnored((Section)block);
                }
                if (!sort) continue;
                sorted.put(key3, block);
                continue;
            }
            if (sort) continue;
            document.remove(route);
        }
        if (sort) {
            document.repopulate(sorted);
        }
    }

    private void resetIgnored(@NotNull Section section) {
        ((Map)section.getStoredValue()).values().forEach(block -> {
            block.setIgnored(false);
            if (block instanceof Section) {
                this.resetIgnored((Section)block);
            }
        });
    }

    @NotNull
    private Block<?> cloneBlock(@NotNull Block<?> block, @NotNull Section newParent) {
        return block instanceof Section ? this.cloneSection((Section)block, newParent) : this.cloneTerminated((TerminatedBlock)block, newParent);
    }

    @NotNull
    private Section cloneSection(@NotNull Section section, @NotNull Section newParent) {
        if (section.getRoute() == null) {
            throw new IllegalArgumentException("Cannot clone the root!");
        }
        YamlDocument root = section.getRoot();
        GeneralSettings generalSettings = root.getGeneralSettings();
        ExtendedRepresenter representer = new ExtendedRepresenter(generalSettings, root.getDumperSettings());
        ExtendedConstructor constructor = new ExtendedConstructor(root.getLoaderSettings().buildEngineSettings(generalSettings), generalSettings.getSerializer());
        Node represented = representer.represent(section);
        constructor.constructSingleDocument(Optional.of(represented));
        section = new Section(newParent.getRoot(), newParent, section.getRoute(), this.moveComments(represented), (MappingNode)represented, constructor);
        constructor.clear();
        return section;
    }

    @NotNull
    private TerminatedBlock cloneTerminated(@NotNull TerminatedBlock entry, @NotNull Section newParent) {
        YamlDocument root = newParent.getRoot();
        GeneralSettings generalSettings = root.getGeneralSettings();
        ExtendedRepresenter representer = new ExtendedRepresenter(generalSettings, root.getDumperSettings());
        ExtendedConstructor constructor = new ExtendedConstructor(root.getLoaderSettings().buildEngineSettings(generalSettings), generalSettings.getSerializer());
        Node represented = representer.represent(entry.getStoredValue());
        constructor.constructSingleDocument(Optional.of(represented));
        entry = new TerminatedBlock((Block<?>)entry, constructor.getConstructed(represented));
        constructor.clear();
        return entry;
    }

    private Node moveComments(@NotNull Node node) {
        ScalarNode scalarNode = new ScalarNode(Tag.STR, "", ScalarStyle.PLAIN);
        scalarNode.setBlockComments(node.getBlockComments());
        scalarNode.setInLineComments(node.getInLineComments());
        scalarNode.setEndComments(node.getEndComments());
        node.setBlockComments(Collections.emptyList());
        node.setInLineComments(null);
        node.setEndComments(null);
        return scalarNode;
    }

    @NotNull
    private Block<?> getPreservedValue(@NotNull Map<MergeRule, Boolean> rules, @NotNull Block<?> documentBlock, @NotNull Supplier<Block<?>> defaultBlock, boolean documentBlockIsSection, boolean defaultBlockIsSection) {
        return rules.get((Object)MergeRule.getFor(documentBlockIsSection, defaultBlockIsSection)) != false ? documentBlock : defaultBlock.get();
    }
}

