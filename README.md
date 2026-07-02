# SMP Plugin

A lightweight Paper/Spigot plugin adding warps, player-to-player teleport requests, per-player homes, and a GUI item-give tool.

**Built for small SMP servers** (the kind you run for yourself and a handful of friends) — it favors simple YAML storage and sane defaults over the configuration surface of a large public-server plugin.

## Requirements

- Minecraft 1.21.x (Paper or Spigot)
- Java 25
- Maven (for building from source)

## Building

```
mvn package
```

The compiled jar is written to `target/SMPPlugin-1.0.0.jar`. Drop it into your server's `plugins/` folder and restart.

> `pom.xml` pins the `paper-api` dependency to a specific `1.21.x-R0.1-SNAPSHOT` version. If that exact version isn't available from the PaperMC repo when you build, bump it to the newest one that is — the API is backwards compatible, so a slightly older API version still runs fine against a newer server jar.

## Commands & Permissions

| Command | Description | Permission | Default |
|---|---|---|---|
| `/warp <name>` | Teleport to a warp | `smp.warp.use` | everyone |
| `/warps` | List all warps (clickable) | `smp.warp.use` | everyone |
| `/setwarp <name>` | Create/overwrite a warp at your location | `smp.warp.set` | op |
| `/delwarp <name>` | Delete a warp | `smp.warp.delete` | op |
| `/tpa <player>` | Request to teleport to a player | `smp.tpa.use` | everyone |
| `/tpaaccept` | Accept a pending teleport request | `smp.tpa.use` | everyone |
| `/tpadeny` | Deny a pending teleport request | `smp.tpa.use` | everyone |
| `/sethome [name]` | Create/overwrite a home (defaults to `"home"`) | `smp.home.set` | everyone |
| `/home [name]` | Teleport to a home | `smp.home.use` | everyone |
| `/delhome [name]` | Delete a home | `smp.home.delete` | everyone |
| `/homes` | List your homes (clickable) | `smp.home.use` | everyone |
| `/itemgive <player> [material] [amount]` | Give an item via the plugin's inventory API, or open a GUI picker | `smp.itemgive` | op (see below) |

Teleport requests from `/tpa` arrive as a chat message with clickable **[Accept]**/**[Deny]** buttons; warp and home listings are clickable too.

`/itemgive` is available to anyone with `smp.itemgive`, plus any player name listed under `itemgive-allowed-players` in `config.yml` — useful for letting a specific trusted player use it without granting them full server-operator status. It calls the Bukkit inventory API directly rather than dispatching vanilla `/give`.

Running `/itemgive <player>` with just a name (no material) opens a GUI modeled on the creative-mode inventory: the top row is a persistent set of category tabs (Tools, Weapons, Armor, Redstone, Brewing, Food, Spawn Eggs, Blocks, Miscellaneous) that stay visible while browsing, so switching categories is a single click rather than a "back" trip. A **Search** tab opens a real anvil text box — type a term and click the result to jump to matching items (Bukkit's `Player#openAnvil` is used here specifically, since a fake/virtual anvil inventory never fires the rename-text event). Clicking an item opens a quantity picker (±1/±10/±64 buttons with a live preview), and **Give** hands the items over and drops you back at that same tab/page so you can keep picking more without retyping the command. Note this is a lookalike, not the literal vanilla creative screen — that screen is hardcoded client-side and isn't something a server plugin can open. Console senders must use the full `/itemgive <player> <material> [amount]` form since there's no inventory to open a GUI in.

## Project Structure

```
src/main/java/com/smpplugin/core/
  SMPPlugin.java     JavaPlugin entry point — wires managers, commands, and listeners
  commands/          One CommandExecutor (+ TabCompleter where useful) per command
  data/              Persistence: WarpManager, HomeManager, TpaManager
  gui/               /itemgive's inventory GUI (category menu, item list, quantity
                     picker, anvil-based search) — see ItemGiveMenu.java for the
                     entry points and ItemGiveGuiListener.java for click handling
  listeners/         Bukkit event listeners (e.g. clearing stale /tpa requests on quit)
  util/              Shared helpers: Messages (chat output), LocationUtil (YAML <-> Location)
src/main/resources/
  plugin.yml         Command + permission declarations
  config.yml         Default settings (copied to plugins/SMPPlugin/ on first run)
```

## Configuration (`config.yml`)

```yaml
# Maximum number of homes a player may create. Set to -1 for no limit (default).
max-homes: -1

# How long (in seconds) a /tpa request stays pending before it auto-expires.
tpa-request-timeout-seconds: 60

# Player names allowed to use /itemgive without needing the smp.itemgive
# permission node granted (players with that permission, or ops, can always use it).
itemgive-allowed-players:
  - ExamplePlayer
```

## License

All rights reserved — see [LICENSE](LICENSE). This repository is source-available for reference only; it is not open source and may not be reused, modified, or redistributed without permission.
