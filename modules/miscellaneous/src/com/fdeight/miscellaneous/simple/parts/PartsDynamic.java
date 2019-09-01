package com.fdeight.miscellaneous.simple.parts;

public class PartsDynamic extends Parts {
    static class PartsDynamicFactory extends PartsFactory {
        @Override
        Parts createParts(final int[] values, final int numParts) {
            return new PartsDynamic(values, numParts);
        }
    }

    static final PartsFactory factory = new PartsDynamicFactory();

    private PartsDynamic(final int[] values, final int numParts) {
        super(values, numParts);
    }

    @Override
    protected boolean computeAlgorithm() {
        return false;
    }
}
