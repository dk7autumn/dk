#!/bin/bash

# 自动化部署脚本
# 用法：./auto-deploy.sh

set -e

echo "=========================================="
echo "  开始自动化部署流程"
echo "=========================================="

# 配置变量
IMAGE_NAME="sale-system"
CONTAINER_NAME="sale-system"
DOCKER_PORT="8080:8080"

# 1. 检查是否有 Docker
echo "[1/6] 检查 Docker..."
if ! command -v docker &> /dev/null; then
    echo "错误：未找到 Docker，请先安装 Docker"
    exit 1
fi
echo "Docker 版本：$(docker --version)"

# 2. 停止并删除旧容器
echo "[2/6] 停止并删除旧容器..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true
echo "旧容器已清理"

# 3. 删除旧镜像
echo "[3/6] 删除旧镜像..."
docker rmi $IMAGE_NAME:latest 2>/dev/null || true
echo "旧镜像已删除"

# 4. 构建新镜像
echo "[4/6] 构建新 Docker 镜像..."
docker build -t $IMAGE_NAME:latest .

# 5. 启动新容器
echo "[5/6] 启动新容器..."
docker run -d \
  --name $CONTAINER_NAME \
  -p $DOCKER_PORT \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/sale_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai \
  --restart unless-stopped \
  $IMAGE_NAME:latest

# 6. 验证容器状态
echo "[6/6] 验证容器状态..."
sleep 2
docker ps --filter "name=$CONTAINER_NAME"

echo "=========================================="
echo "  部署完成！"
echo "  容器名称：$CONTAINER_NAME"
echo "  镜像名称：$IMAGE_NAME:latest"
echo "  访问地址：http://localhost:$DOCKER_PORT"
echo "=========================================="
