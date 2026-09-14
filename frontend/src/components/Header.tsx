import { useCallback, useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios";
import type { CurrentUser } from "../types/auth";
import type { Cart } from "../types/cart";

function Header() {
  const navigate = useNavigate();
  const profileRef = useRef<HTMLDivElement>(null);
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [cartCount, setCartCount] = useState(0);
  const [profileOpen, setProfileOpen] = useState(false);
  const token = localStorage.getItem("accessToken");

  const loadSession = useCallback(async () => {
    if (!localStorage.getItem("accessToken")) { setUser(null); setCartCount(0); return; }
    try {
      const [userResponse, cartResponse] = await Promise.all([api.get<CurrentUser>("/api/users/me"), api.get<Cart>("/api/cart")]);
      setUser(userResponse.data); setCartCount(cartResponse.data.totalQuantity);
    } catch { localStorage.removeItem("accessToken"); setUser(null); setCartCount(0); }
  }, []);

  useEffect(() => {
    void loadSession(); window.addEventListener("auth-change", loadSession); window.addEventListener("cart-change", loadSession);
    return () => { window.removeEventListener("auth-change", loadSession); window.removeEventListener("cart-change", loadSession); };
  }, [loadSession]);

  useEffect(() => {
    const close = (event: MouseEvent) => { if (!profileRef.current?.contains(event.target as Node)) setProfileOpen(false); };
    document.addEventListener("mousedown", close); return () => document.removeEventListener("mousedown", close);
  }, []);

  const logout = () => { localStorage.removeItem("accessToken"); setUser(null); setCartCount(0); setProfileOpen(false); navigate("/"); };

  return <header className="site-header">
    <Link className="brand" to="/"><span className="brand-mark">G</span><span>GameGearMatch<small>PLAY BETTER, GEAR SMARTER</small></span></Link>
    <nav><Link to="/">전체상품</Link>{user?.role === "ADMIN" && <Link className="admin-link" to="/admin/products">상품관리</Link>}
      {token ? <><div className="profile-menu" ref={profileRef}><button className="profile-trigger" type="button" onClick={() => setProfileOpen(!profileOpen)}>{user?.name ?? "회원"}님 <span>⌄</span></button>{profileOpen && user && <div className="profile-popover"><div className="profile-avatar">{user.name.slice(0,1)}</div><strong>{user.name}</strong><span>{user.email}</span><dl><div><dt>회원번호</dt><dd>{user.id}</dd></div><div><dt>회원등급</dt><dd>{user.role === "ADMIN" ? "관리자" : "일반회원"}</dd></div></dl>{user.role === "ADMIN" && <Link to="/admin/products" onClick={() => setProfileOpen(false)}>상품 관리로 이동</Link>}<button className="profile-logout" type="button" onClick={logout}>로그아웃</button></div>}</div><Link to="/cart">장바구니 <b className="cart-count">{cartCount}</b></Link></> : <><Link to="/login">로그인</Link><Link to="/signup">회원가입</Link></>}
    </nav>
  </header>;
}

export default Header;
