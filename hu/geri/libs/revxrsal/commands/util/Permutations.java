/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

import hu.geri.libs.revxrsal.commands.command.CommandActor;
import hu.geri.libs.revxrsal.commands.node.ParameterNode;
import hu.geri.libs.revxrsal.commands.util.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Permutations {
    private Permutations() {
        Preconditions.cannotInstantiate(Permutations.class);
    }

    public static <A extends CommandActor> List<List<ParameterNode<A, Object>>> generatePermutations(List<ParameterNode<A, Object>> list) {
        ArrayList<ParameterNode<A, Object>> required = new ArrayList<ParameterNode<A, Object>>();
        ArrayList<ParameterNode<A, Object>> optional = new ArrayList<ParameterNode<A, Object>>();
        for (ParameterNode<A, Object> node : list) {
            if (node.isRequired()) {
                required.add(node);
                continue;
            }
            optional.add(node);
        }
        ArrayList<List<ParameterNode<A, Object>>> truePermutations = new ArrayList<List<ParameterNode<A, Object>>>();
        ArrayList<List<ParameterNode<A, Object>>> falsePermutations = new ArrayList<List<ParameterNode<A, Object>>>();
        Permutations.permute(required, 0, truePermutations);
        Permutations.permute(optional, 0, falsePermutations);
        ArrayList<List<ParameterNode<A, Object>>> result = new ArrayList<List<ParameterNode<A, Object>>>();
        for (List list2 : truePermutations) {
            for (List list3 : falsePermutations) {
                ArrayList combined = new ArrayList(list2);
                combined.addAll(list3);
                result.add(combined);
            }
        }
        return result;
    }

    private static <A extends CommandActor> void permute(List<ParameterNode<A, Object>> list, int start, List<List<ParameterNode<A, Object>>> result) {
        if (start == list.size()) {
            result.add(new ArrayList<ParameterNode<A, Object>>(list));
            return;
        }
        for (int i = start; i < list.size(); ++i) {
            Collections.swap(list, start, i);
            Permutations.permute(list, start + 1, result);
            Collections.swap(list, start, i);
        }
    }
}

