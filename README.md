# enchantingAngel

A Minecraft 1.21.11 Paper plugin that adds **book-based custom enchantments** to your server. Players can obtain custom enchant books, apply them with anvils, and use enchant effects powered by Persistent Data Container (PDC) tags and lore-based display.[web:219][web:223]

---

## Features

- Book-based custom enchant system
- Custom enchant books with lore and stored metadata
- Enchants applied through anvils
- PDC-backed enchant storage for reliable detection
- Combat-triggered and kill-triggered enchant effects
- Configurable enchant settings through `config.yml`

---

## Current Enchants

| Enchant | Type | Description |
|---|---|---|
| `MobAura CE` | Combat | Damages nearby mobs around the main target when attacking |
| `Experience CE` | Kill | Increases dropped experience from killed mobs |

---

## Commands

| Command | Description |
|---|---|
| `/ce givebook <player> <enchant> <level>` | Gives a custom enchant book to a player |

---

## How It Works

This plugin uses a **custom enchant book** flow instead of trying to register vanilla-style server enchants directly.[web:219][web:223]

1. A custom enchant book is created with stored enchant ID and level data.
2. The player applies the book to a supported item using an anvil.
3. The enchant is saved onto the item using PDC keys and lore.
4. Listeners detect the enchant later during combat or mob kills and trigger the matching effect.[web:219]

---

## Current Behavior

### MobAura CE
- Triggers when the player attacks a valid living target.
- Applies bonus area damage around the main target.
- Uses protected event handling to avoid recursive damage loops.

### Experience CE
- Triggers when a player kills a mob with an enchanted item.
- Increases the exp dropped by the mob.

---

## Configuration

`config.yml` is generated on first run. Example structure:

```yaml
custom-enchants:
  mobaura:
    enabled: true
    max-level: 3
    weight: 10
    min-enchanting-level: 15
    radius-per-level: 2.5
    damage-per-level: 1.5

  experience:
    enabled: true
    max-level: 3
    weight: 10
    min-enchanting-level: 10
    bonus-multiplier-per-level: 0.5

custom-book-system:
  add-book-directly-to-inventory: true
```

You can expand this file to control enchant power, level caps, and availability.

---

## Supported Items

Current enchants are limited to the material lists defined in each enchant class.

Example supported weapon types for `MobAura CE` include:
- Wooden Sword
- Stone Sword
- Golden Sword
- Iron Sword
- Diamond Sword
- Netherite Sword
- Iron Axe
- Diamond Axe
- Netherite Axe

---

## Installation

1. Build the plugin jar with Gradle.
2. Put the compiled `.jar` into your server's `plugins/` folder.
3. Start the Paper server.
4. Edit `plugins/enchantingAngel/config.yml` if needed.
5. Restart or reload the server after config changes.

---

## Building

Requires:
- Java 21
- Gradle
- Paper 1.21.11 development setup

Build with:

```bash
./gradlew build
```

Compiled output will be placed in:

```bash
build/libs/
```

---

## Project Structure

```text
src/main/java/me/angelique/enchantingAngel/
├── command/
├── config/
├── enchant/
├── listener/
├── registry/
├── service/
└── EnchantingAngel.java
```

---

## Notes

- This plugin uses PDC and lore for enchant identification rather than full vanilla enchant registration, which is a common and safer pattern for custom enchant systems on Paper.[web:219]
- Book application and enchant detection are handled entirely in plugin logic.
- If you add new enchants, register them in the registry and implement their trigger behavior in the relevant listener flow.

---

## Dependencies

- [Paper API 1.21.11](https://docs.papermc.io/) [web:223]
- Java 21
- Gradle

---

## License

Add your preferred license here.