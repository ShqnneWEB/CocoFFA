/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class BaseConstructor {
    protected final Map<Tag, ConstructNode> tagConstructors;
    final Map<Node, Object> constructedObjects;
    private final Set<Node> recursiveObjects;
    private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill;
    private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill;
    protected LoadSettings settings;

    public BaseConstructor(LoadSettings settings) {
        this.settings = settings;
        this.tagConstructors = new HashMap<Tag, ConstructNode>();
        this.constructedObjects = new HashMap<Node, Object>();
        this.recursiveObjects = new HashSet<Node>();
        this.maps2fill = new ArrayList();
        this.sets2fill = new ArrayList();
    }

    public Object constructSingleDocument(Optional<Node> optionalNode) {
        if (!optionalNode.isPresent() || Tag.NULL.equals(optionalNode.get().getTag())) {
            ConstructNode construct = this.tagConstructors.get(Tag.NULL);
            return construct.construct(optionalNode.orElse(null));
        }
        return this.construct(optionalNode.get());
    }

    protected Object construct(Node node) {
        try {
            Object data = this.constructObject(node);
            this.fillRecursive();
            Object object = data;
            return object;
        } catch (YamlEngineException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new YamlEngineException(e);
        } finally {
            this.constructedObjects.clear();
            this.recursiveObjects.clear();
        }
    }

    private void fillRecursive() {
        if (!this.maps2fill.isEmpty()) {
            for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> recursiveTuple : this.maps2fill) {
                RecursiveTuple<Object, Object> keyValueTuple = recursiveTuple.getValue2();
                recursiveTuple.getValue1().put(keyValueTuple.getValue1(), keyValueTuple.getValue2());
            }
            this.maps2fill.clear();
        }
        if (!this.sets2fill.isEmpty()) {
            for (RecursiveTuple<Object, Object> recursiveTuple : this.sets2fill) {
                ((Set)recursiveTuple.getValue1()).add(recursiveTuple.getValue2());
            }
            this.sets2fill.clear();
        }
    }

    protected Object constructObject(Node node) {
        Objects.requireNonNull(node, "Node cannot be null");
        if (this.constructedObjects.containsKey(node)) {
            return this.constructedObjects.get(node);
        }
        return this.constructObjectNoCheck(node);
    }

    protected Object constructObjectNoCheck(Node node) {
        if (this.recursiveObjects.contains(node)) {
            throw new ConstructorException(null, Optional.empty(), "found unconstructable recursive node", node.getStartMark());
        }
        this.recursiveObjects.add(node);
        ConstructNode constructor = this.findConstructorFor(node).orElseThrow(() -> new ConstructorException(null, Optional.empty(), "could not determine a constructor for the tag " + node.getTag(), node.getStartMark()));
        Object data = this.constructedObjects.containsKey(node) ? this.constructedObjects.get(node) : constructor.construct(node);
        this.constructedObjects.put(node, data);
        this.recursiveObjects.remove(node);
        if (node.isRecursive()) {
            constructor.constructRecursive(node, data);
        }
        return data;
    }

    protected Optional<ConstructNode> findConstructorFor(Node node) {
        Tag tag = node.getTag();
        if (this.settings.getTagConstructors().containsKey(tag)) {
            return Optional.of(this.settings.getTagConstructors().get(tag));
        }
        if (this.tagConstructors.containsKey(tag)) {
            return Optional.of(this.tagConstructors.get(tag));
        }
        return Optional.empty();
    }

    protected String constructScalar(ScalarNode node) {
        return node.getValue();
    }

    protected List<Object> createEmptyListForNode(SequenceNode node) {
        return this.settings.getDefaultList().apply(node.getValue().size());
    }

    protected Set<Object> createEmptySetForNode(MappingNode node) {
        return this.settings.getDefaultSet().apply(node.getValue().size());
    }

    protected Map<Object, Object> createEmptyMapFor(MappingNode node) {
        return this.settings.getDefaultMap().apply(node.getValue().size());
    }

    protected List<Object> constructSequence(SequenceNode node) {
        List<Object> result = this.settings.getDefaultList().apply(node.getValue().size());
        this.constructSequenceStep2(node, result);
        return result;
    }

    protected void constructSequenceStep2(SequenceNode node, Collection<Object> collection) {
        for (Node child : node.getValue()) {
            collection.add(this.constructObject(child));
        }
    }

    protected Set<Object> constructSet(MappingNode node) {
        Set<Object> set = this.settings.getDefaultSet().apply(node.getValue().size());
        this.constructSet2ndStep(node, set);
        return set;
    }

    protected Map<Object, Object> constructMapping(MappingNode node) {
        Map<Object, Object> mapping = this.settings.getDefaultMap().apply(node.getValue().size());
        this.constructMapping2ndStep(node, mapping);
        return mapping;
    }

    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        List<NodeTuple> nodeValue = node.getValue();
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();
            Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                } catch (Exception e) {
                    throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                }
            }
            Object value = this.constructObject(valueNode);
            if (keyNode.isRecursive()) {
                if (this.settings.getAllowRecursiveKeys()) {
                    this.postponeMapFilling(mapping, key, value);
                    continue;
                }
                throw new YamlEngineException("Recursive key for mapping is detected but it is not configured to be allowed.");
            }
            mapping.put(key, value);
        }
    }

    protected void postponeMapFilling(Map<Object, Object> mapping, Object key, Object value) {
        this.maps2fill.add(0, new RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>(mapping, new RecursiveTuple<Object, Object>(key, value)));
    }

    protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
        List<NodeTuple> nodeValue = node.getValue();
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                } catch (Exception e) {
                    throw new ConstructorException("while constructing a Set", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                }
            }
            if (keyNode.isRecursive()) {
                if (this.settings.getAllowRecursiveKeys()) {
                    this.postponeSetFilling(set, key);
                    continue;
                }
                throw new YamlEngineException("Recursive key for mapping is detected but it is not configured to be allowed.");
            }
            set.add(key);
        }
    }

    protected void postponeSetFilling(Set<Object> set, Object key) {
        this.sets2fill.add(0, new RecursiveTuple<Set<Object>, Object>(set, key));
    }

    private static class RecursiveTuple<T, K> {
        private final T value1;
        private final K value2;

        public RecursiveTuple(T value1, K value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public K getValue2() {
            return this.value2;
        }

        public T getValue1() {
            return this.value1;
        }
    }
}

