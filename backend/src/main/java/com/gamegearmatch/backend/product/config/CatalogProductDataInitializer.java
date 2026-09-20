package com.gamegearmatch.backend.product.config;

import com.gamegearmatch.backend.product.domain.*;
import com.gamegearmatch.backend.product.repository.ProductRepository;
import com.gamegearmatch.backend.product.repository.ProductSpecRepository;
import com.gamegearmatch.backend.review.domain.Review;
import com.gamegearmatch.backend.review.repository.ReviewRepository;
import com.gamegearmatch.backend.user.domain.Role;
import com.gamegearmatch.backend.user.domain.User;
import com.gamegearmatch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogProductDataInitializer implements ApplicationRunner {
    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SS = "https://media.steelseriescdn.com/thumbs/catalog/items/";
    private static final List<String> LEGACY = List.of(
            "AERO X1 무선 게이밍 마우스", "STRIKE M2 RGB 마우스", "PHANTOM PRO 마우스",
            "NOVA 87 기계식 키보드", "CLOUD 75 무선 키보드", "RAPID K60 게이밍 키보드",
            "SONIC H7 무선 헤드셋", "ARENA H3 게이밍 헤드셋", "IMMERSION X 헤드셋", "LITE H1 무선 헤드셋"
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        LEGACY.forEach(name -> productRepository.findByName(name).ifPresent(Product::hide));
        List<Product> products = new ArrayList<>();
        catalog().forEach(item -> products.add(save(item)));
        addDemoReviews(products);
    }

    private Product save(Item item) {
        return productRepository.findByName(item.name()).orElseGet(() -> {
            Product product = productRepository.save(Product.builder().name(item.name()).category(item.category())
                    .brand(item.brand()).price(item.price()).stock(10 + Math.abs(item.name().hashCode()) % 31)
                    .connectionType(item.connection()).imageUrl(item.image()).description(item.description()).build());
            int seed = Math.abs(item.name().hashCode());
            productSpecRepository.save(ProductSpec.builder().product(product)
                    .weight(item.category() == ProductCategory.MOUSE ? 55 + seed % 45 : item.category() == ProductCategory.KEYBOARD ? 650 + seed % 450 : 240 + seed % 120)
                    .dpi(item.category() == ProductCategory.MOUSE ? 12000 + seed % 18001 : null)
                    .pollingRate(item.category() == ProductCategory.HEADSET ? null : 1000)
                    .buttonCount(item.category() == ProductCategory.MOUSE ? 6 + seed % 10 : null)
                    .switchType(item.category() == ProductCategory.KEYBOARD ? "게이밍 스위치" : null)
                    .keyboardLayout(item.category() == ProductCategory.KEYBOARD ? "게이밍 배열" : null)
                    .noiseLevel(item.category() == ProductCategory.KEYBOARD ? 25 + seed % 15 : null)
                    .responseTime(item.category() == ProductCategory.HEADSET ? 18 + seed % 8 : 1)
                    .batteryHours(item.connection() == ConnectionType.WIRED ? null : 30 + seed % 171)
                    .microphone(item.category() == ProductCategory.HEADSET ? true : null).build());
            return product;
        });
    }

    private void addDemoReviews(List<Product> products) {
        List<User> users = List.of(
                demoUser("demo-reviewer1@gamegearmatch.local", "데모 체험단 민준"),
                demoUser("demo-reviewer2@gamegearmatch.local", "데모 체험단 서연"),
                demoUser("demo-reviewer3@gamegearmatch.local", "데모 체험단 지우"));
        String[] starts = {"설정 과정이 간단했고", "며칠 동안 사용하면서", "여러 장르의 게임에서 시험해 보니"};
        String[] ends = {"반응이 안정적이었습니다.", "마감과 사용감이 만족스러웠습니다.", "가격대에 기대한 기능을 충분히 보여줬습니다."};
        for (int p = 0; p < products.size(); p++) {
            Product product = products.get(p);
            for (int r = 0; r < 2 + p % 2; r++) {
                User user = users.get(r);
                if (reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId())) continue;
                String content = "[데모 리뷰] " + product.getName() + " 시연용 후기입니다. "
                        + starts[(p + r) % starts.length] + " " + reviewPoint(product.getCategory(), p + r) + " 부분의 "
                        + ends[(p * 2 + r) % ends.length];
                reviewRepository.save(Review.builder().user(user).product(product)
                        .rating(4 + ((p + r) % 4 == 0 ? 1 : 0)).content(content).build());
            }
        }
    }

    private User demoUser(String email, String name) {
        return userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder().email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())).name(name).role(Role.USER).build()));
    }

    private String reviewPoint(ProductCategory category, int seed) {
        String[][] points = {{"그립감과 클릭", "버튼 배치", "센서 움직임", "연결 안정성"},
                {"키감과 입력", "배열 구성", "소음 수준", "조명 표현"},
                {"착용감과 음질", "마이크 전달력", "공간감", "연결 안정성"}};
        int c = category == ProductCategory.MOUSE ? 0 : category == ProductCategory.KEYBOARD ? 1 : 2;
        return points[c][seed % 4];
    }

    private List<Item> catalog() {
        List<Item> c = new ArrayList<>();
        addMice(c); addKeyboards(c); addHeadsets(c);
        return c;
    }

    private void addMice(List<Item> c) {
        c.add(m("Aerox 5 Wireless","SteelSeries",169000,SS+"62406/d447dc561ffd4b45a8162f663464f34b.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 5","SteelSeries",109000,SS+"62401/4fc6959164d24b90a5ba63deb22bc5c3.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRED));
        c.add(m("Aerox 5 Wireless Diablo IV Edition","SteelSeries",189000,SS+"62403/75b0a134d0c7438cad4e3df8380d152a.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 3 Wireless Onyx","SteelSeries",119000,SS+"62612/f707609b4f0942168066704ab69c820a.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 3 Wireless Snow","SteelSeries",119000,SS+"62608/4e17c3d26d8847cfae7c7085d3964a85.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 3 Wireless Ghost","SteelSeries",119000,SS+"62610/d297d5fd1f344927baa775ed883c9069.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 9 Wireless","SteelSeries",199000,SS+"62618/666a8680fbff440384dc74d49ed324b8.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Rival 5","SteelSeries",79000,SS+"62551/43bea179306044d396b3b82273182916.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRED));
        c.add(m("Prime+","SteelSeries",89000,SS+"62490/68b8b2de3a8f473b820be61533c3398c.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRED));
        c.add(m("Logitech G502 HERO","Logitech G",69000,"https://m.media-amazon.com/images/I/61mpMH5TzkL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(m("Logitech G305 LIGHTSPEED","Logitech G",59000,"https://m.media-amazon.com/images/I/51sg9BLSMTL._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(m("Razer DeathAdder V3 Pro","Razer",199000,"https://m.media-amazon.com/images/I/41tQX4U2dgL.jpg",ConnectionType.BOTH));
        c.add(m("Redragon M612 Predator","Redragon",39000,"https://m.media-amazon.com/images/I/61vF4LdktpL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(m("Aerox 3 Wireless Destiny 2 Edition","SteelSeries",149000,SS+"62610/d297d5fd1f344927baa775ed883c9069.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 3 Wireless Black","SteelSeries",119000,SS+"62612/f707609b4f0942168066704ab69c820a.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(m("Aerox 3 Wireless White","SteelSeries",119000,SS+"62608/4e17c3d26d8847cfae7c7085d3964a85.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
    }

    private void addKeyboards(List<Item> c) {
        c.add(k("Logitech G915 LIGHTSPEED","Logitech G",249000,"https://m.media-amazon.com/images/I/614Jk1dIoGL._AC_SL1500_.jpg",ConnectionType.BOTH));
        c.add(k("SteelSeries Apex 3 RGB","SteelSeries",79000,"https://m.media-amazon.com/images/I/61jhVTLFAEL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("Logitech MK270 Combo","Logitech",49000,"https://m.media-amazon.com/images/I/61gSpxZTZZL._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(k("NPET K10V3PRO","NPET",39000,"https://m.media-amazon.com/images/I/61is2ZwnHEL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("Logitech K120","Logitech",19000,"https://m.media-amazon.com/images/I/611G2Fw-4PL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("Rii RK907","Rii",29000,"https://m.media-amazon.com/images/I/51jkxo3a7bL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("Logitech K270","Logitech",39000,"https://m.media-amazon.com/images/I/715mkYSaJLL._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(k("Dell KB216","Dell",25000,"https://m.media-amazon.com/images/I/51jkxo3a7bL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("AULA F99","AULA",109000,"https://m.media-amazon.com/images/I/61qhXbkJvTL._AC_SL1500_.jpg",ConnectionType.BOTH));
        c.add(k("Rii RK100+","Rii",35000,"https://m.media-amazon.com/images/I/61gSpxZTZZL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("Amazon Basics Wired Keyboard","Amazon Basics",22000,"https://m.media-amazon.com/images/I/61is2ZwnHEL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("Arteck 2.4G Wireless Keyboard","Arteck",59000,"https://m.media-amazon.com/images/I/71Yp7pxBFOL._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(k("Logitech Wave Keys","Logitech",99000,"https://m.media-amazon.com/images/I/61gSpxZTZZL._AC_SL1500_.jpg",ConnectionType.BOTH));
        c.add(k("MageGee MK-Box","MageGee",49000,"https://m.media-amazon.com/images/I/61is2ZwnHEL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(k("MOFII Retro Wireless Keyboard","MOFII",69000,"https://m.media-amazon.com/images/I/61qhXbkJvTL._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(k("Apex Pro TKL Gen 3","SteelSeries",299000,SS+"64663/0b38ae20302d4cb39d7f93999bca64f8.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRED));
        c.add(k("Apex 3 TKL","SteelSeries",69000,SS+"64831/a3b21c0da8ea42c895b7b9374470eed6.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRED));
    }

    private void addHeadsets(List<Item> c) {
        c.add(h("SteelSeries Arctis Nova 1","SteelSeries",79000,"https://m.media-amazon.com/images/I/61+zbpS+6iL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("SteelSeries Arctis 7P","SteelSeries",199000,"https://m.media-amazon.com/images/I/71IvAZyaR0L._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(h("Logitech G PRO X Wireless","Logitech G",249000,"https://m.media-amazon.com/images/I/71geIOeuw-L._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(h("Razer Kraken X","Razer",69000,"https://m.media-amazon.com/images/I/71yqtewaJKL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("HyperX Cloud Stinger Core Wireless","HyperX",99000,"https://m.media-amazon.com/images/I/71IvAZyaR0L._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(h("Razer BlackShark V2 Pro","Razer",229000,"https://m.media-amazon.com/images/I/71hGqu8tSCL._AC_SL1500_.jpg",ConnectionType.BOTH));
        c.add(h("Logitech G733 LIGHTSPEED","Logitech G",179000,"https://m.media-amazon.com/images/I/61EkW1BUMRL._AC_SL1500_.jpg",ConnectionType.WIRELESS));
        c.add(h("Razer Kraken","Razer",109000,"https://m.media-amazon.com/images/I/71yqtewaJKL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("SteelSeries Arctis 1","SteelSeries",69000,"https://m.media-amazon.com/images/I/71IL4SsThNL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("HyperX Cloud II","HyperX",119000,"https://m.media-amazon.com/images/I/61dVV8sjTLL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("Razer Kraken Tournament Edition","Razer",129000,"https://m.media-amazon.com/images/I/71hGqu8tSCL._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("SteelSeries Arctis 3","SteelSeries",99000,"https://m.media-amazon.com/images/I/61aogecaK-L._AC_SL1500_.jpg",ConnectionType.WIRED));
        c.add(h("Arctis Nova 7 Wireless","SteelSeries",239000,SS+"61553/66996dc9997f43029df61a70f1a79b40.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(h("Arctis Nova 5 Wireless","SteelSeries",179000,SS+"61670/f359857a516d4d1ab4b50ec1bcdbc6c4.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(h("Arctis 7X+","SteelSeries",199000,SS+"61472/245988988dbe4a00a591e36570c978e3.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRELESS));
        c.add(h("Arctis Pro Wireless","SteelSeries",399000,"https://media.steelseriescdn.com/thumbs/catalogue/products/00946-arctis-pro-wireless/a41ddd67a5d84418b0a7e1fa2e13488f.png.500x400_q100_crop-fit_optimize.png",ConnectionType.BOTH));
        c.add(h("Arctis 1 for PlayStation","SteelSeries",69000,SS+"61425/d1c6f574880e4e6fa1c7f1daff2e4e30.png.500x400_q100_crop-fit_optimize.png",ConnectionType.WIRED));
    }

    private Item m(String n,String b,int p,String image,ConnectionType connection){return item(n,b,p,image,connection,ProductCategory.MOUSE,"정밀 센서와 빠른 입력을 제공하는 실제 판매 게이밍 마우스");}
    private Item k(String n,String b,int p,String image,ConnectionType connection){return item(n,b,p,image,connection,ProductCategory.KEYBOARD,"게임과 일상 사용에 적합한 실제 판매 키보드");}
    private Item h(String n,String b,int p,String image,ConnectionType connection){return item(n,b,p,image,connection,ProductCategory.HEADSET,"몰입감 있는 사운드와 음성 채팅을 지원하는 실제 판매 헤드셋");}
    private Item item(String n,String b,int p,String image,ConnectionType connection,ProductCategory category,String description){return new Item(n,category,b,p,connection,image,description);}
    private record Item(String name, ProductCategory category, String brand, Integer price, ConnectionType connection, String image, String description) {}
}
