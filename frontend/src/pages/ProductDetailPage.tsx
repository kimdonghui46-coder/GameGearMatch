import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import type { Product } from "../types/product";
import type { Review } from "../types/review";

const categoryNames: Record<string, string> = { MOUSE: "마우스", KEYBOARD: "키보드", HEADSET: "헤드셋" };
const connectionNames: Record<string, string> = { WIRED: "유선", WIRELESS: "무선", BOTH: "유·무선" };

function ProductDetailPage() {
  const { productId } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState<Product | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    setLoading(true);
    Promise.all([
      api.get<Product>(`/api/products/${productId}`),
      api.get<Review[]>(`/api/products/${productId}/reviews`),
    ]).then(([productResponse, reviewResponse]) => {
      setProduct(productResponse.data); setReviews(reviewResponse.data);
    }).catch(() => setMessage("상품 정보를 불러오지 못했습니다."))
      .finally(() => setLoading(false));
  }, [productId]);

  const averageRating = useMemo(() => reviews.length
    ? (reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length).toFixed(1)
    : "0.0", [reviews]);

  if (loading) return <p className="state-message">상품 정보를 불러오는 중입니다...</p>;
  if (!product) return <div className="page-container empty-box"><h2>상품을 찾을 수 없습니다</h2><Link className="primary-button" to="/">목록으로 돌아가기</Link></div>;

  const addToCart = async () => {
    if (!localStorage.getItem("accessToken")) { navigate("/login"); return; }
    try {
      await api.post("/api/cart/items", { productId: product.id, quantity });
      window.dispatchEvent(new Event("cart-change")); setMessage("장바구니에 상품을 담았습니다.");
    } catch { setMessage("상품을 담지 못했습니다. 재고를 확인해 주세요."); }
  };

  const specs = [
    ["무게", product.spec?.weight != null ? `${product.spec.weight}g` : null],
    ["DPI", product.spec?.dpi?.toLocaleString("ko-KR")],
    ["폴링레이트", product.spec?.pollingRate != null ? `${product.spec.pollingRate}Hz` : null],
    ["버튼 수", product.spec?.buttonCount != null ? `${product.spec.buttonCount}개` : null],
    ["스위치", product.spec?.switchType], ["키보드 배열", product.spec?.keyboardLayout],
    ["소음 수치", product.spec?.noiseLevel], ["응답속도", product.spec?.responseTime != null ? `${product.spec.responseTime}ms` : null],
    ["배터리", product.spec?.batteryHours != null ? `${product.spec.batteryHours}시간` : null],
    ["마이크", product.spec?.microphone == null ? null : product.spec.microphone ? "지원" : "미지원"],
  ].filter(([, value]) => value != null);

  return <section className="page-container product-detail-page">
    <div className="product-detail-main">
      <div className="detail-image">{product.imageUrl ? <img src={product.imageUrl} alt={product.name}/> : <div className="product-placeholder"><b>GGM</b><span>{categoryNames[product.category]}</span></div>}</div>
      <div className="detail-info"><span className="eyebrow">{categoryNames[product.category]} · {product.brand}</span><h1>{product.name}</h1><p>{product.description}</p><div className="detail-rating"><b>★ {averageRating}</b><span>리뷰 {reviews.length}개</span></div><strong className="detail-price">{product.price.toLocaleString("ko-KR")}원</strong><dl><div><dt>연결 방식</dt><dd>{connectionNames[product.connectionType ?? ""] ?? "정보 없음"}</dd></div><div><dt>재고</dt><dd className={product.stock < 1 ? "error" : ""}>{product.stock < 1 ? "품절" : `${product.stock}개`}</dd></div></dl><div className="detail-order"><div className="quantity-control"><button disabled={quantity <= 1} onClick={() => setQuantity(quantity - 1)}>−</button><span>{quantity}</span><button disabled={quantity >= product.stock} onClick={() => setQuantity(quantity + 1)}>＋</button></div><button className="detail-cart-button" disabled={product.stock < 1} onClick={() => void addToCart()}>{product.stock < 1 ? "품절" : "장바구니 담기"}</button></div>{message && <p className="form-message">{message}</p>}</div>
    </div>
    <section className="detail-section"><h2>상세 사양</h2><div className="spec-grid">{specs.map(([label, value]) => <div key={String(label)}><span>{label}</span><b>{String(value)}</b></div>)}</div></section>
    <section className="detail-section"><div className="review-heading"><h2>구매 후기</h2><span>평균 ★ {averageRating} · {reviews.length}개</span></div>{reviews.length === 0 ? <p className="state-message">아직 등록된 리뷰가 없습니다.</p> : <div className="public-review-list">{reviews.map((review) => <article key={review.id}><div><b>{review.userName}</b><span>{new Date(review.createdAt).toLocaleDateString("ko-KR")}</span></div><strong>{"★".repeat(review.rating)}{"☆".repeat(5-review.rating)}</strong><p>{review.content}</p></article>)}</div>}</section>
  </section>;
}

export default ProductDetailPage;
