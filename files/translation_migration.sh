#!/usr/bin/env bash

MODULE=KAU

cd ..

current=${PWD##*/}

if [[ "$current" != "$MODULE" ]]; then
    echo "Not in $MODULE";
else
    # DANGEROUS! Removes all files matching regex
    grep -Lir "</string>" --include="strings*.xml" "." | tr '\n' '\0' | xargs -0 -n1 rm
    # Delete empty directories
    find . -type d -empty -delete
fi

echo "Finished cleaning files"