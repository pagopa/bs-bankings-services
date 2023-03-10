package it.pagopa.bs.checkiban.util;

import it.pagopa.bs.checkiban.model.api.request.simple.ValidateAccountHolderRequest;

public class KeyGeneratorUtil {

    private KeyGeneratorUtil() {}

    private static final String CACHE_KEY_SEPARATOR = "§§§";

    public static String computeCachedResponseKey(
            ValidateAccountHolderRequest inputModel,
            boolean requireSingle,
            boolean extended
    ) {
        return inputModel.getAccount().getValue() + CACHE_KEY_SEPARATOR +
                fullOrNull(inputModel.getAccountHolder().getFiscalCode()) + CACHE_KEY_SEPARATOR +
                fullOrNull(inputModel.getAccountHolder().getVatCode()) + CACHE_KEY_SEPARATOR +
                fullOrNull(inputModel.getAccountHolder().getTaxCode()) + CACHE_KEY_SEPARATOR +
                inputModel.getAccountHolder().getType() + CACHE_KEY_SEPARATOR +
                requireSingle + CACHE_KEY_SEPARATOR +
                extended;
    }

    private static String fullOrNull(String s) {
        return (s != null && s.equals("")) ? null : s;
    }
}
