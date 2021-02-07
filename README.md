# A Hodgepodge of Fixes

Requires SpongeMixins mod (https://github.com/GTNewHorizons/SpongeMixins) to work.

## Fixes:
* FixNorthWestBias - Backports removal of the North/West bias
* FixFenceConnections - Tweaks fence connection logic to work with other mods
* FixIc2DirectInventoryAccess - Swaps out direct inventory access in crop functions with `setInventorySlotContents` so they work with OC robots
* FixVanillaUnprotectedGetBlock - Fixes various unprotected Vanilla getBlock() calls, making sure the chunk is loaded first.  Includes the previous `fixGrassChunkLoads`
* FixIc2UnprotectedGetBlock - Fixes various unprotected IC2 getBlock() calls, making sure the chunk is loaded first.
* FixThaumcraftUnprotectedGetBlock - Fixes various unprotected Thaumcraft getBlock() calls, making sure the chunk is loaded first.
* FixIc2Nightvision - Prevents IC2 night vision from blinding you when it's bright out, making it on par with other nightvision available
* FixHungerOverhaul - Patches unintended mod interaction with Spice Of Life - Carrot Edition

## Speedups
* SpeedupChunkCoordinatesHashCode - Swaps out the HashCode function for ChunkCoordinates with one that provides better performance with HashSet

## Running

If running in dev add the following Program arguments: 
```
--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.hodgepodge.json
```
