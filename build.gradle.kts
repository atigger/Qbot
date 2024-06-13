plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "org.qbot"
version = "3.5.4"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    //在IDE内运行的mcl添加滑块模块，请参考https://github.com/project-mirai/mirai-login-solver-selenium把版本更新为最新
    //runtimeOnly("net.mamoe:mirai-login-solver-selenium:1.0-dev-15")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.35")
    implementation("com.baidu.aip:java-sdk:4.16.16")
    implementation("com.google.zxing:javase:3.5.1")
    implementation("org.yaml:snakeyaml:2.0")

}

mirai {
    jvmTarget = JavaVersion.VERSION_17
}
