import { useEffect, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { Cart } from "../types/cart";
import type { Order } from "../types/order";

function CartPage() {
  const navigate=useNavigate();
  const [cart,setCart]=useState<Cart|null>(null);
  const [error,setError]=useState("");
  const token=localStorage.getItem("accessToken");
  const applyCart=(next:Cart)=>{setCart(next); window.dispatchEvent(new Event("cart-change"));};

  useEffect(()=>{ if(token) api.get<Cart>("/api/cart").then((r)=>setCart(r.data)).catch(()=>setError("장바구니를 불러오지 못했습니다.")); },[token]);
  if(!token) return <Navigate to="/login" replace/>;

  const update=async(id:number,quantity:number)=>{ if(quantity<1)return; try{const r=await api.patch<Cart>(`/api/cart/items/${id}`,{quantity});applyCart(r.data);}catch{setError("수량을 변경하지 못했습니다.");} };
  const remove=async(id:number)=>{try{const r=await api.delete<Cart>(`/api/cart/items/${id}`);applyCart(r.data);}catch{setError("상품을 삭제하지 못했습니다.");}};
  const order=async()=>{try{const response=await api.post<Order>("/api/orders");window.dispatchEvent(new Event("cart-change"));navigate("/payment",{state:{order:response.data}});}catch{setError("주문을 생성하지 못했습니다. 상품 재고를 확인해 주세요.");}};

  return <section className="page-container"><div className="page-title"><span className="eyebrow">MY CART</span><h1>장바구니</h1></div>{error&&<p className="error">{error}</p>}{!cart?<p className="state-message">불러오는 중입니다...</p>:cart.items.length===0?<div className="empty-box"><h2>장바구니가 비어 있습니다</h2><p>마음에 드는 게이밍 기어를 담아보세요.</p><Link className="primary-button" to="/">상품 보러 가기</Link></div>:<div className="cart-layout"><div className="cart-list">{cart.items.map((item)=><article className="cart-item" key={item.id}><div className="cart-thumb">{item.imageUrl?<img src={item.imageUrl} alt={item.productName}/>:"GGM"}</div><div className="cart-detail"><small>{item.brand}</small><h3>{item.productName}</h3><strong>{item.price.toLocaleString("ko-KR")}원</strong></div><div className="quantity-control"><button onClick={()=>void update(item.id,item.quantity-1)}>−</button><span>{item.quantity}</span><button disabled={item.quantity>=item.stock} onClick={()=>void update(item.id,item.quantity+1)}>＋</button></div><b className="line-total">{item.lineTotal.toLocaleString("ko-KR")}원</b><button className="remove-button" onClick={()=>void remove(item.id)}>삭제</button></article>)}</div><aside className="cart-summary"><h2>결제 예정 금액</h2><div><span>총 상품 수량</span><b>{cart.totalQuantity}개</b></div><div><span>배송비</span><b>무료</b></div><div className="summary-total"><span>총 결제금액</span><strong>{cart.totalPrice.toLocaleString("ko-KR")}원</strong></div><button onClick={()=>void order()}>주문하기</button></aside></div>}</section>;
}

export default CartPage;
