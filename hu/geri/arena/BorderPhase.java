/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.arena;

public class BorderPhase {
    private int startTime;
    private int size;
    private int seconds;
    private boolean applied;

    public BorderPhase(int startTime, int size, int seconds) {
        this.startTime = startTime;
        this.size = size;
        this.seconds = seconds;
        this.applied = false;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isApplied() {
        return this.applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }
}

