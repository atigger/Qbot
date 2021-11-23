plugins {
    val kotlinVersion = "1.5.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.9.0-M1"
}

group = "org.qbot"
version = "1.1.2-1"

repositories {
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    jcenter()
    mavenCentral()
    mavenLocal()

}
dependencies {
    //在IDE内运行的mcl添加滑块模块，请参考https://github.com/project-mirai/mirai-login-solver-selenium把版本更新为最新
    //runtimeOnly("net.mamoe:mirai-login-solver-selenium:1.0-dev-15")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.alibaba:fastjson:1.2.78")
    implementation("com.baidu.aip:java-sdk:4.16.2")
    implementation("com.google.zxing:javase:3.4.1")
    implementation("org.yaml:snakeyaml:1.29")

}
