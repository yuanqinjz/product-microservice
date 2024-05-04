package qinjus.productms.core;
public record Review(int productId, int reviewId, String author, String subject,
                String content, String serviceAddress) {
    
}
