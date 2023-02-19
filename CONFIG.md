配置文件模板
```yaml
#配置文件版本
Version: 3.2
#机器人QQ
QQ: 0
#百度语音API
BaiDuAPI:
  APP_ID: ""
  API_KEY: ""
  SECRET_KEY: ""
#发送图片数量
ImageNum: 0
#图片自动撤回时间（0为不撤回,单位秒）
ImageRecall: 0
#自动同意好友请求
AgreeFriend: false
#自动同意邀请入群请求
AgreeGroup: false
#智能聊天开关
AI:
  Open: false
  Api_Key: ""
  Api_Secret: ""
#自动化操作
Auto:
  #自动发送运势信息
  AutoFortune: false
  #自动每日新闻信息
  AutoNews: false
  #自动发送摸鱼小提示
  AutoTips: false
  #需要自动发送的群列表 用,隔开
  Group: []
GroupManagement:
  #开启群管系统
  Open: false
  #群管系统管理员QQ
  AdminQQ: 0
#网易云API接口网址
MusicAPIURL: ""
```

