/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package hu.geri.libs.fastboard;

import hu.geri.libs.fastboard.FastReflection;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class FastBoardBase<T> {
    private static final Map<Class<?>, Field[]> PACKETS = new HashMap(8);
    protected static final String[] COLOR_CODES = (String[])Arrays.stream(ChatColor.values()).map(Object::toString).toArray(String[]::new);
    private static final VersionType VERSION_TYPE;
    private static final Class<?> CHAT_COMPONENT_CLASS;
    private static final Class<?> CHAT_FORMAT_ENUM;
    private static final Object RESET_FORMATTING;
    private static final MethodHandle PLAYER_CONNECTION;
    private static final MethodHandle SEND_PACKET;
    private static final MethodHandle PLAYER_GET_HANDLE;
    private static final MethodHandle FIXED_NUMBER_FORMAT;
    private static final FastReflection.PacketConstructor PACKET_SB_OBJ;
    private static final FastReflection.PacketConstructor PACKET_SB_DISPLAY_OBJ;
    private static final FastReflection.PacketConstructor PACKET_SB_TEAM;
    private static final FastReflection.PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;
    private static final MethodHandle PACKET_SB_SET_SCORE;
    private static final MethodHandle PACKET_SB_RESET_SCORE;
    private static final boolean SCORE_OPTIONAL_COMPONENTS;
    private static final Class<?> DISPLAY_SLOT_TYPE;
    private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
    private static final Class<?> ENUM_SB_ACTION;
    private static final Class<?> ENUM_VISIBILITY;
    private static final Class<?> ENUM_COLLISION_RULE;
    private static final Object BLANK_NUMBER_FORMAT;
    private static final Object SIDEBAR_DISPLAY_SLOT;
    private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
    private static final Object ENUM_SB_ACTION_CHANGE;
    private static final Object ENUM_SB_ACTION_REMOVE;
    private static final Object ENUM_VISIBILITY_ALWAYS;
    private static final Object ENUM_COLLISION_RULE_ALWAYS;
    private final Player player;
    private final String id;
    private final List<T> lines = new ArrayList<T>();
    private final List<T> scores = new ArrayList<T>();
    private T title = this.emptyLine();
    private boolean deleted = false;

    protected FastBoardBase(Player player) {
        this.player = Objects.requireNonNull(player, "player");
        this.id = "fb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());
        try {
            this.sendObjectivePacket(ObjectiveMode.CREATE);
            this.sendDisplayObjectivePacket();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create scoreboard", t);
        }
    }

    public T getTitle() {
        return this.title;
    }

    public void updateTitle(T title) {
        if (this.title.equals(Objects.requireNonNull(title, "title"))) {
            return;
        }
        this.title = title;
        try {
            this.sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard title", t);
        }
    }

    public List<T> getLines() {
        return new ArrayList<T>(this.lines);
    }

    public T getLine(int line) {
        this.checkLineNumber(line, true, false);
        return this.lines.get(line);
    }

    public Optional<T> getScore(int line) {
        this.checkLineNumber(line, true, false);
        return Optional.ofNullable(this.scores.get(line));
    }

    public synchronized void updateLine(int line, T text) {
        this.updateLine(line, text, null);
    }

    public synchronized void updateLine(int line, T text, T scoreText) {
        this.checkLineNumber(line, false, false);
        try {
            if (line < this.size()) {
                this.lines.set(line, text);
                this.scores.set(line, scoreText);
                this.sendLineChange(this.getScoreByLine(line));
                if (this.customScoresSupported()) {
                    this.sendScorePacket(this.getScoreByLine(line), ScoreboardAction.CHANGE);
                }
                return;
            }
            ArrayList<T> newLines = new ArrayList<T>(this.lines);
            ArrayList<T> newScores = new ArrayList<T>(this.scores);
            if (line > this.size()) {
                for (int i = this.size(); i < line; ++i) {
                    newLines.add(this.emptyLine());
                    newScores.add(null);
                }
            }
            newLines.add(text);
            newScores.add(scoreText);
            this.updateLines((Collection<T>)newLines, (Collection<T>)newScores);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    public synchronized void removeLine(int line) {
        this.checkLineNumber(line, false, false);
        if (line >= this.size()) {
            return;
        }
        ArrayList<T> newLines = new ArrayList<T>(this.lines);
        ArrayList<T> newScores = new ArrayList<T>(this.scores);
        newLines.remove(line);
        newScores.remove(line);
        this.updateLines((Collection<T>)newLines, (Collection<T>)newScores);
    }

    public void updateLines(T ... lines) {
        this.updateLines((Collection<T>)Arrays.asList(lines));
    }

    public synchronized void updateLines(Collection<T> lines) {
        this.updateLines(lines, null);
    }

    public synchronized void updateLines(Collection<T> lines, Collection<T> scores) {
        Objects.requireNonNull(lines, "lines");
        this.checkLineNumber(lines.size(), false, true);
        if (scores != null && scores.size() != lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }
        ArrayList<T> oldLines = new ArrayList<T>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);
        ArrayList<T> oldScores = new ArrayList<T>(this.scores);
        this.scores.clear();
        this.scores.addAll(scores != null ? scores : Collections.nCopies(lines.size(), null));
        int linesSize = this.lines.size();
        try {
            if (oldLines.size() != linesSize) {
                int i;
                ArrayList<T> oldLinesCopy = new ArrayList<T>(oldLines);
                if (oldLines.size() > linesSize) {
                    for (i = oldLinesCopy.size(); i > linesSize; --i) {
                        this.sendTeamPacket(i - 1, TeamMode.REMOVE);
                        this.sendScorePacket(i - 1, ScoreboardAction.REMOVE);
                        oldLines.remove(0);
                    }
                } else {
                    for (i = oldLinesCopy.size(); i < linesSize; ++i) {
                        this.sendScorePacket(i, ScoreboardAction.CHANGE);
                        this.sendTeamPacket(i, TeamMode.CREATE, null, null);
                    }
                }
            }
            for (int i = 0; i < linesSize; ++i) {
                if (!Objects.equals(this.getLineByScore(oldLines, i), this.getLineByScore(i))) {
                    this.sendLineChange(i);
                }
                if (Objects.equals(this.getLineByScore(oldScores, i), this.getLineByScore(this.scores, i))) continue;
                this.sendScorePacket(i, ScoreboardAction.CHANGE);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    public synchronized void updateScore(int line, T text) {
        this.checkLineNumber(line, true, false);
        this.scores.set(line, text);
        try {
            if (this.customScoresSupported()) {
                this.sendScorePacket(this.getScoreByLine(line), ScoreboardAction.CHANGE);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Unable to update line score", e);
        }
    }

    public synchronized void removeScore(int line) {
        this.updateScore(line, null);
    }

    public synchronized void updateScores(T ... texts) {
        this.updateScores((Collection<T>)Arrays.asList(texts));
    }

    public synchronized void updateScores(Collection<T> texts) {
        Objects.requireNonNull(texts, "texts");
        if (this.scores.size() != this.lines.size()) {
            throw new IllegalArgumentException("The size of the scores must match the size of the board");
        }
        ArrayList<T> newScores = new ArrayList<T>(texts);
        for (int i = 0; i < this.scores.size(); ++i) {
            if (Objects.equals(this.scores.get(i), newScores.get(i))) continue;
            this.scores.set(i, newScores.get(i));
            try {
                if (!this.customScoresSupported()) continue;
                this.sendScorePacket(this.getScoreByLine(i), ScoreboardAction.CHANGE);
                continue;
            } catch (Throwable e) {
                throw new RuntimeException("Unable to update scores", e);
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getId() {
        return this.id;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public boolean customScoresSupported() {
        return BLANK_NUMBER_FORMAT != null;
    }

    public int size() {
        return this.lines.size();
    }

    public void delete() {
        try {
            for (int i = 0; i < this.lines.size(); ++i) {
                this.sendTeamPacket(i, TeamMode.REMOVE);
            }
            this.sendObjectivePacket(ObjectiveMode.REMOVE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to delete scoreboard", t);
        }
        this.deleted = true;
    }

    protected abstract void sendLineChange(int var1) throws Throwable;

    protected abstract Object toMinecraftComponent(T var1) throws Throwable;

    protected abstract String serializeLine(T var1);

    protected abstract T emptyLine();

    private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line number must be positive");
        }
        if (checkInRange && line >= this.lines.size()) {
            throw new IllegalArgumentException("Line number must be under " + this.lines.size());
        }
        if (checkMax && line >= COLOR_CODES.length - 1) {
            throw new IllegalArgumentException("Line number is too high: " + line);
        }
    }

    protected int getScoreByLine(int line) {
        return this.lines.size() - line - 1;
    }

    protected T getLineByScore(int score) {
        return this.getLineByScore(this.lines, score);
    }

    protected T getLineByScore(List<T> lines, int score) {
        return score < lines.size() ? (T)lines.get(lines.size() - score - 1) : null;
    }

    protected void sendObjectivePacket(ObjectiveMode mode) throws Throwable {
        Object packet = PACKET_SB_OBJ.invoke();
        this.setField(packet, String.class, this.id);
        this.setField(packet, Integer.TYPE, mode.ordinal());
        if (mode != ObjectiveMode.REMOVE) {
            this.setComponentField(packet, this.title, 1);
            this.setField(packet, Optional.class, Optional.empty());
            if (VersionType.V1_8.isHigherOrEqual()) {
                this.setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
            }
        } else if (VERSION_TYPE == VersionType.V1_7) {
            this.setField(packet, String.class, "", 1);
        }
        this.sendPacket(packet);
    }

    protected void sendDisplayObjectivePacket() throws Throwable {
        Object packet = PACKET_SB_DISPLAY_OBJ.invoke();
        this.setField(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT);
        this.setField(packet, String.class, this.id);
        this.sendPacket(packet);
    }

    protected void sendScorePacket(int score, ScoreboardAction action) throws Throwable {
        if (VersionType.V1_17.isHigherOrEqual()) {
            this.sendModernScorePacket(score, action);
            return;
        }
        Object packet = PACKET_SB_SET_SCORE.invoke();
        this.setField(packet, String.class, COLOR_CODES[score], 0);
        if (VersionType.V1_8.isHigherOrEqual()) {
            Object enumAction = action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;
            this.setField(packet, ENUM_SB_ACTION, enumAction);
        } else {
            this.setField(packet, Integer.TYPE, action.ordinal(), 1);
        }
        if (action == ScoreboardAction.CHANGE) {
            this.setField(packet, String.class, this.id, 1);
            this.setField(packet, Integer.TYPE, score);
        }
        this.sendPacket(packet);
    }

    private void sendModernScorePacket(int score, ScoreboardAction action) throws Throwable {
        Object enumAction;
        String objName = COLOR_CODES[score];
        Object object = enumAction = action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;
        if (PACKET_SB_RESET_SCORE == null) {
            this.sendPacket(PACKET_SB_SET_SCORE.invoke(enumAction, this.id, objName, score));
            return;
        }
        if (action == ScoreboardAction.REMOVE) {
            this.sendPacket(PACKET_SB_RESET_SCORE.invoke(objName, this.id));
            return;
        }
        T scoreFormat = this.getLineByScore(this.scores, score);
        Object format = scoreFormat != null ? FIXED_NUMBER_FORMAT.invoke(this.toMinecraftComponent(scoreFormat)) : BLANK_NUMBER_FORMAT;
        Object scorePacket = SCORE_OPTIONAL_COMPONENTS ? PACKET_SB_SET_SCORE.invoke(objName, this.id, score, Optional.empty(), Optional.of(format)) : PACKET_SB_SET_SCORE.invoke(objName, this.id, score, null, format);
        this.sendPacket(scorePacket);
    }

    protected void sendTeamPacket(int score, TeamMode mode) throws Throwable {
        this.sendTeamPacket(score, mode, null, null);
    }

    protected void sendTeamPacket(int score, TeamMode mode, T prefix, T suffix) throws Throwable {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw new UnsupportedOperationException();
        }
        Object packet = PACKET_SB_TEAM.invoke();
        this.setField(packet, String.class, this.id + ':' + score);
        this.setField(packet, Integer.TYPE, mode.ordinal(), VERSION_TYPE == VersionType.V1_8 ? 1 : 0);
        if (mode == TeamMode.REMOVE) {
            this.sendPacket(packet);
            return;
        }
        if (VersionType.V1_17.isHigherOrEqual()) {
            Object team = PACKET_SB_SERIALIZABLE_TEAM.invoke();
            this.setComponentField(team, null, 0);
            this.setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING);
            this.setComponentField(team, prefix, 1);
            this.setComponentField(team, suffix, 2);
            this.setField(team, String.class, "always", 0);
            this.setField(team, String.class, "always", 1);
            this.setField(team, ENUM_VISIBILITY, ENUM_VISIBILITY_ALWAYS, 0);
            this.setField(team, ENUM_COLLISION_RULE, ENUM_COLLISION_RULE_ALWAYS, 0);
            this.setField(packet, Optional.class, Optional.of(team));
        } else {
            this.setComponentField(packet, prefix, 2);
            this.setComponentField(packet, suffix, 3);
            this.setField(packet, String.class, "always", 4);
            this.setField(packet, String.class, "always", 5);
        }
        if (mode == TeamMode.CREATE) {
            this.setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score]));
        }
        this.sendPacket(packet);
    }

    private void sendPacket(Object packet) throws Throwable {
        if (this.deleted) {
            throw new IllegalStateException("This FastBoard is deleted");
        }
        if (this.player.isOnline()) {
            Object entityPlayer = PLAYER_GET_HANDLE.invoke(this.player);
            Object playerConnection = PLAYER_CONNECTION.invoke(entityPlayer);
            SEND_PACKET.invoke(playerConnection, packet);
        }
    }

    private void setField(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        this.setField(object, fieldType, value, 0);
    }

    private void setField(Object packet, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if (field.getType() != fieldType || count != i++) continue;
            field.set(packet, value);
        }
    }

    private void setComponentField(Object packet, T value, int count) throws Throwable {
        if (!VersionType.V1_13.isHigherOrEqual()) {
            String line = value != null ? this.serializeLine(value) : "";
            this.setField(packet, String.class, line, count);
            return;
        }
        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if (field.getType() != String.class && field.getType() != CHAT_COMPONENT_CLASS || count != i++) continue;
            field.set(packet, this.toMinecraftComponent(value));
        }
    }

    static {
        try {
            MethodHandle packetSbSetScore;
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            VERSION_TYPE = FastReflection.isRepackaged() ? VersionType.V1_17 : (FastReflection.nmsOptionalClass(null, "ScoreboardServer$Action").isPresent() || FastReflection.nmsOptionalClass(null, "ServerScoreboard$Method").isPresent() ? VersionType.V1_13 : (FastReflection.nmsOptionalClass(null, "IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent() || FastReflection.nmsOptionalClass(null, "ObjectiveCriteria$RenderType").isPresent() ? VersionType.V1_8 : VersionType.V1_7));
            String gameProtocolPackage = "network.protocol.game";
            Class<?> craftPlayerClass = FastReflection.obcClass("entity.CraftPlayer");
            Class<?> entityPlayerClass = FastReflection.nmsClass("server.level", "EntityPlayer", "ServerPlayer");
            Class<?> playerConnectionClass = FastReflection.nmsClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl");
            Class<?> packetClass = FastReflection.nmsClass("network.protocol", "Packet");
            Class<?> packetSbObjClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective", "ClientboundSetObjectivePacket");
            Class<?> packetSbDisplayObjClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective", "ClientboundSetDisplayObjectivePacket");
            Class<?> packetSbScoreClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore", "ClientboundSetScorePacket");
            Class<?> packetSbTeamClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam", "ClientboundSetPlayerTeamPacket");
            Class<?> sbTeamClass = VersionType.V1_17.isHigherOrEqual() ? FastReflection.innerClass(packetSbTeamClass, innerClass -> !innerClass.isEnum()) : null;
            Field playerConnectionField = Arrays.stream(entityPlayerClass.getFields()).filter(field -> field.getType().isAssignableFrom(playerConnectionClass)).findFirst().orElseThrow(NoSuchFieldException::new);
            Method sendPacketMethod = Stream.concat(Arrays.stream(playerConnectionClass.getSuperclass().getMethods()), Arrays.stream(playerConnectionClass.getMethods())).filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == packetClass).findFirst().orElseThrow(NoSuchMethodException::new);
            Optional<Class<?>> displaySlotEnum = FastReflection.nmsOptionalClass("world.scores", "DisplaySlot");
            CHAT_COMPONENT_CLASS = FastReflection.nmsClass("network.chat", "IChatBaseComponent", "Component");
            CHAT_FORMAT_ENUM = FastReflection.nmsClass(null, "EnumChatFormat", "ChatFormatting");
            DISPLAY_SLOT_TYPE = displaySlotEnum.orElse(Integer.TYPE);
            RESET_FORMATTING = FastReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21);
            SIDEBAR_DISPLAY_SLOT = displaySlotEnum.isPresent() ? FastReflection.enumValueOf(DISPLAY_SLOT_TYPE, "SIDEBAR", 1) : Integer.valueOf(1);
            PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass));
            PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField);
            SEND_PACKET = lookup.unreflect(sendPacketMethod);
            PACKET_SB_OBJ = FastReflection.findPacketConstructor(packetSbObjClass, lookup);
            PACKET_SB_DISPLAY_OBJ = FastReflection.findPacketConstructor(packetSbDisplayObjClass, lookup);
            Optional<Class<?>> numberFormat = FastReflection.nmsOptionalClass("network.chat.numbers", "NumberFormat");
            MethodHandle packetSbResetScore = null;
            MethodHandle fixedFormatConstructor = null;
            Object blankNumberFormat = null;
            boolean scoreOptionalComponents = false;
            if (numberFormat.isPresent()) {
                Class<?> blankFormatClass = FastReflection.nmsClass("network.chat.numbers", "BlankFormat");
                Class<?> fixedFormatClass = FastReflection.nmsClass("network.chat.numbers", "FixedFormat");
                Class<?> resetScoreClass = FastReflection.nmsClass(gameProtocolPackage, "ClientboundResetScorePacket");
                MethodType scoreType = MethodType.methodType(Void.TYPE, String.class, String.class, Integer.TYPE, CHAT_COMPONENT_CLASS, numberFormat.get());
                MethodType scoreTypeOptional = MethodType.methodType(Void.TYPE, String.class, String.class, Integer.TYPE, Optional.class, Optional.class);
                MethodType removeScoreType = MethodType.methodType(Void.TYPE, String.class, String.class);
                MethodType fixedFormatType = MethodType.methodType(Void.TYPE, CHAT_COMPONENT_CLASS);
                Optional<Field> blankField = Arrays.stream(blankFormatClass.getFields()).filter(f -> f.getType() == blankFormatClass).findAny();
                Optional<MethodHandle> optionalScorePacket = FastReflection.optionalConstructor(packetSbScoreClass, lookup, scoreTypeOptional);
                fixedFormatConstructor = lookup.findConstructor(fixedFormatClass, fixedFormatType);
                packetSbSetScore = optionalScorePacket.isPresent() ? optionalScorePacket.get() : lookup.findConstructor(packetSbScoreClass, scoreType);
                scoreOptionalComponents = optionalScorePacket.isPresent();
                packetSbResetScore = lookup.findConstructor(resetScoreClass, removeScoreType);
                blankNumberFormat = blankField.isPresent() ? blankField.get().get(null) : null;
            } else if (VersionType.V1_17.isHigherOrEqual()) {
                Class<?> enumSbAction = FastReflection.nmsClass("server", "ScoreboardServer$Action", "ServerScoreboard$Method");
                MethodType scoreType = MethodType.methodType(Void.TYPE, enumSbAction, String.class, String.class, Integer.TYPE);
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, scoreType);
            } else {
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, MethodType.methodType(Void.TYPE));
            }
            PACKET_SB_SET_SCORE = packetSbSetScore;
            PACKET_SB_RESET_SCORE = packetSbResetScore;
            PACKET_SB_TEAM = FastReflection.findPacketConstructor(packetSbTeamClass, lookup);
            PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass != null ? FastReflection.findPacketConstructor(sbTeamClass, lookup) : null;
            FIXED_NUMBER_FORMAT = fixedFormatConstructor;
            BLANK_NUMBER_FORMAT = blankNumberFormat;
            SCORE_OPTIONAL_COMPONENTS = scoreOptionalComponents;
            if (VersionType.V1_17.isHigherOrEqual()) {
                ENUM_VISIBILITY = FastReflection.nmsClass("world.scores", "ScoreboardTeamBase$EnumNameTagVisibility", "Team$Visibility");
                ENUM_COLLISION_RULE = FastReflection.nmsClass("world.scores", "ScoreboardTeamBase$EnumTeamPush", "Team$CollisionRule");
                ENUM_VISIBILITY_ALWAYS = FastReflection.enumValueOf(ENUM_VISIBILITY, "ALWAYS", 0);
                ENUM_COLLISION_RULE_ALWAYS = FastReflection.enumValueOf(ENUM_COLLISION_RULE, "ALWAYS", 0);
            } else {
                ENUM_VISIBILITY = null;
                ENUM_COLLISION_RULE = null;
                ENUM_VISIBILITY_ALWAYS = null;
                ENUM_COLLISION_RULE_ALWAYS = null;
            }
            for (Class clazz : Arrays.asList(packetSbObjClass, packetSbDisplayObjClass, packetSbScoreClass, packetSbTeamClass, sbTeamClass)) {
                Field[] fields;
                if (clazz == null) continue;
                for (Field field2 : fields = (Field[])Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toArray(Field[]::new)) {
                    field2.setAccessible(true);
                }
                PACKETS.put(clazz, fields);
            }
            if (VersionType.V1_8.isHigherOrEqual()) {
                String enumSbActionClass = VersionType.V1_13.isHigherOrEqual() ? "ScoreboardServer$Action" : "PacketPlayOutScoreboardScore$EnumScoreboardAction";
                ENUM_SB_HEALTH_DISPLAY = FastReflection.nmsClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay", "ObjectiveCriteria$RenderType");
                ENUM_SB_ACTION = FastReflection.nmsOptionalClass("server", enumSbActionClass, "ServerScoreboard$Method").orElse(null);
                ENUM_SB_HEALTH_DISPLAY_INTEGER = FastReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0);
                ENUM_SB_ACTION_CHANGE = ENUM_SB_ACTION != null ? FastReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0) : null;
                ENUM_SB_ACTION_REMOVE = ENUM_SB_ACTION != null ? FastReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1) : null;
            } else {
                ENUM_SB_HEALTH_DISPLAY = null;
                ENUM_SB_ACTION = null;
                ENUM_SB_HEALTH_DISPLAY_INTEGER = null;
                ENUM_SB_ACTION_CHANGE = null;
                ENUM_SB_ACTION_REMOVE = null;
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    static enum VersionType {
        V1_7,
        V1_8,
        V1_13,
        V1_17;


        public boolean isHigherOrEqual() {
            return VERSION_TYPE.ordinal() >= this.ordinal();
        }
    }

    public static enum ObjectiveMode {
        CREATE,
        REMOVE,
        UPDATE;

    }

    public static enum ScoreboardAction {
        CHANGE,
        REMOVE;

    }

    public static enum TeamMode {
        CREATE,
        REMOVE,
        UPDATE,
        ADD_PLAYERS,
        REMOVE_PLAYERS;

    }
}

