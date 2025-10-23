# Setup Instructions

## What we Have Now

✅ **Both PostgresSQL and Elasticsearch always running**
✅ **Data consistency** - All CRUD operations sync to both databases
✅ **Separate endpoints**:
- Existing CRUD endpoints → PostgresSQL
- New search endpoints → Elasticsearch
✅ **Same security** - Search endpoints require admin authentication

## Setup Steps

### 1. Start Elasticsearch
Choose one option:

**Option A: Docker (if you have Docker Desktop)**
```bash
docker-compose up elasticsearch
```

**Option B: Download and Run**
1. Go to: https://www.elastic.co/downloads/elasticsearch
2. Download Windows ZIP
3. Extract and run: `bin\elasticsearch.bat`



### 2. Start PostgreSQL
```bash
    local or images from docker
```

### 3. Start Your Application



## API Endpoints

### PostgresSQL Endpoints (Existing - No Changes)
```
POST   /api/v1/users          # Create user
GET    /api/v1/users          # Get all users
GET    /api/v1/users/{id}     # Get user by ID
PUT    /api/v1/users/{id}     # Update user
PATCH  /api/v1/users/{id}     # Partial update
DELETE /api/v1/users/{id}     # Delete user
POST   /api/v1/users/bulk     # Bulk create
```

### Elasticsearch Endpoints (New)
```                 
GET /api/v1/users/search/name?name={name}            # Search by name
GET /api/v1/users/search/email?email={email}         # Search by email
```

## Testing in Postman

### 1. Get Admin Token
```
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

### 2. Test PostgreSQL Endpoints
```
POST /api/v1/users
Authorization: Bearer {token}
{
  "name": "John Doe",
  "email": "john@example.com",
  "age": 30,
  "password": "password123"
}
```

### 3. Test Elasticsearch Endpoints
```
GET /api/v1/users/search?q=john
Authorization: Bearer {token}

GET /api/v1/users/search/name?name=john
Authorization: Bearer {token}

GET /api/v1/users/search/email?email=john@example.com
Authorization: Bearer {token}
```

## Data Flow

1. **Create User**: 
   - Saves to PostgresSQL
   - Automatically indexes to Elasticsearch
   - Both databases stay in sync

2. **Update User**:
   - Updates PostgresSQL
   - Updates Elasticsearch
   - Both databases stay in sync

3. **Delete User**:
   - Soft deletes in PostgresSQL
   - Removes from Elasticsearch
   - Both databases stay in sync

4. **Search**:
   - Uses Elasticsearch for fast, fuzzy search
   - Returns same UserResponseDTO format

## Summary

- ✅ **No conditional logic** - Both databases always active
- ✅ **Data consistency** - All operations sync to both
- ✅ **Same security** - Admin authentication required
- ✅ **Simple endpoints** - Only name and email search for now
- ✅ **Existing functionality preserved** - All CRUD operations work as before
