package qinjus.productms.core.composite.productcomposite.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import qinjus.productms.core.Product;
import qinjus.productms.core.Recommendation;
import qinjus.productms.core.Review;
import qinjus.productms.exceptions.InvalidInputException;
import qinjus.productms.exceptions.NotFoundException;
import qinjus.productms.utils.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProductCompositeIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    public ProductCompositeIntegration(RestTemplate restTemplate,
        @Value("${app.product-service.host}") String productServiceHost,
        @Value("${app.product-service.port}") int productServicePort,
        @Value("${app.recommendation-service.host}") String recommendationServiceHost,
        @Value("${app.recommendation-service.port}") int recommendationServicePort,
        @Value("${app.review-service.host}") String reviewServiceHost,
        @Value("${app.review-service.port}") int reviewServicePort,
        ObjectMapper mapper){
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    public Product getProduct(int productId) {

        try {
          String url = productServiceUrl + productId;
          LOG.debug("Will call getProduct API on URL: {}", url);
    
          Product product = restTemplate.getForObject(url, Product.class);
          LOG.debug("Found a product with id: {}", product.productId());
    
          return product;
    
        } catch (HttpClientErrorException ex) {
    
          switch (HttpStatus.resolve(ex.getStatusCode().value())) {
            case NOT_FOUND:
              throw new NotFoundException(getErrorMessage(ex));
    
            case UNPROCESSABLE_ENTITY:
              throw new InvalidInputException(getErrorMessage(ex));
    
            default:
              LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
              LOG.warn("Error body: {}", ex.getResponseBodyAsString());
              throw ex;
          }
        }
      }
    
      private String getErrorMessage(HttpClientErrorException ex) {
        try {
          return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).message();
        } catch (IOException ioex) {
          return ex.getMessage();
        }
      }
    
      public List<Recommendation> getRecommendations(int productId) {
    
        try {
          String url = recommendationServiceUrl + productId;
    
          LOG.debug("Will call getRecommendations API on URL: {}", url);
          List<Recommendation> recommendations = restTemplate
            .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {})
            .getBody();
    
          LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
          return recommendations;
    
        } catch (Exception ex) {
          LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
          return new ArrayList<>();
        }
      }
    
      public List<Review> getReviews(int productId) {
    
        try {
          String url = reviewServiceUrl + productId;
    
          LOG.debug("Will call getReviews API on URL: {}", url);
          List<Review> reviews = restTemplate
            .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {})
            .getBody();
    
          LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
          return reviews;
    
        } catch (Exception ex) {
          LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
          return new ArrayList<>();
        }
      }
}
