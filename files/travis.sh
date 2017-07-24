#!/usr/bin/env bash

# Add appropriate files for encryption

rm kau.tar.enc
cd ..
tar cvf kau.tar files/gplay-keys.json files/kau.keystore files/kau.properties
travis encrypt-file kau.tar --add
rm kau.tar
mv kau.tar.enc files/