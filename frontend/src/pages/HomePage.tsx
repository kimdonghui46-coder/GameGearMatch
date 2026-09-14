import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { Product } from "../types/product";

const categories = [{ value:"",label:"전체" },{ value:"MOUSE",label:"마우스" },{ value:"KEYBOARD",label:"키보드" },{ value:"HEADSET",label:"헤드셋" }];
const categoryName: Record<string,string> = { MOUSE:"마우스", KEYBOARD:"키보드", HEADSET:"헤드셋" };

function HomePage() {
  const navigate = useNavigate();
  const [products,setProducts] = useState<Product[]>([]);
  const [category,setCategory] = useState("");
  const [search,setSearch] = useState("");
  const [loading,setLoading] = useState(true);
  const [error,setError] = useState("");
  const [toast,setToast] = useState("");

  useEffect(() => {
    setLoading(true); setError("");
    api.get<Product[]>("/api/products",{ params:category?{category}:undefined })
      .then((response)=>setProducts(response.data))
      .catch(()=>setError("상품을 불러오지 못했습니다. 백엔드 서버를 확인해 주세요."))
      .finally(()=>setLoading(false));
  },[category]);

  const filteredProducts = useMemo(() => {
    const keyword=search.trim().toLowerCase();
    return keyword ? products.filter((p)=>`${p.name} ${p.brand} ${p.description ?? ""}`.toLowerCase().includes(keyword)) : products;
  },[products,search]);

  const addToCart = async (product: Product) => {
    if (!localStorage.getItem("accessToken")) { navigate("/login"); return; }
    try {
      await api.post("/api/cart/items",{ productId:product.id,quantity:1 });
      setToast(`${product.name}을(를) 장바구니에 담았습니다.`);
      window.dispatchEvent(new Event("cart-change"));
      window.setTimeout(()=>setToast(""),2200);
    } catch { setToast("장바구니에 담지 못했습니다. 재고를 확인해 주세요."); }
  };

  return <>
    {toast && <div className="toast">{toast}</div>}
    <section className="hero-section"><div className="hero-copy"><span className="eyebrow">SMART GAMING GEAR SHOP</span><h1>당신의 플레이를<br/><em>한 단계 더 높게.</em></h1><p>성능과 취향을 함께 분석해 꼭 맞는 게이밍 기어를 제안합니다.</p><a className="primary-button" href="#products">추천 상품 보기</a></div><div className="hero-visual"><div className="hero-circle">GGM<small>GEAR FOR YOUR GAME</small></div></div></section>
    <section className="shopping-nav"><div className="search-box"><span>⌕</span><input value={search} onChange={(e)=>setSearch(e.target.value)} placeholder="찾고 싶은 게이밍 기어를 검색하세요"/></div><div className="benefits"><span>✓ 맞춤 추천</span><span>✓ 정품 보장</span><span>✓ 빠른 배송</span></div></section>
    <section className="products-section" id="products"><div className="section-heading"><div><span className="eyebrow">BEST PRODUCTS</span><h2>인기 게이밍 기어</h2></div><div className="category-tabs">{categories.map((item)=><button className={category===item.value?"active":""} key={item.value} onClick={()=>setCategory(item.value)}>{item.label}</button>)}</div></div>
      {loading&&<p className="state-message">상품을 불러오는 중입니다...</p>}{error&&<p className="state-message error">{error}</p>}{!loading&&!error&&filteredProducts.length===0&&<p className="state-message">조건에 맞는 상품이 없습니다.</p>}
      <div className="product-grid">{filteredProducts.map((product)=><article className="product-card" key={product.id}><div className="product-image">{product.imageUrl?<img src={product.imageUrl} alt={product.name}/>:<div className="product-placeholder"><b>GGM</b><span>{categoryName[product.category] ?? product.category}</span></div>}<span className="badge">BEST</span></div><div className="product-info"><span className="product-meta">{product.brand} · {categoryName[product.category] ?? product.category}</span><h3>{product.name}</h3><p>{product.description||"게임 성능을 높여주는 엄선된 게이밍 기어입니다."}</p><strong>{product.price.toLocaleString("ko-KR")}원</strong><div className="product-actions"><span>재고 {product.stock}개</span><button disabled={product.stock<1} onClick={()=>void addToCart(product)}>{product.stock<1?"품절":"장바구니 담기"}</button></div></div></article>)}</div>
    </section>
  </>;
}

export default HomePage;
