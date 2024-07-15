package com.xworkz.vendorManagementSystem.service;

import java.util.List;

import com.xworkz.vendorManagementSystem.DTO.ProductDTO;

public interface ProductService {

	boolean SaveProdctDTO(ProductDTO productDTO);

	List<ProductDTO> findProductDetails(String email);

	boolean updateProduct(ProductDTO EditProductDTO);

	List<ProductDTO> readAllProducts();

}
