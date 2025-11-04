#!/bin/bash
# ============================================================================
# Script: sonar-dev.sh
# Descripción: Ejecuta análisis de SonarQube en entorno de DESARROLLO
# Uso: ./scripts/sonar-dev.sh
# Requisitos:
#   - Archivo .env en la raíz con SONAR_HOST_URL_DEV y SONAR_TOKEN_DEV
#   - Maven instalado
#   - Ejecutar desde la raíz del proyecto
# ============================================================================

set -e  # Detiene la ejecución si ocurre un error
set -u  # Trata las variables no definidas como error

# ============================================================================
# AUTO-CARGAR .env SI EXISTE (AUTOMÁTICO)
# ============================================================================
if [ -f ".env" ]; then
    echo "Cargando variables desde .env"

    # shellcheck disable=SC2046
    export $(grep -v '^#' .env | xargs)

    echo "SUCCESS: Variables cargadas"
else
    echo "ERROR: No se encontró .env en la raíz del proyecto"
    echo "Solución: Crea el archivo .env desde la plantilla .env.example"
    exit 1
fi

# ============================================================================
# VALIDAR VARIABLES REQUERIDAS
# ============================================================================
if [ -z "${SONAR_HOST_URL_DEV:-}" ]; then
    echo "ERROR: SONAR_HOST_URL_DEV no está definida en .env"
    exit 1
fi

if [ -z "${SONAR_TOKEN_DEV:-}" ]; then
    echo "ERROR: SONAR_TOKEN_DEV no está definida en .env"
    exit 1
fi

# ============================================================================
# CONFIGURACIÓN DEL ENTORNO
# ============================================================================
ENVIRONMENT="DEV"
PROJECT_KEY="spring-sonarqube"
SONAR_HOST_URL=${SONAR_HOST_URL_DEV}
SONAR_TOKEN=${SONAR_TOKEN_DEV}

# ============================================================================
# INFORMACIÓN DEL ANÁLISIS
# ============================================================================
echo ""
echo "============================================================"
echo "Ejecutando análisis SonarQube"
echo "============================================================"
echo "Entorno:          ${ENVIRONMENT}"
echo "Servidor:         ${SONAR_HOST_URL}"
echo "Proyecto:         ${PROJECT_KEY}"
echo "Token:            Configurado OK"
echo "============================================================"
echo ""

# ============================================================================
# EJECUCIÓN DEL ANÁLISIS
# ============================================================================
echo "Ejecutando tests y generando reporte de cobertura..."
echo ""

mvn clean verify sonar:sonar \
  -Dsonar.host.url="${SONAR_HOST_URL}" \
  -Dsonar.token="${SONAR_TOKEN}"

# ============================================================================
# FINALIZACIÓN
# ============================================================================
echo ""
echo "============================================================"
echo "¡Análisis completado con éxito!"
echo "============================================================"
echo "Ver resultados en:"
echo "${SONAR_HOST_URL}/dashboard?id=${PROJECT_KEY}"
echo "============================================================"
