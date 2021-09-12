#!/usr/bin/env bash

# Add appropriate files for encryption
# https://docs.github.com/en/actions/reference/encrypted-secrets#limits-for-secrets

rm kau_github.tar.gpg
cd ..
tar cvf kau_github.tar files/gplay-keys.json files/kau.keystore files/kau.properties
gpg --symmetric --cipher-algo AES256 kau_github.tar
rm kau_github.tar
mv kau_github.tar.gpg files/