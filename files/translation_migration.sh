#!/usr/bin/env bash

BASE_MODULE=kpref-activity
FOLDER=values-es-rES
OUTPUT=values-es

cd ..

current=${PWD##*/}

if [ "$current" != "KAU" ]; then
    echo "Not in KAU";
    return;
fi

mkdir -p about/src/main/res/${OUTPUT}
mv ${BASE_MODULE}/src/main/res/${FOLDER}/strings_about.xml about/src/main/res/${OUTPUT}/strings_about.xml

mkdir -p colorpicker/src/main/res/${OUTPUT}
mv ${BASE_MODULE}/src/main/res/${FOLDER}/strings_colorpicker.xml colorpicker/src/main/res/${OUTPUT}/strings_colorpicker.xml

mkdir -p core/src/main/res-public/${OUTPUT}
mv ${BASE_MODULE}/src/main/res/${FOLDER}/strings_commons.xml core/src/main/res-public/${OUTPUT}/strings_commons.xml

mkdir -p mediapicker/src/main/res/${OUTPUT}
mv ${BASE_MODULE}/src/main/res/${FOLDER}/strings_mediapicker.xml mediapicker/src/main/res/${OUTPUT}/strings_mediapicker.xml