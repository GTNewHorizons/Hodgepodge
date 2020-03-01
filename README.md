# A Hodgepodge of Fixes

## Fixes:
* RandomPositionGenerator - Backports removal of the North/West bias
* FixGrassChunkLoads - Stops grass tick/spread from loading chunks
* FixIc2DirectInventoryAccess - Swaps out direct inventory access in crop functions with `setInventorySlotContents` so they work with OC robots

## Running


If running in dev add the following arguments: 
```
-Dfml.coreMods.load=com.mitchej123.hodgepodge.core.HodgepodgeLoadingPlugin
```
