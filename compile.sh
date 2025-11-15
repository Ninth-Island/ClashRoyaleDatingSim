#!/bin/bash
cd "$(dirname "$0")"

echo "Compiling with JLayer library..."
javac -cp "lib/jlayer-1.0.1.jar" -d out/production/ClashRoyaleDatingSim src/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Copying resources and images..."
    cp -r src/resources out/production/ClashRoyaleDatingSim/
    cp -r src/images out/production/ClashRoyaleDatingSim/
    echo "Done! Run with: ./run.sh"
else
    echo "Compilation failed!"
    exit 1
fi
