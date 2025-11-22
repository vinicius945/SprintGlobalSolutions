#!/bin/bash
# Script de Infraestrutura Completo (Idempotente) - Global Solution 2025
# Criação de Banco de Dados (SQL) e Web App Java (PaaS)
# Seguro para rodar múltiplas vezes: se o recurso já existe, ele apenas atualiza/ignora.

# --- 1. Variáveis de Configuração ---
RG="rg-gsfinal"
LOCATION="eastus2"                 # Região do seu SQL existente
SQL_SERVER="sqlserver-gsfinal-945" # Seu servidor existente
SQL_DB="sqldb-gsfinal"             # Seu banco existente
SQL_ADMIN="adminGSFINAL"           # Seu usuário
SQL_PASS="GsFinal@2025"            # Sua senha (Cuidado: em prod real usaríamos KeyVault)

PLAN_NAME="plan-gs-java-paas"
APP_NAME="webapp-ltakn-2025-vini"  # Nome do seu Web App
RUNTIME="JAVA:17-java17"

# Cores para logs
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${GREEN}>>> [1/6] Verificando Grupo de Recursos ($RG)...${NC}"
az group create --name $RG --location $LOCATION --output none

echo -e "${GREEN}>>> [2/6] Verificando Servidor SQL ($SQL_SERVER)...${NC}"
az sql server create \
    --name $SQL_SERVER \
    --resource-group $RG \
    --location $LOCATION \
    --admin-user $SQL_ADMIN \
    --admin-password $SQL_PASS \
    --output none

echo -e "${GREEN}>>> [3/6] Configurando Firewall do SQL (Permitir Azure)...${NC}"
# Regra essencial para o Web App conseguir falar com o Banco
az sql server firewall-rule create \
    --resource-group $RG \
    --server $SQL_SERVER \
    --name AllowAzureServices \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0 \
    --output none 2>/dev/null || true

echo -e "${GREEN}>>> [4/6] Verificando Banco de Dados ($SQL_DB)...${NC}"
az sql db create \
    --resource-group $RG \
    --server $SQL_SERVER \
    --name $SQL_DB \
    --service-objective S0 \
    --output none

echo -e "${GREEN}>>> [5/6] Verificando App Service Plan e Web App...${NC}"
az appservice plan create --name $PLAN_NAME --resource-group $RG --sku B1 --is-linux --location $LOCATION --output none
az webapp create --name $APP_NAME --resource-group $RG --plan $PLAN_NAME --runtime "$RUNTIME" --output none

echo -e "${GREEN}>>> [6/6] Conectando Web App ao Banco e RabbitMQ...${NC}"
# Monta a Connection String dinamicamente
CONN_STR="jdbc:sqlserver://$SQL_SERVER.database.windows.net:1433;databaseName=$SQL_DB;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

az webapp config appsettings set --resource-group $RG --name $APP_NAME --settings \
  SPRING_DATASOURCE_URL="$CONN_STR" \
  SPRING_DATASOURCE_USERNAME="$SQL_ADMIN" \
  SPRING_DATASOURCE_PASSWORD="$SQL_PASS" \
  SPRING_RABBITMQ_HOST="fly-01.rmq.cloudamqp.com" \
  SPRING_RABBITMQ_USERNAME="ciadsexh" \
  SPRING_RABBITMQ_PASSWORD="nebfh7Q6NYX7hlAnzDcf_zcv1ZKEhcO4" \
  SPRING_RABBITMQ_VIRTUAL_HOST="ciadsexh" \
  SPRING_RABBITMQ_SSL_ENABLED="true" \
  GROQ_API_KEY="dummy_key" \
  GROQ_API_URL="https://api.groq.com/openai/v1" \
  ai.python.service.url="http://localhost/api/mock" \
  WEBSITES_PORT="8080" \
  --output none

echo -e "${GREEN}>>> SUCESSO! Infraestrutura pronta e segura. <<<${NC}"