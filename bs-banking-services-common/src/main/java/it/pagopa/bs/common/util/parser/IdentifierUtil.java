package it.pagopa.bs.common.util.parser;

import it.pagopa.bs.common.exception.ResourceNotFoundException;

public class IdentifierUtil {

    private IdentifierUtil() {}

    public static long tryParseId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Resource");
        }
    }
}
