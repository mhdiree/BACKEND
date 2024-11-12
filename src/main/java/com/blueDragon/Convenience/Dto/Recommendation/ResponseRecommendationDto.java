package com.blueDragon.Convenience.Dto.Recommendation;


import com.blueDragon.Convenience.Dto.Product.ProductDto;
import com.blueDragon.Convenience.Model.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
public class ResponseRecommendationDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime createdAt;
    private String content;
    private String imageUrls;
    private List<String> availableAt;
    private List<String> foodTypes;
    @Setter
    private List<ProductDto> productList;
    private String totalPrice;
    private int countLikes;
    private int countComments;

    public static ResponseRecommendationDto entityToCreateDto(RecommendBoard recommendBoard) {
        return ResponseRecommendationDto.builder()
                .id(recommendBoard.getId())
                .userId(recommendBoard.getUser().getId())
                .title(recommendBoard.getTitle())
                .content(recommendBoard.getContent())
                .createdAt(recommendBoard.getCreatedAt())
                .countLikes(0)
                .countComments(0)
                .totalPrice(String.format("%,d", recommendBoard.getTotalPrice()))
                .foodTypes(recommendBoard.getFoodTypes())
                .availableAt(recommendBoard.getAvailableAt())
                .imageUrls(recommendBoard.getImageUrl())
                .productList(recommendBoard.getProductList().stream().map((ProductDto::entityToDto)).collect(Collectors.toList()))
                .build();
    }

    public static ResponseRecommendationDto entityToDto(RecommendBoard recommendBoard) {
        return ResponseRecommendationDto.builder()
                .id(recommendBoard.getId())
                .userId(recommendBoard.getUser().getId())
                .title(recommendBoard.getTitle())
                .content(recommendBoard.getContent())
                .createdAt(recommendBoard.getCreatedAt())
                .countLikes(recommendBoard.getRecommendLikes().size())
                .countComments(0)
                .totalPrice(String.format("%,d", recommendBoard.getTotalPrice()))
                .foodTypes(recommendBoard.getFoodTypes())
                .availableAt(recommendBoard.getAvailableAt())
                .imageUrls(recommendBoard.getImageUrl())
                .build();
    }
}