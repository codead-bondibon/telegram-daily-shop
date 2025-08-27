package codeadlive.bot.daily.shops.bot;

import codeadlive.bot.daily.shops.entity.Good;
import codeadlive.bot.daily.shops.entity.GoodsPrice;
import codeadlive.bot.daily.shops.entity.Receipt;
import codeadlive.bot.daily.shops.entity.Shop;
import codeadlive.bot.daily.shops.service.GoodService;
import codeadlive.bot.daily.shops.service.GoodsPriceService;
import codeadlive.bot.daily.shops.service.OcrService;
import codeadlive.bot.daily.shops.service.ReceiptService;
import codeadlive.bot.daily.shops.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotHandler extends TelegramLongPollingBot {

    private final ShopService shopService;
    private final GoodService goodService;
    private final GoodsPriceService goodsPriceService;
    private final ReceiptService receiptService;
    private final OcrService ocrService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (messageText.startsWith("/")) {
                    handleCommand(messageText, chatId);
                } else {
                    handleMessage(messageText, chatId);
                }
            } else if (update.getMessage().hasPhoto()) {
                handlePhoto(update.getMessage().getPhoto().get(0), chatId);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery().getData(), update.getCallbackQuery().getMessage().getChatId());
        }
    }

    private void handleCommand(String command, long chatId) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "/start":
                sendWelcomeMessage(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            case "/shops":
                sendShopsList(chatId);
                break;
            case "/goods":
                sendGoodsList(chatId);
                break;
            case "/addshop":
                handleAddShop(args, chatId);
                break;
            case "/addgood":
                handleAddGood(args, chatId);
                break;
            case "/searchshop":
                handleSearchShop(args, chatId);
                break;
            case "/searchgood":
                handleSearchGood(args, chatId);
                break;
            case "/setprice":
                handleSetPrice(args, chatId);
                break;
            case "/prices":
                handlePrices(args, chatId);
                break;
            case "/cheapest":
                handleCheapestPrice(args, chatId);
                break;
            case "/receipts":
                handleReceipts(args, chatId);
                break;
            case "/searchreceipt":
                handleSearchReceipt(args, chatId);
                break;
            default:
                sendMessage(chatId, "Unknown command. Use /help to see available commands.");
        }
    }

    private void handleMessage(String message, long chatId) {
        // Handle non-command messages (could be used for interactive flows)
        sendMessage(chatId, "I received your message: " + message + "\nUse /help to see available commands.");
    }

    private void handleCallbackQuery(String callbackData, long chatId) {
        // Handle inline keyboard callbacks
        if (callbackData.startsWith("shop_")) {
            String shopId = callbackData.substring(5);
            showShopDetails(shopId, chatId);
        } else if (callbackData.startsWith("good_")) {
            String goodId = callbackData.substring(5);
            showGoodDetails(goodId, chatId);
        }
    }

    private void sendWelcomeMessage(long chatId) {
        String message = "🎉 Welcome to Daily Shops Bot!\n\n" +
                "I can help you manage shops and goods. Here are some commands:\n\n" +
                "📋 /help - Show all available commands\n" +
                "🏪 /shops - List all shops\n" +
                "🛍️ /goods - List all goods\n" +
                "➕ /addshop <name> - Add a new shop\n" +
                "➕ /addgood <name> - Add a new good\n" +
                "💰 /setprice <goodId> <shopId> <price> - Set price for good\n" +
                "🔍 /searchshop <name> - Search shops\n" +
                "🔍 /searchgood <name> - Search goods\n" +
                "💵 /prices <goodId> - Show all prices for good\n" +
                "🏆 /cheapest <goodId> - Show cheapest price for good\n" +
                "🧾 /receipts - List all receipts\n" +
                "🔍 /searchreceipt <text> - Search receipts by text\n" +
                "📸 Send photo of receipt to process it";

        sendMessage(chatId, message);
    }

    private void sendHelpMessage(long chatId) {
        String message = "🤖 Available Commands:\n\n" +
                "🏪 Shop Management:\n" +
                "• /shops - List all shops\n" +
                "• /addshop <name> - Add a new shop\n" +
                "• /searchshop <name> - Search shops by name\n\n" +
                "🛍️ Good Management:\n" +
                "• /goods - List all goods\n" +
                "• /addgood <name> - Add a new good\n" +
                "• /searchgood <name> - Search goods by name\n\n" +
                "💰 Price Management:\n" +
                "• /setprice <goodId> <shopId> <price> - Set price for good\n" +
                "• /prices <goodId> - Show all prices for good\n" +
                "• /cheapest <goodId> - Show cheapest price for good\n\n" +
                "🧾 Receipt Management:\n" +
                "• /receipts - List all receipts\n" +
                "• /searchreceipt <text> - Search receipts by text\n" +
                "• Send photo of receipt to process it\n\n" +
                "💡 Examples:\n" +
                "• /addshop Electronics Store\n" +
                "• /addgood Smartphone\n" +
                "• /setprice good123 shop456 999.99\n" +
                "• /prices good123\n" +
                "• /cheapest good123\n" +
                "• /searchreceipt молоко";

        sendMessage(chatId, message);
    }

    private void sendShopsList(long chatId) {
        List<Shop> shops = shopService.findAll();
        if (shops.isEmpty()) {
            sendMessage(chatId, "No shops found. Use /addshop to create your first shop!");
            return;
        }

        StringBuilder message = new StringBuilder("🏪 Available Shops:\n\n");
        for (Shop shop : shops) {
            message.append("• ").append(shop.getName()).append(" (ID: ").append(shop.getId()).append(")\n");
        }

        sendMessage(chatId, message.toString());
    }

    private void sendGoodsList(long chatId) {
        List<Good> goods = goodService.findAll();
        if (goods.isEmpty()) {
            sendMessage(chatId, "No goods found. Use /addgood to create your first good!");
            return;
        }

        StringBuilder message = new StringBuilder("🛍️ Available Goods:\n\n");
        for (Good good : goods) {
            message.append("• ").append(good.getName()).append(" (ID: ").append(good.getId()).append(")\n");
        }

        sendMessage(chatId, message.toString());
    }

    private void handleAddShop(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide a shop name.\nUsage: /addshop <shop name>");
            return;
        }

        try {
            Shop shop = new Shop();
            shop.setName(args.trim());
            Shop savedShop = shopService.saveShop(shop);
            sendMessage(chatId, "✅ Shop '" + savedShop.getName() + "' created successfully!\nID: " + savedShop.getId());
        } catch (Exception e) {
            log.error("Error creating shop", e);
            sendMessage(chatId, "❌ Error creating shop. Please try again.");
        }
    }

    private void handleAddGood(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide a good name.\nUsage: /addgood <good name>");
            return;
        }

        try {
            Good good = new Good();
            good.setName(args.trim());
            Good savedGood = goodService.saveGood(good);
            sendMessage(chatId, "✅ Good '" + savedGood.getName() + "' created successfully!\nID: " + savedGood.getId());
        } catch (Exception e) {
            log.error("Error creating good", e);
            sendMessage(chatId, "❌ Error creating good. Please try again.");
        }
    }

    private void handleSearchShop(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide a search term.\nUsage: /searchshop <search term>");
            return;
        }

        List<Shop> shops = shopService.findByNameContaining(args.trim());
        if (shops.isEmpty()) {
            sendMessage(chatId, "🔍 No shops found matching '" + args.trim() + "'");
            return;
        }

        StringBuilder message = new StringBuilder("🔍 Shops matching '" + args.trim() + "':\n\n");
        for (Shop shop : shops) {
            message.append("• ").append(shop.getName()).append(" (ID: ").append(shop.getId()).append(")\n");
        }

        sendMessage(chatId, message.toString());
    }

    private void handleSearchGood(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide a search term.\nUsage: /searchgood <search term>");
            return;
        }

        List<Good> goods = goodService.findByNameContaining(args.trim());
        if (goods.isEmpty()) {
            sendMessage(chatId, "🔍 No goods found matching '" + args.trim() + "'");
            return;
        }

        StringBuilder message = new StringBuilder("🔍 Goods matching '" + args.trim() + "':\n\n");
        for (Good good : goods) {
            message.append("• ").append(good.getName()).append(" (ID: ").append(good.getId()).append(")\n");
        }

        sendMessage(chatId, message.toString());
    }

    private void showShopDetails(String shopId, long chatId) {
        shopService.findById(shopId).ifPresentOrElse(
                shop -> {
                    StringBuilder message = new StringBuilder("🏪 Shop Details:\n\n");
                    message.append("Name: ").append(shop.getName()).append("\n");
                    message.append("ID: ").append(shop.getId()).append("\n");

                    sendMessage(chatId, message.toString());
                },
                () -> sendMessage(chatId, "❌ Shop not found.")
        );
    }

    private void showGoodDetails(String goodId, long chatId) {
        goodService.findById(goodId).ifPresentOrElse(
                good -> {
                    StringBuilder message = new StringBuilder("🛍️ Good Details:\n\n");
                    message.append("Name: ").append(good.getName()).append("\n");
                    message.append("ID: ").append(good.getId()).append("\n");

                    sendMessage(chatId, message.toString());
                },
                () -> sendMessage(chatId, "❌ Good not found.")
        );
    }

    private void handleSetPrice(String args, long chatId) {
        String[] parts = args.trim().split("\\s+");
        if (parts.length < 3) {
            sendMessage(chatId, "❌ Please provide good ID, shop ID, and price.\nUsage: /setprice <goodId> <shopId> <price>");
            return;
        }

        try {
            String goodId = parts[0];
            String shopId = parts[1];
            BigDecimal price = new BigDecimal(parts[2]);

            GoodsPrice savedPrice = goodsPriceService.setPrice(goodId, shopId, price, "USD");
            sendMessage(chatId, "✅ Price set successfully!\n" +
                    "Good: " + savedPrice.getGood().getName() + "\n" +
                    "Shop: " + savedPrice.getShop().getName() + "\n" +
                    "Price: $" + savedPrice.getPrice() + "\n" +
                    "ID: " + savedPrice.getId());
        } catch (NumberFormatException e) {
            sendMessage(chatId, "❌ Invalid price format. Please use numbers (e.g., 999.99)");
        } catch (RuntimeException e) {
            sendMessage(chatId, "❌ Error setting price. Please check good ID and shop ID.");
        }
    }

    private void handlePrices(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide a good ID.\nUsage: /prices <goodId>");
            return;
        }

        List<GoodsPrice> prices = goodsPriceService.findByGoodId(args.trim());
        if (prices.isEmpty()) {
            sendMessage(chatId, "🔍 No prices found for good ID: " + args.trim());
            return;
        }

        StringBuilder message = new StringBuilder("💵 Prices for good '" + prices.get(0).getGood().getName() + "':\n\n");
        for (GoodsPrice price : prices) {
            message.append("🏪 ").append(price.getShop().getName())
                    .append(": $").append(price.getPrice())
                    .append(" (").append(price.getCurrency()).append(")\n");
        }

        sendMessage(chatId, message.toString());
    }

    private void handleCheapestPrice(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide a good ID.\nUsage: /cheapest <goodId>");
            return;
        }

        Optional<GoodsPrice> cheapestPrice = goodsPriceService.findCheapestPriceForGood(args.trim());
        if (cheapestPrice.isPresent()) {
            GoodsPrice price = cheapestPrice.get();
            sendMessage(chatId, "🏆 Cheapest price for '" + price.getGood().getName() + "':\n\n" +
                    "🏪 Shop: " + price.getShop().getName() + "\n" +
                    "💰 Price: $" + price.getPrice() + "\n" +
                    "💱 Currency: " + price.getCurrency());
        } else {
            sendMessage(chatId, "🔍 No prices found for good ID: " + args.trim());
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableMarkdown(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
    
    /**
     * Обработка фотографии чека
     */
    private void handlePhoto(org.telegram.telegrambots.meta.api.objects.PhotoSize photo, long chatId) {
        try {
            sendMessage(chatId, "🔄 Processing receipt image...");
            
            // Проверка доступности OCR
            if (!ocrService.isTesseractAvailable()) {
                sendMessage(chatId, "❌ OCR service is not available. Please check Tesseract installation.");
                return;
            }
            
            // Получение файла
            String fileId = photo.getFileId();
            org.telegram.telegrambots.meta.api.methods.GetFile getFile = new org.telegram.telegrambots.meta.api.methods.GetFile();
            getFile.setFileId(fileId);
            
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            String filePath = file.getFilePath();
            
            log.info("Downloading file from Telegram: {}", filePath);
            
            // Скачивание файла
            java.net.URL url = new java.net.URL("https://api.telegram.org/file/bot" + botToken + "/" + filePath);
            java.io.InputStream inputStream = url.openStream();
            
            // Создание MultipartFile
            MultipartFile multipartFile = new MultipartFile() {
                @Override
                public String getName() {
                    return "receipt";
                }
                
                @Override
                public String getOriginalFilename() {
                    return "receipt_" + System.currentTimeMillis() + ".jpg";
                }
                
                @Override
                public String getContentType() {
                    return "image/jpeg";
                }
                
                @Override
                public boolean isEmpty() {
                    return false;
                }
                
                @Override
                public long getSize() {
                    return photo.getFileSize();
                }
                
                @Override
                public byte[] getBytes() throws IOException {
                    return inputStream.readAllBytes();
                }
                
                @Override
                public java.io.InputStream getInputStream() throws IOException {
                    return inputStream;
                }
                
                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    java.nio.file.Files.copy(getInputStream(), dest.toPath());
                }
            };
            
            log.info("Processing receipt with size: {} bytes", multipartFile.getSize());
            
            // Обработка чека
            Receipt receipt = receiptService.processAndSaveReceipt(multipartFile);
            
            StringBuilder message = new StringBuilder("✅ Receipt processed successfully!\n\n");
            message.append("📄 ID: ").append(receipt.getId()).append("\n");
            message.append("📁 File: ").append(receipt.getFileName()).append("\n");
            message.append("📅 Date: ").append(receipt.getCreatedAt()).append("\n\n");
            message.append("📝 Recognized text:\n");
            message.append(receipt.getProcessedText());
            
            sendMessage(chatId, message.toString());
            
        } catch (net.sourceforge.tess4j.TesseractException e) {
            log.error("OCR processing error", e);
            sendMessage(chatId, "❌ OCR processing failed. Please check if Tesseract is installed correctly.");
        } catch (IOException e) {
            log.error("File processing error", e);
            sendMessage(chatId, "❌ File processing error. Please try again.");
        } catch (IllegalArgumentException e) {
            log.error("Invalid file format", e);
            sendMessage(chatId, "❌ Invalid image format. Please send a valid image file.");
        } catch (Exception e) {
            log.error("Error processing receipt photo", e);
            sendMessage(chatId, "❌ Unexpected error processing receipt. Please try again.");
        }
    }
    
    /**
     * Показать список всех чеков
     */
    private void handleReceipts(String args, long chatId) {
        List<Receipt> receipts = receiptService.findAll();
        if (receipts.isEmpty()) {
            sendMessage(chatId, "📄 No receipts found. Send a photo of a receipt to process it!");
            return;
        }
        
        StringBuilder message = new StringBuilder("🧾 Available Receipts:\n\n");
        for (Receipt receipt : receipts) {
            message.append("📄 ").append(receipt.getFileName())
                    .append(" (ID: ").append(receipt.getId()).append(")\n");
            message.append("📅 ").append(receipt.getCreatedAt()).append("\n");
            if (receipt.getProcessedText().length() > 50) {
                message.append("📝 ").append(receipt.getProcessedText().substring(0, 50)).append("...\n");
            } else {
                message.append("📝 ").append(receipt.getProcessedText()).append("\n");
            }
            message.append("\n");
        }
        
        sendMessage(chatId, message.toString());
    }
    
    /**
     * Поиск чеков по тексту
     */
    private void handleSearchReceipt(String args, long chatId) {
        if (args.trim().isEmpty()) {
            sendMessage(chatId, "❌ Please provide search text.\nUsage: /searchreceipt <text>");
            return;
        }
        
        List<Receipt> receipts = receiptService.findByTextContaining(args.trim());
        if (receipts.isEmpty()) {
            sendMessage(chatId, "🔍 No receipts found matching '" + args.trim() + "'");
            return;
        }
        
        StringBuilder message = new StringBuilder("🔍 Receipts matching '" + args.trim() + "':\n\n");
        for (Receipt receipt : receipts) {
            message.append("📄 ").append(receipt.getFileName())
                    .append(" (ID: ").append(receipt.getId()).append(")\n");
            message.append("📅 ").append(receipt.getCreatedAt()).append("\n");
            if (receipt.getProcessedText().length() > 100) {
                message.append("📝 ").append(receipt.getProcessedText().substring(0, 100)).append("...\n");
            } else {
                message.append("📝 ").append(receipt.getProcessedText()).append("\n");
            }
            message.append("\n");
        }
        
        sendMessage(chatId, message.toString());
    }
}
