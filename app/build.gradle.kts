plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

    //id("kotlin-android-extensions")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.genesiseternity.incomemate"
    compileSdk = 33

    //dataBinding {
    //    enabled = true
    //}

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.genesiseternity.incomemate"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            //minifyEnabled = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

apply(plugin = "kotlin-kapt")
kapt {
    generateStubs = true

    //arguments {
    //    arg("room.schemaLocation", "$projectDir/schemas")
    //}
}


dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // JUnit
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    //implementation "androidx.navigation:navigation-fragment:2.4.2"
    //implementation "androidx.navigation:navigation-ui:2.4.2"

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:31.0.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // ViewModel LiveData
    val lifecycle_version = "2.6.0-alpha03"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // RxAndroid & RxJava
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxjava:3.1.5")

    implementation("com.jakewharton.rxbinding4:rxbinding:4.0.0")

    // Dagger2
    val dagger_version = "2.44"
    //implementation("com.google.dagger:dagger:$dagger_version")
    //kapt("com.google.dagger:dagger-compiler:$dagger_version")
    implementation("com.google.dagger:dagger:$dagger_version")
    implementation("com.google.dagger:dagger-android-support:$dagger_version")
    kapt("com.google.dagger:dagger-android-processor:$dagger_version")
    kapt("com.google.dagger:dagger-compiler:$dagger_version")


    //implementation("com.google.dagger:dagger:$dagger_version")
    //implementation("com.google.dagger:dagger-android-support:$dagger_version")
    //annotationProcessor("com.google.dagger:dagger-android-processor:$dagger_version")
    //annotationProcessor("com.google.dagger:dagger-compiler:$dagger_version")

    // Room
    val room_version = "2.4.3"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    //annotationProcessor("androidx.room:room-compiler:$room_version")
    testImplementation("android.arch.persistence.room:testing:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")

    // REST API
    val retrofit_version = "2.9.0"
    val okhttp_version = "4.10.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-simplexml:$retrofit_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")

    // LeakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")


}