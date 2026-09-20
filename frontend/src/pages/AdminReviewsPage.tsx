import { useEffect, useState } from "react";
import { Link, Navigate } from "react-router-dom";
import api from "../api/axios";
import type { CurrentUser } from "../types/auth";
import type { Review } from "../types/review";

function AdminReviewsPage(){
  const [authorized,setAuthorized]=useState<boolean|null>(null);
  const [reviews,setReviews]=useState<Review[]>([]);
  const [error,setError]=useState("");
  useEffect(()=>{api.get<CurrentUser>("/api/users/me").then(({data})=>{const admin=data.role==="ADMIN";setAuthorized(admin);if(admin)api.get<Review[]>("/api/admin/reviews").then(r=>setReviews(r.data)).catch(()=>setError("리뷰를 불러오지 못했습니다."));}).catch(()=>setAuthorized(false));},[]);
  if(authorized===false)return <Navigate to="/" replace/>;
  if(authorized===null)return <p className="state-message">권한을 확인하는 중입니다...</p>;
  const remove=async(review:Review)=>{if(!window.confirm(`${review.userName}님의 리뷰를 삭제할까요?`))return;setError("");try{await api.delete(`/api/admin/reviews/${review.id}`);setReviews(current=>current.filter(item=>item.id!==review.id));}catch{setError("리뷰를 삭제하지 못했습니다.");}};
  return <section className="page-container admin-reviews"><div className="page-title"><span className="eyebrow">ADMIN CONSOLE</span><h1>리뷰 관리</h1><p>전체 상품 리뷰를 확인하고 부적절한 리뷰를 삭제합니다.</p></div>{error&&<p className="error">{error}</p>}<div className="review-admin-summary">등록 리뷰 <b>{reviews.length}</b>개</div>{reviews.length===0?<div className="empty-box"><h2>등록된 리뷰가 없습니다</h2></div>:<div className="admin-review-list">{reviews.map(review=><article key={review.id}><div className="admin-review-main"><div><Link to={`/products/${review.productId}`}>{review.productName}</Link><span>{"★".repeat(review.rating)} {review.rating}점</span></div><p>{review.content}</p><small>{review.userName} · {new Date(review.createdAt).toLocaleString("ko-KR")}</small></div><button onClick={()=>void remove(review)}>삭제</button></article>)}</div>}</section>;
}
export default AdminReviewsPage;
