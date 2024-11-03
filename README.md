# Carpet-Extra-Extras
Even more features for the carpet mod.

## Carpet Extra Extras Rules:
### trackEnderPearls
Tracks all thrown Ender Pearls on the server; this or `enderPearlChunkLoadingFix` must be enabled for `/log pearls` to work.
* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `VANILLA`

### enderPearlChunkLoadingFix
At high speeds, Mojang's implementation of ender pearl chunk loading can fail. This implementation attempts to fix this. This may not be required if using `pre21ThrowableEntityBehavior`. This also may improve performance.
* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `VANILLA`, `BUGFIX`

### pre21ThrowableEntityBehavior
Restores 1.16.2-1.21.1 throwable entity (ender pearl, snowball, etc.) behavior, specifically behavior found in 24w28a where the thrown randomness changes are present. This also fixes a bug introduced where at high speeds an Ender Pearl would land in the wrong position.
* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `FEATURE`, `BUGFIX`, `LTS`

### emptyShulkerStackLimitAllContainers
> [!NOTE]
This overrides any other `emptyShulkerStack` rule.

Allows empty shulker boxes to stack to the set amount when being transferred by hoppers. This is intended to be used with `stackableShulkerBoxes`
* Type: `String`
* Default value: `false`
* Allowed options: `false`, `1-64`
* Categories: `FEATURE`, `LTS`

### emptyShulkerStackLimitHoppers
Allows empty shulker boxes to stack to the set amount inside hoppers when being transferred by hoppers. This is intended to be used with `stackableShulkerBoxes
* Type: `String`
* Default value: `false`
* Allowed options: `false`, `1-64`
* Categories: `FEATURE`, `LTS`

### emptyShulkerStackLimitDroppers
Allows empty shulker boxes to stack to the set amount inside droppers when being transferred by hoppers. This is intended to be used with `stackableShulkerBoxes
* Type: `String`
* Default value: `false`
* Allowed options: `false`, `1-64`
* Categories: `FEATURE`, `LTS`

### emptyShulkerStackLimitDispensers
Allows empty shulker boxes to stack to the set amount inside dispensers when being transferred by hoppers. This is intended to be used with `stackableShulkerBoxes`
* Type: `String`
* Default value: `false`
* Allowed options: `false`, `1-64`
* Categories: `FEATURE`, `LTS`

### carpetBotsSkipNight
Allows the night to be skipped without carpet bots/fake players being asleep.
* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `FEATURE`

### carpetBotTeam
If carpet bots/fake players should be added to a special team.
* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `FEATURE`

### carpetBotTeamName
The name of the team the carpet bots/fake use.
* Type: `String`
* Default value: `cee_bots`
* Allowed options: `any string up to 64 characters in length`
* Categories: `FEATURE`

### carpetBotTeamPrefixName
The prefix of the team the carpet bots/fake will use.
* Type: `String`
* Default value: `[Bots]`
* Allowed options: `any string up to 16 characters in length`
* Categories: `FEATURE`

### carpetBotTeamColor
The color the team for the carpet bots/fake will use.
* Type: `Chat Formatter (Color)`
* Default value: `gray`
* Allowed options: `aqua`, `black`, `blue`, `bold`, `dark_aqua`, `dark_blue`, `dark_gray`, `dark_green`, `dark_purple`, `dark_red`, `gold`, `gray`, `green`, `italic`, `light_purple`, `obfuscated`, `red`, `reset`, `strikethrough`, `underline`, `white`, `yellow`
* Categories: `FEATURE`

### carpetBotTeamPrefixColor
The color of the prefix of the team the carpet bots/fake will use.
* Type: `Chat Formatter (Color)`
* Default value: `gold`
* Allowed options: `aqua`, `black`, `blue`, `bold`, `dark_aqua`, `dark_blue`, `dark_gray`, `dark_green`, `dark_purple`, `dark_red`, `gold`, `gray`, `green`, `italic`, `light_purple`, `obfuscated`, `red`, `reset`, `strikethrough`, `underline`, `white`, `yellow`
* Categories: `FEATURE`

## Carpet Extra Extras Commands:
### commandCam
Enables a freecam mode that can be used by using `/c` or `/cam`.
* Type: `Boolean`
* Default value: `false`
* Allowed options: `true`, `false`
* Categories: `FEATURE`, `COMMAND`
