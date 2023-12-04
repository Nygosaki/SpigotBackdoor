#!/usr/bin/env bash

trap "export x=1" SIGINT

export i=0
while [[ $x -eq 0 ]]; do
	bash player_chunk.sh | python image.py frame_$i.png
	export i=$(expr $i + 1)
	sleep 0.5
done

ffmpeg -framerate 2 -i frame_%d.png -c:v libx264 -r 30 vis.mp4
rm frame_*.png
