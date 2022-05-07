QQBOT

本项目基于 [mirai](https://github.com/mamoe/mirai) [![Version](https://img.shields.io/badge/version-2.11.0--RC-red)](https://github.com/mamoe/mirai/releases/tag/v2.11.0-M2.2)
开发

[如何使用](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/ConfiguringProjects.md) ？

[如何配置](https://github.com/duan649953543/Qbot/blob/main/CONFIG.md) ？

[在群中使用](https://www.miraiqbot.xyz) ？

---

已有或正在开发的功能:

- [x] 今日运势查询

- [x] 今日新闻查询

- [x] 今日星座运势

- [x] 随机求签

- [x] 音乐分享（暂只支持网易云）

- [x] 发送语音（需要前往[百度开发者平台](https://ai.baidu.com/tech/speech)获取key）

- [x] 王者战力查询

- [x] 随机发送~~美女~~图片 需要手动将图片放到（/data/org.qbot.plugin/image）内

- [x] 群管理插件（关键字撤回、自动审核入群申请）[帮助文档](https://qbot.7733princess.top/#/?id=%e7%be%a4%e7%ae%a1%e7%90%86%e5%8a%9f%e8%83%bd)

Tips:有想要的新功能可以提交issue

---

更新日志：

2022年5月7日v2.0.2更新

1.mirai-core更新到[2.11.0-RC](https://github.com/mamoe/mirai/releases/tag/v2.11.0-RC)

2.更新摸鱼办

3.依赖更新到fastjson2

群管理可能还有bug，请自行测试

---
2022年4月20日v2.0.1更新

1.mirai-core更新到[2.11.0-M2.2](https://github.com/mamoe/mirai/releases/tag/v2.11.0-M2.2)

2.群管理插件开放([帮助文档](https://www.miraiqbot.xyz/#/?id=%e7%be%a4%e7%ae%a1%e7%90%86%e5%8a%9f%e8%83%bd))

3.[配置文件更新](https://github.com/duan649953543/Qbot/blob/main/CONFIG.md)

4.删除公告栏组件

2021年11月26日v1.1.3更新

1.新增公告栏

2.优化代码结构

下个版本将是大版本更新，将开放群管理插件，请耐心等待

2021年11月23日v1.1.2-1更新

1.mirai-core更新到2.9.0-M1

2.修复一些BUG

2021年11月17日v1.1.2更新

1.mirai-core更新到2.8.0

2.新增【摸鱼办】提醒放假时间

3.修改15点的饮茶小助手为摸鱼办

2021年11月1日v1.1.1更新

1.修复了自动获取运势有可能失败的问题

2021年9月23日v1.1.0更新

1.mirai-core更新到2.8.0-M1

2.修复获取英雄战力时英雄名为null的问题

3.新增获取战力时显示英雄头像

2021年9月22日v1.0.9更新

1.mirai-core更新到2.7.1

2.修复当image文件夹中只有一张图片时无法正常发送的问题

3.删除福利功能

4.优化代码结构，规范代码

注:本次更新代码改动较大，可能出现无法预知的bug，请谨慎升级

2021年9月17日v1.0.8更新

1.mirai-core更新到2.7.0

2.依赖更新

3.删除无用代码

2021年8月20日v1.0.7更新

1.mirai-core更新到2.7-RC

2.kotlin版本更新到1.5.0

3.新增维护模式（/onwh 开启维护，/closewh 关闭维护，维护模式下将不会处理消息）

2021年8月19日v1.0.6-1更新

1.修复了可能获取不到运势的问题

2021年8月18日v1.0.6更新

1.修复了运势线程可能会中断的情况

2021年7月28日v1.0.5更新

1.新增奥运查询功能

2.mirai-core更新到2.7-M2

3.删除一些无用代码

2021年7月1日v1.0.4更新

1.优化新闻获取逻辑，无需再手动更新

2.修复自动发送逻辑错误

2021年6月22日v1.0.3更新

1.新增查看羊毛福利

2.优化匹配逻辑

2021年6月21日v1.0.2更新

1.mirai-core更新到2.7-M1

2.优化语言和歌曲分享逻辑

2021年6月16日 v1.0.1更新

1.优化求签功能

2.燃鹅相关菜单删除

2021年6月11日 v1.0.0更新

正式版1.0.0上线