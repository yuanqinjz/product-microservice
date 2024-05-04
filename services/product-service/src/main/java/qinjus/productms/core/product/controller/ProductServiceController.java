package qinjus.productms.core.product.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import qinjus.productms.core.Product;
import qinjus.productms.exceptions.InvalidInputException;
import qinjus.productms.exceptions.NotFoundException;
import qinjus.productms.utils.ServiceUtil;

@RestController
public class ProductServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceController.class);

    private final ServiceUtil serviceUtil;
  
    public ProductServiceController(ServiceUtil serviceUtil) {
      this.serviceUtil = serviceUtil;
    }

    @GetMapping(
        value = "/product/{productId}",
        produces = "application/json")
    public Product getProduct(@PathVariable("productId") int productId) {
      LOG.debug("/product return the found product for productId={}", productId);
  
      if (productId < 1) {
        throw new InvalidInputException("Invalid productId: " + productId);
      }
  
      if (productId == 13) {
        throw new NotFoundException("No product found for productId: " + productId);
      }
  
      return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
    }
  }