import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import api from "../api/axios";
import type { CurrentUser } from "../types/auth";
import { orderStatusLabel, type Order, type OrderStatus } from "../types/order";

const nextStatus:Partial<Record<OrderStatus,OrderStatus>>={PAID:"PREPARING",PREPARING:"SHIPPING",SHIPPING:"DELIVERED"};

function AdminOrdersPage(){
  const [authorized,setAuthorized]=useState<boolean|null>(null);const [orders,setOrders]=useState<Order[]>([]);const [error,setError]=useState("");
  useEffect(()=>{api.get<CurrentUser>("/api/users/me").then(({data})=>{const admin=data.role==="ADMIN";setAuthorized(admin);if(admin)api.get<Order[]>("/api/admin/orders").then(r=>setOrders(r.data)).catch(()=>setError("주문을 불러오지 못했습니다."));}).catch(()=>setAuthorized(false));},[]);
  if(authorized===false)return <Navigate to="/" replace/>;if(authorized===null)return <p className="state-message">권한을 확인하는 중입니다...</p>;
  const advance=async(order:Order)=>{const status=nextStatus[order.status];if(!status)return;try{const {data}=await api.patch<Order>(`/api/admin/orders/${order.id}/status`,{status});setOrders(current=>current.map(item=>item.id===data.id?data:item));}catch{setError("주문 상태를 변경하지 못했습니다.");}};
  return <section className="page-container admin-orders"><div className="page-title"><span className="eyebrow">ADMIN CONSOLE</span><h1>주문 및 배송 관리</h1><p>결제 완료 주문의 배송 단계를 순서대로 변경합니다.</p></div>{error&&<p className="error">{error}</p>}<div className="admin-order-list">{orders.map(order=><article key={order.id}><div><b>주문 #{order.id}</b><span>{order.orderName}</span><small>{order.recipientName} · {order.recipientPhone}<br/>({order.postalCode}) {order.address}</small></div><div className="admin-order-action"><strong>{orderStatusLabel[order.status]}</strong><span>{order.totalPrice.toLocaleString("ko-KR")}원</span>{nextStatus[order.status]&&<button onClick={()=>void advance(order)}>{orderStatusLabel[nextStatus[order.status]!] }로 변경</button>}</div></article>)}</div></section>;
}
export default AdminOrdersPage;
