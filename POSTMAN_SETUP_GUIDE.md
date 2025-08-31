# Postman Collection Setup Guide

This guide will help you set up and use the Postman collection for testing the User API.

## Files Included

1. **`User_API_Collection.json`** - The main Postman collection with all API endpoints
2. **`User_API_Environment.json`** - Environment variables for different configurations
3. **`POSTMAN_SETUP_GUIDE.md`** - This setup guide

## Prerequisites

1. **Postman Desktop App** - Download and install from [postman.com](https://www.postman.com/downloads/)
2. **Spring Boot Application** - Your User API should be running on `http://localhost:8080`

## Setup Instructions

### Step 1: Import the Collection

1. Open Postman
2. Click **Import** button (top left)
3. Select **Upload Files** tab
4. Choose `User_API_Collection.json`
5. Click **Import**

### Step 2: Import the Environment

1. In Postman, click **Import** again
2. Select **Upload Files** tab
3. Choose `User_API_Environment.json`
4. Click **Import**

### Step 3: Select Environment

1. In the top-right corner of Postman, click the environment dropdown
2. Select **"User API Environment"**

## API Endpoints Overview

The collection includes the following endpoints:

| Method | Endpoint          | Description       |
| ------ | ----------------- | ----------------- |
| GET    | `/api/users`      | Get all users     |
| GET    | `/api/users/{id}` | Get user by ID    |
| POST   | `/api/users`      | Create a new user |
| PUT    | `/api/users/{id}` | Update a user     |
| DELETE | `/api/users/{id}` | Delete a user     |

## Testing Workflow

### 1. Basic CRUD Operations

1. **Create User**: Use the "Create User" request to add a new user
2. **Get All Users**: Use "Get All Users" to verify the user was created
3. **Get User by ID**: Use "Get User by ID" with the returned user ID
4. **Update User**: Use "Update User" to modify user details
5. **Delete User**: Use "Delete User" to remove the user

### 2. Test Scenarios

The collection includes organized test scenarios:

- **Test Scenarios**: Create multiple users for testing
- **Error Handling Tests**: Test various error conditions

### 3. Error Handling Tests

- **Get Non-existent User**: Tests 404 response
- **Create User with Invalid Data**: Tests validation errors
- **Create User with Duplicate Email**: Tests unique constraint violation

## Environment Variables

The environment includes these variables:

| Variable          | Default Value           | Description                           |
| ----------------- | ----------------------- | ------------------------------------- |
| `base_url`        | `http://localhost:8080` | Base URL for the API                  |
| `user_id`         | `1`                     | Default user ID for testing           |
| `created_user_id` | ``                      | Dynamically set after creating a user |
| `test_user_name`  | `Test User`             | Test user name                        |
| `test_user_email` | `test.user@example.com` | Test user email                       |
| `test_user_age`   | `25`                    | Test user age                         |

## Sample Request Bodies

### Create User

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30
}
```

### Update User

```json
{
  "name": "Jane Smith Updated",
  "email": "jane.smith.updated@example.com",
  "age": 28
}
```

## Running Tests

### Manual Testing

1. Select any request from the collection
2. Click **Send** to execute the request
3. Review the response in the response panel

### Automated Testing

1. Right-click on the collection
2. Select **Run collection**
3. Choose the environment
4. Click **Run User API Collection**

## Expected Responses

### Successful Responses

**GET /api/users** (200 OK)

```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "age": 30
  }
]
```

**POST /api/users** (200 OK)

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "age": 30
}
```

### Error Responses

**GET /api/users/{id}** (404 Not Found)

```json
{
  "timestamp": "2024-01-01T00:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/users/999"
}
```

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure your Spring Boot application is running
2. **404 Not Found**: Check if the base URL is correct
3. **500 Internal Server Error**: Check application logs for server-side errors

### Debugging Tips

1. **Check Application Logs**: Monitor your Spring Boot console for errors
2. **Verify Database**: Ensure H2 console is accessible at `http://localhost:8080/h2-console`
3. **Test with curl**: Use curl commands to verify API functionality

### H2 Database Console

Access the H2 database console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Customization

### Adding New Environments

1. Create a new environment in Postman
2. Set different `base_url` values:
   - Development: `http://localhost:8080`
   - Staging: `http://staging-server:8080`
   - Production: `http://production-server:8080`

### Adding New Tests

1. Duplicate existing requests
2. Modify request parameters
3. Add test scripts in the "Tests" tab
4. Use environment variables for dynamic values

## Best Practices

1. **Use Environment Variables**: Never hardcode URLs or IDs
2. **Test Error Scenarios**: Always test both success and failure cases
3. **Validate Responses**: Use test scripts to validate response structure
4. **Organize Collections**: Group related requests in folders
5. **Document Requests**: Add descriptions to all requests

## Support

If you encounter issues:

1. Check the Spring Boot application logs
2. Verify the API endpoints are accessible
3. Ensure the database is properly configured
4. Test with simple curl commands first
