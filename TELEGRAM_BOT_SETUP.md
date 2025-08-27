# Telegram Daily Shops Bot Setup Guide

## 🚀 Overview

This application provides both a REST API and a Telegram bot for managing shops and goods with a Many-to-Many relationship.

## 📋 Prerequisites

- Java 21
- Maven
- MongoDB (via Docker)
- Telegram Bot Token
- Tesseract OCR (for receipt processing)

## 🔧 Setup Instructions

### 1. Create a Telegram Bot

1. Open Telegram and search for `@BotFather`
2. Send `/newbot` command
3. Follow the instructions to create your bot
4. Save the bot token and username

### 2. Configure Environment Variables

Create a `.env` file or set environment variables:

```bash
# MongoDB Configuration
MONGODB_URI=mongodb://localhost:27017/telegram-daily-shop
MONGODB_DATABASE=telegram-daily-shop

# Telegram Bot Configuration
TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_BOT_TOKEN=your_bot_token
```

### 3. Start MongoDB

```bash
docker-compose up -d
```

### 4. Install Tesseract OCR

#### Windows
1. Download Tesseract from: https://github.com/UB-Mannheim/tesseract/wiki
2. Install with Russian language pack
3. Set environment variable: `TESSDATA_PATH=C:\Program Files\Tesseract-OCR\tessdata`

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install tesseract-ocr tesseract-ocr-rus
```

#### macOS
```bash
brew install tesseract tesseract-lang
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

## 🤖 Telegram Bot Commands

### Basic Commands
- `/start` - Welcome message and help
- `/help` - Show all available commands

### Shop Management
- `/shops` - List all shops
- `/addshop <name>` - Add a new shop
- `/searchshop <name>` - Search shops by name

### Good Management
- `/goods` - List all goods
- `/addgood <name>` - Add a new good
- `/searchgood <name>` - Search goods by name

### Price Management
- `/setprice <goodId> <shopId> <price>` - Set price for good in shop
- `/prices <goodId>` - Show all prices for a good
- `/cheapest <goodId>` - Show cheapest price for a good

### Receipt Management
- `/receipts` - List all receipts
- `/searchreceipt <text>` - Search receipts by text
- Send photo of receipt to process it automatically

### Examples
```
/addshop Electronics Store
/addgood Smartphone
/setprice good123 shop456 999.99
/prices good123
/cheapest good123
/searchshop electronics
/searchgood phone
/receipts
/searchreceipt молоко
```

## 🌐 REST API Endpoints

### Shops API (`/api/shops`)

#### Create Shop
```http
POST /api/shops
Content-Type: application/json

{
  "name": "Electronics Store"
}
```

#### Get All Shops
```http
GET /api/shops
```

#### Get Shop by ID
```http
GET /api/shops/{id}
```

#### Update Shop
```http
PUT /api/shops/{id}
Content-Type: application/json

{
  "name": "Updated Shop Name"
}
```

#### Delete Shop
```http
DELETE /api/shops/{id}
```

#### Search Shops
```http
GET /api/shops/search?name=electronics
```



### Goods API (`/api/goods`)

#### Create Good
```http
POST /api/goods
Content-Type: application/json

{
  "name": "Smartphone"
}
```

#### Get All Goods
```http
GET /api/goods
```

#### Get Good by ID
```http
GET /api/goods/{id}
```

#### Update Good
```http
PUT /api/goods/{id}
Content-Type: application/json

{
  "name": "Updated Good Name"
}
```

#### Delete Good
```http
DELETE /api/goods/{id}
```

#### Search Goods
```http
GET /api/goods/search?name=phone
```



### Prices API (`/api/prices`)

#### Create or Update Price
```http
POST /api/prices
Content-Type: application/json

{
  "goodId": "good_id_here",
  "shopId": "shop_id_here",
  "price": 999.99,
  "currency": "USD"
}
```

#### Get All Prices
```http
GET /api/prices
```

#### Get Price by ID
```http
GET /api/prices/{id}
```

#### Get Price by Good and Shop
```http
GET /api/prices/good/{goodId}/shop/{shopId}
```

#### Get All Prices for a Good
```http
GET /api/prices/good/{goodId}
```

#### Get All Prices for a Shop
```http
GET /api/prices/shop/{shopId}
```

#### Search Prices by Good Name
```http
GET /api/prices/search/good?goodName=smartphone
```

#### Search Prices by Shop Name
```http
GET /api/prices/search/shop?shopName=electronics
```

#### Find Prices by Range
```http
GET /api/prices/range?minPrice=100&maxPrice=1000
```

#### Find Prices by Currency
```http
GET /api/prices/currency/USD
```

#### Get Cheapest Price for Good
```http
GET /api/prices/good/{goodId}/cheapest
```

#### Get Most Expensive Price for Good
```http
GET /api/prices/good/{goodId}/most-expensive
```

#### Update Price
```http
PUT /api/prices/{id}
Content-Type: application/json

{
  "goodId": "good_id_here",
  "shopId": "shop_id_here",
  "price": 899.99,
  "currency": "USD"
}
```

#### Delete Price
```http
DELETE /api/prices/{id}
```

#### Delete Price by Good and Shop
```http
DELETE /api/prices/good/{goodId}/shop/{shopId}
```

### Receipts API (`/api/receipts`)

#### Upload and Process Receipt
```http
POST /api/receipts/upload
Content-Type: multipart/form-data

file: [image file]
```

#### Get All Receipts
```http
GET /api/receipts
```

#### Get Receipt by ID
```http
GET /api/receipts/{id}
```

#### Search Receipts by Text
```http
GET /api/receipts/search?text=молоко
```

#### Search Receipts by File Name
```http
GET /api/receipts/search/filename?fileName=receipt
```

#### Get Receipts After Date
```http
GET /api/receipts/after?date=2024-01-01T00:00:00
```

#### Get Receipts Between Dates
```http
GET /api/receipts/between?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

#### Update Receipt Text
```http
PUT /api/receipts/{id}
Content-Type: text/plain

Updated receipt text content
```

#### Delete Receipt
```http
DELETE /api/receipts/{id}
```

#### Check OCR Status
```http
GET /api/receipts/status
```

### Test API (`/api/test`)

#### Test OCR Service
```http
GET /api/test/ocr
```

#### Get System Information
```http
GET /api/test/system
```

## 🗄️ Database Schema

### Shop Collection
```json
{
  "_id": "ObjectId",
  "name": "String"
}
```

### Good Collection
```json
{
  "_id": "ObjectId",
  "name": "String"
}
```

### Goods Price Collection
```json
{
  "_id": "ObjectId",
  "good": {
    "$ref": "goods",
    "$id": "ObjectId"
  },
  "shop": {
    "$ref": "shops",
    "$id": "ObjectId"
  },
  "price": "BigDecimal",
  "currency": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### Receipt Collection
```json
{
  "_id": "ObjectId",
  "originalText": "String",
  "processedText": "String",
  "imageUrl": "String",
  "fileName": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

## 🔍 Testing the Bot

1. Start the application
2. Open Telegram and search for your bot
3. Send `/start` to begin
4. Try creating shops and goods using the commands

## 🔍 Testing the API

### Using curl

```bash
# Create a shop
curl -X POST http://localhost:8080/api/shops \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics Store"}'

# Create a good
curl -X POST http://localhost:8080/api/goods \
  -H "Content-Type: application/json" \
  -d '{"name": "Smartphone"}'

# Set price for good
curl -X POST http://localhost:8080/api/prices \
  -H "Content-Type: application/json" \
  -d '{"goodId": "good_id_here", "shopId": "shop_id_here", "price": 999.99, "currency": "USD"}'

# Get all prices for a good
curl http://localhost:8080/api/prices/good/good_id_here

# Get cheapest price for a good
curl http://localhost:8080/api/prices/good/good_id_here/cheapest

# Get all shops
curl http://localhost:8080/api/shops

# Get all goods
curl http://localhost:8080/api/goods

# Upload and process receipt
curl -X POST http://localhost:8080/api/receipts/upload \
  -F "file=@receipt.jpg"

# Get all receipts
curl http://localhost:8080/api/receipts

# Search receipts by text
curl "http://localhost:8080/api/receipts/search?text=молоко"

# Check OCR status
curl http://localhost:8080/api/receipts/status

# Test OCR service
curl http://localhost:8080/api/test/ocr

# Get system info
curl http://localhost:8080/api/test/system

### Using Postman

Import the following collection:

```json
{
  "info": {
    "name": "Daily Shops API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Shop",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Electronics Store\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/shops",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "shops"]
        }
      }
    },
    {
      "name": "Get All Shops",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/api/shops",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "shops"]
        }
      }
    }
  ]
}
```

## 🐛 Troubleshooting

### Common Issues

1. **Bot not responding**: Check if the bot token is correct
2. **MongoDB connection error**: Ensure MongoDB is running via Docker
3. **Validation errors**: Check request body format and required fields
4. **OCR not working**: Check Tesseract installation and tessdata path

### OCR Troubleshooting

#### Check OCR Status
```bash
curl http://localhost:8080/api/receipts/status
```

#### Test OCR Service
```bash
curl http://localhost:8080/api/test/ocr
```

#### Common OCR Issues:

1. **Tesseract not found**: Install Tesseract OCR
2. **Language data missing**: Install language packs (rus+eng)
3. **Wrong tessdata path**: Set correct `tessdata.path` property
4. **Image format issues**: Use JPEG or PNG format
5. **Image quality**: Ensure high resolution and good contrast

#### Debug Steps:
1. Check system info: `curl http://localhost:8080/api/test/system`
2. Verify Tesseract installation: `tesseract --version`
3. Check tessdata directory exists and contains language files
4. Test with a simple image first

### Logs

Check application logs for detailed error information:

```bash
tail -f logs/application.log
```

## 📝 Environment Variables Reference

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/telegram-daily-shop` |
| `MONGODB_DATABASE` | MongoDB database name | `telegram-daily-shop` |
| `TELEGRAM_BOT_USERNAME` | Telegram bot username | `your_bot_username` |
| `TELEGRAM_BOT_TOKEN` | Telegram bot token | `your_bot_token` |
| `TELEGRAM_WEBHOOK_PATH` | Webhook path (if using webhooks) | `/webhook` |
