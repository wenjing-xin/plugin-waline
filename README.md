# plugin-waline

为 halo2.x 集成 waline 评论系统

##  1、部署 waline
推荐 docker-compose 方式部署，其他方式参考文档：https://waline.js.org/guide/get-started 

```docker
# docker-compose.yml 文件内容
version: '3'

services:
  waline:
    container_name: waline
    image: docker.nastool.de/lizheming/waline:latest
    restart: always
    ports:
      - 8360:8360
    volumes:
      - ${PWD}/data:/app/data
    environment:
      TZ: 'Asia/Shanghai'
      SQLITE_PATH: '/app/data'
      JWT_TOKEN: 'e-JKKKJIb3368EFD544316006'
      SITE_NAME: 'dreamChaser的小屋'
      SITE_URL: 'https://blog.wenjng.xin'
      SECURE_DOMAINS: 'blog.wenjng.xin'
      AUTHOR_EMAIL: 'my-emial@outlook.com'
```
> 该方式使用的数据库是 sqlite，其他数据库配置请参考文档 https://waline.js.org/guide/database.html

> 使用 sqlite 时，请在对应的数据库文件目录下替换 waline.sqlite 文件，文件[下载地址](https://github.com/walinejs/waline/blob/main/assets/waline.sqlite)

## 2、使用说明及其注意事项

Halo-V2.17.0及其以上版本使用该插件的时候，请勾选后台中的扩展配置的评论组件部分配置，如下图所示，如果勾选了此选项不生效，请关闭其他评论组件！
![](https://camo.githubusercontent.com/d7f69a8cb5cb334d8e743fd18372abb9983ce3828f104295bfa2accea2bc7b51/68747470733a2f2f646f6765636c6f75642e77656e6a696e672e78696e2f696d6167652f617274616c6b2d706c7567696e2d657874656e73696f6e2e706e67)

Halo-V2.7.0一下版本使用该插件前确保关闭其他评论插件！！！
因为该插件是通过扩展官方提供的 CommentWidget 接口进行实现的，理论上是不能同时存在多个，请知悉！

插件配置注意事项：
如果部署的 waline 是最新的V3版本，静态资源不需要自行引入，其他版本需要自行引入！
其他配置选项根据描述配置即可，参考文档：https://waline.js.org

### 3、赞助我
如果你感觉这个插件还不错，请我喝杯咖啡☕️☕️☕️
<div>
&emsp;&emsp;<img src="https://api.minio.yyds.pink/bbs/2024-06-05/1717550152-502697-wxpay.png" width=150px />
</div>

### 4、问题反馈

- [提issue](https://github.com/wenjing-xin/plugin-waline/issues)
- [个人站点](https://blog.wenjing.xin)
- [反馈论坛](https://support.qq.com/product/651063)

先在群内提问，若问题没有得到解决，则在 GitHub提交提 issues
QQ交流群与QQ频道，加群后管理员自动审核
<div>
&emsp;&emsp;<img src="https://api.minio.yyds.pink/bbs/2024-06-04/1717467713-802505-qq.png" width=150px />
&emsp;&emsp;<img src="https://api.minio.yyds.pink/bbs/2024-06-04/1717467714-226493-qq.jpg" width=158px />
</div>

### 5、开发环境

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/introduction>


