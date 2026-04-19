package com.mitchej123.hodgepodge.util;

/**
 * Shared sign-line length caps used by the four mixins that together bypass vanilla's 15-char sign limit for raw text
 * that contains {@code &} color codes. All four mixins must agree on the raw cap, so it lives here.
 */
public final class SignLimits {

    private SignLimits() {}

    /** Vanilla's visible-chars-per-line cap. What the player actually sees on the sign. */
    public static final int VISIBLE = 15;

    /**
     * Raw cap we allow past vanilla's limit so {@code &} color codes can fit alongside {@value #VISIBLE} visible chars.
     */
    public static final int RAW = 90;
}
