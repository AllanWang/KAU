#!/usr/bin/env bash

# Add appropriate files for encryption
# https://docs.github.com/en/actions/reference/encrypted-secrets#limits-for-secrets

rm kau_github.tar.gpg
tar cvf kau_github.tar gplay-keys.json kau.keystore kau.properties
gpg --symmetric --cipher-algo AES256 kau_github.tar
rm kau_github.tar