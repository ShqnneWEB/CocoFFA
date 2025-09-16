/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.arena;

import java.util.List;

public class EffectPhase {
    private int startTime;
    private List<String> effects;
    private boolean applied;

    public EffectPhase(int startTime, List<String> effects) {
        this.startTime = startTime;
        this.effects = effects;
        this.applied = false;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public List<String> getEffects() {
        return this.effects;
    }

    public void setEffects(List<String> effects) {
        this.effects = effects;
    }

    public boolean isApplied() {
        return this.applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }
}

