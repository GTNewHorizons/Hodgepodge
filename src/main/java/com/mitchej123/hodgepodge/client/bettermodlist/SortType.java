package com.mitchej123.hodgepodge.client.bettermodlist;

import java.util.Comparator;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;

import cpw.mods.fml.common.ModContainer;

public enum SortType {

    NORMAL(24),
    A_TO_Z(25),
    Z_TO_A(26);

    private final int buttonID;
    private final ModComparator comparator;

    // Constructor that accepts button ID
    private SortType(int buttonID) {
        this.buttonID = buttonID;
        this.comparator = new ModComparator(this);
    }

    // Get the comparator associated with this SortType
    public ModComparator getComparator() {
        return comparator;
    }

    // Get the button ID for this SortType
    public int getButtonID() {
        return buttonID;
    }

    // Get SortType based on button ID (to match button press with the sorting mode)
    public static SortType getTypeForButton(int buttonID) {
        for (SortType t : values()) {
            if (t.buttonID == buttonID) {
                return t;
            }
        }
        return null; // Default to null if no match is found
    }

    // Comparator to compare mod containers based on the selected sort type
    public static class ModComparator implements Comparator<ModContainer> {

        private final SortType sortType;

        private ModComparator(SortType sortType) {
            this.sortType = sortType;
        }

        @Override
        public int compare(ModContainer o1, ModContainer o2) {
            String name1 = StringUtils.stripControlCodes(o1.getName()).toLowerCase();
            String name2 = StringUtils.stripControlCodes(o2.getName()).toLowerCase();

            switch (sortType) {
                case A_TO_Z:
                    return name1.compareTo(name2);
                case Z_TO_A:
                    return name2.compareTo(name1);
                default:
                    return 0; // No sorting applied (i.e., default state)
            }
        }
    }

    // Method to get the name of the SortType
    public String getName() {
        return I18n.format("bettermodlist.sort." + this.name().toLowerCase());
    }
}
