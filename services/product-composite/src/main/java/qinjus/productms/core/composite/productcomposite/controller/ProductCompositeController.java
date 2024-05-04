package qinjus.productms.core.composite.productcomposite.controller;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import qinjus.productms.composite.ProductAggregate;
import qinjus.productms.composite.RecommendationSummary;
import qinjus.productms.composite.ReviewSummary;
import qinjus.productms.composite.ServiceAddresses;
import qinjus.productms.core.Product;
import qinjus.productms.core.Recommendation;
import qinjus.productms.core.Review;
import qinjus.productms.exceptions.NotFoundException;
import qinjus.productms.utils.ServiceUtil;

@RestController
public class ProductCompositeController {
    private final ServiceUtil serviceUtil;
    private ProductCompositeIntegration integration;


    public ProductCompositeController(
        ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @GetMapping(
        value = "/product-composite/{productId}",
        produces = "application/json")
    public ProductAggregate getProduct(@PathVariable("productId") int productId) {
        Product product = integration.getProduct(productId);
        if (product == null) {
        throw new NotFoundException("No product found for productId: " + productId);
        }

        List<Recommendation> recommendations = integration.getRecommendations(productId);

        List<Review> reviews = integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(
        Product product,
        List<Recommendation> recommendations,
        List<Review> reviews,
        String serviceAddress) {

        // 1. Setup product info
        int productId = product.productId();
        String name = product.name();
        int weight = product.weight();

        // 2. Copy summary recommendation info, if available
        List<RecommendationSummary> recommendationSummaries =
        (recommendations == null) ? null : recommendations.stream()
            .map(r -> new RecommendationSummary(r.recommendationId(), r.author(), r.rate()))
            .collect(Collectors.toList());

        // 3. Copy summary review info, if available
        List<ReviewSummary> reviewSummaries = 
        (reviews == null) ? null : reviews.stream()
            .map(r -> new ReviewSummary(r.reviewId(), r.author(), r.subject()))
            .collect(Collectors.toList());

        // 4. Create info regarding the involved microservices addresses
        String productAddress = product.serviceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).serviceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).serviceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
    }
}
