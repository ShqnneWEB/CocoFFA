/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.bstats.charts;

import hu.geri.libs.bstats.charts.CustomChart;
import hu.geri.libs.bstats.json.JsonObjectBuilder;
import java.util.concurrent.Callable;

public class SingleLineChart
extends CustomChart {
    private final Callable<Integer> callable;

    public SingleLineChart(String chartId, Callable<Integer> callable) {
        super(chartId);
        this.callable = callable;
    }

    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        int value = this.callable.call();
        if (value == 0) {
            return null;
        }
        return new JsonObjectBuilder().appendField("value", value).build();
    }
}

