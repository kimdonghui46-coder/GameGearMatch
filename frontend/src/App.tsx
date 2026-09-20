import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Header from "./components/Header";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import CartPage from "./pages/CartPage";
import AdminProductsPage from "./pages/AdminProductsPage";
import SecurityStatus from "./components/SecurityStatus";
import ProfilePage from "./pages/ProfilePage";
import OrdersPage from "./pages/OrdersPage";
import RecommendationPage from "./pages/RecommendationPage";
import ProductDetailPage from "./pages/ProductDetailPage";
import AdminImageUploadPage from "./pages/AdminImageUploadPage";
import PaymentPage from "./pages/PaymentPage";
import PaymentSuccessPage from "./pages/PaymentSuccessPage";
import PaymentFailPage from "./pages/PaymentFailPage";
import AdminOrdersPage from "./pages/AdminOrdersPage";
import AdminReviewsPage from "./pages/AdminReviewsPage";
import "./App.css";
import "./order.css";
import "./admin-reviews.css";
import "./address.css";

function App() {
  return (
    <BrowserRouter>
      <Header />
      <main>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/recommendations" element={<RecommendationPage />} />
          <Route path="/products/:productId" element={<ProductDetailPage />} />
          <Route path="/admin/products" element={<><SecurityStatus /><AdminProductsPage /></>} />
          <Route path="/admin/images" element={<AdminImageUploadPage />} />
          <Route path="/admin/orders" element={<AdminOrdersPage />} />
          <Route path="/admin/reviews" element={<AdminReviewsPage />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/payment/success" element={<PaymentSuccessPage />} />
          <Route path="/payment/fail" element={<PaymentFailPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
