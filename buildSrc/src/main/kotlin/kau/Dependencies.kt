package kau

/**
 * Some common dependencies, backed by the supplied versions
 */
object Dependencies {
    @JvmStatic
    fun kotlin(type: String) = "org.jetbrains.kotlin:kotlin-$type:${Versions.kotlin}"

    @JvmField
    val kotlin = kotlin("stdlib")
    @JvmField
    val kotlinTest = kotlin("test-junit")
    @JvmField
    val kotlinReflect = kotlin("reflect")

    const val junit = "junit:junit:${Versions.junit}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
    const val cardView = "androidx.cardview:cardview:${Versions.cardView}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val googleMaterial = "com.google.android.material:material:${Versions.googleMaterial}"

    const val iconics = "com.mikepenz:iconics-core:${Versions.iconics}"
    const val iconicsGoogle = "com.mikepenz:google-material-typeface:${Versions.iconicsGoogle}.original-kotlin@aar"
    const val iconicsMaterial = "com.mikepenz:material-design-iconic-typeface:${Versions.iconicsMaterial}-kotlin@aar"
    const val iconicsCommunity = "com.mikepenz:community-material-typeface:${Versions.iconicsCommunity}-kotlin@aar"

    const val aboutLibraries = "com.mikepenz:aboutlibraries:${Versions.aboutLibraries}"

    const val blurry = "jp.wasabeef:blurry:${Versions.blurry}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideKapt = "com.github.bumptech.glide:compiler:${Versions.glide}"

    @JvmStatic
    fun materialDialog(type: String) = "com.afollestad.material-dialogs:$type:${Versions.materialDialog}"

    @JvmField
    val materialDialog = materialDialog("core")

    const val fastAdapter = "com.mikepenz:fastadapter:${Versions.fastAdapter}"
    @JvmStatic
    fun fastAdapter(type: String) = "com.mikepenz:fastadapter-$type:${Versions.fastAdapter}"
    @JvmField
    val fastAdapterCommons = fastAdapter("commons")

    const val bugsnag = "com.bugsnag:bugsnag-android:${Versions.bugsnag}"

    @JvmStatic
    fun espresso(type: String) = "androidx.test.espresso:espresso-$type:${Versions.espresso}"

    @JvmField
    val espresso = espresso("core")

    const val testRunner = "androidx.test.ext:junit:${Versions.testRunner}"
    const val testRules = "androidx.test:rules:${Versions.testRules}"
}