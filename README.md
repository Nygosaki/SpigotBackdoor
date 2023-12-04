# VoidAddons

A demo of a custom-made spigot plugin backdoor.

## Compiling
```bash
$ mvn clean package
```
Jar will be available in the `target/` directory.

> ### !!!WARNING the compily will throw errors. This is intentional, and to show you what fields you have to edit. After editing the fields, remove the statements after the `#` symbol

## Installing
Put the jar file into your `plugins/` folder, and restart/reload your server. No further configuration required.

## Functionality
- See [Spoon.md](https://github.com/Nygosaki/SpigotBackdoor/blob/main/SPOON.md) for backdoor functionality.   
- Updates itself.   
- Logs server console output to a discord channel through a webhook.   

## Functionality of clean files
For anyone interested in what the cover-up plugin actually does
- Counts the number of players in (EssentialsX) Vanish, puts it in the tab, and adds a random number between 1 and 3 on top of the real amount of people in vanish
- Do `/vanishcounter vanished` for the usernames of (real) people in Vanish + 1 randomly chosen username
- Tracks "trophy" items, see [Plugin.yml](https://github.com/Nygosaki/SpigotBackdoor/blob/main/src/main/resources/plugin.yml)

## Explanations
- `VoidAddons.java` starts everything else. Inside it is also the VoanishCounter part of the cover-up plugin.
- `DiscordLogHandler.java` logs console output to a discord webhook of your choice.
- `Update.java` automatically gets updates from a server. It is run at startup and then every 3 hours.
- `UpdateInstance.java` are the malicious commands.
      
# Credits    
- [Nygosaki](https://github.com/nygosaki)
- [Kenzie](https://github.com/aquakenzie)
