#!/bin/bash
cd "$(dirname "$0")"

echo "Starting Clash Royale Dating Sim..."
java -cp "out/production/ClashRoyaleDatingSim:lib/jlayer-1.0.1.jar" ClashRoyaleDating
