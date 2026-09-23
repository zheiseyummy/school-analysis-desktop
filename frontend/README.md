> 此文件以下为原模板说明，仅作来源参考；其中在线演示、权限系统与服务器部署步骤不代表当前单机改造工程。请先阅读上级目录的 `README.md` 和 `outputs/单机桌面版改造-阶段进度.md`。

<p align="center">
    <img src="https://img.shields.io/badge/Vue-3.4.19-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/Vite-5.1.3-green.svg"/>
    <img src="https://img.shields.io/badge/Element Plus-2.5.6-blue.svg"/>
    <img src="https://img.shields.io/badge/license-MIT-green.svg"/>
    <a href="https://gitee.com/youlaiorg" target="_blank">
        <img src="https://img.shields.io/badge/Author-有来开源组织-orange.svg"/>
    </a>
</p>


[vue3-element-admin](https://gitee.com/youlaiorg/vue3-element-admin) 是基于 Vue3 + Vite5+ TypeScript5 + Element-Plus + Pinia 等最新主流技术栈构建的后台管理前端模板（配套[后端源码](https://gitee.com/youlaiorg/youlai-boot)）。

![](https://foruda.gitee.com/images/1708618984641188532/a7cca095_716974.png "rainbow.png")

<p align="center">
 <a target="_blank" href="http://vue3.youlai.tech">👀 在线预览</a> |  <a target="_blank" href="https://juejin.cn/post/7228990409909108793">📖 阅读文档</a>  
</p>

## 项目特性


- 基于 vue-element-admin 升级到 vue3 版本，无自定义封装，易上手，减少学习成本。
- 提供了配套的 Java 后端接口，真实的接口数据，而非使用 Mock 数据。您可以访问在[线接口文档](https://www.apifox.cn/apidoc/shared-195e783f-4d85-4235-a038-eec696de4ea5)查看接口详情。
- 权限系统功能齐全，包括用户管理、角色管理、菜单管理、字典管理和部门管理等，以满足您对权限管理的需求。
- 项目还提供了基础设施支持，包括动态路由、按钮级别的权限控制、国际化支持、代码规范、Git 提交规范以及常用组件的封装，以便开发人员更高效地开发和维护项目。

## 项目预览

- **在线预览**： [https://vue3.youlai.tech/](https://vue3.youlai.tech/)

- **控制台**

  ![暗黑模式](https://foruda.gitee.com/images/1687755822903300961/a4d63e22_716974.png)

- **接口文档**

  ![接口文档](https://foruda.gitee.com/images/1687755822857820115/96054330_716974.png)
 


- **权限管理系统**

  |![在这里插入图片描述](https://foruda.gitee.com/images/1687755822816437081/b7620905_716974.png) | ![角色管理](https://foruda.gitee.com/images/1687755822852085747/c13a4d19_716974.png) |
  | ------------------------------------------------------ | ------------------------------------------------------ |
  | ![菜单管理](https://foruda.gitee.com/images/1687755822966247550/4d4f8118_716974.png) | ![在这里插入图片描述](https://foruda.gitee.com/images/1687755822828758939/8035a91f_716974.png)

## 项目地址

| 项目 | Gitee                                                        | Github                                                       | GitCode                                                      |
| ---- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 前端 | [vue3-element-admin](https://gitee.com/youlaiorg/vue3-element-admin) | [vue3-element-admin](https://github.com/youlaitech/vue3-element-admin) | [vue3-element-admin](https://gitcode.net/youlai/vue3-element-admin) |
| 后端 | [youlai-boot](https://gitee.com/youlaiorg/youlai-boot)       | [youlai-boot](https://github.com/haoxianrui/youlai-boot.git) | [youlai-boot](https://gitcode.net/youlai/youlai-boot)        |

## 环境准备

| 环境                 | 名称版本                                                     | 备注                                                         |
| -------------------- | :----------------------------------------------------------- | ------------------------------------------------------------ |
| **开发工具**         | VSCode                                                       | [下载地址](https://code.visualstudio.com/Download)           |
| **运行环境**         | Node 18+                                                     | [下载地址](http://nodejs.cn/download)                        |
| **VSCode插件(必装)** | 1. `Vue Language Features (Volar) ` <br/> 2. `TypeScript Vue Plugin (Volar) `  <br/>3. 禁用 Vetur | ![vscode-plugin](https://foruda.gitee.com/images/1687755823108948048/d0198b2d_716974.png) |


## 项目启动

```bash
# 克隆代码
git clone https://gitee.com/youlaiorg/vue3-element-admin.git

# 切换目录
cd vue3-element-admin

# 安装 pnpm
npm install pnpm -g

# 安装依赖
pnpm install

# 启动运行
pnpm run dev
```

## 开启Mock

项目同时支持在线和 Mock 接口，默认使用线上接口，如需替换为 Mock 接口，只需在 `vite.config.ts` 文件的 `plugins` 配置中取消对 `mockDevServerPlugin()` 的注释**即可**。

## 项目部署

```bash
# 项目打包
pnpm run build:prod

# 上传文件至远程服务器
将打包生成在 `dist` 目录下的文件拷贝至 `/usr/share/nginx/html` 目录

# nginx.cofig 配置
server {
	listen     80;
	server_name  localhost;
	location / {
			root /usr/share/nginx/html;
			index index.html index.htm;
	}
	# 反向代理配置
	location /prod-api/ {
			proxy_pass http://vapi.youlai.tech/; # vapi.youlai.tech替换成你的后端API地址
	}
}
```

## 注意事项

- **自动导入插件自动生成默认关闭**

  模板项目的组件类型声明已自动生成。如果添加和使用新的组件，请按照图示方法开启自动生成。在自动生成完成后，记得将其设置为 `false`，避免重复执行引发冲突。

  ![](https://foruda.gitee.com/images/1687755823137387608/412ea803_716974.png)

- **项目启动浏览器访问空白**

  请升级浏览器尝试，低版本浏览器内核可能不支持某些新的 JavaScript 语法，比如可选链操作符 `?.`。

- **项目同步仓库更新升级**

  项目同步仓库更新升级之后，建议 `pnpm install` 安装更新依赖之后启动 。

- **项目组件、函数和引用爆红**

	重启 VSCode 尝试

- **其他问题**

  如果有其他问题或者建议，建议 [ISSUE](https://gitee.com/youlaiorg/vue3-element-admin/issues/new)

## 后端接口

- **后端接口地址**：[https://vapi.youlai.tech](https://vapi.youlai.tech)

- **接口文档地址**：[在线接口文档](https://www.apifox.cn/apidoc/shared-195e783f-4d85-4235-a038-eec696de4ea5)

- **OpenAPI地址**：[http://vapi.youlai.tech/v3/api-docs](http://vapi.youlai.tech/v3/api-docs)

- **本地接口环境搭建**：
   - 首先，获取基于 `Java` 和 `SpringBoot` 开发的后端 [youlai-boot](https://gitee.com/youlaiorg/youlai-boot.git) 源码。
   - 其次，根据后端工程的说明文档 [README.md](https://gitee.com/youlaiorg/youlai-boot#%E9%A1%B9%E7%9B%AE%E8%BF%90%E8%A1%8C) 完成本地启动。
   - 最后，修改 `vite.config.ts` 文件中的 `server.proxy` 配置项，将 target 的值从 `http://vapi.youlai.tech` 更改为 `http://localhost:8989`。




## 项目文档

- [基于 Vue3 + Vite4 + TypeScript + Element-Plus 从0到1搭建后台管理系统](https://blog.csdn.net/u013737132/article/details/130191394)

- [ESLint+Prettier+Stylelint+EditorConfig 约束和统一前端代码规范](https://blog.csdn.net/u013737132/article/details/130190788)
- [Husky + Lint-staged + Commitlint + Commitizen + cz-git 配置 Git 提交规范](https://blog.csdn.net/u013737132/article/details/130191363)




## 提交规范

执行 `pnpm run commit` 唤起 git commit 交互，根据提示完成信息的输入和选择。

![](https://foruda.gitee.com/images/1687755823165218215/c1705416_716974.png)



## 交流群🚀

> **关注「有来技术」公众号，获取交流群二维码。**
>
> 如果交流群的二维码过期，请加微信(haoxianrui)并备注「前端」、「后端」或「全栈」以获取最新二维码。
>
> 为确保交流群质量，防止营销广告人群混入，我们采取了此措施。望各位理解！

| 公众号 | 交流群 |
|:----:|:----:|
| ![有来技术公众号二维码](https://foruda.gitee.com/images/1687689212187063809/3c69eaee_716974.png) | ![交流群二维码](https://foruda.gitee.com/images/1687689212139273561/6a65ef69_716974.png) |
