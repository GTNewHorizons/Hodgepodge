package com.mitchej123.hodgepodge.client.bettermodlist;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;

import cpw.mods.fml.common.ModContainer;

public enum SortType {

    NORMAL(24, (a, b) -> 0),
    A_TO_Z(25, Comparator.comparing(SortType::sortKey)),
    Z_TO_A(26, Comparator.comparing(SortType::sortKey).reversed());

    public static final SortType[] VALUES = SortType.values();

    private static final Map<Integer, SortType> BY_BUTTON_ID = new HashMap<>();
    static {
        for (SortType t : values()) {
            BY_BUTTON_ID.put(t.buttonID, t);
        }
    }

    private static String sortKey(ModContainer m) {
        return StringUtils.stripControlCodes(m.getName()).toLowerCase(Locale.US);
    }

    private final int buttonID;
    private final Comparator<ModContainer> comparator;

    SortType(int buttonID, Comparator<ModContainer> comparator) {
        this.buttonID = buttonID;
        this.comparator = comparator;
    }

    // Get the comparator associated with this SortType
    public Comparator<ModContainer> getComparator() {
        return comparator;
    }

    // Get the button ID for this SortType
    public int getButtonID() {
        return buttonID;
    }

    // Get SortType based on button ID (to match button press with the sorting mode)
    public static SortType getTypeForButton(int buttonID) {
        return BY_BUTTON_ID.get(buttonID);
    }

    public String getName() {
        return I18n.format("bettermodlist.sort." + this.name().toLowerCase());
    }
}
