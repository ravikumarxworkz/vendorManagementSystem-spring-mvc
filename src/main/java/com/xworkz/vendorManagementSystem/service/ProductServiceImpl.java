package com.xworkz.vendorManagementSystem.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xworkz.vendorManagementSystem.DTO.ProductDTO;
import com.xworkz.vendorManagementSystem.Entity.ProductEntity;
import com.xworkz.vendorManagementSystem.Entity.VendorEntity;
import com.xworkz.vendorManagementSystem.repository.ProductRepo;
import com.xworkz.vendorManagementSystem.repository.VendorRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepo productRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Override
	public boolean SaveProdctDTO(ProductDTO productDTO) {
		log.info("Attempting to save product DTO: {}", productDTO);
		ProductEntity productEntity = new ProductEntity();

		VendorEntity vendorEntity = vendorRepository.findVendorByEmail(productDTO.getEmail());

		if (vendorEntity != null) {
			productDTO.setVendor(vendorEntity);
			BeanUtils.copyProperties(productDTO, productEntity);
			boolean saveProduct = this.productRepository.saveProdctEntity(productEntity);
			if (saveProduct) {
				log.info("Product DTO saved successfully: {}", productDTO);
				return true;
			} else {
				log.warn("Failed to save product DTO: {}", productDTO);
			}
		}
		log.warn("Vendor not found for email: {}", productDTO.getEmail());
		return false;
	}

	@Override
	public List<ProductDTO> findProductDetails(String email) {
		log.info("Finding product details for email: {}", email);
		int vendorID = vendorRepository.findIdByEmail(email);
		List<ProductEntity> productEntities = productRepository.findProductByEmail(vendorID);
		List<ProductDTO> productDTOs = new ArrayList<>();
		for (ProductEntity productEntity : productEntities) {
			ProductDTO productDTO = new ProductDTO();
			BeanUtils.copyProperties(productEntity, productDTO);
			productDTOs.add(productDTO);
		}
		log.info("Product details found for email {}: {}", email, productDTOs);
		return productDTOs;
	}

	@Override
	public boolean updateProduct(ProductDTO EditProductDTO) {
		log.info("Attempting to update product: {}", EditProductDTO);
		if (EditProductDTO != null) {
			ProductEntity UpdateproductEntity = new ProductEntity();
			BeanUtils.copyProperties(EditProductDTO, UpdateproductEntity);
			boolean updateproduct = productRepository.updateProductById(EditProductDTO.getId(), UpdateproductEntity);
			if (updateproduct) {
				log.info("Product updated successfully: {}", EditProductDTO);
				return true;
			} else {
				log.warn("Failed to update product: {}", EditProductDTO);
			}
		} else {
			log.warn("EditProductDTO is null");
		}
		return false;
	}

	@Override
	public List<ProductDTO> readAllProducts() {
		log.info("Reading all products");
		List<ProductEntity> productEntities = productRepository.readAllProducts();
		List<ProductDTO> productDTOs = new ArrayList<>();
		for (ProductEntity productEntity : productEntities) {
			productEntity.getVendor().setOTPGenerationTime(null);
			productEntity.getVendor().setLoginOTP(null);
			ProductDTO productDTO = new ProductDTO();
			try {
				BeanUtils.copyProperties(productEntity, productDTO);
				productDTOs.add(productDTO);
			} catch (Exception e) {
				log.error("Error copying properties from entity to DTO: {}", productEntity, e);
			}
		}
		log.info("All products read successfully: {}", productDTOs);
		return productDTOs;
	}

}
