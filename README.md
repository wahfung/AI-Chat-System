# AI Chat System - 智能对话系统

基于 Spring Boot + Vue.js + MySQL + Ollama/DeepSeek 的实时流式对话系统。

## 🛠 技术栈

- **Frontend**: Vue.js 3 + Vite + Tailwind CSS + Pinia
- **Backend**: Spring Boot 3.2 + Spring Data JPA + WebFlux
- **Database**: MySQL 8.0
- **AI Model**: Ollama + DeepSeek-R1

## 🚀 启动指南 (How to Run)

### 前置要求

1. 确保 Docker Desktop 已安装并启动
2. 如果需要 GPU 加速，请确保已安装 NVIDIA Container Toolkit

### 启动步骤

```bash
# 1. 进入项目目录
cd ai-chat-system

# 2. 启动所有服务
docker compose up -d --build

# 3. 等待服务启动完成（首次启动需要下载镜像和构建，约需 5-10 分钟）

# 4. 下载 DeepSeek 模型（首次运行需要）
docker exec -it works-529-ollama-1 ollama pull deepseek-r1:1.5b
```

### 验证服务

```bash
# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f
```

## 🔗 服务地址 (Services)

| 服务         | 地址                             | 说明        |
| ------------ | -------------------------------- | ----------- |
| Frontend     | http://localhost:3000            | 前端界面    |
| Backend API  | http://localhost:8080            | 后端接口    |
| Health Check | http://localhost:8080/api/health | 健康检查    |
| MySQL        | localhost:3306                   | 数据库      |
| Ollama       | http://localhost:11434           | AI 模型服务 |

## 🧪 测试账号

| 用户名 | 密码   | 角色     |
| ------ | ------ | -------- |
| admin  | 123456 | 管理员   |
| demo   | 123456 | 演示用户 |

## 📋 功能特性

- ✅ 实时流式对话：支持 SSE 流式响应，逐字显示 AI 回复
- ✅ 对话管理：创建、切换、删除对话
- ✅ 历史记录：自动保存对话历史到 MySQL
- ✅ 上下文理解：支持多轮对话上下文
- ✅ 响应式设计：支持 PC 端和移动端
- ✅ Markdown 渲染：支持代码高亮和格式化

## 🗄 数据库结构

```sql
-- 用户表
users (id, username, password, nickname, avatar, created_at)

-- 对话表
conversations (id, title, user_id, created_at, updated_at)

-- 消息表
chat_messages (id, conversation_id, role, content, created_at)
```

## 🐳 Docker 镜像源配置

项目已配置以下镜像源加速：

- **Maven**: 阿里云镜像 (maven.aliyun.com)
- **npm**: 淘宝镜像 (registry.npmmirror.com)
- **Docker**: 官方 Docker Hub 镜像

## 📁 项目结构

```
529/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/      # Java 源码
│   ├── src/main/resources/ # 配置文件
│   ├── Dockerfile          # 后端镜像构建
│   └── pom.xml             # Maven 依赖
├── frontend/               # Vue.js 前端
│   ├── src/               # 前端源码
│   ├── Dockerfile         # 前端镜像构建
│   └── nginx.conf         # Nginx 配置
├── docker-compose.yml     # 容器编排
├── init.sql               # 数据库初始化
└── README.md              # 项目文档
```

## ⚠️ 注意事项

1. **GPU 支持**: 如果没有 NVIDIA GPU，请在 `docker-compose.yml` 中移除 ollama 服务的 `deploy.resources` 配置
2. **模型下载**: 首次运行需要手动拉取模型，命令：`docker exec -it ai-chat-ollama ollama pull deepseek-r1:1.5b`
3. **端口冲突**: 确保 3000、8080、3306、11434 端口未被占用

## 🔧 开发模式

如需本地开发调试：

```bash
# 仅启动数据库和 Ollama
docker compose up -d db ollama

# 后端开发
cd backend && mvn spring-boot:run

# 前端开发
cd frontend && npm install && npm run dev
```
