package it.pagopa.bs.common.util.parser;

public class EnumUtil {

    private EnumUtil() {}

    public static <T extends Enum<T>> T tryParseEnum(Class<T> enumType, String enumString) {
        try {
            return Enum.valueOf(enumType, enumString);
        } catch (Exception e) {
            return null;
        }
    }
}
