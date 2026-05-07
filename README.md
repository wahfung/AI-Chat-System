# AI Chat System - 智能对话系统

基于 Spring Boot + Vue.js + MySQL + Ollama/DeepSeek 的实时流式对话系统。

## 🛠 技术栈

- **Frontend**: Vue.js 3 + Vite + Tailwind CSS + Pinia
- **Backend**: Spring Boot 3.2 + Spring Data JPA + WebFlux
- **Database**: MySQL 8.0
- **AI Model**: Ollama + DeepSeek-R1

## 🚀 启动指南 (How to Run)

### 前置要求

1. 确保已安装 JDK 17 及以上版本
2. 确保已安装 Node.js (推荐 v20) 和 npm
3. 确保已安装并启动 MySQL 8.0
4. 确保已安装 Ollama

### 本地启动步骤

#### 1. 启动数据库与 AI 模型

请先创建数据库并导入初始数据：
```bash
mysql -u root -p < init.sql
```

启动 Ollama 并下载 DeepSeek 模型：
```bash
ollama run deepseek-r1:1.5b
```

#### 2. 启动后端 (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

#### 3. 启动前端 (Vue.js)

```bash
cd frontend
npm install
npm run dev
```

## 🔗 服务地址 (Services)

| 服务         | 地址                             | 说明        |
| ------------ | -------------------------------- | ----------- |
| Frontend     | http://localhost:80              | 前端界面    |
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

## 📁 项目结构

```
repo/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/      # Java 源码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml             # Maven 依赖
├── frontend/               # Vue.js 前端
│   ├── src/               # 前端源码
│   └── nginx.conf         # Nginx 配置
├── init.sql               # 数据库初始化
└── README.md              # 项目文档
```

## ⚠️ 注意事项

1. **模型下载**: 首次运行需要手动拉取模型，命令：`ollama pull deepseek-r1:1.5b`
2. **端口冲突**: 确保 80、8080、3306、11434 端口未被占用
