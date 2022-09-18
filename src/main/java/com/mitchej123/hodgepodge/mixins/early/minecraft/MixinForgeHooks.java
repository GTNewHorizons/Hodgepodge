package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeHooks {
    private static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR        namespace                 port     path
            //         ends
            //   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|
            //   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    /**
     * @author LexManos
     * @reason Crash fix backported from https://github.com/MinecraftForge/MinecraftForge/commit/5b28eb53e8623448b1c2bdb46b8924662e690995
     */
    @Overwrite
    public static IChatComponent newChatWithLinks(String string) {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        IChatComponent ichat = new ChatComponentText("");
        Matcher matcher = MixinForgeHooks.URL_PATTERN.matcher(string);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            ichat.appendText(string.substring(lastEnd, start));
            lastEnd = end;
            String url = string.substring(start, end);
            IChatComponent link = new ChatComponentText(url);

            try {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null) url = "http://" + url;
            } catch (URISyntaxException e) {
                // Bad syntax bail out!
                ichat.appendText(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            ichat.appendSibling(link);
        }

        // Append the rest of the message.
        ichat.appendText(string.substring(lastEnd));
        return ichat;
    }
}
