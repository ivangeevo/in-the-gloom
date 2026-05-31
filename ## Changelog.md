## v1.4.3
+ Added compatibility with LambDynamicLights, so the player will not get gloom when holding a dynamic light source that's registered with the mod
+ Updated the mod to Fabric API 0.116.11, Fabric Loader 0.19.2 & BTWR: Shared Library 0.8.4

## v1.4.2
+ Changed the gloom living entity event to be handled through the BTWREvents class from BTWR: Shared Library instead of having it in the mod
+ Updated the mod to BTWR: Shared Library 0.7

## v1.4.1
+ Generally changed how gloom is calculated. This should improve some cases where gloom was being considered active in the wrong places
+ Fixed a bug with gloom applying on boats and minecarts because of wrong Y position calculation when checking light level
+ Changed the config settings for the mod so that gloom is no longer applied in The Nether and The End by default

## v1.4
+ The mod is officially in Beta now!
+ Added authors(ivangeevo, jeffinitup) to the mod files, so they should display properly in places where they should be displayed
+ Changed all configuration options in the mod to use the custom config library added by BTWR: Shared Library
+ Changed player gloom mixin modifications to be handled with the Fabric Data Attachment API instead
+ Changed the mod license internally to be CC-BY-4.0
+ Split client side logic into its own package
+ Refactored pretty much the whole code; mainly for readability and cleaning up, but also so it's more in order with other mods from the BTWR project
+ Updated the mod to Fabric API 0.116.7, Fabric Loader 0.17.3 & BTWR: Shared Library 0.6.5

## v1.3
+ Added BTWR SL as dependency (jeffyjamzhd)
+ Rewritten hud rendering to use the new PenaltyDisplayManager from BTWRSL (jeffyjamzhd)
+ Added translation keys for the gloom penalties (jeffyjamzhd)
+ Updated the mod to Fabric API 0.115.6, Fabric Loader 0.16.13 & BTWR: Shared Library 0.53 (ivangeevo)

## v1.2
+ Added configuration options with Mod Menu for selecting which dimensions are affected by gloom.
+ Added mod icon and mod description to display properly
+ Fixed the player being able to get gloom effects while in spectator mode.
+ Changed (fixed?) the FOV calculations code for gloom to work better. To be tested further.

## v1.1
+ Added compatibility with Im 'movens mod's status effect text
+ Made the gloom effects to activate only on gloom nights (simplified code logic, ignoring rain/thundering, but works better for now)
+ Updated the mod to Fabric API 0.110.0

## v1.0
+ Initial release