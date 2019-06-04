include(
    ":core",
    ":core-ui",

    ":gradle-plugin",

    ":about",
    ":adapter",
    ":colorpicker",
    ":mediapicker",
    ":kpref-activity",
    ":searchview"
)

if (System.getenv("JITPACK") == null) {
    include(":sample")
}

project(":gradle-plugin").projectDir = file("buildSrc")