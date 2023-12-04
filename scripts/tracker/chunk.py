import math
import sys

def to_chunk(player):
	chunk_coords = {}

	for axis in ['x', 'y', 'z']:
		chunk_coords[axis] = math.floor(player[axis] / 16)

	return chunk_coords

player = {'x': int(sys.argv[1]), 'y': int(sys.argv[2]), 'z': int(sys.argv[3])}
print(to_chunk(player))
