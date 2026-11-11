package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.EnumChatFormatting;

import org.junit.jupiter.api.Test;

import com.mitchej123.hodgepodge.util.StringUtil;

public class StringUtilsTest {

    @Test
    void testRemoveFormattingCodes() {
        String[] testArray = new String[] { null, "", "§", "a", "OZI§bUNvnZV§a auivniuv", "nvuz§e",
                "n§BnvuZVBzbvpizBVUBZUIbvIPZBVIZbuivbiZBVUI", "§RvjkzVBvVUIOVEUIV VIJVQEUHI", " §f\ud83c\udf89 2",
                " §2\ud83c\udfc0 5", " ZIUECBPZbc iZUCUN\u26bd rs6", " §1\ud83d\udc7e 9",
                " Players: §a37/10\ud83d\udc0d §a010", " §0\ud83d\udc7d 12",
                "§6[Y] §fOncionzC§6\ud83c\udf20§6 HP§7: §61,0008", "§c[R] §fIB85AC§c\ud83d\udc7e§c HP§7: §c1,0009",
                "§2[G] §fACAZ§2\ud83d\udc0d§2 HP§7: §21,00010", "§1[B] 1470§1 H\ud83d\udd2e§1P§7: §11,00011" };

        for (String text : testArray) {
            assertEquals(
                    EnumChatFormatting.getTextWithoutFormattingCodes(text),
                    StringUtil.removeFormattingCodes(text));
        }
    }

    @Test
    void testRemoveExtendedFormattingCodes() {
        // §x RGB color (14-char sequence decomposes into 7 pairs: §x §F §F §6 §B §4 §A)
        assertEquals("Coral", StringUtil.removeFormattingCodes("§x§F§F§6§B§4§ACoral"));
        // §g gradient (30-char sequence: §g + two §x blocks)
        assertEquals("Fire", StringUtil.removeFormattingCodes("§g§x§F§F§0§0§0§0§x§F§F§D§7§0§0Fire"));
        // §q rainbow
        assertEquals("Rainbow", StringUtil.removeFormattingCodes("§qRainbow"));
        // §z wave
        assertEquals("Wave", StringUtil.removeFormattingCodes("§zWave"));
        // §v dinnerbone
        assertEquals("Flip", StringUtil.removeFormattingCodes("§vFlip"));
        // Mixed vanilla + extended
        assertEquals("Hello World!", StringUtil.removeFormattingCodes("§x§F§F§0§0§0§0Hello §qWorld§r!"));
        // Case-insensitive: uppercase extended codes
        assertEquals("test", StringUtil.removeFormattingCodes("§Xtest"));
        assertEquals("test", StringUtil.removeFormattingCodes("§Qtest"));
        assertEquals("test", StringUtil.removeFormattingCodes("§Ztest"));
        assertEquals("test", StringUtil.removeFormattingCodes("§Vtest"));
        assertEquals("test", StringUtil.removeFormattingCodes("§Gtest"));
    }
}
