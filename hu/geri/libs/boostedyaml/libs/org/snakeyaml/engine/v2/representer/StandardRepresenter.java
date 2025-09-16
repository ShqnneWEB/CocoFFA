/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.representer;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.DumpSettings;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.api.RepresentToNode;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.representer.BaseRepresenter;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class StandardRepresenter
extends BaseRepresenter {
    public static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|\u0085");
    protected Map<Class<? extends Object>, Tag> classTags;
    protected DumpSettings settings;

    public StandardRepresenter(DumpSettings settings) {
        this.defaultFlowStyle = settings.getDefaultFlowStyle();
        this.defaultScalarStyle = settings.getDefaultScalarStyle();
        this.nullRepresenter = new RepresentNull();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Character.class, new RepresentString());
        this.representers.put(UUID.class, new RepresentUuid());
        this.representers.put(Optional.class, new RepresentOptional());
        this.representers.put(byte[].class, new RepresentByteArray());
        RepresentPrimitiveArray primitiveArray = new RepresentPrimitiveArray();
        this.representers.put(short[].class, primitiveArray);
        this.representers.put(int[].class, primitiveArray);
        this.representers.put(long[].class, primitiveArray);
        this.representers.put(float[].class, primitiveArray);
        this.representers.put(double[].class, primitiveArray);
        this.representers.put(char[].class, primitiveArray);
        this.representers.put(boolean[].class, primitiveArray);
        this.parentClassRepresenters.put(Number.class, new RepresentNumber());
        this.parentClassRepresenters.put(List.class, new RepresentList());
        this.parentClassRepresenters.put(Map.class, new RepresentMap());
        this.parentClassRepresenters.put(Set.class, new RepresentSet());
        this.parentClassRepresenters.put(Iterator.class, new RepresentIterator());
        this.parentClassRepresenters.put(new Object[0].getClass(), new RepresentArray());
        this.parentClassRepresenters.put(Enum.class, new RepresentEnum());
        this.classTags = new HashMap<Class<? extends Object>, Tag>();
        this.settings = settings;
    }

    protected Tag getTag(Class<?> clazz, Tag defaultTag) {
        return this.classTags.getOrDefault(clazz, defaultTag);
    }

    @Deprecated
    public Tag addClassTag(Class<? extends Object> clazz, Tag tag) {
        if (tag == null) {
            throw new NullPointerException("Tag must be provided.");
        }
        return this.classTags.put(clazz, tag);
    }

    protected class RepresentNull
    implements RepresentToNode {
        protected RepresentNull() {
        }

        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representScalar(Tag.NULL, "null");
        }
    }

    public class RepresentString
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Tag tag = Tag.STR;
            ScalarStyle style = ScalarStyle.PLAIN;
            String value = data.toString();
            if (StandardRepresenter.this.settings.getNonPrintableStyle() == NonPrintableStyle.BINARY && !StreamReader.isPrintable(value)) {
                tag = Tag.BINARY;
                byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                String checkValue = new String(bytes, StandardCharsets.UTF_8);
                if (!checkValue.equals(value)) {
                    throw new YamlEngineException("invalid string value has occurred");
                }
                value = Base64.getEncoder().encodeToString(bytes);
                style = ScalarStyle.LITERAL;
            }
            if (StandardRepresenter.this.defaultScalarStyle == ScalarStyle.PLAIN && MULTILINE_PATTERN.matcher(value).find()) {
                style = ScalarStyle.LITERAL;
            }
            return StandardRepresenter.this.representScalar(tag, value, style);
        }
    }

    public class RepresentBoolean
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            String value = Boolean.TRUE.equals(data) ? "true" : "false";
            return StandardRepresenter.this.representScalar(Tag.BOOL, value);
        }
    }

    public class RepresentUuid
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(data.getClass(), new Tag(UUID.class)), data.toString());
        }
    }

    public class RepresentOptional
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Optional opt = (Optional)data;
            if (opt.isPresent()) {
                Node node = StandardRepresenter.this.represent(opt.get());
                node.setTag(new Tag(Optional.class));
                return node;
            }
            return StandardRepresenter.this.representScalar(Tag.NULL, "null");
        }
    }

    public class RepresentByteArray
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representScalar(Tag.BINARY, Base64.getEncoder().encodeToString((byte[])data), ScalarStyle.LITERAL);
        }
    }

    public class RepresentPrimitiveArray
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Class<?> type = data.getClass().getComponentType();
            FlowStyle style = StandardRepresenter.this.settings.getDefaultFlowStyle();
            if (Byte.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asByteList(data), style);
            }
            if (Short.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asShortList(data), style);
            }
            if (Integer.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asIntList(data), style);
            }
            if (Long.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asLongList(data), style);
            }
            if (Float.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asFloatList(data), style);
            }
            if (Double.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asDoubleList(data), style);
            }
            if (Character.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asCharList(data), style);
            }
            if (Boolean.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asBooleanList(data), style);
            }
            throw new YamlEngineException("Unexpected primitive '" + type.getCanonicalName() + "'");
        }

        private List<Byte> asByteList(Object in) {
            byte[] array = (byte[])in;
            ArrayList<Byte> list = new ArrayList<Byte>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Short> asShortList(Object in) {
            short[] array = (short[])in;
            ArrayList<Short> list = new ArrayList<Short>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Integer> asIntList(Object in) {
            int[] array = (int[])in;
            ArrayList<Integer> list = new ArrayList<Integer>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Long> asLongList(Object in) {
            long[] array = (long[])in;
            ArrayList<Long> list = new ArrayList<Long>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Float> asFloatList(Object in) {
            float[] array = (float[])in;
            ArrayList<Float> list = new ArrayList<Float>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(Float.valueOf(array[i]));
            }
            return list;
        }

        private List<Double> asDoubleList(Object in) {
            double[] array = (double[])in;
            ArrayList<Double> list = new ArrayList<Double>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Character> asCharList(Object in) {
            char[] array = (char[])in;
            ArrayList<Character> list = new ArrayList<Character>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(Character.valueOf(array[i]));
            }
            return list;
        }

        private List<Boolean> asBooleanList(Object in) {
            boolean[] array = (boolean[])in;
            ArrayList<Boolean> list = new ArrayList<Boolean>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }
    }

    public class RepresentNumber
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            String value;
            Tag tag;
            if (data instanceof Byte || data instanceof Short || data instanceof Integer || data instanceof Long || data instanceof BigInteger) {
                tag = Tag.INT;
                value = data.toString();
            } else {
                Number number = (Number)data;
                tag = Tag.FLOAT;
                value = number.equals(Double.NaN) || number.equals(Float.valueOf(Float.NaN)) ? ".nan" : (number.equals(Double.POSITIVE_INFINITY) || number.equals(Float.valueOf(Float.POSITIVE_INFINITY)) ? ".inf" : (number.equals(Double.NEGATIVE_INFINITY) || number.equals(Float.valueOf(Float.NEGATIVE_INFINITY)) ? "-.inf" : number.toString()));
            }
            return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(data.getClass(), tag), value);
        }
    }

    public class RepresentList
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representSequence(StandardRepresenter.this.getTag(data.getClass(), Tag.SEQ), (List)data, StandardRepresenter.this.settings.getDefaultFlowStyle());
        }
    }

    public class RepresentMap
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representMapping(StandardRepresenter.this.getTag(data.getClass(), Tag.MAP), (Map)data, StandardRepresenter.this.settings.getDefaultFlowStyle());
        }
    }

    public class RepresentSet
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            LinkedHashMap value = new LinkedHashMap();
            Set set = (Set)data;
            for (Object key : set) {
                value.put(key, null);
            }
            return StandardRepresenter.this.representMapping(StandardRepresenter.this.getTag(data.getClass(), Tag.SET), value, StandardRepresenter.this.settings.getDefaultFlowStyle());
        }
    }

    public class RepresentIterator
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Iterator iter = (Iterator)data;
            return StandardRepresenter.this.representSequence(StandardRepresenter.this.getTag(data.getClass(), Tag.SEQ), new IteratorWrapper(iter), StandardRepresenter.this.settings.getDefaultFlowStyle());
        }
    }

    public class RepresentArray
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Object[] array = (Object[])data;
            List<Object> list = Arrays.asList(array);
            return StandardRepresenter.this.representSequence(Tag.SEQ, list, StandardRepresenter.this.settings.getDefaultFlowStyle());
        }
    }

    public class RepresentEnum
    implements RepresentToNode {
        @Override
        public Node representData(Object data) {
            Tag tag = new Tag(data.getClass());
            return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(data.getClass(), tag), ((Enum)data).name());
        }
    }

    private static class IteratorWrapper
    implements Iterable<Object> {
        private final Iterator<Object> iter;

        public IteratorWrapper(Iterator<Object> iter) {
            this.iter = iter;
        }

        @Override
        public Iterator<Object> iterator() {
            return this.iter;
        }
    }
}

