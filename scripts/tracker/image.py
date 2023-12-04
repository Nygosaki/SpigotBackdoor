from PIL import Image
import sys

# Read coordinates from stdin
coordinates = [line.strip().split() for line in sys.stdin.readlines()]

# Convert coordinates to integer values
coordinates = [(int(x), int(y)) for x, y in coordinates]

# Calculate image size based on the maximum and minimum coordinates
min_x = -32
max_x = 32
min_y = -32
max_y = 32

width = max_x - min_x + 1
height = max_y - min_y + 1

# Create an image with a white background
image = Image.new('RGB', (width, height), 'black')
pixels = image.load()

# Color the pixels corresponding to the coordinates
for x, y in coordinates:
    pixels[x - min_x, y - min_y] = (255, 255, 255)  # Black pixel

# Save the image
image.save(sys.argv[1])
print(f"Image saved as {sys.argv[1]}")

