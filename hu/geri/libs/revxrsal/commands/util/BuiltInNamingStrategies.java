/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.revxrsal.commands.util;

public final class BuiltInNamingStrategies {
    public static String separateCamelCase(String name, String separator) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
        }
        return translation.toString();
    }

    public static String upperCaseFirstLetter(String name) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = name.charAt(index);
        while (index < name.length() - 1 && !Character.isLetter(firstCharacter)) {
            fieldNameBuilder.append(firstCharacter);
            firstCharacter = name.charAt(++index);
        }
        if (!Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = BuiltInNamingStrategies.modifyString(Character.toUpperCase(firstCharacter), name, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        }
        return name;
    }

    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return indexOfSubstring < srcString.length() ? firstCharacter + srcString.substring(indexOfSubstring) : String.valueOf(firstCharacter);
    }
}

