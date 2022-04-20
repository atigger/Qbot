plugins {
    val kotlinVersion = "1.6.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.0-M2.2"
}

group = "org.qbot"
version = "2.0.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    //在IDE内运行的mcl添加滑块模块，请参考https://github.com/project-mirai/mirai-login-solver-selenium把版本更新为最新
    //runtimeOnly("net.mamoe:mirai-login-solver-selenium:1.0-dev-15")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.alibaba:fastjson:1.2.79")
    implementation("com.baidu.aip:java-sdk:4.16.5")
    implementation("com.google.zxing:javase:3.4.1")
    implementation("org.yaml:snakeyaml:1.30")

}