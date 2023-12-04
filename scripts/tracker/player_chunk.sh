#!/usr/bin/env bash

players=$(echo -n 'list' | timeout 0.1 nc -u localhost 2137 | tail -n +2 | sed -Ee "s/^- //g")
for player in $players; do
	coords=$(echo -n "coords $player" | timeout 0.1 nc -u localhost 2137)
	coords=$(echo $coords | awk '{print $3,$4,$5}' | sed -e "s/,//g")
	python3 chunk.py $coords | awk '{print $2, $6}' | sed -e "s/,//g" -e "s/}$//g"
done
