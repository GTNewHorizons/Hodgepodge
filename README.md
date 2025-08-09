# A Hodgepodge of Fixes

Requires UniMixins 0.1.14+ (https://github.com/LegacyModdingMC/UniMixins/) to work.  
Requires GTNHLib 0.2.2+ (https://github.com/GTNewHorizons/GTNHLib) to work.

## Running

If running in dev add the following Program arguments: 
```
--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.hodgepodge.json
```

## License

Hodgepodge is LGPL-3.

## Tweak Features

### Chat
- The bed "cannot sleep" messages display above the hotbar instead of in chat.
- On MacOS, the Command key is used for copy/cut/paste shortcuts in all text fields.
- When chat is not open, the shading behind chat messages is removed.
- Repeated identical consecutive chat messages are condensed into a single line and repetition count.
- Chat history buffer is increased. (default 8191, vanilla 100)
- Chat messages can be a maximum of 256 characters instead of 100.

### Base Minecraft

- Enchantments and amplified potions use arabic numerals (1, 5, 9) instead of roman numerals (I, V, IX).
- Minecraft Realms button is disabled.
- Hostile mobs that pick up dropped items drop those items when despawning.
- Hostile mobs are prevented from picking up dropped items.
- The inventory no longer shifts to the side when potion effects are active.
- The crosshair is hidden in third-person F5 mode.
- The particle rendering limit is increased. (default 8000, vanilla 4000)
- Pick-block is enabled in survival (swap with current hotbar slot).
- The sound on placing a minecart is removed.
- The sprint keybind is moved to the Movement keybind category.
- Autosave interval is decreased to 45 seconds.
- Water opacity is reduced from 3 to 1 to match modern Minecraft versions.
- When a container is closed, items are placed in the player's inventory instead of dropped.
- You no longer see your own potion particle effects.

### Base Minecraft, default off
- Night vision can be changed to not have the blueish sky tint.
- Block placing repetition delay can be decreased.
- Crosshair color can be set to not be inverted.
- Port number of LAN server can be changed.
- Prevent endermen grabbing and placing blocks.
- Skyblock support
  - Terrain generation is replaced with all air blocks.
  - Chunk population (structures) is disabled.
  - Modded chunk population is disabled.
 
### Mod tweaks
- Several mods have their keybinds unbound on first launch to avoid conflicts.
- Forge: The loading screen progress bar is sped up.
- Automagy: The Thirsty Tank implements the fluid container interface.
- Biomes O' Plenty: 5 Fir Saplings planted together in a plus (+) shape grow a large Fir tree.
- Biomes O' Plenty: Quicksand generation can be disabled.
- Extra Utilities: When a zombie is killed by Extra Utilities spikes that use a fake player (Gold, Diamond), zombie aid is blocked.
- Extra Utilities: Drums no longer eat your IC2 cells or Forestry capsules, returning the empty container to you.
- Extra Utilities: The Last Millenium no longer has rain or creatures.
- Extra Utilities: Fluid retrieval node will not void fluid.
- Industrialcraft 2: Fluid cells display the localized name of the fluid.
- Industrialcraft 2: Coolant slots cannot be accessed by automation unless the reactor is a fluid reactor.
- Industrialcraft 2: Fluid cells implement the fluid container interface.
- Industrialcraft 2: Seeds stack to 64 if they have identical stats.
- Journey Map: The waypoints screen scrolling code is fixed.
- Minechem: The effect ID of Atropine High (Delirium) is configurable to resolve ID conflicts.
- NotEnoughItems: The NEI interface will wrap around the vanilla potion effects display.
- Optifine: The 'GL error' messages from shaders are hidden. 
- ProjectRed: The problem where components would pop off due to unloaded chunks is fixed.
- ProjectRed: A HUD lighting glitch is fixed.
- Railcraft: Personal anchors and Passive anchors are woken up on login.
- Thaumcraft: Aspects are sorted by localized name instead of internal tag.
- Thaumcraft: Golems function in dimensions higher than 255.
- Thaumcraft: Wand Recharge Pedestals accept Centivis.
- Traveller's Gear: Return items that were placed in TG slots after the mod is removed.
- VoxelMap: The file extension is changed from .zip to .data to stop an adverse reaction with the Technic launcher.
- VoxelMap: The Y coordinate is no longer off by one.
- Witchery: Inhabited mirrors have fixed player skin reflections.

## GregTech Pollution

- Grass, flowers, and water are recolored based on pollution levels.
- Pollution is added to Minecraft furnaces, all explosions, Galacticraft rockets, and to Railcraft coke ovens, fireboxes, tunnel bore, and Hobbyist steam engine.

## Other changes

Various speedups and crash fixes are implemented, such as preventing "chunk bans" by increasing the packet size limits.
