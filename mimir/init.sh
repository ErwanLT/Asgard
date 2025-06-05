#!/bin/bash

# Créer le réseau Docker s'il n'existe pas
docker network create asgard-network 2>/dev/null || true

# Créer les répertoires nécessaires
mkdir -p ../logstash/config
mkdir -p ../logstash/pipeline
mkdir -p ../logs

# Démarrer les conteneurs
docker compose up -d

# Attendre que Logstash soit prêt
echo "Attente du démarrage de Logstash..."
sleep 30

# Vérifier que Logstash est bien démarré
if docker compose ps | grep -q "mimir-logstash.*Up"; then
    echo "Logstash est démarré et prêt à recevoir des logs"
else
    echo "Erreur : Logstash n'a pas démarré correctement"
    docker compose logs logstash
    exit 1
fi 