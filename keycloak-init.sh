#!/bin/bash

# Wait for Keycloak to be ready
echo "Waiting for Keycloak to be ready..."
until curl -s http://localhost:8081/health/ready; do
    sleep 5
done

# Get admin token
echo "Getting admin token..."
TOKEN=$(curl -s -X POST http://localhost:8081/realms/master/protocol/openid-connect/token \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" | jq -r '.access_token')

# Create realm
echo "Creating realm..."
curl -s -X POST http://localhost:8081/admin/realms \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "realm": "ecommerce",
        "enabled": true,
        "registrationAllowed": true,
        "resetPasswordAllowed": true,
        "rememberMe": true,
        "loginWithEmailAllowed": true,
        "duplicateEmailsAllowed": false,
        "ssoSessionIdleTimeout": 1800,
        "ssoSessionMaxLifespan": 36000,
        "accessTokenLifespan": 3600
    }'

# Create client
echo "Creating client..."
curl -s -X POST http://localhost:8081/admin/realms/ecommerce/clients \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "clientId": "ecommerce-app",
        "enabled": true,
        "protocol": "openid-connect",
        "publicClient": false,
        "bearerOnly": false,
        "standardFlowEnabled": true,
        "implicitFlowEnabled": true,
        "directAccessGrantsEnabled": true,
        "serviceAccountsEnabled": true,
        "attributes": {
            "access.token.audience": "ecommerce-app",
            "access.token.lifespan": "3600",
            "access.token.format": "jwt",
            "access.token.signing.alg": "RS256"
        },
        "redirectUris": ["http://localhost:3000/*", "http://localhost:8080/*"],
        "webOrigins": ["http://localhost:3000", "http://localhost:8080"]
    }'

# Get client ID
CLIENT_ID=$(curl -s http://localhost:8081/admin/realms/ecommerce/clients?clientId=ecommerce-app \
    -H "Authorization: Bearer $TOKEN" | jq -r '.[0].id')

# Create client secret
echo "Creating client secret..."
curl -s -X POST http://localhost:8081/admin/realms/ecommerce/clients/$CLIENT_ID/client-secret \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{}'

# Create roles
echo "Creating roles..."
curl -s -X POST http://localhost:8081/admin/realms/ecommerce/roles \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "ROLE_USER"}'

curl -s -X POST http://localhost:8081/admin/realms/ecommerce/roles \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "ROLE_ADMIN"}'

# Create test user
echo "Creating test user..."
curl -s -X POST http://localhost:8081/admin/realms/ecommerce/users \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "enabled": true,
        "credentials": [{
            "type": "password",
            "value": "testpass",
            "temporary": false
        }],
        "email": "test@example.com",
        "firstName": "Test",
        "lastName": "User"
    }'

# Get user ID
USER_ID=$(curl -s http://localhost:8081/admin/realms/ecommerce/users?username=testuser \
    -H "Authorization: Bearer $TOKEN" | jq -r '.[0].id')

# Assign roles to user
echo "Assigning roles to user..."
USER_ROLE_ID=$(curl -s http://localhost:8081/admin/realms/ecommerce/roles/ROLE_USER \
    -H "Authorization: Bearer $TOKEN" | jq -r '.id')

curl -s -X POST http://localhost:8081/admin/realms/ecommerce/users/$USER_ID/role-mappings/realm \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "[{\"id\":\"$USER_ROLE_ID\",\"name\":\"ROLE_USER\"}]"

echo "Keycloak initialization completed!" 