package com.gamegearmatch.backend.product.config;

import com.gamegearmatch.backend.product.domain.ConnectionType;
import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.domain.ProductCategory;
import com.gamegearmatch.backend.product.domain.ProductSpec;
import com.gamegearmatch.backend.product.repository.ProductRepository;
import com.gamegearmatch.backend.product.repository.ProductSpecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoProductDataInitializer implements ApplicationRunner {
    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (productRepository.count() >= 10) {
            return;
        }

        List<DemoProduct> samples = List.of(
                new DemoProduct("AERO X1 무선 게이밍 마우스", ProductCategory.MOUSE, "GGM", 89900, 30, ConnectionType.WIRELESS, "/demo-gaming-mouse.png", "초경량 설계와 정밀 센서를 적용한 FPS용 무선 마우스", 58, 26000, 1000, 6, null, null, null, 1, 70, null),
                new DemoProduct("STRIKE M2 RGB 마우스", ProductCategory.MOUSE, "NEXONIC", 45900, 42, ConnectionType.WIRED, null, "정확한 클릭감과 7단계 RGB 조명을 제공하는 입문용 마우스", 76, 16000, 1000, 7, null, null, null, 1, null, null),
                new DemoProduct("PHANTOM PRO 마우스", ProductCategory.MOUSE, "VOLT", 129000, 18, ConnectionType.BOTH, null, "유무선 전환과 고성능 센서를 지원하는 프로 게이밍 마우스", 63, 30000, 2000, 8, null, null, null, 1, 90, null),
                new DemoProduct("NOVA 87 기계식 키보드", ProductCategory.KEYBOARD, "GGM", 109000, 25, ConnectionType.WIRED, null, "빠른 입력과 안정적인 타건감을 제공하는 텐키리스 키보드", 820, null, 1000, null, "적축", "텐키리스", 32, 1, null, null),
                new DemoProduct("CLOUD 75 무선 키보드", ProductCategory.KEYBOARD, "KEYLAB", 149000, 14, ConnectionType.BOTH, null, "가스켓 마운트와 멀티페어링을 지원하는 75% 키보드", 940, null, 1000, null, "저소음 리니어", "75%", 24, 1, 120, null),
                new DemoProduct("RAPID K60 게이밍 키보드", ProductCategory.KEYBOARD, "VOLT", 79000, 37, ConnectionType.WIRED, null, "컴팩트 배열과 빠른 응답속도에 집중한 게이밍 키보드", 610, null, 8000, null, "광축", "60%", 38, 1, null, null),
                new DemoProduct("SONIC H7 무선 헤드셋", ProductCategory.HEADSET, "GGM", 119000, 22, ConnectionType.WIRELESS, null, "입체적인 사운드와 탈착식 마이크를 갖춘 무선 헤드셋", 285, null, null, null, null, null, null, 20, 45, true),
                new DemoProduct("ARENA H3 게이밍 헤드셋", ProductCategory.HEADSET, "AUDION", 59900, 33, ConnectionType.WIRED, null, "가벼운 착용감과 선명한 음성 전달을 제공하는 헤드셋", 250, null, null, null, null, null, null, 18, null, true),
                new DemoProduct("IMMERSION X 헤드셋", ProductCategory.HEADSET, "NEXONIC", 169000, 11, ConnectionType.BOTH, null, "공간 음향과 액티브 노이즈 감소 기능을 적용한 프리미엄 헤드셋", 310, null, null, null, null, null, null, 15, 55, true),
                new DemoProduct("LITE H1 무선 헤드셋", ProductCategory.HEADSET, "KEYLAB", 84900, 28, ConnectionType.WIRELESS, null, "장시간 게임에도 편안한 초경량 무선 헤드셋", 218, null, null, null, null, null, null, 22, 60, true)
        );

        for (DemoProduct sample : samples) {
            if (productRepository.count() >= 10) {
                break;
            }
            if (productRepository.existsByName(sample.name())) {
                continue;
            }
            Product product = productRepository.save(Product.builder()
                    .name(sample.name()).category(sample.category()).brand(sample.brand())
                    .price(sample.price()).stock(sample.stock()).connectionType(sample.connectionType())
                    .imageUrl(sample.imageUrl()).description(sample.description()).build());
            productSpecRepository.save(ProductSpec.builder()
                    .product(product).weight(sample.weight()).dpi(sample.dpi()).pollingRate(sample.pollingRate())
                    .buttonCount(sample.buttonCount()).switchType(sample.switchType()).keyboardLayout(sample.keyboardLayout())
                    .noiseLevel(sample.noiseLevel()).responseTime(sample.responseTime()).batteryHours(sample.batteryHours())
                    .microphone(sample.microphone()).build());
        }
    }

    private record DemoProduct(String name, ProductCategory category, String brand, Integer price, Integer stock,
                               ConnectionType connectionType, String imageUrl, String description, Integer weight,
                               Integer dpi, Integer pollingRate, Integer buttonCount, String switchType,
                               String keyboardLayout, Integer noiseLevel, Integer responseTime,
                               Integer batteryHours, Boolean microphone) {
    }
}
