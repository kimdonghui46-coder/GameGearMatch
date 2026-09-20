import { useEffect, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { Cart } from "../types/cart";
import type { Order } from "../types/order";

const emptyAddress = { recipientName: "", recipientPhone: "", postalCode: "", address: "", deliveryRequest: "" };

function CartPage() {
  const navigate=useNavigate();
  const [cart,setCart]=useState<Cart|null>(null);
  const [shipping,setShipping]=useState(emptyAddress);
  const [error,setError]=useState("");
  const [ordering,setOrdering]=useState(false);
  const token=localStorage.getItem("accessToken");
  const applyCart=(next:Cart)=>{setCart(next); window.dispatchEvent(new Event("cart-change"));};
  useEffect(()=>{ if(token) api.get<Cart>("/api/cart").then((r)=>setCart(r.data)).catch(()=>setError("장바구니를 불러오지 못했습니다.")); },[token]);
  if(!token) return <Navigate to="/login" replace/>;

  const update=async(id:number,quantity:number)=>{ if(quantity<1)return; try{const r=await api.patch<Cart>(`/api/cart/items/${id}`,{quantity});applyCart(r.data);}catch{setError("수량을 변경하지 못했습니다.");} };
  const remove=async(id:number)=>{try{const r=await api.delete<Cart>(`/api/cart/items/${id}`);applyCart(r.data);}catch{setError("상품을 삭제하지 못했습니다.");}};
  const field=(name:keyof typeof shipping,value:string)=>setShipping(current=>({...current,[name]:value}));
  const order=async()=>{
    if(!shipping.recipientName.trim()||!shipping.recipientPhone.trim()||!shipping.postalCode.trim()||!shipping.address.trim()){setError("받는 분, 전화번호, 우편번호, 주소를 모두 입력해 주세요.");return;}
    setOrdering(true); setError("");
    try{const response=await api.post<Order>("/api/orders",shipping);window.dispatchEvent(new Event("cart-change"));navigate("/payment",{state:{order:response.data}});}
    catch{setError("주문을 생성하지 못했습니다. 배송지와 상품 재고를 확인해 주세요.");setOrdering(false);}
  };

  return <section className="page-container"><div className="page-title"><span className="eyebrow">MY CART</span><h1>장바구니</h1></div>{error&&<p className="error">{error}</p>}{!cart?<p className="state-message">불러오는 중입니다...</p>:cart.items.length===0?<div className="empty-box"><h2>장바구니가 비어 있습니다</h2><p>마음에 드는 게이밍 기어를 담아보세요.</p><Link className="primary-button" to="/">상품 보러 가기</Link></div>:<><div className="cart-layout"><div className="cart-list">{cart.items.map((item)=><article className="cart-item" key={item.id}><Link className="cart-product-link" to={`/products/${item.productId}`} aria-label={`${item.productName} 상세 보기`}><div className="cart-thumb">{item.imageUrl?<img src={item.imageUrl} alt={item.productName}/>:"GGM"}</div><div className="cart-detail"><small>{item.brand}</small><h3>{item.productName}</h3><strong>{item.price.toLocaleString("ko-KR")}원</strong></div></Link><div className="quantity-control"><button onClick={()=>void update(item.id,item.quantity-1)}>−</button><span>{item.quantity}</span><button disabled={item.quantity>=item.stock} onClick={()=>void update(item.id,item.quantity+1)}>＋</button></div><b className="line-total">{item.lineTotal.toLocaleString("ko-KR")}원</b><button className="remove-button" onClick={()=>void remove(item.id)}>삭제</button></article>)}</div><aside className="cart-summary"><h2>결제 예정 금액</h2><div><span>총 상품 수량</span><b>{cart.totalQuantity}개</b></div><div><span>배송비</span><b>무료</b></div><div className="summary-total"><span>총 결제금액</span><strong>{cart.totalPrice.toLocaleString("ko-KR")}원</strong></div><button disabled={ordering} onClick={()=>void order()}>{ordering?"주문 생성 중...":"주문하기"}</button></aside></div><section className="shipping-form"><h2>배송지 정보</h2><div className="shipping-grid"><label>받는 분<input value={shipping.recipientName} maxLength={50} onChange={e=>field("recipientName",e.target.value)} placeholder="홍길동"/></label><label>전화번호<input value={shipping.recipientPhone} maxLength={20} onChange={e=>field("recipientPhone",e.target.value)} placeholder="010-1234-5678"/></label><label>우편번호<input value={shipping.postalCode} inputMode="numeric" maxLength={6} onChange={e=>field("postalCode",e.target.value)} placeholder="12345"/></label><label className="wide">주소<input value={shipping.address} maxLength={255} onChange={e=>field("address",e.target.value)} placeholder="도로명 주소와 상세 주소"/></label><label className="wide">배송 요청사항<input value={shipping.deliveryRequest} maxLength={255} onChange={e=>field("deliveryRequest",e.target.value)} placeholder="예: 문 앞에 놓아주세요 (선택)"/></label></div></section></>}</section>;
}

export default CartPage;
