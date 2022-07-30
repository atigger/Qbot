plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.12.0"
}

group = "org.qbot"
version = "2.0.7"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    //在IDE内运行的mcl添加滑块模块，请参考https://github.com/project-mirai/mirai-login-solver-selenium把版本更新为最新
    //runtimeOnly("net.mamoe:mirai-login-solver-selenium:1.0-dev-15")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.7")
    implementation("com.baidu.aip:java-sdk:4.16.8")
    implementation("com.google.zxing:javase:3.5.0")
    implementation("org.yaml:snakeyaml:1.30")

}