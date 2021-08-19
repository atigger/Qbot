plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.7-M2"
}

group = "org.qbot"
version = "1.0.6-1"

repositories {
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    jcenter()
    mavenCentral()
    mavenLocal()

}
dependencies {
    //在IDE内运行的mcl添加滑块模块，请参考https://github.com/project-mirai/mirai-login-solver-selenium把版本更新为最新
    //runtimeOnly("net.mamoe:mirai-login-solver-selenium:1.0-dev-15")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.squareup.okhttp3:okhttp:3.14.7")
    implementation("com.alibaba:fastjson:1.1.25")
    implementation("com.baidu.aip:java-sdk:4.15.8")
    implementation("com.google.zxing:javase:3.3.0")
    implementation("org.yaml:snakeyaml:1.23")

}
