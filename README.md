# A Hodgepodge of Fixes

Requires SpongeMixins mod (https://github.com/GTNewHorizons/SpongeMixins) to work.

## Fixes:
* RandomPositionGenerator - Backports removal of the North/West bias
* FixGrassChunkLoads - Stops grass tick/spread from loading chunks
* FixFenceConnections - Tweaks fence connection logic to work with other mods
* FixIc2DirectInventoryAccess - Swaps out direct inventory access in crop functions with `setInventorySlotContents` so they work with OC robots
* SpeedupChunkCoordinatesHashCode - Swaps out the HashCode function for ChunkCoordinates with one that provides better performance with HashSet
## Running


If running in dev add the following Program arguments: 
```
--tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.hodgepodge.json
```
